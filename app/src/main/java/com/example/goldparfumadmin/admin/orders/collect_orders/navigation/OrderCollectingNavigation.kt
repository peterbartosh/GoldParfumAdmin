package com.example.goldparfumadmin.admin.orders.collect_orders.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.goldparfumadmin.admin.orders.collect_orders.ui.OrderCollectingScreen
import com.example.goldparfumadmin.admin.orders.collect_orders.ui.OrderCollectingViewModel

const val orderCollectingRoute = "order"

fun NavController.navigateToOrderCollecting(navOptions: NavOptions? = null){
    this.navigate(orderCollectingRoute, navOptions)
}

fun NavGraphBuilder.orderCollectingScreen() {
    composable(orderCollectingRoute) {
        val orderCollectingViewModel = hiltViewModel<OrderCollectingViewModel>()
        OrderCollectingScreen(orderCollectingViewModel = orderCollectingViewModel)

    }
}