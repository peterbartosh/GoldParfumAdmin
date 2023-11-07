package com.example.goldparfumadmin.presentation.multiple.add.ui

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goldparfumadmin.data.utils.ProductType
import com.example.goldparfumadmin.data.utils.getSafe
import com.example.goldparfumadmin.data.utils.showToast
import com.example.goldparfumadmin.presentation.components.AlertMessage
import com.example.goldparfumadmin.presentation.components.Label
import com.example.goldparfumadmin.presentation.components.Loading
import com.example.goldparfumadmin.presentation.components.OptionsList
import com.example.goldparfumadmin.presentation.components.PickProductData
import com.example.goldparfumadmin.presentation.single.add.ui.InputField
import com.example.goldparfumadmin.presentation.single.add.ui.MyAlertDialog

@Composable
fun AddMScreen(addMViewModel: AddMViewModel) {

    val context = LocalContext.current

    val showAlertDialog = remember {
        mutableStateOf(false)
    }

    val selectedTypeInd = remember {
        mutableStateOf(ProductType.NotSpecified.ordinal)
    }

    val selectedVolumeInd = remember {
        mutableStateOf(-1)
    }

    val dollarCurrency = remember {
        mutableStateOf("3.29")
    }

    val fileUri : MutableState<Uri?> = remember {
        mutableStateOf(null)
    }

    val fileName = remember(fileUri.value) {
        val uri = fileUri.value
        var name = "Не выбран файл"
        if (uri != null) {
            val newName = addMViewModel.getName(uri, context, type = ProductType.getType(selectedTypeInd.value))
            if (newName.isNotEmpty()) name = newName
        }
        mutableStateOf(name)
    }

    val validInputState = remember(
        selectedTypeInd.value,
        selectedVolumeInd.value,
        fileUri.value,
        dollarCurrency.value
    ) {
        Log.d("SELECT_IND_TEST", "AddMScreen: ${selectedVolumeInd.value}")
        mutableStateOf(
            selectedTypeInd.value != ProductType.NotSpecified.ordinal &&
                    (
                            if (selectedTypeInd.value in listOf(1,2))
                                selectedVolumeInd.value != -1
                            else
                                true
                            )
                    &&
                    fileUri.value != null &&
                    dollarCurrency.value.replace(",", ".").trim().toDoubleOrNull() != null
        )
    }



    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {



        if (addMViewModel.isLoading.value)
            Loading(addMViewModel.progressState.value, addMViewModel.fileRowsAmount)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Label(text = "Добавление новых продуктов")

            AlertMessage(isAdd = true)

            Spacer(modifier = Modifier.height(50.dp))

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent(),
                onResult = {
                    fileUri.value = it
                    Log.d("FILE_URI", "AddMScreen: ${fileUri.value?.path}" +
                            " ${fileUri.value?.encodedPath} " +
                            "${fileUri.value?.lastPathSegment}" +
                            "${fileUri.value?.pathSegments}" +
                            "")
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    modifier = Modifier.padding(start = 10.dp),
                    onClick = {
                    try {
                        launcher.launch("*/*")
                    } catch (e: Exception) {
                        showToast(context, e.message ?: "NULL_MESSAGE")
                    }
                }) {
                    Text(text = "Открыть медиа")
                }

                Text(
                    text = fileName.value,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(end = 30.dp)
                )
            }

            PickProductData(selectedTypeInd = selectedTypeInd) {
                OptionsList(
                    values = when (selectedTypeInd.value){
                        1 -> ProductType.Tester.getVolumes()
                        2 -> ProductType.Probe.getVolumes()
                        else -> emptyList()
                    }.map { it.toInt().toString() },
                    selectedInd = selectedVolumeInd
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            InputField(modifier = Modifier, valueState = dollarCurrency, label = "Курс доллара", enabled = true)
        }



        Button(
            enabled = validInputState.value,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            onClick = { showAlertDialog.value = true }
        ) {
            Text(text = "Добавить продукты")
        }

        if (showAlertDialog.value){
            val type = ProductType.getTypes()[selectedTypeInd.value]
            val volume = type.getVolumes().getSafe(selectedVolumeInd.value)

            MyAlertDialog(
                text = "Выбранные действия:\n\nДобавление продуктов типа ${type.toRus()} объёмом $volume мл\n\nВыполнить?",
                showAlertDialog = showAlertDialog,
                onConfirmClick = {
                    fileUri.value?.let { uri ->
                        addMViewModel.addAll(
                            uri = uri,
                            context = context,
                            dollarCurrency = dollarCurrency.value,
                            type = type,
                            volume = volume
                        )
                    }
                }
            )
        }
    }
}

