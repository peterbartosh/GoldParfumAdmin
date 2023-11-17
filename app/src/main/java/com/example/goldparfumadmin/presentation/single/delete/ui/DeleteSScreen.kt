package com.example.goldparfumadmin.presentation.single.delete.ui

import android.content.Context
import android.net.ConnectivityManager
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
import com.example.goldparfumadmin.data.utils.ProductType
import com.example.goldparfumadmin.data.utils.UiState
import com.example.goldparfumadmin.data.utils.getSafe
import com.example.goldparfumadmin.data.utils.showToast
import com.example.goldparfumadmin.presentation.components.Loading
import com.example.goldparfumadmin.presentation.components.OptionsList
import com.example.goldparfumadmin.presentation.components.PickProductData
import com.example.goldparfumadmin.presentation.components.SubmitButton
import com.example.goldparfumadmin.presentation.single.add.ui.InputField
import com.example.goldparfumadmin.presentation.single.add.ui.MyAlertDialog
import com.example.goldparfumadmin.presentation.single.add.ui.ProductList

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DeleteSScreen(deleteSViewModel: DeleteSViewModel) {

    val context = LocalContext.current

    val keyboard = LocalSoftwareKeyboardController.current

    val uiState = deleteSViewModel.uiState.collectAsState()

    val idState = remember {
        mutableStateOf("-1")
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

    val validInputState = remember(
        selectedTypeInd.value,
        selectedVolumeInd.value,
        volumeState.value,
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
            idState.value != "-1" &&
            idState.value.isNotEmpty()
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

    val showTableState = remember(uiState.value){
        mutableStateOf(uiState.value is UiState.Success)
    }

    val submitEnabled = remember(showTableState.value, inputStateChanged.value) {
        mutableStateOf(showTableState.value && !inputStateChanged.value)
    }

    val showAlertDialog = remember {
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
                enabled = selectedTypeInd.value != -1 &&
                        selectedTypeInd.value != ProductType.NotSpecified.ordinal &&
                        (
                                if (selectedTypeInd.value in listOf(1,2))
                                    selectedVolumeInd.value != -1
                                else
                                    volumeState.value.isNotEmpty()
                                ) && idState.value.isNotEmpty(),
                onClick = {
                    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                    val connected = manager.activeNetwork

                    if (connected == null) {
                        showToast(context, "Ошибка.\nВы не подключены к сети.")
                        return@Button
                    }
                    keyboard?.hide()
                    inputStateChanged.value = false
                    val volume = try {
                        if (selectedVolumeInd.value == -1)
                            volumeState.value.toInt().toString()
                        else
                            ProductType.getType(selectedTypeInd.value).getVolumes()
                                .getSafe(selectedVolumeInd.value)?.toInt().toString()

                    } catch (e : Exception){ "" }

                    deleteSViewModel.findProduct(
                        productId = "${selectedTypeInd.value}.$volume.${idState.value}",
                        onError = { message -> showToast(context, "Ошибка.\n$message") }
                    )
            }) {
                Text(text = "Найти продукт")
            }

            if (showTableState.value)
                deleteSViewModel.product.collectAsState().value?.let { product ->
                    ProductList(product = product)
                }

            if (showAlertDialog.value) {
                val type = ProductType.getType(selectedTypeInd.value)
                val volume = if (selectedVolumeInd.value == -1)
                    volumeState.value.toInt().toString()
                else
                    type.getVolumes().getSafe(selectedVolumeInd.value)?.toInt().toString()

                MyAlertDialog(
                    text = "Удаление продукта типа ${type.toRus()} объёмом $volume (мл).\nВыполнить?",
                    showAlertDialog = showAlertDialog,
                    onConfirmClick = {
                        deleteSViewModel.deleteProduct(
                            productId = "${selectedTypeInd.value}.$volume.${idState.value}",
                            onSuccess = { showToast(context, "Продукт удалён") },
                            onFailure = { message -> showToast(context, "Ошибка.\n$message") }
                        )
                    }
                )
            }


        }


        SubmitButton(
            text = "Удалить продукт",
            enabled = validInputState.value && submitEnabled.value
        ) {
            showAlertDialog.value = true
        }


    }

    if (uiState.value is UiState.Loading) Loading()


}