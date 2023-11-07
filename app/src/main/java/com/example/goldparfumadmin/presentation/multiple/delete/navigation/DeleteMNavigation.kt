package com.example.goldparfumadmin.presentation.multiple.delete.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.goldparfumadmin.presentation.multiple.delete.ui.DeleteMScreen
import com.example.goldparfumadmin.presentation.multiple.delete.ui.DeleteMViewModel

const val deleteMRoute = "deleteM"

fun NavController.navigateToDeleteM(navOptions: NavOptions? = null){
    this.navigate(deleteMRoute, navOptions)
}

fun NavGraphBuilder.deleteMScreen(){
    composable(deleteMRoute){
        val deleteMViewModel = hiltViewModel<DeleteMViewModel>()
        DeleteMScreen(deleteMViewModel = deleteMViewModel)
    }
}