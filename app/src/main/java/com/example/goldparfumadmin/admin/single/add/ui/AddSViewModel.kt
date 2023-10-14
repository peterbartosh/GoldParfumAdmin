package com.example.goldparfumadmin.admin.single.add.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldparfumadmin.model.Product
import com.example.goldparfumadmin.repository.FireRepository
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
        val createState = repository.createProduct(product)
        isSuccess = createState.first
        message = createState.second.value
        isLoading.value = false
    }



}