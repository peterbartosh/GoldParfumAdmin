package com.example.goldparfumadmin.di

import com.example.goldparfumadmin.data.repository.FireRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object AppModule {
    @Singleton
    @Provides
    fun provideFireRepository() : FireRepository {
        val fireInstance = FirebaseFirestore.getInstance()
        return FireRepository(
            productsCollection = fireInstance.collection("products"),
            blackListCollection = fireInstance.collection("black_list"),
            ordersProductsCollection = fireInstance.collection("orders_products"),
            ordersCollection = fireInstance.collection("orders")
        )
    }
}