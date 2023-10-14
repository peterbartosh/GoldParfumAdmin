package com.example.goldparfumadmin.admin.multiple.add.ui

import android.net.Uri
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goldparfumadmin.admin.single.add.ui.MyAlertDialog
import com.example.goldparfumadmin.utils.Loading
import com.example.goldparfumadmin.utils.ProductType
import com.example.goldparfumadmin.utils.getVolumes
import com.example.goldparfumadmin.utils.showToast

@Composable
fun AddMScreen(addMViewModel: AddMViewModel) {

    val context = LocalContext.current

    val types = listOf(
        ProductType.volume, ProductType.tester, ProductType.probe,
        ProductType.licensed, ProductType.auto, ProductType.original,
        ProductType.diffuser, ProductType.lux, ProductType.notSpecified)

    val fileUri : MutableState<Uri?> = remember {
        mutableStateOf(null)
    }

    val fileName = remember(fileUri.value) {
        val uri = fileUri.value
        var name = "Не выбран файл"
        if (uri != null) {
            val newName = addMViewModel.getName(uri, context)
            if (newName.isNotEmpty()) name = newName
        }
        mutableStateOf(name)
    }

    val showAlertDialog = remember {
        mutableStateOf(false)
    }

    val selectedTypeInd = remember {
        mutableStateOf(8)
    }

    val selectedVolumeInd = remember {
        mutableStateOf(-1)
    }

    val valid = remember(selectedTypeInd.value, selectedVolumeInd.value, fileUri.value) {
        mutableStateOf(
            fileUri.value != null &&
                    selectedTypeInd.value != 8
                    && selectedVolumeInd.value != -1
        )
    }


    Column(modifier = Modifier.fillMaxSize(),
           verticalArrangement = Arrangement.SpaceBetween
    ) {

        if (addMViewModel.isLoading.value)
            Loading(addMViewModel.progressState, addMViewModel.fileRowsAmount)

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
                onResult = { fileUri.value = it}
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

            PickProductData(
                selectedTypeInd = selectedTypeInd,
                optionsList = {
                    OptionsList(
                        values = getVolumes(types[selectedTypeInd.value]),
                        selectedInd = selectedVolumeInd
                    )
                }
            )

        }



        Button(
            enabled = valid.value,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            onClick = { showAlertDialog.value = true }
        ) {
            Text(text = "Добавить продукты")
        }

        if (showAlertDialog.value){
            val type = types[selectedTypeInd.value]
            val volume  = getVolumes(type)[selectedVolumeInd.value]

            MyAlertDialog(
                text = "Выбранные действия:\n\nДобавление продуктов типа ${type.toRus()} объёмом $volume мл\n\nВыполнить?",
                showAlertDialog = showAlertDialog,
                onConfirmClick = {
                    addMViewModel.addAll(fileUri.value!!, context, type = type, volume = volume)
                }
            )
        }
    }
}

