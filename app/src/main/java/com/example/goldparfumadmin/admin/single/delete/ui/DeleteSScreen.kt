package com.example.goldparfumadmin.admin.single.delete.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.goldparfumadmin.admin.single.add.ui.InputField
import com.example.goldparfumadmin.admin.single.add.ui.MyAlertDialog
import com.example.goldparfumadmin.admin.single.add.ui.ProductList
import com.example.goldparfumadmin.utils.Loading
import com.example.goldparfumadmin.utils.ProductType
import com.example.goldparfumadmin.utils.getVolumes
import com.example.goldparfumadmin.utils.showToast

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DeleteSScreen(deleteSViewModel: DeleteSViewModel) {

    val context = LocalContext.current

    val keyboard = LocalSoftwareKeyboardController.current

    val types = listOf(
        ProductType.volume, ProductType.tester, ProductType.probe,
        ProductType.licensed, ProductType.auto, ProductType.original,
        ProductType.diffuser, ProductType.lux, ProductType.notSpecified)

    val idState = remember {
        mutableStateOf("-1")
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

    val showTableState = remember(deleteSViewModel.isSuccess.value){
        mutableStateOf(
            deleteSViewModel.isSuccess.value
        )
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

        Column(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()) {

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
                enabled = validInputState.value,
                onClick = {
                    keyboard?.hide()
                    inputStateChanged.value = false
                    deleteSViewModel.findProduct(
                        "${selectedTypeInd.value}." +
                                "${getVolumes(types[selectedTypeInd.value])[selectedVolumeInd.value]}." +
                                idState.value,
                            context)
            }) {
                Text(text = "Найти продукт")
            }


            if (deleteSViewModel.isLoading.value)
                Loading()

            if (showTableState.value) {
                val p = deleteSViewModel.product.collectAsState().value
                if (p != null) ProductList(product = p)
            }

            if (showAlertDialog.value) {
                val type = types[selectedTypeInd.value]
                val volume = getVolumes(type)[selectedVolumeInd.value]
                MyAlertDialog(
                    text = "Удаление продукта типа ${type.toRus()} объёмом $volume (мл).\nВыполнить?",
                    showAlertDialog = showAlertDialog,
                    onConfirmClick = {
                        showToastState.value = true
                        deleteSViewModel.deleteProduct(
                            "${selectedTypeInd.value}." +
                                    "${getVolumes(types[selectedTypeInd.value])[selectedVolumeInd.value]}." +
                                    idState.value)
                    })
            }


        }

        if (showToastState.value)
            if (!deleteSViewModel.isLoading.value) {
                if (deleteSViewModel.isSuccess.value)
                    showToast(context, "Продукт удалён")
                else
                    showToast(context, "Ошибка. Продукт не удалён")
                showToastState.value = false
            }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = validInputState.value && submitEnabled.value,
            onClick = { showAlertDialog.value = true }
        ) {
            Text(text = "Удалить продукт")
        }


    }


}