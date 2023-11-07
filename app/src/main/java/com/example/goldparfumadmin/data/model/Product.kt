package com.example.goldparfumadmin.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName


data class Product(
    @Exclude
    var id : String? = null,

    var type : String? = null,

    var volume: Double? = null,

    var brand : String? = null,

    //var sex : String? = null,

    @get:PropertyName("cash_price")
    @set:PropertyName("cash_price")
    var cashPrice : Double? = null, // add currency converter (BLR, USD, EUR)

    @get:PropertyName("cashless_price")
    @set:PropertyName("cashless_price")
    var cashlessPrice : Double? = null, // add currency converter (BLR, USD, EUR)

    @get:PropertyName("is_on_hand")
    @set:PropertyName("is_on_hand")
    var isOnHand : Boolean? = null
)
