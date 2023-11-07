package com.example.goldparfumadmin.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.goldparfumadmin.R
import com.example.goldparfumadmin.data.utils.ProductType
import com.example.goldparfumadmin.presentation.theme.Gold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun Loading(progress : Int? = null, maxValue : MutableState<Int>? = null) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()) {
        Text(text = "Loading...  " + if (progress == null) "" else progress.toString()
                + if (maxValue == null || maxValue.value == 0) "/..." else " / ${maxValue.value}")
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
}

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
        contentPadding = PaddingValues(7.dp),
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
    OptionsList: @Composable() (() -> Unit),
) {

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
                        values = ProductType.getTypes().map { it.toRus() },
                        selectedInd = selectedTypeInd,
                        showTypesDialog = showTypesDialog)

                }
            }

        Text(
            text = if (selectedTypeInd.value == -1) "Не задано" else ProductType.getType(selectedTypeInd.value).toRus(),
            fontSize = 14.sp,
            modifier = Modifier.padding(end = 30.dp)
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    if (selectedTypeInd.value !in listOf(-1, ProductType.NotSpecified.ordinal)) {
        OptionsList()
    }
}

@Composable
fun OptionsList(
    oneLine : Boolean = true,
    showTypesDialog : MutableState<Boolean>? = null,
    values: List<String>,
    selectedInd: MutableState<Int>
) {
    if (values.isEmpty()) Box{}

    else {
        LazyVerticalGrid(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(start = 10.dp, end = 10.dp),
            columns = GridCells.Fixed(3)){
            itemsIndexed(values) { ind, item ->
                SelectButton(
                    text = item,
                    ind = ind,
                    selectedInd = selectedInd,
                    optionalOnClick = { showTypesDialog?.value = false }
                )
            }
        }
    }
}

@Composable
fun Label(text : String) {
    Spacer(modifier = Modifier.height(5.dp))
    Divider()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            fontSize = 25.sp,
            modifier = Modifier
        )
    }
    Divider()
    Spacer(modifier = Modifier.height(5.dp))
}

@Composable
fun MaintenanceOption(showStartMaintenance: MutableState<Boolean?>, onClick: suspend (Boolean) -> Unit) {
    when (showStartMaintenance.value){
        true -> MaintenanceIcon(
            tint = Gold,
            showStartMaintenance = showStartMaintenance,
            onClick = onClick
        )
        false -> MaintenanceIcon(
            tint = Color.White,
            showStartMaintenance = showStartMaintenance,
            onClick = onClick
        )
        else -> CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Gold, strokeWidth = 3.dp)
    }
}

@Composable
fun MaintenanceIcon(tint: Color, showStartMaintenance: MutableState<Boolean?>, onClick: suspend (Boolean) -> Unit) {

    Icon(
        modifier = Modifier
            .size(40.dp)
            .border(width = 2.dp, color = tint, shape = CircleShape)
            .clip(CircleShape)
            .clickable {
                val curValue = showStartMaintenance.value
                showStartMaintenance.value = null
                curValue?.let {
                    CoroutineScope(Job() + Dispatchers.IO).launch {
                        onClick(!it)
                    }.invokeOnCompletion {
                        showStartMaintenance.value = !curValue
                    }
                }
            },
        painter = painterResource(id = R.drawable.ic_launcher_foreground),
        contentDescription = "main logo",
        tint = tint
    )

}