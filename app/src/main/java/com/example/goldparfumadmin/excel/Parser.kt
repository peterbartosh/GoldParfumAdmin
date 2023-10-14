package com.example.goldparfumadmin.excel

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.goldparfumadmin.model.Product
import com.example.goldparfumadmin.utils.ProductType
import com.example.goldparfumadmin.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class Parser(private val context : Context, private val uri: Uri) {

    var mutableStateFlow = mutableListOf<Product>()

    suspend fun getRowsAmount(): Int {
        var amount = 0
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val xb = XSSFWorkbook(inputStream)
            val sheet = xb.getSheetAt(0)
            amount = sheet.lastRowNum - 3
        }
        return amount
    }

    fun parse(
        volume: String,
        type: ProductType,
        scope: CoroutineScope,
        // saveProductToDatabase: suspend (Product) -> Unit
    ): Deferred<List<Unit>> {

        var sheet : XSSFSheet? = null

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val xb = XSSFWorkbook(inputStream)
            sheet = xb.getSheetAt(0)
        }

        Log.d("DIJID",
              sheet?.drop(3000)?.take(1000)?.joinToString(separator = ", ") { it.getCell(1).stringCellValue }.toString())


        return scope.async {
            val defs = sheet?.drop(3000)?.take(1000)?.mapIndexed { ind, row ->
                async {
                    try {
                        val id = row.getCell(0).numericCellValue.toInt().toString()
                        val product = Product(
                            id = type.ordinal.toString() + "." + volume + "." + id,
                            brand = row.getCell(1).stringCellValue.lowercase(),
                            cashPrice = row.getCell(2).numericCellValue,
                            volume = volume,
                            type = type.name
                        )
                        mutableStateFlow.add(product)
                        Log.d("LONG_OPER_TEST", "parse: $ind retrieved")

                    } catch (_: Exception) {
                    }
                    Unit
                }
            }
            defs?.awaitAll()!!
        }
    }



        fun getFileName(): String {
            val cursor = context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )

            return if (cursor != null) {
                cursor.moveToFirst()
                if (cursor.count == 0)
                    showToast(context, "The given Uri doesn't represent any file")
                val displayNameColumnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val displayName = cursor.getString(displayNameColumnIndex)
                cursor.close()
                displayName
            } else {
                showToast(context, "Failed to obtain cursor from the content resolver")
                "not found"
            }
        }

}