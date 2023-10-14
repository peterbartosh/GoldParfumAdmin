package com.example.goldparfumadmin.admin.orders.edit_order_status.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.goldparfumadmin.admin.multiple.add.ui.AddMScreen
import com.example.goldparfumadmin.admin.multiple.add.ui.AddMViewModel
import com.example.goldparfumadmin.admin.orders.edit_order_status.ui.EditOrderStatusScreen
import com.example.goldparfumadmin.admin.orders.edit_order_status.ui.EditOrderStatusViewModel

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