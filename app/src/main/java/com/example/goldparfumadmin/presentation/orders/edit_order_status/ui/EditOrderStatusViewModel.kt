package com.example.goldparfumadmin.presentation.orders.edit_order_status.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldparfumadmin.data.model.Order
import com.example.goldparfumadmin.data.model.OrderProduct
import com.example.goldparfumadmin.data.repository.FireRepository
import com.example.goldparfumadmin.data.utils.OrderStatus
import com.example.goldparfumadmin.data.utils.UiState
import com.example.goldparfumadmin.data.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditOrderStatusViewModel @Inject constructor(private val repository: FireRepository)  : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.NotInitialized())
    var uiState : StateFlow<UiState> = _uiState

    var foundOrders : List<Order> = emptyList()

    var orderProducts = mutableMapOf<String, List<OrderProduct>>()

    fun findOrder(orderNumber : String) = viewModelScope.launch {
        _uiState.value = UiState.Loading()
        foundOrders = repository.findOrders(orderNumber)
        foundOrders.forEach { order ->
            order.id?.let { id ->
                orderProducts[id] = repository.getOrderProducts(id)
            }
        }
        _uiState.value = if (foundOrders.isEmpty())
            UiState.Failure()
        else
            UiState.Success()
    }


    fun setOrderStatus(orderId : String, orderStatus : OrderStatus, context: Context) = viewModelScope.launch {
        _uiState.value = UiState.Loading()
        val result = repository.setOrderStatusExplicitly(orderId, orderStatus)
        _uiState.value = if (result.isSuccess){
            showToast(context, "Статус заказа обновлён!")
            UiState.Success()
        }
        else {
            showToast(context, "${result.exceptionOrNull()} ${result.exceptionOrNull()?.message}")
            UiState.Failure()
        }
    }
}
