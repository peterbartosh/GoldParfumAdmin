package com.example.goldparfumadmin.presentation.orders.edit_order_status.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.goldparfumadmin.data.utils.OrderStatus
import com.example.goldparfumadmin.data.utils.UiState
import com.example.goldparfumadmin.presentation.components.Loading
import com.example.goldparfumadmin.presentation.components.SubmitButton
import com.example.goldparfumadmin.presentation.single.add.ui.InputField
import com.example.goldparfumadmin.presentation.single.add.ui.MyAlertDialog

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditOrderStatusScreen(
    editOrderStatusViewModel: EditOrderStatusViewModel
) {

    val context = LocalContext.current

    val uiState = editOrderStatusViewModel.uiState.collectAsState()

    val keyboard = LocalSoftwareKeyboardController.current

    val showAlertDialog = remember {
        mutableStateOf(false)
    }

    val orderNumberState = remember {
        mutableStateOf("")
    }

    val selectedOrder = remember {
        mutableStateOf(-1)
    }

    val selectedOrderStatus = remember(selectedOrder.value) {
        mutableStateOf(-1)
    }

    val valid by remember(orderNumberState.value) {
        mutableStateOf(orderNumberState.value.length == 6)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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

            when (uiState.value) {
                is UiState.Success -> {
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
                }
                is UiState.Loading -> Loading()
                is UiState.Failure -> Text(text = "Ничего не найдено")
                else -> Box{}
            }

            MyAlertDialog(
                text = "Обновление статуса продукта.\nПродолжить?",
                showAlertDialog = showAlertDialog,
                onConfirmClick = {
                    showAlertDialog.value = false
                    editOrderStatusViewModel.foundOrders[selectedOrder.value].id?.let { id ->
                        editOrderStatusViewModel.setOrderStatus(
                            id,
                            OrderStatus.values()[selectedOrderStatus.value],
                            context
                        )
                    }
                }
            )
        }

        SubmitButton(
            text = "Изменить статус продукта",
            enabled = selectedOrder.value != -1 && selectedOrderStatus.value != -1
        ) {
            showAlertDialog.value = true
        }
    }
}