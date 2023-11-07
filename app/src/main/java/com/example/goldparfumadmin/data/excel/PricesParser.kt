package com.example.goldparfumadmin.data.excel

import com.example.goldparfumadmin.data.utils.ProductType
import org.apache.poi.ss.usermodel.Sheet

interface PricesParser {
    fun parsePrice(sheet: Sheet?, productType: ProductType, volume : Double?) : Map<Double, Pair<Double?, Double?>>
}