package com.example.goldparfumadmin.model

import com.google.firebase.firestore.PropertyName

data class OrderProduct(
    //var id : String? = null,

    @get:PropertyName("order_id")
    @set:PropertyName("order_id")
    var orderId : String? = null,

    @get:PropertyName("product_id")
    @set:PropertyName("product_id")
    var productId : String? = null,

    var amount : Int? = null,

    @get:PropertyName("is_cash_price")
    @set:PropertyName("is_cash_price")
    var isCashPrice : Boolean? = null
)
