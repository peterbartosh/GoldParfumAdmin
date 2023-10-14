package com.example.goldparfumadmin.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.goldparfumadmin.admin.black_list.navigation.addUserScreen
import com.example.goldparfumadmin.admin.black_list.navigation.deleteUserScreen
import com.example.goldparfumadmin.admin.black_list.navigation.navigateToAddUser
import com.example.goldparfumadmin.admin.black_list.navigation.navigateToDeleteUser
import com.example.goldparfumadmin.admin.home.navigation.homeAdminRoute
import com.example.goldparfumadmin.admin.home.navigation.homeAdminScreen
import com.example.goldparfumadmin.admin.multiple.add.navigation.addScreenM
import com.example.goldparfumadmin.admin.multiple.add.navigation.navigateToAddM
import com.example.goldparfumadmin.admin.multiple.delete.navigation.deleteMScreen
import com.example.goldparfumadmin.admin.multiple.delete.navigation.navigateToDeleteM
import com.example.goldparfumadmin.admin.orders.collect_orders.navigation.navigateToOrderCollecting
import com.example.goldparfumadmin.admin.orders.collect_orders.navigation.orderCollectingScreen
import com.example.goldparfumadmin.admin.orders.edit_order_status.navigation.editOrderStatusScreen
import com.example.goldparfumadmin.admin.orders.edit_order_status.navigation.navigateToEditOrderStatus
import com.example.goldparfumadmin.admin.single.add.navigation.addSScreen
import com.example.goldparfumadmin.admin.single.add.navigation.navigateToAddS
import com.example.goldparfumadmin.admin.single.delete.navigation.deleteSScreen
import com.example.goldparfumadmin.admin.single.delete.navigation.navigateToDeleteS
import com.example.goldparfumadmin.admin.single.edit.navigation.editSScreen
import com.example.goldparfumadmin.admin.single.edit.navigation.navigateToEditS


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNavHost(
    navController: NavHostController
) {







    val showBackIcon = remember {
        mutableStateOf(false)
    }

    navController.addOnDestinationChangedListener{ c, d, a ->
        showBackIcon.value = d.route != homeAdminRoute
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text(text = "GOLD PARFUM ADMIN") },

            navigationIcon = {
                if (showBackIcon.value)
                    IconButton(onClick = { navController.popBackStack() } ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "arrow back icon")
                    }
            else Box{}
        })
    }) { paddingValues ->

        Surface(modifier = Modifier.padding(paddingValues)) {


            NavHost(navController = navController, startDestination = homeAdminRoute) {

                homeAdminScreen(
                    navigateToAddM = navController::navigateToAddM,
                    navigateToDeleteM = navController::navigateToDeleteM,
                    navigateToAddS = navController::navigateToAddS,
                    navigateToDeleteS = navController::navigateToDeleteS,
                    navigateToEditS = navController::navigateToEditS,
                    navigateToAddUser = navController::navigateToAddUser,
                    navigateToRemoveUser = navController::navigateToDeleteUser,
                    navigateToEditOrderStatus = navController::navigateToEditOrderStatus,
                    navigateToCollectOrders = navController::navigateToOrderCollecting
                )

                addScreenM()
                deleteMScreen()
                addSScreen()
                editSScreen()
                deleteSScreen()
                addUserScreen()
                deleteUserScreen()
                orderCollectingScreen()
                editOrderStatusScreen()
            }
        }
    }
}