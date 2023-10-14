package com.example.goldparfumadmin.admin.single.add.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goldparfumadmin.admin.multiple.add.ui.SelectButton
import com.example.goldparfumadmin.model.Product

@Composable
fun MyAlertDialog(
    showAlertDialog : MutableState<Boolean>,
    onConfirmClick : () -> Unit,
    text: String = ""
) {
    AlertDialog(
        title = { Text(text = "Подтверждение действия") },
        text = { Text(text = text) },
        onDismissRequest = { showAlertDialog.value = false },
        dismissButton = {
            Button(onClick = { showAlertDialog.value = false }) {
                Text(text = "Нет")
            }
        },
        confirmButton = {
            Button(onClick = {
                showAlertDialog.value = false
                onConfirmClick()
            }) {
                Text(text = "Да")
            }
        }
    )
}



@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {

    Text(
        text = text,
        Modifier
            //.wrapContentHeight()
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp),
        fontSize = 12.sp
    )
}

@Composable
fun TableScreen(tableData : List<Pair<String, String>>) {
    // Just a fake data... a Pair of Int and String
    // Each cell of a column must have the same weight.
    val column1Weight = .4f // 40%
    val column2Weight = .6f // 60%
    // The LazyColumn will be our table. Notice the use of the weights below
    LazyColumn(
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        // Here is the header
        item {
            Row(Modifier.background(Color.Gray)) {
                TableCell(text = "Название", weight = column1Weight)
                TableCell(text = "Значение", weight = column2Weight)
            }
        }
        // Here are all the lines of your table.
        items(tableData) {
            val (id, text) = it
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                   //  .border(1.dp, Color.Black)
            ) {
                TableCell(text = id, weight = column1Weight)
               // Divider()
                TableCell(text = text, weight = column2Weight)
            }
        }
    }
}


@Composable
fun ProductList(product: Product) {
    val listOfData = listOf(Pair("Идентификатор", product.id ?: "null"),
                            Pair("Тип", product.type ?: "null"),
                            Pair("Объём", product.volume ?: "null"),
                            Pair("Брэнд", product.brand ?: "null"),
                            Pair("Цена(нал.)", product.cashPrice.toString()),
                            Pair("Цена(безнал.)", product.cashlessPrice.toString()),
                            Pair("В наличии", if (product.isOnHand == true) "Да" else "Нет"),
                            Pair("Кому", product.sex ?: "null"))


    TableScreen(listOfData)


//    Table(
//        modifier = Modifier,
//        content = listOfData,
//        cellContent = { f, s ->
//            Text("$f;$s")
//        })
}

@Composable
fun EditList(
    enabled: Boolean = true,
    idState : MutableState<String>,
    brandState : MutableState<String>,
    cashPriceState: MutableState<String>,
    cashlessPriceState : MutableState<String>,
    sexState : MutableState<Int>,
    isOnHandState : MutableState<Boolean>
) {

    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()) {

        InputField(
            modifier = Modifier
                .padding(3.dp)
                .fillMaxWidth(0.7f),
            valueState = idState,
            keyboardType = KeyboardType.Number,
            label = "Порядковый номер",
            enabled = enabled
        )

        Spacer(modifier = Modifier.height(3.dp))

        Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()) {

            InputField(
                modifier = Modifier
                    .padding(3.dp)
                    .weight(0.5f),
                valueState = cashPriceState,
                keyboardType = KeyboardType.Number,
                label = "Цена (нал.)",
                enabled = true
            )

            Spacer(modifier = Modifier.width(5.dp))


            InputField(
                modifier = Modifier
                    .padding(3.dp)
                    .weight(0.5f),
                valueState = cashlessPriceState,
                keyboardType = KeyboardType.Number,
                label = "Цена (безнал.)",
                enabled = true
            )
        }

        Spacer(modifier = Modifier.height(10.dp))


        InputField(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            valueState = brandState,
            label = "Брэнд",
            enabled = true
        )

        Spacer(modifier = Modifier.height(3.dp))

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                enabled = enabled,
                selected = isOnHandState.value,
                onClick = { isOnHandState.value = !isOnHandState.value }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = if (isOnHandState.value) "В наличии:" else "Нет в наличии")
        }

        Spacer(modifier = Modifier.height(3.dp))

        SexPicker(selectedIndState = sexState)

    }
}

@Composable
fun SexPicker(
    enabled : Boolean = true,
    selectedIndState : MutableState<Int>
) {
    val sexes = listOf("Мужской", "Женский", "Унисекс")

    Row(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Пол:")

        Spacer(modifier = Modifier.width(10.dp))
        repeat(3){ ind ->
            SelectButton(enabled = enabled, text = sexes[ind], ind = ind, selectedInd = selectedIndState)
            Spacer(modifier = Modifier.width(7.dp))
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    modifier: Modifier,
    valueState: MutableState<String>,
    onValueChange : (String) -> Unit = { valueState.value = it},
    label: String,
    enabled: Boolean,
    isSingleLine: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    textStyle: TextStyle = TextStyle(),
    keyboardType: KeyboardType = KeyboardType.Text,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable() (() -> Unit)? = {},
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {

    OutlinedTextField(value = valueState.value,
                      onValueChange = onValueChange,
                      label = { Text(text = label)},
                      singleLine = isSingleLine,
                      textStyle = textStyle,
                      modifier = modifier.padding(start = 10.dp, end = 10.dp),
                      enabled = enabled,
                      keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
                      keyboardActions = keyboardActions,
                      visualTransformation = visualTransformation,
                      trailingIcon = trailingIcon
    )
}