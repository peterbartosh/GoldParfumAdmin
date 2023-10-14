package com.example.goldparfumadmin.admin.orders.collect_orders.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.goldparfumadmin.utils.Loading

@Composable
fun OrderCollectingScreen(
    orderCollectingViewModel : OrderCollectingViewModel
) {

    val context = LocalContext.current

    var showAlertDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (orderCollectingViewModel.isLoading)
            Loading()

        Button(onClick = { showAlertDialog = true }) {
            Text(text = "Получить заказы")
        }

        if (showAlertDialog)
            AlertDialog(
                text = { Text(text = "Обновление базы данных заказов, генерация файлов с заказами.\nПродолжить?") },
                onDismissRequest = { showAlertDialog = false },
                confirmButton = { Button(onClick = {
                    showAlertDialog = false
//                    orderCollectingViewModel.generateExcelFile()
//                    orderCollectingViewModel.updateProductsStates()
                }){
                    Text(text = "Да")
                } } ,
                dismissButton = { Button(onClick = { showAlertDialog = false }){
                    Text(text = "Нет")
                } }
            )

    }


}