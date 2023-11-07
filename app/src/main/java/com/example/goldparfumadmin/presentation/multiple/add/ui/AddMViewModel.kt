package com.example.goldparfumadmin.presentation.multiple.add.ui


import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldparfumadmin.data.excel.ExcelDataLoader
import com.example.goldparfumadmin.data.repository.FireRepository
import com.example.goldparfumadmin.data.utils.ProductType
import com.example.goldparfumadmin.data.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min
import kotlin.system.measureTimeMillis

const val TAG = "AddMViewModel"

@HiltViewModel
class AddMViewModel @Inject constructor(private val repository: FireRepository)  : ViewModel() {

    val isLoading = mutableStateOf(false)
    val isSuccess = mutableStateOf(false)
    val fileRowsAmount = mutableStateOf(0)

    val progressState = mutableStateOf(0)

    @Synchronized fun incrementProgress(){ progressState.value++ }


    fun getName(uri: Uri, context: Context, type : ProductType) : String {
        return ExcelDataLoader(context = context, uri = uri, productType = type).getFileName()
    }

    fun addAll(
        uri: Uri,
        context : Context,
        type : ProductType,
        volume : Double?,
        dollarCurrency : String
    ) = viewModelScope.launch(Dispatchers.IO) {

        isLoading.value = true
        progressState.value = 0


        val currency = try {
            dollarCurrency.replace(",", ".").trim().toDouble()
        } catch (e : Exception){
            Log.d(TAG, "addAll: $e")
            3.29
        }

        val createStateAmount = mutableStateOf(0)
        val parseFailedAmount = mutableStateOf(0)

        val excelDataLoader = ExcelDataLoader(context = context, uri = uri, productType = type)

        fileRowsAmount.value = excelDataLoader.getRowsAmount() ?: 0

        //parser.parse(volume = volume, type = type, scope = this)

        val time1 = measureTimeMillis {
            //volume?.let { vol ->
            excelDataLoader.parse(volume = volume, scope = this, dollarCurrency = currency)
            //}
        }



        //Log.d("TIME_TEST", "addAll: ${excelDataLoader.products.map { it == null }}")

        //showToast(context, "Не преобразовано продуктов - ${excelDataLoader.errorsCounter.get()}")


        delay(1000)

        val map = mutableMapOf<Double, MutableList<String?>>()
        excelDataLoader.products.forEach { p ->
            if (p == null) {
                delay(200)

                    p?.volume?.let { vol ->
                        if (map[vol] == null)
                            map[vol] = mutableListOf()
                        map[vol]?.add(p.id)
                }
            }
        }

//        map.forEach { (key, value) ->
//            Log.d("KISHIOJ", "$key : ${value.size}")
//        }
//
//        excelDataLoader.products.mapIndexed {i, v ->
//            Log.d("SKOAHUSI", "$i: ${v.id}")
//        }

//        val data = excelDataLoader.products.sortedBy { it.id?.split(".")?.last()?.toInt() }
//            .joinToString("") { it.id.toString() + " " + it.brand.toString() + "\n" }
//
//        Log.d("DKJIOJS", data)

        Log.d("TIME_TEST", "time 1 = $time1 ms")

        Log.d("ERROR_ERROR", "addAll: ${excelDataLoader.errorsCounter.get()}")

        val defs = excelDataLoader.products.map { product ->
            async {
                if (product != null) {
                        val createResult = repository.createProduct(product)
                        if (createResult.isSuccess)
                            incrementProgress()
                        else
                            Log.d("ERROR_ERROR", product.id.toString())
                        Unit

                    }
                }
            }

        val time2 = measureTimeMillis {
            defs.awaitAll()
        }

        Log.d("TIME_TEST", "time 2 = $time2 ms")

        excelDataLoader.products.clear()

        val min = min(createStateAmount.value, parseFailedAmount.value)

        if (min != 0)
            showToast(context, "Не добавлено $min позиций")


        isLoading.value = false
    }

}