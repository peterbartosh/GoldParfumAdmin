package com.example.goldparfumadmin.data.excel

import com.example.goldparfumadmin.data.model.Product
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet

interface ProductsParser {
    fun initialize(sheet: Sheet?, volume: Double?) : List<Double>

    fun getStartIndex() : Int

    fun parse(row: Row, volume : Double?, ind: Int) : Product?
}