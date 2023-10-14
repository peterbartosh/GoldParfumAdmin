package com.example.goldparfumadmin.admin.orders.collect_orders.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldparfumadmin.model.Order
import com.example.goldparfumadmin.repository.FireRepository
import com.example.goldparfumadmin.utils.OrderStatus
import com.example.goldparfumadmin.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderCollectingViewModel @Inject constructor(private val repository: FireRepository)  : ViewModel() {


    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var isFailure by mutableStateOf(false)

    private val _ordersList = mutableListOf<Order>()
    var ordersList = _ordersList.toList()

    private val _orderProductsIds = mutableMapOf<String, List<String>>()
    var orderProductsIds = _orderProductsIds.toMap()



    fun getActiveOrders() = viewModelScope.launch {
        val deferred = async {



        }

        deferred.await()
    }

    fun generateExcelFile() = viewModelScope.launch {
        val deferred = async(Dispatchers.Default) {

        }

        deferred.await()
    }

    fun updateProductsStates() = viewModelScope.launch {
        val deferred = async(Dispatchers.IO) {

        }

        deferred.await()
    }




}
