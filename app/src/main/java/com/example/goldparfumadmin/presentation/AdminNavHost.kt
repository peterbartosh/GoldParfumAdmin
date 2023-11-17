package com.example.goldparfumadmin.presentation

import android.content.Context
import android.net.ConnectivityManager
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.goldparfumadmin.data.repository.FireRepository
import com.example.goldparfumadmin.data.utils.showToast
import com.example.goldparfumadmin.presentation.black_list.navigation.addUserScreen
import com.example.goldparfumadmin.presentation.black_list.navigation.deleteUserScreen
import com.example.goldparfumadmin.presentation.black_list.navigation.navigateToAddUser
import com.example.goldparfumadmin.presentation.black_list.navigation.navigateToDeleteUser
import com.example.goldparfumadmin.presentation.components.MaintenanceOption
import com.example.goldparfumadmin.presentation.home.navigation.homeAdminRoute
import com.example.goldparfumadmin.presentation.home.navigation.homeAdminScreen
import com.example.goldparfumadmin.presentation.multiple.add.navigation.addScreenM
import com.example.goldparfumadmin.presentation.multiple.add.navigation.navigateToAddM
import com.example.goldparfumadmin.presentation.multiple.delete.navigation.deleteMScreen
import com.example.goldparfumadmin.presentation.multiple.delete.navigation.navigateToDeleteM
import com.example.goldparfumadmin.presentation.orders.collect_orders.navigation.navigateToOrderCollecting
import com.example.goldparfumadmin.presentation.orders.collect_orders.navigation.orderCollectingScreen
import com.example.goldparfumadmin.presentation.orders.edit_order_status.navigation.editOrderStatusScreen
import com.example.goldparfumadmin.presentation.orders.edit_order_status.navigation.navigateToEditOrderStatus
import com.example.goldparfumadmin.presentation.single.add.navigation.addSScreen
import com.example.goldparfumadmin.presentation.single.add.navigation.navigateToAddS
import com.example.goldparfumadmin.presentation.single.delete.navigation.deleteSScreen
import com.example.goldparfumadmin.presentation.single.delete.navigation.navigateToDeleteS
import com.example.goldparfumadmin.presentation.single.edit.navigation.editSScreen
import com.example.goldparfumadmin.presentation.single.edit.navigation.navigateToEditS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNavHost(
    navController: NavHostController
) {

    val context = LocalContext.current

    var showBackIcon by rememberSaveable {
        mutableStateOf(false)
    }

    val showStartMaintenance = remember {
        val isBlocked: MutableState<Boolean?> = mutableStateOf(null)
        isBlocked
    }

    LaunchedEffect(key1 = true){
        val manager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val connected = manager.activeNetwork
        if (connected == null) {
            showToast(context, "Ошибка.\nВы не подключены к сети.")
        }
        else
        showStartMaintenance.value = FireRepository.getIsBlockedForMaintenance{message ->
            showToast(context, message)
        }
    }

    navController.addOnDestinationChangedListener{ _, d, _ ->
        showBackIcon = d.route != homeAdminRoute
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text(text = "GOLD PARFUM ADMIN") },

            navigationIcon = {
                if (showBackIcon)
                    IconButton(onClick = { navController.popBackStack() } ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "arrow back icon")
                    }
                else
                    Box{}
            },

            actions = {
                MaintenanceOption(
                    showStartMaintenance = showStartMaintenance,
                    onClick = FireRepository::setIsBlockedForMaintenance
                )
            }
        )
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