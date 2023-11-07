package com.example.goldparfumadmin.presentation.orders.collect_orders.ui

import android.os.Environment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.goldparfumadmin.presentation.components.Loading
import com.example.goldparfumadmin.presentation.single.add.ui.MyAlertDialog

@Composable
fun OrderCollectingScreen(orderCollectingViewModel: OrderCollectingViewModel) {

    val context = LocalContext.current

    val showAlertDialog = remember {
        mutableStateOf(false)
    }

//    val selectedTypeInd = remember {
//        mutableStateOf(8)
//    }
//
//    val selectedVolumeInd = remember {
//        mutableStateOf(-1)
//    }


//    val fileUri : MutableState<Uri?> = remember {
//        mutableStateOf(null)
//    }
//
//    val fileName = remember(fileUri.value) {
//        val uri = fileUri.value
//        var name = "Не выбран файл"
//        if (uri != null) {
//            val newName = orderCollectingViewModel.getFileName(uri, context)
//            if (newName.isNotEmpty()) name = newName
//        }
//        mutableStateOf(name)
//    }

//    val validInputState = remember(
//        selectedTypeInd.value,
//        selectedVolumeInd.value,
//        fileUri.value
//    ) {
//        Log.d("SELECT_IND_TEST", "AddMScreen: ${selectedVolumeInd.value}")
//        mutableStateOf(
//            selectedTypeInd.value != 8 &&
//                    (
//                            if (selectedTypeInd.value in listOf(1,2))
//                                selectedVolumeInd.value != -1
//                            else
//                                true
//                            )
//                    &&
//                    fileUri.value != null
//        )
//    }


    if (orderCollectingViewModel.isLoading)
        Loading()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

            Box{}
//
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .fillMaxHeight(0.7f),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//
//            Label(text = "Добавление новых продуктов")
//
//            AlertMessage(isAdd = true)
//
//            Spacer(modifier = Modifier.height(50.dp))
//
//            val launcher = rememberLauncherForActivityResult(
//                contract = ActivityResultContracts.GetContent(),
//                onResult = { fileUri.value = it }
//            )
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .wrapContentHeight(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Button(
//                    modifier = Modifier.padding(start = 10.dp),
//                    onClick = {
//                        try {
//                            launcher.launch("*/*")
//                        } catch (e: Exception) {
//                            showToast(context, e.message ?: "NULL_MESSAGE")
//                        }
//                    }) {
//                    Text(text = "Открыть медиа")
//                }
//
//                Text(
//                    text = fileName.value,
//                    fontSize = 14.sp,
//                    modifier = Modifier.padding(end = 30.dp)
//                )
//            }
//
//            PickProductData(selectedTypeInd = selectedTypeInd) {
//                OptionsList(
//                    values = when (selectedTypeInd.value){
//                        1 -> ProductType.Tester.getVolumes()
//                        2 -> ProductType.Probe.getVolumes()
//                        else -> emptyList()
//                    },
//                    selectedInd = selectedVolumeInd
//                )
//            }
//
//            Spacer(modifier = Modifier.height(30.dp))
//        }

        Button(
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            onClick = { showAlertDialog.value = true }
        ) {
            Text(text = "Собрать заказы")
        }

        if (showAlertDialog.value){
//            val type = ProductType.getTypes()[selectedTypeInd.value]
//            val volume = type.getVolumes().getSafe(selectedVolumeInd.value)

            MyAlertDialog(
                text = "Выбранные действия:\n\nСбор всех заказов за последние 7 дней.\n\nВыполнить?",
                showAlertDialog = showAlertDialog,
                onConfirmClick = {
                    //fileUri.value?.let { uri ->
                    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + "/Telegram"

                    orderCollectingViewModel.run(path, context)
                        //addMViewModel.addAll(uri = uri, context = context, dollarCurrency = dollarCurrency.value, type = type, volume = volume)
                            //}
                }
            )
        }
    }
}

