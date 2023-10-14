package com.example.goldparfumadmin.admin.black_list.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.goldparfumadmin.admin.black_list.ui.AddUserScreen
import com.example.goldparfumadmin.admin.black_list.ui.BlackListViewModel
import com.example.goldparfumadmin.admin.black_list.ui.DeleteUserScreen

const val deleteUserRoute = "deleteU"

fun NavController.navigateToDeleteUser(navOptions: NavOptions? = null){
    this.navigate(deleteUserRoute, navOptions)
}

fun NavGraphBuilder.deleteUserScreen(){
    composable(deleteUserRoute){
        val blackListViewModel = hiltViewModel<BlackListViewModel>()
        DeleteUserScreen(blackListViewModel = blackListViewModel)
    }
}