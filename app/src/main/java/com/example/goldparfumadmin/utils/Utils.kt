package com.example.goldparfumadmin.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun getWidthPercent(context: Context): Dp {
    val displayMetrics = context.resources.displayMetrics
    return ((displayMetrics.widthPixels / displayMetrics.density) / 100).dp
}

@Composable
fun getHeightPercent(context: Context): Dp {
    val displayMetrics = context.resources.displayMetrics
    return ((displayMetrics.heightPixels / displayMetrics.density) / 100).dp
}

fun showToast(context : Context, text : String){
    val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
    toast.show()
}

@Composable
fun Loading(progress : MutableState<Int>? = null, maxValue : MutableState<Int>? = null) {
    Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
        Text(text = "Loading...  " + if (progress == null) "" else progress.value.toString()
                + if (maxValue == null || maxValue.value == 0) "/..." else " / ${maxValue.value}")
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
}