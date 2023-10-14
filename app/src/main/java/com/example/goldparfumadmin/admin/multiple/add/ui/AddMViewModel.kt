package com.example.goldparfumadmin.admin.multiple.add.ui


import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldparfumadmin.excel.Parser
import com.example.goldparfumadmin.repository.FireRepository
import com.example.goldparfumadmin.utils.ProductType
import com.example.goldparfumadmin.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min
import kotlin.system.measureTimeMillis

@HiltViewModel
class AddMViewModel @Inject constructor(private val repository: FireRepository)  : ViewModel() {

    val isLoading = mutableStateOf(false)
    val isSuccess = mutableStateOf(false)
    val progressState = mutableStateOf(0)
    val fileRowsAmount = mutableStateOf(0)

    fun getName(uri: Uri, context: Context) : String {
        return Parser(context, uri).getFileName()
    }

    fun addAll(
        uri: Uri,
        context : Context,
        type : ProductType,
        volume : String
    ) = viewModelScope.launch(Dispatchers.IO) {

        isLoading.value = true

        val createStateAmount = mutableStateOf(0)
        val parseFailedAmount = mutableStateOf(0)

        val parser = Parser(context, uri)

        fileRowsAmount.value = parser.getRowsAmount()

        val deferred = parser.parse(volume = volume, type = type, scope = this)

        val time1 = measureTimeMillis {
            deferred.await()
        }

        delay(2000)

        Log.d("TIME_TEST", "time 1 = $time1 ms")

        val defs = parser.mutableStateFlow.map { product ->
            async {
                repository.createProduct(product)
                progressState.value++
               // Log.d("LONG_OPER_TEST", "add: ${progressState.value} added")
                Unit
            }
        }

        val time2 = measureTimeMillis {
            defs.awaitAll()
        }

        Log.d("TIME_TEST", "time 2 = $time2 ms")

        parser.mutableStateFlow.clear()

        val min = min(createStateAmount.value, parseFailedAmount.value)

        if (min != 0)
            showToast(context, "Не добавлено $min позиций")


        isLoading.value = false
    }

}