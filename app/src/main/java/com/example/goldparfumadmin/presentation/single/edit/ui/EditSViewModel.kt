package com.example.goldparfumadmin.presentation.single.edit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldparfumadmin.data.model.Product
import com.example.goldparfumadmin.data.repository.FireRepository
import com.example.goldparfumadmin.data.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditSViewModel @Inject constructor(private val repository: FireRepository)  : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.NotInitialized())
    var uiState : StateFlow<UiState> = _uiState

    private val _product = MutableStateFlow<Product?>(null)
    var product : StateFlow<Product?> = _product

    fun updateProduct(
        productId : String,
        updatedProduct: Product,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) = viewModelScope.launch {
        _uiState.value = UiState.Loading()
        val updatedResult = repository.updateProduct(productId, updatedProduct)
        _uiState.value = if (updatedResult.isSuccess)
            UiState.Success().also { onSuccess() }
        else
            UiState.Failure().also {
                onFailure(updatedResult.exceptionOrNull()?.message.toString())
            }
    }

    fun findProduct(
        productId: String,
        onFailure: (String) -> Unit
    ) = viewModelScope.launch {
        _uiState.value = UiState.Loading()
        repository.findProduct(productId)?.let { prod ->
            _product.value = prod
            _uiState.value = UiState.Success()
            return@launch
        }

        _uiState.value = UiState.Failure()
        onFailure("Продукт не найден")
    }

}