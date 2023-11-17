package com.example.goldparfumadmin.presentation.orders.collect_orders.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.goldparfumadmin.R
import com.example.goldparfumadmin.data.utils.UiState
import com.example.goldparfumadmin.presentation.components.AlertMessage
import com.example.goldparfumadmin.presentation.components.Loading
import com.example.goldparfumadmin.presentation.components.SubmitButton
import com.example.goldparfumadmin.presentation.single.add.ui.MyAlertDialog

@Composable
fun OrderCollectingScreen(orderCollectingViewModel: OrderCollectingViewModel) {

    val context = LocalContext.current

    val uiState = orderCollectingViewModel.uiState.collectAsState()

    val showAlertDialog = remember {
        mutableStateOf(false)
    }

    if (uiState.value is UiState.Loading)
        Loading()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Box{}

        AlertMessage(stringId = R.string.collect_orders_alert_message)

        SubmitButton(text = "Cобрать заказы", enabled = true) {
            showAlertDialog.value = true
        }

        MyAlertDialog(
            text = "Выбранные действия:\n\nСбор всех заказов за последние 7 дней.\n\nВыполнить?",
            showAlertDialog = showAlertDialog,
            onConfirmClick = {
                orderCollectingViewModel.run(context)
            }
        )

    }
}

