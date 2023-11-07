package com.example.goldparfumadmin.data.excel

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.goldparfumadmin.data.model.Product
import com.example.goldparfumadmin.data.utils.ProductType
import com.example.goldparfumadmin.data.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.util.concurrent.atomic.AtomicInteger


class ExcelDataLoader(
    private val context : Context,
    private val uri: Uri,
    private val productType: ProductType
) {

    var products = mutableListOf<Product>()

    val errorsCounter = AtomicInteger(0)


    suspend fun getRowsAmount(): Int? {
        //var amount = 0
        return withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val xb = XSSFWorkbook(inputStream)
                val sheet = xb.getSheetAt(0)
                sheet.lastRowNum - 2
            }
        }
        //return amount
    }

    fun getFileName(): String {

        //withContext(Dispatchers.IO) {

            val cursor = context.contentResolver.query(
                uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
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
       // }
    }

    suspend fun parse(
        volume: Double?,
        scope: CoroutineScope,
        dollarCurrency: Double
    ) {
        withContext(Dispatchers.IO) {

            var sheet: XSSFSheet? = null

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val workbook = XSSFWorkbook(inputStream)
                sheet = workbook.getSheetAt(0)
            }

            val parser = ProductsParserImpl(productType)

            val startIndex = parser.getStartIndex()

            //Log.d("START_INDEX", "parse: $startIndex")

            //Log.d(TAG, "parse: ${productType.name}")

            when (productType) {
                ProductType.Compact -> {

                    val initDeferred = async {
                        parser.initialize(sheet = sheet, volume = volume, dollarCurrency = dollarCurrency)
                    }
                    val volumes = initDeferred.await()

                    Log.d("SKOAHUSI", "parse: $volumes")

                    var curVolumeInd = 0
                    var splitting = false

                    val map = mutableMapOf<Double, MutableList<(Double) -> Unit>>()

                    volumes.forEach { v ->
                        map[v] = mutableListOf()
                    }

                        sheet?.drop(startIndex)?.forEachIndexed { ind, row ->

                            //Log.d("YSUGAUS", "parse: $ind) ${row?.getCell(0)?.numericCellValue}, vol = $vol")

                            if (curVolumeInd <= 6)
                                try {
                                    val id = row?.getCell(0)?.numericCellValue?.toInt()
                                    if (id != null && id != 0) {

                                        splitting = false
                                        map[volumes[curVolumeInd]]?.add { vol ->
                                                val product = parser.parse(row, vol, ind)

                                                if (product == null)
                                                    errorsCounter.incrementAndGet()
                                                else if (!products.add(product))
                                                    errorsCounter.incrementAndGet()

                                            }

                                    } else {
                                        if (!splitting) {
                                            Log.d("DONE_TAG", "parse: $ind) $curVolumeInd")
                                            curVolumeInd++
                                            splitting = true
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.d(TAG, "parse: ${e.message}")
                                }
                        }

                        map.map{ (key, value) ->
                            async {
                                value.forEach { func ->
                                    func.invoke(key)
                                }
                            }
                        }.awaitAll()


                }

                else -> {

                    val initDeferred = async {
                        parser.initialize(sheet = sheet, volume = volume, dollarCurrency = dollarCurrency)
                    }

                    initDeferred.await()

//                    val deferred =
//                        scope.async {

                            val deferreds = sheet?.drop(startIndex)
                                //?.take(5000)
                                ?.mapIndexed { ind, row ->
                                scope.async {
                                    val product = parser.parse(
                                        row = row,
                                        volume = volume,
                                        ind = ind
                                    )
                                    if (product == null)
                                        errorsCounter.incrementAndGet()
                                    else if (!products.add(product))
                                        errorsCounter.incrementAndGet()
                                    else 0
                                }
                            }
                            deferreds?.awaitAll()
                       // }

                    //deferred.await()
                }
            }

            //products.removeIf { it == null }

        }
    }
}