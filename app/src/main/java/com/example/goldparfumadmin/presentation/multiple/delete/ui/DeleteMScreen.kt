package com.example.goldparfumadmin.presentation.multiple.delete.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.goldparfumadmin.R
import com.example.goldparfumadmin.data.utils.ProductType
import com.example.goldparfumadmin.data.utils.UiState
import com.example.goldparfumadmin.data.utils.getSafe
import com.example.goldparfumadmin.data.utils.showToast
import com.example.goldparfumadmin.presentation.components.AlertMessage
import com.example.goldparfumadmin.presentation.components.Label
import com.example.goldparfumadmin.presentation.components.Loading
import com.example.goldparfumadmin.presentation.components.OptionsList
import com.example.goldparfumadmin.presentation.components.PickProductData
import com.example.goldparfumadmin.presentation.components.SubmitButton
import com.example.goldparfumadmin.presentation.single.add.ui.MyAlertDialog

@Composable
fun DeleteMScreen(deleteMViewModel: DeleteMViewModel) {

    val context = LocalContext.current

    val uiState = deleteMViewModel.uiState.collectAsState()

    val showAlertDialog = remember {
        mutableStateOf(false)
    }

    val selectedTypeInd = remember {
        mutableStateOf(ProductType.NotSpecified.ordinal)
    }

    val selectedVolumeInd = remember(selectedTypeInd.value) {
        mutableStateOf(-1)
    }

    val validInputState = remember(
        selectedTypeInd.value,
        selectedVolumeInd.value
    ) {
        mutableStateOf(
            selectedTypeInd.value != ProductType.NotSpecified.ordinal &&
                    (
                            if (selectedTypeInd.value in listOf(1,2))
                                selectedVolumeInd.value != -1
                            else
                                true
                            )
        )
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        if (uiState.value is UiState.Loading) Loading()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f),
            //verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Label(text = "Удаление продуктов")

            AlertMessage(stringId = R.string.delete_multiple_alert_message)

            Spacer(modifier = Modifier.height(50.dp))

            PickProductData(selectedTypeInd = selectedTypeInd) {
               OptionsList(
                   values = ProductType.getType(selectedTypeInd.value).getVolumes().map { it.toInt().toString() },
                   selectedInd = selectedVolumeInd
               )
            }
        }

        SubmitButton(text = "Удалить продукты", enabled = validInputState.value) {
            showAlertDialog.value = true
        }


        if (showAlertDialog.value){

            val type = ProductType.getTypes()[selectedTypeInd.value]
            val volume = type.getVolumes().getSafe(selectedVolumeInd.value)

            MyAlertDialog(
                showAlertDialog = showAlertDialog,
                onConfirmClick = {
                    showAlertDialog.value = false
                    deleteMViewModel.delete(type = type, volume = volume){ message ->
                        showToast(context, "Ошибка.\n$message")
                    }
                },
                text = "Выбранные действия:\n\nУдаление всех продуктов типа ${type.toRus()} объёмом $volume (мл).\n\nВыполнить?"
            )
        }
    }
}

