package com.example.goldparfumadmin.presentation.single.add.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.goldparfumadmin.data.model.Product
import com.example.goldparfumadmin.data.utils.ProductType
import com.example.goldparfumadmin.data.utils.UiState
import com.example.goldparfumadmin.data.utils.getSafe
import com.example.goldparfumadmin.data.utils.showToast
import com.example.goldparfumadmin.presentation.components.Loading
import com.example.goldparfumadmin.presentation.components.OptionsList
import com.example.goldparfumadmin.presentation.components.PickProductData
import com.example.goldparfumadmin.presentation.components.SubmitButton

@Composable
fun AddSScreen(addSViewModel: AddSViewModel) {

    val context = LocalContext.current

    val uiState = addSViewModel.uiState.collectAsState()

    val idState = remember {
        mutableStateOf("1")
    }

    val brandState = remember {
        mutableStateOf("Gucci")
    }
    val cashPriceState = remember {
        mutableStateOf("0.0")
    }
    val cashlessPriceState = remember {
        mutableStateOf("0.0")
    }

    val isOnHandState = remember {
        mutableStateOf(false)
    }

    val selectedTypeInd = remember {
        mutableStateOf(ProductType.NotSpecified.ordinal)
    }

    val selectedVolumeInd = remember(selectedTypeInd.value) {
        mutableStateOf(-1)
    }

    val volumeState = remember(selectedTypeInd.value) {
        mutableStateOf("")
    }

    val showAlertDialog = remember {
        mutableStateOf(false)
    }

    val showToastState = remember {
        mutableStateOf(false)
    }

    val validInputsState = remember(
        idState.value,
        cashPriceState.value,
        cashlessPriceState.value,
        selectedTypeInd.value,
        selectedVolumeInd.value,
        volumeState.value
    ) {

        mutableStateOf(
            selectedTypeInd.value != ProductType.NotSpecified.ordinal &&
                    (
                            if (selectedTypeInd.value in listOf(1,2))
                                selectedVolumeInd.value != -1
                            else
                                volumeState.value.isNotEmpty()
                    )
                    &&
                    idState.value != "-1" &&
                    idState.value.isNotEmpty() &&
                    cashPriceState.value.isNotEmpty() &&
                    cashlessPriceState.value.isNotEmpty() &&
                    try {
                        val doubleValCash = cashPriceState.value.toDouble()
                        val doubleValCashless = cashlessPriceState.value.toDouble()
                        doubleValCash != 0.0 && doubleValCashless != 0.0
                    } catch (e : Exception){
                        false
                    }

        )
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()) {

            PickProductData(selectedTypeInd = selectedTypeInd) {
                if (ProductType.getType(selectedTypeInd.value).getVolumes().isNotEmpty())
                    OptionsList(
                        values = ProductType.getType(selectedTypeInd.value).getVolumes().map { it.toInt().toString() },
                        selectedInd = selectedVolumeInd
                    )
                else
                    InputField(
                        modifier = Modifier,
                        valueState = volumeState,
                        label = "Введите объём",
                        enabled = true,
                        keyboardType = KeyboardType.Number
                    )
            }

            Spacer(modifier = Modifier.height(10.dp))

            EditList(
                idState = idState,
                brandState = brandState,
                cashPriceState = cashPriceState,
                cashlessPriceState = cashlessPriceState,
                isOnHandState = isOnHandState
            )

            if (showAlertDialog.value) {

                val type = ProductType.getType(selectedTypeInd.value)
                val volume = if (selectedVolumeInd.value == -1)
                    volumeState.value.toDouble()
                else
                    ProductType.getType(selectedTypeInd.value).getVolumes()
                        .getSafe(selectedVolumeInd.value)

                MyAlertDialog(
                    text = "Добавление продукта типа ${type.toRus()} объёмом $volume.\nВыполнить?",
                    showAlertDialog = showAlertDialog,
                    onConfirmClick = {
                        showToastState.value = true
                        try {
                            val product = Product(
                                id = "${selectedTypeInd.value}.${volume?.toInt()}.${idState.value}",
                                type = type.name,
                                volume = volume,
                                brand = brandState.value.lowercase(),
                                cashPrice = cashPriceState.value.toDouble(),
                                cashlessPrice = cashlessPriceState.value.toDouble(),
                                isOnHand = isOnHandState.value
                            )
                            addSViewModel.addProduct(
                                product = product,
                                onSuccess = { showToast(context, "Продукт добавлен") },
                                onFailure = { message ->
                                    showToast(context, "Ошибка.\nПродукт не добавлен\n$message")
                                }
                            )
                        } catch (e: Exception) {
                            showToast(context, "Ошибка.\nПроверьте корректность данных")
                            Log.d("ERROR_ERROR", "AddSScreen: ${e.message}")
                        }
                    }
                )
            }

        }

        SubmitButton(
            text = "Добавить продукт",
            enabled = validInputsState.value &&
                    selectedTypeInd.value != ProductType.NotSpecified.ordinal &&
                    (if (selectedTypeInd.value in listOf(1,2))
                        selectedVolumeInd.value != -1
                    else
                        volumeState.value.isNotEmpty())
        ) {
            showAlertDialog.value = true
        }


    }

    if (uiState.value is UiState.Loading) Loading()

}