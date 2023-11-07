package com.example.goldparfumadmin.data.excel

import android.util.Log
import com.example.goldparfumadmin.data.utils.ProductType
import com.example.goldparfumadmin.data.utils.getPrice
import com.example.goldparfumadmin.data.utils.round
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Sheet

class PricesParserImpl(private var dollarCurrency: Double = 3.29) : PricesParser {

    private val defaultValue = 0.0

    override fun parsePrice(
        sheet: Sheet?,
        productType: ProductType,
        volume: Double?
    ): Map<Double, Pair<Double?, Double?>> {

        return when (productType) {

            ProductType.Tester, ProductType.Probe, ProductType.Auto -> {
                val coordinates = getPriceAddress(sheet, "заказ")

                if (coordinates.first == -1 || coordinates.second == -1)
                    return mapOf((volume ?: defaultValue) to Pair(null, null))

                val row = sheet?.getRow(coordinates.first)
                var cashPrice = row?.getCell(coordinates.second)?.stringCellValue?.getPrice()
                var cashlessPrice = row?.getCell(coordinates.second + 1)?.stringCellValue?.getPrice()

                if (cashPrice != null) cashPrice = (cashPrice * dollarCurrency).round(2)
                if (cashlessPrice != null) cashlessPrice = (cashlessPrice * dollarCurrency).round(2)

                val key = when (productType) {
                    ProductType.Auto -> 12.0
                    else -> volume ?: defaultValue
                }
                return mapOf(key to Pair(cashPrice, cashlessPrice))
            }

            ProductType.Diffuser -> {
                val coordinates = getPriceAddress(sheet, "нал")

                if (coordinates.first == -1 || coordinates.second == -1)
                    return mapOf((volume ?: defaultValue) to Pair(null, null))

                val fifthRow = sheet?.getRow(coordinates.first + 1)
                var cashPrice = fifthRow?.getCell(coordinates.second)?.numericCellValue
                var cashlessPrice = fifthRow?.getCell(coordinates.second + 1)?.numericCellValue

                if (cashPrice != null) cashPrice = (cashPrice * dollarCurrency).round(2)
                if (cashlessPrice != null) cashlessPrice = (cashlessPrice * dollarCurrency).round(2)

                return mapOf(200.0 to Pair(cashPrice, cashlessPrice))
            }

            // special case
            ProductType.Compact -> {
                val coordinates = getPriceAddress(sheet, "цена")

                var rowInd = coordinates.first
                val cellInd = coordinates.second

                if (rowInd == -1 || cellInd == -1)
                    return mapOf((volume ?: defaultValue) to Pair(null, null))

                val data = mutableMapOf<Double, Pair<Double, Double>>()

                var go = true

                while (go) {
                    try {

                        val cellData = DataFormatter().formatCellValue(sheet?.getRow(rowInd + 1)?.getCell(cellInd))

                        Log.d("SADSZSODKJD1", cellData)

                        val vol = if (cellData.contains("100"))
                             100.0
                        else if (cellData.contains("20")) //3х20
                            60.0
                        else
                            cellData.toDouble()

                        Log.d("SADSZSODKJD2", "$vol $cellData")


                        var cashPrice = sheet?.getRow(rowInd + 1)?.getCell(cellInd + 1)?.numericCellValue
                        var cashlessPrice = sheet?.getRow(rowInd + 1)?.getCell(cellInd + 2)?.numericCellValue

                        if (cashPrice != null) cashPrice = (cashPrice * dollarCurrency).round(2)
                        if (cashlessPrice != null) cashlessPrice = (cashlessPrice * dollarCurrency).round(2)

                        if (
                            //vol == null ||
                            cashPrice == null || cashlessPrice == null)
                            throw Exception()
                        else
                            data[vol] = Pair(cashPrice, cashlessPrice)

                    } catch (e: Exception) {
                        Log.d(TAG, "parsePrice: ${e.message}")
                        go = false
                    }
                    rowInd++
                }

                return data
            }

            ProductType.Licensed -> {

                val coordinates = getPriceAddress(sheet, "цена")

                val rowInd = coordinates.first
                val cellInd = coordinates.second

                if (rowInd == -1 || cellInd == -1)
                    return mapOf((volume ?: defaultValue) to Pair(null, null))

                var cashPrice = sheet?.getRow(rowInd + 1)?.getCell(cellInd + 1)?.numericCellValue
                var cashlessPrice = sheet?.getRow(rowInd + 2)?.getCell(cellInd + 1)?.numericCellValue

                if (cashPrice != null) cashPrice = (cashPrice * dollarCurrency).round(2)
                if (cashlessPrice != null) cashlessPrice = (cashlessPrice * dollarCurrency).round(2)

                return mapOf(defaultValue to Pair(cashPrice, cashlessPrice))
            }

            ProductType.Lux -> mapOf((volume ?: defaultValue) to Pair(null, null))

            ProductType.EuroA -> {
                val coordinates = getPriceAddress(sheet, "заказ")

                if (coordinates.first == -1 || coordinates.second == -1)
                    return mapOf((volume ?: defaultValue) to Pair(null, null))

                val row = sheet?.getRow(coordinates.first)
                var cashPrice = row?.getCell(coordinates.second)?.stringCellValue?.getPrice()
                var cashlessPrice = row?.getCell(coordinates.second + 1)?.stringCellValue?.getPrice()

                if (cashPrice != null) cashPrice = (cashPrice * dollarCurrency).round(2)
                if (cashlessPrice != null) cashlessPrice = (cashlessPrice * dollarCurrency).round(2)

                val key = volume ?: defaultValue

                return mapOf(key to Pair(cashPrice, cashlessPrice))
            }

            else -> mapOf((volume ?: defaultValue) to Pair(null, null))
        }

    }

    private fun getPriceAddress(sheet : Sheet?, containsValue: String) : Pair<Int, Int>{
        var cellNum = -1
        var rowNum = -1

        sheet?.find { row ->
            val cell = row.find { cell ->
                DataFormatter().formatCellValue(cell).contains(other = containsValue, ignoreCase = true)
            }

            cellNum = cell?.address?.column ?: -1
            rowNum = cell?.address?.row ?: -1

            cell != null
        }?.rowNum ?: -1

        return Pair(rowNum, cellNum)
    }

}