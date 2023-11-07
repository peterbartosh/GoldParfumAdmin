package com.example.goldparfumadmin.data.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.round

const val TAG = "Utils_TAG"

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

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

fun String.firstLetterToUpperCase() : String {
    if (this.isEmpty()) return ""
    val firstLetter = this.first().uppercase()
    return firstLetter + this.substring(1)
}

fun String.getVolume(defaultValue : Double) : Double {
    return try {
        this.split(" ").last { it.any { ch -> ch.isDigit() } }.filter { ch -> ch.isDigit() }.toDouble()
    } catch (e : Exception){
        Log.d(TAG, "getVolume: $e ${e.message}")
        defaultValue
    }
}

fun String.getVolumeForLicensed(defaultValue: Double) : Double {
    val primaryItems = this.split(" ")

    val items = primaryItems.filter { it.trim().isNotEmpty() }

    val nextItem = items.find { it.contains("мл") } ?: return defaultValue
    val nextItemInd = items.indexOf(nextItem)
    val returnValue = items[nextItemInd - 1]
    return try {
        returnValue.toInt()
        returnValue.toDouble()
    } catch (e : Exception){
        Log.d("ERROR_ERROR", "getVolumeForLicensed1: ${e.message}")
        try {
            val alternative = nextItem.replace("мл", "").replace(".", "")
            alternative.toInt()
            alternative.toDouble()
        } catch (e : Exception){
            Log.d("ERROR_ERROR", "getVolumeForLicensed2: ${e.message}")
            defaultValue.toDouble()
        }
    }
}

fun String?.getPrice() : Double? {
    val braceInd = this?.indexOf("(") ?: 0
    val dollarInd = this?.indexOf("$") ?: 0
    return try {
        this?.substring(braceInd + 1, dollarInd)?.trim()?.replace(",", ".")?.toDouble()
    } catch (e : Exception){
        Log.d(TAG, "getPrice: ${e.message}")
        null
    }
}