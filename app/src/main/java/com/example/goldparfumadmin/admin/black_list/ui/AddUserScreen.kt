package com.example.goldparfumadmin.admin.black_list.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.goldparfumadmin.R
import com.example.goldparfumadmin.admin.single.add.ui.InputField

@Composable
fun AddUserScreen(
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

    val validInputs = remember(phoneNumberState.value) {
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

        if (showAlertDialog.value)
            AlertDialog(
                text = { Text(text = "Добавить пользователя с номером $code${phoneNumberState.value} в чёрный список?") },
                onDismissRequest = { showAlertDialog.value = false },
                confirmButton = { Button(onClick = {
                    showAlertDialog.value = false
                    blackListViewModel.addUser(code + phoneNumberState.value, context = context)
                }){
                    Text(text = "Да")
                } } ,
                dismissButton = { Button(onClick = { showAlertDialog.value = false }){
                    Text(text = "Нет")
                } }
            )

        Button(
            enabled = validInputs.value,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            onClick = {
                showAlertDialog.value = true

            }
        ) {
            Text(text = "Добавить в чёрный список")
        }


    }



}