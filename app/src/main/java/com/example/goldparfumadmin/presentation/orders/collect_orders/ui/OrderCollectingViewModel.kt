package com.example.goldparfumadmin.presentation.orders.collect_orders.ui

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldparfumadmin.data.excel.ExcelFileGeneratorImpl
import com.example.goldparfumadmin.data.model.Order
import com.example.goldparfumadmin.data.model.OrderProduct
import com.example.goldparfumadmin.data.repository.FireRepository
import com.example.goldparfumadmin.data.utils.ProductType
import com.example.goldparfumadmin.data.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class PairInt(var first : Int, var second : Int)

@HiltViewModel
class OrderCollectingViewModel @Inject constructor(private val repository: FireRepository)  : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.NotInitialized())
    var uiState : StateFlow<UiState> = _uiState

    fun run(context: Context) = viewModelScope.launch {

        _uiState.value = UiState.Loading()

        val activeOrders = getActiveOrders()

        val updateResult = repository.updateActiveOrders()

        val groupedOrders = groupOrders(activeOrders)

        val deferreds = groupedOrders.map { entry ->
            async{
                when (entry.key) {
                    ProductType.Tester -> {

                        val map = mutableMapOf<Double, MutableMap<String, PairInt>>()

                        entry.value.forEach { mem ->
                            try {
                                val volume = mem.key.split(".")[1].toDouble()
                                if (map[volume] == null)
                                    map[volume] = mutableMapOf()

                                map[volume]?.set(mem.key, mem.value)
                            } catch (e: Exception) {
                                Log.d("ERROR_ERROR", "run: ${e.message}")
                            }
                        }

                        map.forEach { en ->
                            generateExcelFile(
                                testerVolume = en.key,
                                probeVolume = null,
                                context = context,
                                productType = ProductType.Tester,
                                orders = en.value,
                                compactOrders = null
                            )
                        }
                    }

                    ProductType.Probe -> {

                        val map = mutableMapOf<Double, MutableMap<String, PairInt>>()

                        ProductType.Probe.getVolumes().forEach { vol ->
                            map[vol] = mutableMapOf()
                        }

                        entry.value.forEach { mem ->
                            try {
                                val volume = mem.key.split(".")[1].toDouble()
                                map[volume]?.set(mem.key, mem.value)
                            } catch (e: Exception) {
                                Log.d("ERROR_ERROR", "run: ${e.message}")
                            }
                        }

                        map.forEach { en ->
                            generateExcelFile(
                                testerVolume = null,
                                probeVolume = en.key,
                                context = context,
                                productType = ProductType.Probe,
                                orders = en.value,
                                compactOrders = null
                            )
                        }
                    }

                    ProductType.Compact -> {

                        val compactOrders = mutableMapOf<String, MutableMap<String, PairInt>>()

                        val volumes = listOf("15", "45", "35", "80", "10", "60", "100")

                        volumes.forEach { vol ->
                            compactOrders[vol] = mutableMapOf()
                        }

                        entry.value.forEach { mem ->
                            try {
                                val volume = mem.key.split(".")[1]
                                val ind = mem.key.split(".").last().trim()
                                compactOrders[volume]?.put(ind, mem.value)
                            } catch (e: Exception) {
                                Log.d("ERROR_ERROR", "run: ${e.message}")
                            }
                        }

                        generateExcelFile(
                            productType = entry.key,
                            context = context,
                            orders = null,
                            probeVolume = null,
                            testerVolume = null,
                            compactOrders = compactOrders
                        )
                    }

                    else -> {
                        generateExcelFile(
                            productType = entry.key,
                            context = context,
                            orders = entry.value,
                            probeVolume = null,
                            testerVolume = null,
                            compactOrders = null
                        )
                    }
                }
            }
        }

        deferreds.awaitAll()

        _uiState.value = UiState.Success()
    }

    private suspend fun getActiveOrders() : List<OrderProduct> {

        val ordersList = mutableListOf<Order>()
        val orderProductsList = mutableListOf<OrderProduct>()

        repository.getActiveOrders()
            .catch { e ->
                Log.d("ERROR_ERROR", "getActiveOrders: $e: ${e.message}")
            }
            .collect { order -> ordersList.add(order) }

        ordersList.forEach { order ->
            order.id?.let { id ->
                orderProductsList.addAll(repository.getOrderProducts(id))
            }
        }

        return orderProductsList
    }

    private suspend fun groupOrders(
        orderProductsList : List<OrderProduct>
    ) : Map<ProductType, Map<String, PairInt>> {

        return withContext(Dispatchers.IO) {

            try {

                val map = mutableMapOf<String, PairInt>()


                orderProductsList.forEach { orderProduct ->
                    orderProduct.productId?.let { productId ->
                        map[productId] = PairInt(0, 0)
                    }
                }

                orderProductsList.forEach { orderProduct ->
                    orderProduct.productId?.let { productId ->
                        map[productId]?.first = (map[productId]?.first ?: 0) + (orderProduct.cashAmount ?: 0)
                        map[productId]?.second = (map[productId]?.second ?: 0) + (orderProduct.cashlessAmount ?: 0)
                    }
                }


                val returnMap = mutableMapOf<ProductType, MutableMap<String, PairInt>>()

                map.forEach { entry ->
                    repository.findProduct(entry.key)?.type?.let { type ->
                        val productType = ProductType.valueOf(type)
                        if (returnMap[productType] == null) returnMap[productType] = mutableMapOf()
                        returnMap[productType]?.put(entry.key, entry.value)
                    }
                }

                returnMap.toMap()

            } catch (e: Exception) {
                Log.d("ERROR_ERROR", "groupOrders: $e ${e.message}")
                emptyMap()
            }
        }
    }


    private suspend fun generateExcelFile(
        testerVolume: Double?,
        probeVolume: Double?,
        context: Context,
        productType: ProductType,
        orders: Map<String, PairInt>?,
        compactOrders: Map<String, Map<String, PairInt>>?
    ) {
        withContext(Dispatchers.IO) {

            var fileName = ""

            val indices = when (productType) {
                ProductType.Original -> Pair(3, 3).also { fileName = "Прайс на оригиналы"}
                ProductType.Tester -> Pair(2, 3).also {
                    fileName = when (testerVolume?.toInt()!!){
                        55 -> "Tester-55ml"
                        60 -> "Тестер 60 ml"
                        65 -> "Тестеры 65 мл"
                        110 -> "Тестер 110 ml"
                        115 -> "Тестеры 115 мл"
                        125 -> "Tester 125 ml"
                        else -> ""
                    }
                }
                ProductType.Probe -> Pair(2, 3).also { fileName = when (probeVolume?.toInt()!!){
                    30 -> "Пробник 30 мл"
                    35 -> "Пробник 35мл"
                    else -> ""
                }}
                ProductType.Auto -> Pair(2, 3).also { fileName = "Автопарфюм" }
                ProductType.Diffuser -> Pair(3, 5).also { fileName = "Ароматы для дома (диффузоры)" }
                ProductType.Compact -> Pair(3, 3).also { fileName = "Компакт" }
                ProductType.Licensed -> Pair(2, 2).also { fileName = "Лицензионная парфюмерия" }
                ProductType.Lux -> null
                ProductType.Selectives -> Pair(2, 3).also { fileName = "Прайс на СЕЛЕКТИВЫ" }
                ProductType.EuroA -> Pair(1, 2).also { fileName = "Прайс на ЕВРО А+" }
                ProductType.NotSpecified -> null
            }

            indices?.let { inds ->
                val downloads =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val telegram = downloads.listFiles { _, name -> name == "Telegram" }?.first()

                val excelFiles = telegram?.listFiles()

                val file = excelFiles?.find { it.canRead() && it.name.contains(fileName) }

                val uri = file?.toUri()

                if (uri != null) {

                    val generator = ExcelFileGeneratorImpl(uri, context, fileName)

                    when (productType) {
                        ProductType.Compact -> generator.generateCompact(
                            firstCellInd = inds.first,
                            secondCellInd = inds.second,
                            orders = compactOrders ?: mapOf()
                        )

                        ProductType.Selectives, ProductType.EuroA -> generator.generateEuroOrSelectives(
                            firstCellInd = inds.first,
                            secondCellInd = inds.second,
                            orders = orders?.mapKeys { it.key.split(".").last() } ?: mapOf()
                        )

                        else -> generator.generate(
                            firstCellInd = inds.first,
                            secondCellInd = inds.second,
                            orders = orders?.mapKeys { it.key.split(".").last() } ?: mapOf()
                        )
                    }
                } else {
                    Log.d("ERROR_ERROR", "generateExcelFile: uri == null, ${productType.name}")
                }
            }
        }
    }
}
