package com.example.goldparfumadmin.admin.single.add.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.goldparfumadmin.admin.multiple.add.ui.OptionsList
import com.example.goldparfumadmin.admin.multiple.add.ui.PickProductData
import com.example.goldparfumadmin.model.Product
import com.example.goldparfumadmin.ui.theme.Gold
import com.example.goldparfumadmin.utils.Loading
import com.example.goldparfumadmin.utils.ProductType
import com.example.goldparfumadmin.utils.Sex
import com.example.goldparfumadmin.utils.getVolumes
import com.example.goldparfumadmin.utils.showToast

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddSScreen(addSViewModel: AddSViewModel) {

    val context = LocalContext.current

    val keyboard = LocalSoftwareKeyboardController.current

    val types = listOf(
        ProductType.volume, ProductType.tester, ProductType.probe,
        ProductType.licensed, ProductType.auto, ProductType.original,
        ProductType.diffuser, ProductType.lux, ProductType.notSpecified)

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

    val showAlertDialog = remember {
        mutableStateOf(false)
    }

    val showToastState = remember {
        mutableStateOf(false)
    }

    val stateChanged = remember(
        idState.value, brandState.value,
        cashPriceState.value, cashlessPriceState.value,
        sexState.value, isOnHandState.value,
        selectedTypeInd.value, selectedVolumeInd.value
    ) {
        val stateIsDefault = idState.value == "1" &&  brandState.value == "Gucci" &&
                cashPriceState.value == "0.0" && cashlessPriceState.value == "0.0" &&
                sexState.value == 2 && !isOnHandState.value &&
                selectedTypeInd.value == 8 && selectedVolumeInd.value == -1
        mutableStateOf(!stateIsDefault)
    }

    if (addSViewModel.isLoading.value) Loading()


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()) {

            PickProductData(selectedTypeInd = selectedTypeInd) {
                OptionsList(
                    values = getVolumes(types[selectedTypeInd.value]),
                    selectedInd = selectedVolumeInd
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            EditList(
                idState = idState,
                brandState = brandState,
                cashPriceState = cashPriceState,
                cashlessPriceState = cashlessPriceState,
                sexState = sexState,
                isOnHandState = isOnHandState
            )

            if (showAlertDialog.value) {

                val type = types[selectedTypeInd.value]
                val volume =  getVolumes(type)[selectedVolumeInd.value]

                MyAlertDialog(
                    text = "Добавление продукта типа ${type.toRus()} объёмом $volume.\nВыполнить?",
                    showAlertDialog = showAlertDialog,
                    onConfirmClick = {
                        showToastState.value = true
                        try {
                            val sexes = listOf(Sex.Male, Sex.Female, Sex.Unisex)
                            val product = Product(
                                id = "${types[selectedTypeInd.value].ordinal}." +
                                        "${getVolumes(types[selectedTypeInd.value])[selectedVolumeInd.value]}." +
                                        idState.value,
                                type = types[selectedTypeInd.value].name,
                                volume = getVolumes(types[selectedTypeInd.value])[selectedVolumeInd.value],
                                brand = brandState.value.lowercase(),
                                cashPrice = cashPriceState.value.toDouble(),
                                cashlessPrice = cashlessPriceState.value.toDouble(),
                                sex = sexes[sexState.value].name,
                                isOnHand = isOnHandState.value
                            )
                            addSViewModel.addProduct(product)
                        } catch (e: Exception) {
                            showToast(context, "Ошибка.\nПроверьте корректность данных")
                            Log.d("ERROR_ERROR", "AddSScreen: ${e.message}")
                        }
                    }
                )
            }

        }


        if (showToastState.value)
            if (!addSViewModel.isLoading.value) {
                if (addSViewModel.isSuccess.value)
                    showToast(context, "Продукт обновлён")
                else
                    showToast(context, "Ошибка.\nПродукт не обновлён\n${addSViewModel.message}")
                showToastState.value = false
            }

        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Gold),
            modifier = Modifier.fillMaxWidth(),
            enabled = stateChanged.value && selectedTypeInd.value != 8 && selectedVolumeInd.value != -1,
            onClick = { showAlertDialog.value = true }
        ) {
            Text(text = "Добавить продукт")

        }
    }

}