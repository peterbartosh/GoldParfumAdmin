package com.example.goldparfumadmin.di

import com.example.goldparfumadmin.repository.FireRepository
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
    fun provideFireRepository() = FireRepository(
        queryProducts = FirebaseFirestore.getInstance().collection("products")
    )
}