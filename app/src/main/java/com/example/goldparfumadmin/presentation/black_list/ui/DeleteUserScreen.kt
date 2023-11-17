package com.example.goldparfumadmin.presentation.black_list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.goldparfumadmin.presentation.components.SubmitButton
import com.example.goldparfumadmin.presentation.single.add.ui.MyAlertDialog

@Composable
fun DeleteUserScreen(
    blackListViewModel: BlackListViewModel
) {


    val context = LocalContext.current


    val code = "+375"

    val showAlertDialog = remember {
        mutableStateOf(false)
    }

    val phoneNumberState = remember {
        mutableStateOf("")
    }

    val validInputsState = remember(phoneNumberState.value) {
        mutableStateOf(
            phoneNumberState.value.length == 9
        )
    }

    Column(modifier = Modifier.fillMaxSize(),
           verticalArrangement = Arrangement.SpaceBetween
    ) {


        Column(modifier = Modifier.fillMaxSize(0.8f)) {

            Spacer(modifier = Modifier.height(50.dp))

            PhoneInput(
                phone = phoneNumberState,
                mask = "(00) 000-00-00",
                maskNumber = '0',
                enabled = true,
                onPhoneChanged = {
                    phoneNumberState.value = it
                }
            )
        }

        MyAlertDialog(
            text = "Удалить пользователя с номером $code${phoneNumberState.value} из чёрного списка?",
            showAlertDialog = showAlertDialog,
            onConfirmClick = {
                showAlertDialog.value = false
                blackListViewModel.deleteUser(code + phoneNumberState.value, context = context)
            }
        )

        SubmitButton(text = "Убрать из чёрного спика", enabled = validInputsState.value) {
            showAlertDialog.value = true
        }

    }
}