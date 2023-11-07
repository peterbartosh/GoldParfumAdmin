package com.example.goldparfumadmin.presentation.orders.edit_order_status.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.goldparfumadmin.presentation.orders.edit_order_status.ui.EditOrderStatusScreen
import com.example.goldparfumadmin.presentation.orders.edit_order_status.ui.EditOrderStatusViewModel

const val editOrderStatusRoute = "editOrderStatus"

fun NavController.navigateToEditOrderStatus(navOptions: NavOptions? = null){
    this.navigate(editOrderStatusRoute, navOptions)
}

fun NavGraphBuilder.editOrderStatusScreen(){
    composable(editOrderStatusRoute){
        val editOrderStatusViewModel = hiltViewModel<EditOrderStatusViewModel>()
        EditOrderStatusScreen(editOrderStatusViewModel = editOrderStatusViewModel)
    }
}