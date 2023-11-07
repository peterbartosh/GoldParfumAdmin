package com.example.goldparfumadmin.presentation.orders.edit_order_status.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldparfumadmin.data.model.Order
import com.example.goldparfumadmin.data.model.OrderProduct
import com.example.goldparfumadmin.data.repository.FireRepository
import com.example.goldparfumadmin.data.utils.OrderStatus
import com.example.goldparfumadmin.data.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditOrderStatusViewModel @Inject constructor(private val repository: FireRepository)  : ViewModel() {


    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var isFailure by mutableStateOf(false)

    var foundOrders : List<Order> = emptyList()

    var orderProducts = mutableMapOf<String, List<OrderProduct>>()

    fun findOrder(orderNumber : String) = viewModelScope.launch {
        isLoading = true
        isSuccess = false
        isFailure = false
        foundOrders = repository.findOrders(orderNumber)
        foundOrders.forEach { order ->
            //Log.d("ORDER_TEST", "findOrder: ${order.address}")
            order.id?.let { id ->
                //Log.d("ORDER_TEST", "findOrder: ${order.address} $id")
                orderProducts[id] = repository.getOrderProducts(id)
                //Log.d("ORDER_TEST", "findOrder: ${orderProducts[id]?.map { it.productId }}")
            }
        }
        if (foundOrders.isEmpty()) isFailure = true
        isSuccess = !isFailure
        isLoading = false
    }


    fun setOrderStatus(orderId : String, orderStatus : OrderStatus, context: Context) = viewModelScope.launch {
        isLoading = true
        val result = repository.setOrderStatusExplicitly(orderId, orderStatus)
        if (result.isSuccess)
            showToast(context, "Статус заказа обновлён!")
        else
            showToast(context, "${result.exceptionOrNull()} ${result.exceptionOrNull()?.message}")
        isLoading = false
    }
}
