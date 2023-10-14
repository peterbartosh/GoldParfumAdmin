package com.example.goldparfumadmin.utils


enum class OrderStatus{
    Processing, Accepted, Delivering, Succeed, Canceled
}

enum class Sex {
    Male, Female, Unisex;
    fun toRus() : String = when (this){
        Male -> "Мужское"
        Female -> "Женское"
        Unisex -> "Унисекс"
    }
}

enum class ProductType{
    volume, tester, probe, licensed, auto, original, diffuser, lux, notSpecified;

    fun toRus() : String = when (this){
        volume -> "Объемы"
        tester -> "Тестеры"
        probe -> "Пробники"
        licensed -> "Лицензионные"
        auto -> "Авто"
        original -> "Оригиналы"
        diffuser -> "Диффузоры"
        lux -> "Люкс"
        notSpecified -> "Не задано"
    }
}

fun getVolumes(type : ProductType) : List<String> =  when (type.name){
    ProductType.volume.name -> listOf("10", "15", "35", "45", "3x20", "80")
    ProductType.tester.name -> listOf("60", "65", "110", "115", "125")
    ProductType.probe.name -> listOf("30", "35", "55")
    ProductType.licensed.name -> listOf("50")
    ProductType.auto.name -> listOf("50")
    ProductType.original.name -> listOf("50")
    ProductType.diffuser.name -> listOf("50")
    ProductType.lux.name -> listOf("50")
    else -> emptyList()
}