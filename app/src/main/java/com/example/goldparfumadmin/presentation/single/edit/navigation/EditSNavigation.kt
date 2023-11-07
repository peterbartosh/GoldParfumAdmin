package com.example.goldparfumadmin.presentation.single.edit.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.goldparfumadmin.presentation.single.edit.ui.EditSScreen
import com.example.goldparfumadmin.presentation.single.edit.ui.EditSViewModel

const val editSRoute = "editS"

fun NavController.navigateToEditS(navOptions: NavOptions? = null){
    this.navigate(editSRoute, navOptions)
}

fun NavGraphBuilder.editSScreen(){
    composable(editSRoute){
        val editSViewModel = hiltViewModel<EditSViewModel>()
        EditSScreen(editSViewModel = editSViewModel)
    }
}