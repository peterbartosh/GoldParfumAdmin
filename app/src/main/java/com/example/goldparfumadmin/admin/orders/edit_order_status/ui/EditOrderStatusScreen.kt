package com.example.goldparfumadmin.admin.orders.edit_order_status.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.goldparfumadmin.admin.single.add.ui.InputField
import com.example.goldparfumadmin.utils.Loading
import com.example.goldparfumadmin.utils.OrderStatus

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditOrderStatusScreen(
    editOrderStatusViewModel: EditOrderStatusViewModel
) {

    val context = LocalContext.current

    val keyboard = LocalSoftwareKeyboardController.current

    var showAlertDialog by remember {
        mutableStateOf(false)
    }

    val orderNumberState = remember {
        mutableStateOf("")
    }

    val selectedOrder = remember {
        mutableIntStateOf(-1)
    }

    val selectedOrderStatus = remember(selectedOrder.value) {
        mutableIntStateOf(-1)
    }

    val valid by remember(orderNumberState.value) {
        mutableStateOf(orderNumberState.value.length == 6)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //Spacer(modifier = Modifier.height(20.dp))


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(30.dp))

            Text(text = "Введите номер заказа")

            Spacer(modifier = Modifier.height(5.dp))

            InputField(
                modifier = Modifier,
                valueState = orderNumberState,
                label = "Номер заказа",
                enabled = true,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )


            Button(
                enabled = valid,
                onClick = {
                    editOrderStatusViewModel.findOrder(orderNumber = orderNumberState.value)
                    keyboard?.hide()
                }
            ) {
                Text(text = "Найти заказ")
            }

            Spacer(modifier = Modifier.height(5.dp))

            if (editOrderStatusViewModel.isSuccess) {
                if (editOrderStatusViewModel.foundOrders.size > 1) {
                    Text(text = "Найдено несколько заказов.\nВыберите подходящий")
                    Spacer(modifier = Modifier.height(5.dp))
                }
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    itemsIndexed(items = editOrderStatusViewModel.foundOrders) { ind, order ->
                        order.id?.let { id ->
                            OrderRow(
                                ind = ind,
                                selectedOrderStatus = selectedOrderStatus,
                                selectedOrder = selectedOrder,
                                order = order,
                                orderProducts = editOrderStatusViewModel.orderProducts[id]
                                    ?: emptyList()
                            )
                        }
                    }
                }
            } else if (editOrderStatusViewModel.isLoading)
                Loading()
            else if (editOrderStatusViewModel.isFailure)
                Text(text = "Is empty")





            if (showAlertDialog)
                AlertDialog(
                    text = { Text(text = "Изменить статус продукта?") },
                    onDismissRequest = { showAlertDialog = false },
                    confirmButton = {
                        Button(onClick = {
                            showAlertDialog = false
                            editOrderStatusViewModel.foundOrders[selectedOrder.intValue].id?.let { id ->
                                editOrderStatusViewModel.setOrderStatus(
                                    id,
                                    OrderStatus.values()[selectedOrderStatus.intValue],
                                    context
                                )
                            }
//                    orderCollectingViewModel.generateExcelFile()
//                    orderCollectingViewModel.updateProductsStates()
                        }) {
                            Text(text = "Да")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showAlertDialog = false }) {
                            Text(text = "Нет")
                        }
                    }
                )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled =  selectedOrder.intValue != -1 && selectedOrderStatus.intValue != -1,
            onClick = { showAlertDialog = true }
        ) {
            Text(text = "Изменить статус продукта")
        }

    }


}