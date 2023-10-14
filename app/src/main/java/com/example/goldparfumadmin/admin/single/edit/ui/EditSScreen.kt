package com.example.goldparfumadmin.admin.single.edit.ui

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
import com.example.goldparfumadmin.admin.multiple.add.ui.OptionsList
import com.example.goldparfumadmin.admin.multiple.add.ui.PickProductData
import com.example.goldparfumadmin.admin.single.add.ui.EditList
import com.example.goldparfumadmin.admin.single.add.ui.InputField
import com.example.goldparfumadmin.admin.single.add.ui.MyAlertDialog
import com.example.goldparfumadmin.model.Product
import com.example.goldparfumadmin.utils.Loading
import com.example.goldparfumadmin.utils.ProductType
import com.example.goldparfumadmin.utils.Sex
import com.example.goldparfumadmin.utils.getVolumes
import com.example.goldparfumadmin.utils.showToast
import java.lang.Exception

fun updateStates(
    product: Product,
    idState : MutableState<String>,
    brandState : MutableState<String>,
    cashPriceState : MutableState<String>,
    cashlessPriceState : MutableState<String>,
    sexState : MutableState<Int>,
    isOnHandState : MutableState<Boolean>
){
    idState.value = product.id?.split(".")?.last().toString()
    brandState.value = product.brand.toString()
    cashPriceState.value = product.cashPrice.toString()
    cashlessPriceState.value = product.cashlessPrice.toString()
    sexState.value = when (product.sex){
        "Male" -> 0
        "Female" -> 1
        "Unisex" -> 2
        else -> -1
    }

    isOnHandState.value = product.isOnHand == true
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditSScreen(editSViewModel: EditSViewModel) {

    val context = LocalContext.current

    val keyboard = LocalSoftwareKeyboardController.current

    val types = listOf(
        ProductType.volume, ProductType.tester, ProductType.probe,
        ProductType.licensed, ProductType.auto, ProductType.original,
        ProductType.diffuser, ProductType.lux, ProductType.notSpecified)

    val idState = remember {
        mutableStateOf("-1")
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
    val sexState = remember {
        mutableStateOf(2)
    }
    val isOnHandState = remember {
        mutableStateOf(false)
    }

    val selectedTypeInd = remember {
        mutableStateOf(8)
    }

    val selectedVolumeInd = remember {
        mutableStateOf(-1)
    }

    val validInputState = remember(
            selectedTypeInd.value,
            selectedVolumeInd.value,
            idState.value
        ) {
        if (selectedTypeInd.value == 8 || selectedVolumeInd.value == -1 || idState.value == "-1" || idState.value.isEmpty())
            mutableStateOf(false)
        else
            mutableStateOf(true)
    }

    val inputStateChanged = remember(
        selectedTypeInd.value,
        selectedVolumeInd.value,
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
                OptionsList(
                    values = getVolumes(types[selectedTypeInd.value]),
                    selectedInd = selectedVolumeInd
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
                enabled = validInputState.value,
                onClick = {
                    keyboard?.hide()
                    inputStateChanged.value = false
                    editSViewModel.findProduct(
                        "${selectedTypeInd.value}." +
                                "${getVolumes(types[selectedTypeInd.value])[selectedVolumeInd.value]}." +
                                idState.value,
                            context)
                    updateTableState.value = true
                }) {
                Text(text = "Найти продукт")
            }

            if (editSViewModel.isLoading.value) Loading()

            if (showTableState.value) {
                val p = editSViewModel.product.collectAsState().value
                if (p != null && updateTableState.value)
                    updateStates(p, idState, brandState, cashPriceState, cashlessPriceState, sexState, isOnHandState)
                updateTableState.value = false
                EditList(
                    idState = idState,
                    brandState = brandState,
                    cashPriceState = cashPriceState,
                    cashlessPriceState = cashlessPriceState,
                    sexState = sexState,
                    isOnHandState = isOnHandState
                )
            }

            if (showAlertDialog.value) {
                val type = types[selectedTypeInd.value]
                val volume =  getVolumes(type)[selectedVolumeInd.value]
                val sexes = listOf(Sex.Male, Sex.Female, Sex.Unisex)
                MyAlertDialog(
                    text = "Изменение продукта типа ${type.toRus()} объёмом $volume.\nВыполнить?",
                    showAlertDialog = showAlertDialog,
                    onConfirmClick = {
                        showToastState.value = true
                        val id = "${selectedTypeInd.value}." +
                                "${getVolumes(types[selectedTypeInd.value])[selectedVolumeInd.value]}." +
                                idState.value
                        try {
                            val product = Product(id = id, type = type.name, volume = volume,
                                    brand = brandState.value.lowercase(),
                                    cashPrice = cashPriceState.value.toDouble(),
                                    cashlessPrice = cashlessPriceState.value.toDouble(),
                                    sex = sexes[sexState.value].name, isOnHand = isOnHandState.value)
                            editSViewModel.updateProduct(productId = id, updatedProduct = product)
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