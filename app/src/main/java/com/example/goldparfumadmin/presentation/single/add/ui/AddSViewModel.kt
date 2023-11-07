package com.example.goldparfumadmin.presentation.single.add.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldparfumadmin.data.model.Product
import com.example.goldparfumadmin.data.repository.FireRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSViewModel @Inject constructor(private val repository: FireRepository) : ViewModel() {

    val isLoading = mutableStateOf(false)
    var isSuccess = mutableStateOf(false)
    var message = ""

    fun addProduct(product: Product) = viewModelScope.launch {
        isLoading.value = true
        val createResult = repository.createProduct(product)
        isSuccess.value = createResult.isSuccess
        if (!isSuccess.value) message = createResult.exceptionOrNull()?.message.toString()
        isLoading.value = false
    }



}