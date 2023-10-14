package com.example.goldparfumadmin.admin.multiple.delete.ui

import OptionsList
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.goldparfumadmin.admin.multiple.add.ui.AlertMessage
import com.example.goldparfumadmin.utils.ProductType
import com.example.goldparfumadmin.utils.getVolumes
import com.example.goldparfumadmin.admin.multiple.add.ui.Label
import com.example.goldparfumadmin.admin.multiple.add.ui.PickProductData
import com.example.goldparfumadmin.admin.single.add.ui.MyAlertDialog
import com.example.goldparfumadmin.utils.Loading

@Composable
fun DeleteMScreen(deleteMViewModel: DeleteMViewModel) {

    val types = listOf(
        ProductType.volume, ProductType.tester, ProductType.probe,
        ProductType.licensed, ProductType.auto, ProductType.original,
        ProductType.diffuser, ProductType.lux, ProductType.notSpecified)

    val context = LocalContext.current

    val showAlertDialog = remember {
        mutableStateOf(false)
    }

    val selectedTypeInd = remember {
        mutableStateOf(8)
    }

    val listOfVolumesStates = remember {
        List(6){ mutableStateOf(false) }
    }

    val valid = remember(selectedTypeInd.value) {
        mutableStateOf(selectedTypeInd.value != 8)
    }


    Column(modifier = Modifier.fillMaxSize(),
           //horizontalAlignment = Alignment.CenterHorizontally,
           verticalArrangement = Arrangement.SpaceBetween
    ) {

        if (deleteMViewModel.isLoading.value)
            Loading()


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f),
            //verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Label(text = "Удаление продуктов")

            AlertMessage(isAdd = false)

            Spacer(modifier = Modifier.height(50.dp))

            PickProductData(selectedTypeInd = selectedTypeInd) {
                OptionsList(
                    values = getVolumes(types[selectedTypeInd.value]),
                    listOfButtonsStates = listOfVolumesStates
                )
            }


        }

        Button(
            enabled = valid.value,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            onClick = {
                showAlertDialog.value = true

            }
        ) {
            Text(text = "Удалить продукты")
        }


        if (showAlertDialog.value){

            val type = types[selectedTypeInd.value]
            val v = getVolumes(type)
            val volumes = mutableListOf<String>()
            listOfVolumesStates.forEachIndexed{i, el ->
                if (el.value) volumes.add(v[i])
            }
            val vStr = if (volumes.isEmpty()) " любым объёмом." else
                " объёмом " + volumes.joinToString(separator = " или ") + " (мл)."

            MyAlertDialog(
                showAlertDialog = showAlertDialog,
                onConfirmClick = {
                    showAlertDialog.value = false
                    deleteMViewModel.delete(type = type, volumes = volumes, context = context)
                },
                text = "Выбранные действия:\n\nУдаление всех продуктов типа ${type.toRus()}$vStr\n\nВыполнить?"
            )


        }

    }
}

