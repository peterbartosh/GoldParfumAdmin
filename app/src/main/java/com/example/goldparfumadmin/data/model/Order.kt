package com.example.goldparfumadmin.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class Order(
    @Exclude var id: String? = null,

    var number: String? = null,

    var address: String? = null,

    var date: Timestamp?  = null,

    val status : String? = null,

    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String? = null,
)
