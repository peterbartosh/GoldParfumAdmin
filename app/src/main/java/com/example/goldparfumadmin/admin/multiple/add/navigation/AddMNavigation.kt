package com.example.goldparfumadmin.admin.multiple.add.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.goldparfumadmin.admin.multiple.add.ui.AddMScreen
import com.example.goldparfumadmin.admin.multiple.add.ui.AddMViewModel

const val addMRoute = "addM"

fun NavController.navigateToAddM(navOptions: NavOptions? = null){
    this.navigate(addMRoute, navOptions)
}

fun NavGraphBuilder.addScreenM(){
    composable(addMRoute){
        val addMViewModel = hiltViewModel<AddMViewModel>()
        AddMScreen(addMViewModel = addMViewModel)
    }
}