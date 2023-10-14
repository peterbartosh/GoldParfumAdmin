package com.example.goldparfumadmin.admin.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.goldparfumadmin.admin.home.ui.AdminScreen

const val homeAdminRoute = "home_admin"

fun NavController.navigateToHomeAdmin(navOptions: NavOptions? = null){
    this.navigate(homeAdminRoute, navOptions)
}

fun NavGraphBuilder.homeAdminScreen(
    navigateToAddM: () -> Unit,
    navigateToDeleteM: () -> Unit,
    navigateToAddS: () -> Unit,
    navigateToDeleteS: () -> Unit,
    navigateToEditS: () -> Unit,
    navigateToAddUser : () -> Unit,
    navigateToRemoveUser : () -> Unit,
    navigateToEditOrderStatus : () -> Unit,
    navigateToCollectOrders : () -> Unit
) {
    composable(homeAdminRoute) {
        AdminScreen(
            onAddMultipleClick = navigateToAddM,
            onDeleteMultipleClick = navigateToDeleteM,
            onAddSingleClick = navigateToAddS,
            onDeleteSingleClick = navigateToDeleteS,
            onEditSingleClick = navigateToEditS,
            onAddUserClick = navigateToAddUser,
            onRemoveUserClick = navigateToRemoveUser,
            onEditOrderStatusClick = navigateToEditOrderStatus,
            onCollectOrdersClick = navigateToCollectOrders
        )
    }
}