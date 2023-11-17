package com.example.goldparfumadmin.data.excel

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.example.goldparfumadmin.presentation.orders.collect_orders.ui.PairInt
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

class ExcelFileGeneratorImpl(
    private val uri : Uri,
    private val context : Context,
    private val fileName: String
) {

    private val fileFormat = ".xlsx"


    fun generateCompact(
        orders: Map<String, Map<String, PairInt>>,
        firstCellInd : Int,
        secondCellInd : Int,
    ) {

        try {

            var workbook: XSSFWorkbook? = null
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                workbook = XSSFWorkbook(inputStream)
            }
            val sheet = workbook?.getSheetAt(0)

            var newWorkbook: XSSFWorkbook? = null
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                newWorkbook = XSSFWorkbook(inputStream)
            }
            val newSheet = newWorkbook?.getSheetAt(0)

            val volumes = listOf("15", "45", "35", "80", "10", "60", "100")
            var curVolumeInd = 0
            var splitting = false

            sheet?.drop(5)?.forEachIndexed { ind, row ->

                if (curVolumeInd < volumes.size) {
                    try {
                        val vol = volumes[curVolumeInd]
                        val rowId = row.getCell(0).numericCellValue.toInt()
                        if (rowId == 0) throw NumberFormatException("not a product")

                        splitting = false

                        orders[vol]?.get(rowId.toString().trim())?.let { orderPair ->
                            newSheet?.getRow((ind + 5))?.getCell(firstCellInd)
                                ?.setCellValue(orderPair.first.toDouble() + orderPair.second.toDouble())
                        }

                    } catch (e: Exception) {
                        Log.d(TAG, "generate: ${e.message}")
                        if (!splitting) {
                            curVolumeInd++
                        }
                        splitting = true
                    }
                }
            }

            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

            val file = File(path, "/$fileName$fileFormat")

            newWorkbook?.write(file.outputStream())
            newWorkbook?.close()

        } catch (e: Exception) {
            Log.d(TAG, "generateCompact: ${e.message}")
        }
    }

    fun generate(
        firstCellInd : Int,
        secondCellInd : Int,
        orders : Map<String, PairInt>
    ){
        try {

            var workbook: XSSFWorkbook? = null
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                workbook = XSSFWorkbook(inputStream)
            }
            val sheet = workbook?.getSheetAt(0)

            var newWorkbook: XSSFWorkbook? = null
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                newWorkbook = XSSFWorkbook(inputStream)
            }
            val newSheet = newWorkbook?.getSheetAt(0)

            sheet?.forEachIndexed { ind, row ->
                try {
                    val rowId = row.getCell(0).numericCellValue.toInt()
                    if (rowId == 0) throw NumberFormatException("not a product")
                    else {

                        val orderPair = orders[rowId.toString()] ?: throw Exception("not found")

                        if (firstCellInd == secondCellInd)
                            newSheet?.getRow(ind)?.getCell(firstCellInd)
                                ?.setCellValue((orderPair.first + orderPair.second).toDouble())
                        else {
                            newSheet?.getRow(ind)?.getCell(firstCellInd)
                                ?.setCellValue(orderPair.first.toDouble())
                            newSheet?.getRow(ind)?.getCell(secondCellInd)
                                ?.setCellValue(orderPair.second.toDouble())
                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "generate: $e ${e.message}")
                }
            }


            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val file = File(path, "/$fileName$fileFormat")

            newWorkbook?.write(file.outputStream())
            newWorkbook?.close()

        } catch (e : Exception){
            Log.d(TAG, "$e")
        }

    }


    fun generateEuroOrSelectives(
        firstCellInd : Int,
        secondCellInd : Int,
        orders : Map<String, PairInt>
    ){
        try {
            var workbook: XSSFWorkbook? = null
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                workbook = XSSFWorkbook(inputStream)
            }
            val sheet = workbook?.getSheetAt(0)

            var newWorkbook: XSSFWorkbook? = null
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                newWorkbook = XSSFWorkbook(inputStream)
            }
            val newSheet = newWorkbook?.getSheetAt(0)

            sheet?.forEachIndexed { ind, row ->
                try {

                    val orderPair = orders[(ind+1).toString()] ?: throw Exception("not found")

                    newSheet?.getRow(ind)?.getCell(firstCellInd)
                        ?.setCellValue(orderPair.first.toDouble())
                    newSheet?.getRow(ind)?.getCell(secondCellInd)
                        ?.setCellValue(orderPair.second.toDouble())

                } catch (e: Exception) {
                    Log.d(TAG, "generate: $e ${e.message}")
                }
            }

            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val file = File(path, "/$fileName$fileFormat")

            newWorkbook?.write(file.outputStream())
            newWorkbook?.close()
        } catch (e : Exception){
            Log.d(TAG, "$e")
        }

    }
}