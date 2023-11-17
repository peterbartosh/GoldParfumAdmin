package com.example.goldparfumadmin.presentation.multiple.delete.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldparfumadmin.data.repository.FireRepository
import com.example.goldparfumadmin.data.utils.ProductType
import com.example.goldparfumadmin.data.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteMViewModel @Inject constructor(private val repository: FireRepository)  : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.NotInitialized())
    var uiState : StateFlow<UiState> = _uiState

    fun delete(
        type: ProductType,
        volume: Double?,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        _uiState.value = UiState.Loading()
        repository.deleteProducts(productType = type, volume = volume, onError = {message -> onError(message)})
        _uiState.value = UiState.Success()
    }

}