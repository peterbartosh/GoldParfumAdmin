package com.example.goldparfumadmin.data.utils


enum class OrderStatus{
    Processing, Accepted, Delivering, Succeed, Canceled
}

enum class ProductType{

    Original, Tester, Probe, Auto, Diffuser, Compact, Licensed, Lux, Selectives, EuroA, NotSpecified;

    companion object {
        private val types =
            listOf(Original, Tester, Probe, Auto, Diffuser, Compact, Licensed, Lux, Selectives, EuroA, NotSpecified)

        fun getType(ind : Int) = if (ind in types.indices) types[ind] else NotSpecified

        fun getTypes() : List<ProductType> = types

    }

    fun toRus() : String = when (this) {
        Original -> "Оригиналы"
        Tester -> "Тестеры"
        Probe -> "Пробники"
        Auto -> "Авто"
        Diffuser -> "Диффузоры"
        Compact -> "Компакт"    // issues
        Licensed -> "Лицензионные"
        Lux -> "Люкс"
        Selectives -> "Селективы"
        EuroA -> "Евро А+"
        NotSpecified -> "Не задано"
    }

    fun getVolumes(): List<Double> = when (this) {
        Tester -> listOf(55.0, 60.0, 65.0, 110.0, 115.0, 125.0)
        Probe -> listOf(30.0, 35.0)
        else -> emptyList()
    }
}

fun List<Double>.getSafe(ind: Int) : Double? = if (ind in this.indices) this[ind] else null
