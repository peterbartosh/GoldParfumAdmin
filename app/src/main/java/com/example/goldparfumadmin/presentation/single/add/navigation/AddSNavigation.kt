package com.example.goldparfumadmin.presentation.single.add.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.goldparfumadmin.presentation.single.add.ui.AddSScreen
import com.example.goldparfumadmin.presentation.single.add.ui.AddSViewModel

const val addSRoute = "addS"

fun NavController.navigateToAddS(navOptions: NavOptions? = null){
    this.navigate(addSRoute, navOptions)
}

fun NavGraphBuilder.addSScreen(){
    composable(addSRoute){
        val addSViewModel = hiltViewModel<AddSViewModel>()
        AddSScreen(addSViewModel = addSViewModel)
    }
}