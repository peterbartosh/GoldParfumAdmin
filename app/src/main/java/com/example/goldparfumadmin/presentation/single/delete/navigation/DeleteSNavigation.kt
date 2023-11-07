package com.example.goldparfumadmin.presentation.single.delete.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.goldparfumadmin.presentation.single.delete.ui.DeleteSScreen
import com.example.goldparfumadmin.presentation.single.delete.ui.DeleteSViewModel

const val deleteSRoute = "deleteS"

fun NavController.navigateToDeleteS(navOptions: NavOptions? = null){
    this.navigate(deleteSRoute, navOptions)
}

fun NavGraphBuilder.deleteSScreen(){
    composable(deleteSRoute){
        val deleteSViewModel = hiltViewModel<DeleteSViewModel>()
        DeleteSScreen(deleteSViewModel = deleteSViewModel)
    }
}