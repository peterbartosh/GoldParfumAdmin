package com.example.goldparfumadmin.data.model

import com.google.firebase.firestore.PropertyName

data class OrderProduct(

    @get:PropertyName("order_id")
    @set:PropertyName("order_id")
    var orderId : String? = null,

    @get:PropertyName("product_id")
    @set:PropertyName("product_id")
    var productId : String? = null,

    @get:PropertyName("cash_price_amount")
    @set:PropertyName("cash_price_amount")
    var cashAmount : Int? = null,

    @get:PropertyName("cashless_price_amount")
    @set:PropertyName("cashless_price_amount")
    var cashlessAmount : Int? = null,

)
