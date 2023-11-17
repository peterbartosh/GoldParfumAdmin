package com.example.goldparfumadmin.presentation.orders.edit_order_status.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goldparfumadmin.data.model.Order
import com.example.goldparfumadmin.data.model.OrderProduct
import com.example.goldparfumadmin.data.utils.OrderStatus
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun OrderRow(
    ind : Int,
    selectedOrderStatus : MutableState<Int>,
    order: Order,
    orderProducts: List<OrderProduct>,
    selectedOrder : MutableState<Int>
) {

    var selected by remember(selectedOrder.value) {
        mutableStateOf(selectedOrder.value == ind)
    }

    var showDropDown by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(5.dp)
            .clickable {
                if (selected) selectedOrder.value = -1 else selectedOrder.value = ind
                selected = !selected
            }
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) Color.Green else Color.LightGray
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .padding(10.dp)
            ) {

                Spacer(modifier = Modifier.height(5.dp))
                Text(text = order.id?.toString() ?: "ID не найден", fontSize = 10.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = order.number?.toString() ?: "Номер заказа не найден", fontSize = 10.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = order.address?.toString() ?: "Адрес не найден", fontSize = 10.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = order.status?.toString() ?: "Статус не найден", fontSize = 10.sp)
                Spacer(modifier = Modifier.height(5.dp))
                order.date?.toDate()?.let { date ->
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss")
                    val local = date.toInstant().atZone(ZoneId.of("UTC+03:00"))?.toLocalDateTime()
                    Text(
                        text = local?.format(formatter) ?: "Дата не найдена",
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }

            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(10.dp)
            ) {

                Column(
                    modifier = Modifier
                        .wrapContentWidth(),
                    horizontalAlignment = Alignment.End
                ) {

                    orderProducts.forEach { orderProduct ->
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Идент. продукта: " + orderProduct.productId.toString(),
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Кол-во (нал.): " + orderProduct.cashAmount.toString(),
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Кол-во (безнал.): " + orderProduct.cashlessAmount.toString(),
                            fontSize = 10.sp
                        )
                        Divider(modifier = Modifier.width(30.dp))
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier
                        .wrapContentWidth().fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    IconButton(
                        enabled = selected,
                        modifier = Modifier.size(30.dp),
                        onClick = { showDropDown = true }
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                    }

                    if (selectedOrderStatus.value != -1 && selectedOrder.value == ind) {

                        Spacer(modifier = Modifier.height(10.dp))

                        Card(
                            shape = CircleShape,
                            modifier = Modifier.wrapContentSize(),
                            colors = CardDefaults.cardColors(
                                contentColor = Color.Green,
                                containerColor = MaterialTheme.colorScheme.background
                            ),
                            border = BorderStroke(2.dp, Color.Green)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Text(
                                    text = (selectedOrderStatus.value + 1).toString(),
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    Box {
        DropdownMenu(
            modifier = Modifier.wrapContentWidth(),
            expanded = showDropDown,
            onDismissRequest = { showDropDown = false }) {
            Column(modifier = Modifier.wrapContentWidth()) {
                OrderStatus.values().forEachIndexed { ind, status ->
                    Text(text = (ind + 1).toString() + ") " + status.name,
                         fontSize = 20.sp,
                         modifier = Modifier.clickable {
                             showDropDown = false
                             selectedOrderStatus.value = ind
                         })
                }
            }
        }
    }
}

