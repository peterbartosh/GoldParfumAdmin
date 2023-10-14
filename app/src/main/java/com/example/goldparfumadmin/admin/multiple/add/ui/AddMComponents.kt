package com.example.goldparfumadmin.admin.multiple.add.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.goldparfumadmin.R
import com.example.goldparfumadmin.ui.theme.Gold
import com.example.goldparfumadmin.utils.ProductType


@Composable
fun AlertMessage(isAdd : Boolean) {
    Text(text = if (isAdd)
        stringResource(id = R.string.add_multiple_alert_message)
         else
             stringResource(id = R.string.delete_multiple_alert_message),
         fontSize = 12.sp,
         color = Color.Red,
         textAlign = TextAlign.Center
    )
}

@Composable
fun SelectButton(
    enabled : Boolean = true,
    text : String,
    ind : Int,
    selectedInd : MutableState<Int>,
    optionalOnClick : () -> Unit = {}
) {

    Button(
        onClick = {
            if (selectedInd.value == ind)
                selectedInd.value = -1
            else {
                selectedInd.value = ind
            }
            optionalOnClick()

        },
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        border = BorderStroke(width = 3.dp, color = if (selectedInd.value == ind) Gold else Color.LightGray),
        contentPadding = ButtonDefaults.ContentPadding,
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight()
    ) {

        Text(text = text, fontSize = 10.sp, color = Color.Black)
    }
}



@Composable
fun PickProductData(
    selectedTypeInd : MutableState<Int>,
    optionsList: @Composable() (() -> Unit),
) {
    val types = listOf(
        ProductType.volume, ProductType.tester, ProductType.probe,
        ProductType.licensed, ProductType.auto, ProductType.original,
        ProductType.diffuser, ProductType.lux, ProductType.notSpecified)

    val showTypesDialog = remember {
        mutableStateOf(false)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Button(
            modifier = Modifier.padding(start = 10.dp),
            onClick = { showTypesDialog.value = true }) {
            Text(text = "Выбрать тип продукта")
        }

        if (showTypesDialog.value)
            Dialog(onDismissRequest = { showTypesDialog.value = false }) {
                Surface(modifier = Modifier.wrapContentSize()) {
                    OptionsList(
                        oneLine = false,
                        values = types.map { it.toRus() },
                        selectedInd = selectedTypeInd,
                        showTypesDialog = showTypesDialog)

                }
            }

        Text(
            text = if (selectedTypeInd.value == -1) "Не задано" else types[selectedTypeInd.value].toRus(),
            fontSize = 14.sp,
            modifier = Modifier.padding(end = 30.dp)
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    if (selectedTypeInd.value !in listOf(-1,8)) {
        optionsList()
    }
}

@Composable
fun OptionsList(
    oneLine : Boolean = true,
    showTypesDialog : MutableState<Boolean>? = null,
    values: List<String>,
    selectedInd: MutableState<Int>
) {
    var rowsAmount = if (values.size % 3 == 0) values.size / 3 else values.size / 3 + 1
    if (oneLine) rowsAmount = 1
    Column(modifier = Modifier
        .wrapContentSize()
        .padding(start = 10.dp, end = 10.dp)) {
        for (i in 0 until rowsAmount)
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
            ) {
                for (j in 0 until values.size / rowsAmount) {
                    if (i * 3 + j < values.size)
                        SelectButton(
                            text = values[i * 3 + j],
                            ind = i * 3 + j,
                            selectedInd = selectedInd,
                            optionalOnClick = { showTypesDialog?.value = false }
                        )
                }
            }
    }
}

@Composable
fun Label(text : String) {
    Spacer(modifier = Modifier.height(10.dp))
    Divider()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            fontSize = 25.sp,
            modifier = Modifier
        )
    }
    Divider()
    Spacer(modifier = Modifier.height(10.dp))
}