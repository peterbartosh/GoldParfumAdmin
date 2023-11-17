package com.example.goldparfumadmin.presentation.single.add.ui

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
class AddSViewModel @Inject constructor(private val repository: FireRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.NotInitialized())
    var uiState : StateFlow<UiState> = _uiState

    fun addProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) = viewModelScope.launch {
        _uiState.value = UiState.Loading()

        val createResult = repository.createProduct(product)
        _uiState.value = if (createResult.isSuccess)
             UiState.Success().also { onSuccess() }
        else
            UiState.Failure().also {
                val message = createResult.exceptionOrNull()?.message.toString()
                onFailure(message)
            }

    }
}