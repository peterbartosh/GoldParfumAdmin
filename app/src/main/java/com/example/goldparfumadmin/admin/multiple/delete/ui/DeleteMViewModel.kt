package com.example.goldparfumadmin.admin.multiple.delete.ui


import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldparfumadmin.repository.FireRepository
import com.example.goldparfumadmin.utils.ProductType
import com.example.goldparfumadmin.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteMViewModel @Inject constructor(private val repository: FireRepository)  : ViewModel() {

    val isLoading = mutableStateOf(false)

    fun delete(
        type: ProductType,
        volumes: List<String>,
        context : Context
    ) = viewModelScope.launch {
        isLoading.value = true
        val deleteState = repository.deleteProducts(productType = type, volumes = volumes, onSuccess = {})
        if (deleteState.first.value)
            showToast(context, "Не удалено ${deleteState.second.value} позиций")
        isLoading.value = false
    }

}