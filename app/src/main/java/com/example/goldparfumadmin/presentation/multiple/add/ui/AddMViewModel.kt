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
import com.example.goldparfumadmin.data.utils.UiState
import com.example.goldparfumadmin.data.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min
import kotlin.system.measureTimeMillis

const val TAG = "AddMViewModel"

@HiltViewModel
class AddMViewModel @Inject constructor(private val repository: FireRepository)  : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.NotInitialized())
    var uiState : StateFlow<UiState> = _uiState

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
        volume : Double?
    ) = viewModelScope.launch(Dispatchers.IO) {

        _uiState.value = UiState.Loading()
        progressState.value = 0

        val createStateAmount = mutableStateOf(0)
        val parseFailedAmount = mutableStateOf(0)

        val excelDataLoader = ExcelDataLoader(context = context, uri = uri, productType = type)

        fileRowsAmount.value = excelDataLoader.getRowsAmount() ?: 0

        val time1 = measureTimeMillis {
            excelDataLoader.parse(volume = volume, scope = this)
        }

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

        _uiState.value = if (min != 0) {
            showToast(context, "Не добавлено $min позиций")
            UiState.Failure()
        } else {
            UiState.Success()
        }
    }

}