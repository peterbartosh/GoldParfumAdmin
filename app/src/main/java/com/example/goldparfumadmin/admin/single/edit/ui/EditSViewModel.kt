package com.example.goldparfumadmin.admin.single.edit.ui

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldparfumadmin.model.Product
import com.example.goldparfumadmin.repository.FireRepository
import com.example.goldparfumadmin.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditSViewModel @Inject constructor(private val repository: FireRepository)  : ViewModel() {


    val isLoading = mutableStateOf(false)
    var isSuccess = mutableStateOf(false)
    var product : MutableStateFlow<Product?> = MutableStateFlow(null)

    fun updateProduct(productId : String, updatedProduct: Product) = viewModelScope.launch {
        isLoading.value = true
            val updated = repository.updateProduct(productId, updatedProduct)
            isSuccess.value = updated.value
        isLoading.value = false
    }

    fun findProduct(productId: String, context : Context) = viewModelScope.launch {
        isSuccess.value = false
        isLoading.value = true
        product = MutableStateFlow(null)
        withContext(Dispatchers.IO) {
            repository.findProduct(productId).collect {
                product.emit(it)
                isSuccess.value = it != null
            }
        }
        if (!isSuccess.value) showToast(context, "Ошибка.\nПродукт не найден")
        isLoading.value = false
    }

}