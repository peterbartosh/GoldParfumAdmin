package com.example.goldparfumadmin.data.excel

import android.util.Log
import com.example.goldparfumadmin.data.model.Product
import com.example.goldparfumadmin.data.utils.ProductType
import com.example.goldparfumadmin.data.utils.getVolume
import com.example.goldparfumadmin.data.utils.getVolumeForLicensed
import com.example.goldparfumadmin.data.utils.round
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet

const val TAG = "Parser_TAG"

class ProductsParserImpl(private val productType: ProductType) : ProductsParser {

    private lateinit var prices : Map<Double, Pair<Double?, Double?>>

    private val defaultValue = 0.0

    override fun initialize(sheet: Sheet?, volume: Double?) : List<Double> {
        val pricesParser = PricesParserImpl()
        this.prices = pricesParser.parsePrice(sheet, productType, volume)
        return this.prices.keys.toList()
    }

    override fun getStartIndex() : Int = when(productType){
        ProductType.Original, ProductType.Tester, ProductType.Probe, ProductType.Auto -> 2
        ProductType.Diffuser -> 4
        ProductType.Compact -> 5
        ProductType.Licensed -> 3
        ProductType.EuroA, ProductType.Selectives -> 1
        else -> 5
    }

    override fun parse(row: Row, volume : Double?, ind: Int) : Product? =
        when (productType) {

            ProductType.Original -> parseOriginal(row = row)

            ProductType.Tester, ProductType.Probe, ProductType.Compact -> parseDefault(
                type = productType,
                row = row,
                volume = volume ?: defaultValue
            )

            ProductType.Auto -> parseDefault(
                type = ProductType.Auto,
                row = row,
                volume = 12.0
            )

            ProductType.Diffuser -> parseDefault(
                type = ProductType.Diffuser,
                row = row,
                volume = 200.0
            )

            ProductType.Licensed -> parseLicensed(row = row)

            ProductType.Lux -> null

            ProductType.Selectives -> parseSelectives(rowInd = ind, row = row)

            ProductType.EuroA -> parseEuroA(rowInd = ind, row =  row, keyVolume = defaultValue)

            ProductType.NotSpecified -> null
    }

    private fun parseOriginal(row: Row) : Product? {
        return try {
            val id = row.getCell(0)?.numericCellValue?.toInt()
            if (id == null || id == 0) null

            else {
                val data = row.getCell(1).stringCellValue.lowercase()
                val cashPrice = row.getCell(2)?.numericCellValue?.round(2) ?: return null
                val cashlessPrice = cashPrice * 1.2
                val isFilled = row.getCell(1)?.cellStyle?.fillBackgroundColorColor != null


                Product(
                    id = "0.${data.getVolume(defaultValue).toInt()}.$id",
                    brand = data,
                    cashPrice = cashPrice,
                    cashlessPrice = cashlessPrice,
                    volume = data.getVolume(defaultValue),
                    type = "Original",
                    isOnHand = !isFilled
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, "parseOriginal: ${e.message}")
            null
        }
    }

    private fun parseDefault(
        type : ProductType,
        row : Row,
        volume: Double,
    ) : Product? {

        if (prices.values.any { pair -> pair.first == null || pair.second == null }) return null

        return try {
            val id = row.getCell(0)?.numericCellValue?.toInt()
            if (id == null || id == 0) null

            else {
                val data = row.getCell(1).stringCellValue.lowercase()
                val isFilled = row.getCell(1)?.cellStyle?.fillBackgroundColorColor != null

                val product = Product(
                    id = "${type.ordinal}.${volume.toInt()}.$id",
                    brand = data,
                    cashPrice = prices[volume]?.first,
                    cashlessPrice = prices[volume]?.second,
                    volume = volume,
                    type = type.name,
                    isOnHand = !isFilled
                )

                product
            }
        } catch (e: Exception) {
            Log.d(TAG, "parseNotOriginal: ${e.message}")
            null
        }
    }

    private fun parseLicensed(row : Row) : Product? {

        if (prices.values.any { pair -> pair.first == null || pair.second == null }) return null

        return try {

            val id = row.getCell(0)?.numericCellValue?.toInt()
            if (id == null || id == 0) null

            else {
                val data = row.getCell(1).stringCellValue.lowercase()
                val isFilled = row.getCell(1)?.cellStyle?.fillBackgroundColorColor != null
                val volume = data.getVolumeForLicensed(defaultValue = 100.0)

                val product = Product(
                    id = "${ProductType.Licensed.ordinal}.${volume.toInt()}.$id",
                    brand = data,
                    cashPrice = prices[defaultValue]?.first,
                    cashlessPrice = prices[defaultValue]?.second,
                    volume = volume.toDouble(),
                    type = ProductType.Licensed.name,
                    isOnHand = !isFilled
                )

                product
            }
        } catch (e: Exception) {
            Log.d(TAG, "parseNotOriginal: ${e.message}")
            null
        }
    }

    private fun parseSelectives(
        rowInd: Int,
        indexOffset: Int = 2,
        row: Row
    ): Product? {
        return try {
            if (rowInd == 0) null

            else {
                val type = ProductType.Selectives
                val data = row.getCell(0).stringCellValue.lowercase()
                val productPrices = DataFormatter().formatCellValue(row.getCell(1))
                val cashPrice = productPrices.split("/").first().replace(",", ".").toDouble().round(2)
                val cashlessPrice = productPrices.split("/").last().replace(",", ".").toDouble().round(2)
                val isFilled = row.getCell(0)?.cellStyle?.fillBackgroundColorColor != null
                val fontIndex = row.getCell(0)?.cellStyle?.fontIndex

                if (isFilled && fontIndex != 0 && cashPrice == 0.0 && cashlessPrice == 0.0) return null

                val id = rowInd + indexOffset
                val volume = data.getVolume(defaultValue)

                Product(
                    id = "${type.ordinal}.${volume.toInt()}.$id",
                    brand = data,
                    cashPrice = cashPrice,
                    cashlessPrice = cashlessPrice,
                    volume = volume,
                    type = type.name,
                    isOnHand = !isFilled
                )

            }
        } catch (e: Exception) {
            Log.d(TAG, "parseSelectives: ${e.message}")
            null
        }
    }

    private fun parseEuroA(
        rowInd: Int,
        row: Row,
        indexOffset: Int = 2,
        keyVolume: Double
    ): Product? {
        if (prices.values.any { pair -> pair.first == null || pair.second == null }) return null
        val type = ProductType.EuroA

        return try {

            if (rowInd == 0) null

            else {
                val data = row.getCell(0).stringCellValue.lowercase()
                val isFilled = row.getCell(0)?.cellStyle?.fillBackgroundColorColor != null
                val fontIndex = row.getCell(0)?.cellStyle?.fontIndex

                val id = rowInd + indexOffset
                val volume = data.getVolume(defaultValue)

                if (isFilled && fontIndex != 0) null

                else {

                    val product = Product(
                        id = "${type.ordinal}.${volume.toInt()}.$id",
                        brand = data,
                        cashPrice = prices[keyVolume]?.first,
                        cashlessPrice = prices[keyVolume]?.second,
                        volume = volume,
                        type = type.name,
                        isOnHand = !isFilled
                    )

                    product
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "parseEuroA: ${e.message}")
            null
        }
    }


}