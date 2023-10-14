package com.example.goldparfumadmin.admin.single.edit.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.goldparfumadmin.admin.multiple.delete.ui.DeleteMScreen
import com.example.goldparfumadmin.admin.multiple.delete.ui.DeleteMViewModel
import com.example.goldparfumadmin.admin.single.edit.ui.EditSScreen
import com.example.goldparfumadmin.admin.single.edit.ui.EditSViewModel

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