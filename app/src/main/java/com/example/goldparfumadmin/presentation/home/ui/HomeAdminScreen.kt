package com.example.goldparfumadmin.presentation.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.goldparfumadmin.presentation.components.Label
import com.example.goldparfumadmin.presentation.theme.NormalYellow
import com.example.goldparfumadmin.presentation.theme.Purple40

@Composable
fun AdminScreen(
    onAddMultipleClick: () -> Unit,
    onDeleteMultipleClick: () -> Unit,
    onAddSingleClick: () -> Unit,
    onDeleteSingleClick: () -> Unit,
    onEditSingleClick: () -> Unit,
    onAddUserClick : () -> Unit,
    onRemoveUserClick : () -> Unit,
    onEditOrderStatusClick : () -> Unit,
    onCollectOrdersClick : () -> Unit
) {

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        Column(
            modifier = Modifier.fillMaxSize(),
            //verticalArrangement = Arrangement.SpaceAround
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 10.dp)
            ) {

                Label(text = "Множественные операции")


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionCard(
                        onClick = onAddMultipleClick,
                        title = "Добавить продукты",
                        imageVector = Icons.Filled.Add,
                        contentColor = Color.Green
                    )

                    ActionCard(
                        onClick = onDeleteMultipleClick,
                        title = "Удалить продукты",
                        imageVector = Icons.Filled.Delete,
                        contentColor = Color.Red
                    )
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {

                Label(text = "Единичные операции")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionCard(
                        onClick = onAddSingleClick,
                        title = "Добавить продукт",
                        imageVector = Icons.Outlined.Add,
                        contentColor = Color.Green
                    )

                    ActionCard(
                        onClick = onEditSingleClick,
                        title = "Редактировать продукт",
                        imageVector = Icons.Outlined.Edit,
                        contentColor = NormalYellow
                    )

                    ActionCard(
                        onClick = onDeleteSingleClick,
                        title = "Удалить продукт",
                        imageVector = Icons.Outlined.Delete,
                        contentColor = Color.Red
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {

                Label(text = "Чёрный список")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    ActionCard(
                        onClick = onAddUserClick,
                        title = "Добавить в чёрный список",
                        imageVector = Icons.Default.ArrowForward,
                        contentColor = Color.Magenta
                    )

                    ActionCard(
                        onClick = onRemoveUserClick,
                        title = "Убрать из чёрного списка",
                        imageVector = Icons.Default.ArrowBack,
                        contentColor = Purple40
                    )

                }
            }



            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {

                Label(text = "Заказы")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    ActionCard(
                        onClick = onEditOrderStatusClick,
                        title = "Изменить статус заказа",
                        imageVector = Icons.Default.Edit,
                        contentColor = Color.White
                    )

                    ActionCard(
                        onClick = onCollectOrdersClick,
                        title = "Собрать заказы",
                        imageVector = Icons.Default.Refresh,
                        contentColor = Color.Cyan
                    )

                }


            }
        }

    }
}