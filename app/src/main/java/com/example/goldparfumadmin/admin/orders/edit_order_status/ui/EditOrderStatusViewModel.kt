package com.example.goldparfumadmin.admin.orders.edit_order_status.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldparfumadmin.model.Order
import com.example.goldparfumadmin.model.OrderProduct
import com.example.goldparfumadmin.repository.FireRepository
import com.example.goldparfumadmin.utils.OrderStatus
import com.example.goldparfumadmin.utils.showToast
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
        foundOrders = repository.findOrders(orderNumber)
        foundOrders.forEach { order ->
            //Log.d("ORDER_TEST", "findOrder: ${order.address}")
            order.id?.let { id ->
                //Log.d("ORDER_TEST", "findOrder: ${order.address} $id")
                orderProducts[id] = repository.getOrderProducts(id)
                Log.d("ORDER_TEST", "findOrder: ${orderProducts[id]?.map { it.productId }}")
            }
        }
        if (foundOrders.isEmpty()) isFailure = true
        isSuccess = !isFailure
        isLoading = false
    }


    fun setOrderStatus(orderId : String, orderStatus : OrderStatus, context: Context) = viewModelScope.launch {
        isLoading = true
        val state = repository.setOrderStatusExplicitly(orderId, orderStatus)
        if (state.first)
            showToast(context, "Статус заказа обновлён!")
        else
            showToast(context, state.second)
        isLoading = false
    }
}
