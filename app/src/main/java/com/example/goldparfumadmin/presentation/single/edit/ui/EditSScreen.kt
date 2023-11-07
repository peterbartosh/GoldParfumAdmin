package com.example.goldparfumadmin.presentation.single.edit.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.goldparfumadmin.data.model.Product
import com.example.goldparfumadmin.data.utils.ProductType
import com.example.goldparfumadmin.data.utils.getSafe
import com.example.goldparfumadmin.data.utils.showToast
import com.example.goldparfumadmin.presentation.components.Loading
import com.example.goldparfumadmin.presentation.components.OptionsList
import com.example.goldparfumadmin.presentation.components.PickProductData
import com.example.goldparfumadmin.presentation.single.add.ui.EditList
import com.example.goldparfumadmin.presentation.single.add.ui.InputField
import com.example.goldparfumadmin.presentation.single.add.ui.MyAlertDialog


fun updateStates(
    product: Product,
    idToEditState : MutableState<String>,
    brandState : MutableState<String>,
    cashPriceState : MutableState<String>,
    cashlessPriceState : MutableState<String>,
    isOnHandState : MutableState<Boolean>
){
    idToEditState.value = product.id?.split(".")?.last().toString()
    brandState.value = product.brand.toString()
    cashPriceState.value = product.cashPrice.toString()
    cashlessPriceState.value = product.cashlessPrice.toString()

    isOnHandState.value = product.isOnHand == true
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditSScreen(editSViewModel: EditSViewModel) {

    val context = LocalContext.current

    val keyboard = LocalSoftwareKeyboardController.current

    val idState = remember {
        mutableStateOf("-1")
    }

    val idToEditState = remember {
        mutableStateOf("")
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

    val selectedVolumeInd = remember {
        mutableStateOf(-1)
    }

    val volumeState = remember {
        mutableStateOf("")
    }

    val validInputState = remember(
            selectedTypeInd.value,
            selectedVolumeInd.value,
            volumeState.value,
            cashPriceState.value,
            cashlessPriceState.value,
            idToEditState.value,
            idState.value
        ) {
            mutableStateOf(
                selectedTypeInd.value != ProductType.NotSpecified.ordinal &&
                (
                    if (selectedTypeInd.value in listOf(1,2))
                        selectedVolumeInd.value != -1
                    else
                        volumeState.value.isNotEmpty()
                ) &&
                idToEditState.value.isNotEmpty() &&
                idState.value != "-1" && idState.value.isNotEmpty() &&
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

    val inputStateChanged = remember(
        selectedTypeInd.value,
        selectedVolumeInd.value,
        volumeState.value,
        idState.value
    ) {
        mutableStateOf(true)
    }

    val updateTableState = remember {
        mutableStateOf(false)
    }

    val showTableState = remember(editSViewModel.isSuccess.value){
        mutableStateOf(editSViewModel.isSuccess.value)
    }


    val submitEnabled = remember(showTableState.value, inputStateChanged.value) {
        mutableStateOf(showTableState.value && !inputStateChanged.value)
    }

    val showAlertDialog = remember {
        mutableStateOf(false)
    }

    val showToastState = remember {
        mutableStateOf(false)
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {

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

            Spacer(modifier = Modifier.height(3.dp))

            InputField(modifier = Modifier,
                       valueState = idState,
                       keyboardType = KeyboardType.Number,
                       label = "Порядковый номер",
                       enabled = true)

            Spacer(modifier = Modifier.height(3.dp))

            Button(
                modifier = Modifier.padding(start = 10.dp),
                enabled = selectedTypeInd.value != -1 &&
                        selectedTypeInd.value != ProductType.NotSpecified.ordinal &&
                        (
                            if (selectedTypeInd.value in listOf(1,2))
                                selectedVolumeInd.value != -1
                            else
                                volumeState.value.isNotEmpty()
                        ) && idState.value.isNotEmpty(),
                onClick = {
                    keyboard?.hide()
                    inputStateChanged.value = false

                    val volume = if (selectedVolumeInd.value == -1)
                        volumeState.value.toInt().toString()
                    else
                        ProductType.getType(selectedTypeInd.value).getVolumes()
                            .getSafe(selectedVolumeInd.value)?.toInt()?.toString()

                    editSViewModel.findProduct(
                        productId = "${selectedTypeInd.value}.$volume.${idState.value}",
                        context = context
                    )
                    updateTableState.value = true
                }) {
                Text(text = "Найти продукт")
            }

            if (editSViewModel.isLoading.value) Loading()

            if (showTableState.value) {
                val prod = editSViewModel.product.collectAsState().value
                if (prod != null && updateTableState.value)
                    updateStates(prod, idToEditState, brandState, cashPriceState, cashlessPriceState, isOnHandState)

                updateTableState.value = false
                EditList(
                    idState = idToEditState,
                    brandState = brandState,
                    cashPriceState = cashPriceState,
                    cashlessPriceState = cashlessPriceState,
                    isOnHandState = isOnHandState
                )
            }

            if (showAlertDialog.value) {
                val type = ProductType.getType(selectedTypeInd.value)
                val volume = try {
                    if (selectedVolumeInd.value == -1)
                        volumeState.value.toDouble()
                    else
                        type.getVolumes().getSafe(selectedVolumeInd.value)
                } catch (e : Exception){
                    0.0
                }

                MyAlertDialog(
                    text = "Изменение продукта типа ${type.toRus()} объёмом $volume.\nВыполнить?",
                    showAlertDialog = showAlertDialog,
                    onConfirmClick = {
                        showToastState.value = true

                        val oldId = "${selectedTypeInd.value}.${volume?.toInt()}.${idState.value}"
                        val newId = "${selectedTypeInd.value}.${volume?.toInt()}.${idToEditState.value}"

                        try {
                            val product = Product(
                                id = newId, type = type.name, volume = volume,
                                brand = brandState.value.lowercase(),
                                cashPrice = cashPriceState.value.toDouble(),
                                cashlessPrice = cashlessPriceState.value.toDouble(),
                                isOnHand = isOnHandState.value
                            )
                            editSViewModel.updateProduct(productId = oldId, updatedProduct = product)
                        } catch (e : Exception){
                            showToast(context, "Ошибка.\nВведены некорректные данные")
                            Log.d("ERROR_ERROR", "EditSScreen: ${e.message}")
                        }
                    })
            }
        }

        if (showToastState.value)
        if (!editSViewModel.isLoading.value) {
            if (editSViewModel.isSuccess.value)
                showToast(context, "Продукт обновлён")
            else
                showToast(context, "Ошибка. Продукт не обновлён")
            showToastState.value = false
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = validInputState.value && submitEnabled.value,
            onClick = { showAlertDialog.value = true }
        ) {
            Text(text = "Изменить продукт")
        }

    }


}