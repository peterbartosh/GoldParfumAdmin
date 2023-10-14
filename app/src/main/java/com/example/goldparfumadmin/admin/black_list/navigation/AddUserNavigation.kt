package com.example.goldparfumadmin.admin.black_list.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.goldparfumadmin.admin.black_list.ui.AddUserScreen
import com.example.goldparfumadmin.admin.black_list.ui.BlackListViewModel
import com.example.goldparfumadmin.admin.multiple.add.ui.AddMScreen
import com.example.goldparfumadmin.admin.multiple.add.ui.AddMViewModel

const val addUserRoute = "addU"

fun NavController.navigateToAddUser(navOptions: NavOptions? = null){
    this.navigate(addUserRoute, navOptions)
}

fun NavGraphBuilder.addUserScreen(){
    composable(addUserRoute){
        val blackListViewModel = hiltViewModel<BlackListViewModel>()
        AddUserScreen(blackListViewModel = blackListViewModel)
    }
}