package com.example.goldparfumadmin.data.repository

import android.util.Log
import com.example.goldparfumadmin.data.model.Order
import com.example.goldparfumadmin.data.model.OrderProduct
import com.example.goldparfumadmin.data.model.Product
import com.example.goldparfumadmin.data.utils.OrderStatus
import com.example.goldparfumadmin.data.utils.ProductType
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FireRepository @Inject constructor(
    private val productsCollection : CollectionReference,
    private val blackListCollection : CollectionReference,
    private val ordersProductsCollection : CollectionReference,
    private val ordersCollection : CollectionReference
) {

    companion object{
        suspend fun getIsBlockedForMaintenance(onError: (String) -> Unit) =
            try {
                withContext(Dispatchers.IO) {
                    FirebaseFirestore.getInstance()
                        .collection("main_app_blocker")
                        .document("blocker_id")
                        .get()
                        .addOnCompleteListener {
                            Log.d(
                                "FIRE_TAG",
                                "getIsBlockedForMaintenance: ${it.isSuccessful}"
                            )
                        }
                        .await().getBoolean("is_blocked_for_maintenance")
                }

            } catch (e : Exception){
                onError(e.message ?: "error")
                false
            }


        suspend fun setIsBlockedForMaintenance(newValue: Boolean) {
            withContext(Dispatchers.IO) {
                FirebaseFirestore.getInstance()
                    .collection("main_app_blocker")
                    .document("blocker_id")
                    .update("is_blocked_for_maintenance", newValue)
                    .addOnCompleteListener {
                        Log.d(
                            "FIRE_TAG",
                            "setIsBlockedForMaintenance: ${it.isSuccessful}"
                        )
                    }
                    .await()
            }
        }
    }

    private suspend inline fun <reified T> Query.queryToFlow() =
        this.get().await().toObjects(T::class.java).asFlow()


    suspend fun getActiveOrders() : Flow<Order> = try {
        ordersCollection
            .whereEqualTo("status", OrderStatus.Accepted.name)
            .queryToFlow()
    } catch (e : Exception) {
        emptyFlow()
    }

    suspend fun updateActiveOrders() : Result<String> = try {

        var result1 = Result.success("")

        val activeOrdersIds = ordersCollection
            .whereEqualTo("status", OrderStatus.Accepted.name)
            .get()
            .addOnCompleteListener{ task ->
                if (!task.isSuccessful)
                    result1 = Result.failure(task.exception ?: Exception("null exception"))
            }
            .await().map { it.id }

        var result2 = Result.success("")

        activeOrdersIds.forEach { activeOrderId ->
            ordersCollection.document(activeOrderId)
                .update("status", OrderStatus.Delivering)
                .addOnCompleteListener{ task ->
                    if (!task.isSuccessful)
                        result2 = Result.failure(task.exception ?: Exception("null exception"))

                }.await()
        }

        if (result1.isFailure) result1
        else if (result2.isFailure) result2
        else Result.success("")
    } catch (e : Exception) { Result.failure(e) }

    suspend fun findOrders(orderNumber: String) : List<Order> = try {
        ordersCollection.whereEqualTo("number", orderNumber).get().await().toObjects(Order::class.java)
    }  catch (e : Exception) {
        emptyList()
    }

    suspend fun setOrderStatusExplicitly(orderId : String, orderStatus : OrderStatus) : Result<String> = try {
        var result = Result.success("")

        if (!ordersCollection.document(orderId).get().await().exists())
            Result.failure(Exception("Ошибка.\nЗаказ не существует"))
        else {
            ordersCollection.document(orderId).update("status", orderStatus.name)
                .addOnCompleteListener { task ->
                    result = if (task.isSuccessful)
                        Result.success("")
                    else {
                        Log.d(
                            "ERROR_ERROR",
                            "addUserToBlackList: ${task.exception} ${task.exception?.message}"
                        )
                        Result.failure(task.exception ?: Exception("null exception"))
                    }
                }
                .await()
            result
        }
    }  catch (e : Exception) { Result.failure(e) }

    suspend fun getOrderProducts(orderId : String) : List<OrderProduct> = try {
        ordersProductsCollection.whereEqualTo("order_id", orderId)
            .get()
            .addOnCompleteListener{
                Log.d("FIRE_TAG", "getOrderProducts: ${it.isSuccessful} ${it.exception} ${it.exception?.message}")
            }
            .await().toObjects(OrderProduct::class.java)
    }  catch (e : Exception) {
        emptyList()
    }

    suspend fun addUserToBlackList(phoneNumber : String) : Result<String> = try {
        var result = Result.success("")

        val exists = blackListCollection.document(phoneNumber).get().await().exists()
        if (exists)  Result.failure(Exception("Ошибка.\nПользователь уже в чёрном списке"))
        else {
            blackListCollection.document(phoneNumber).set(mapOf<String, String>())
                .addOnCompleteListener { task ->
                    result = if (task.isSuccessful)
                        Result.success("")
                    else {
                        Log.d(
                            "ERROR_ERROR",
                            "addUserToBlackList: ${task.exception} ${task.exception?.message}"
                        )
                        Result.failure(task.exception ?: Exception("null exception"))
                    }
                }.await()

            result
        }
    }  catch (e : Exception) { Result.failure(e) }

    suspend fun deleteUserFromBlackList(phoneNumber : String) : Result<String> = try {
        var result = Result.success("")

        val exists = blackListCollection.document(phoneNumber).get().await().exists()
        if (!exists) Result.failure(Exception("Ошибка.\nПользователь не найден чёрном списке"))
        else {

            blackListCollection.document(phoneNumber).delete().addOnCompleteListener { task ->
                result = if (task.isSuccessful)
                    Result.success("")
                else {
                    Log.d(
                        "ERROR_ERROR",
                        "addUserToBlackList: ${task.exception} ${task.exception?.message}"
                    )
                    Result.failure(task.exception ?: Exception("null exception"))
                }
            }.await()

            result
        }
    } catch (e : Exception) { Result.failure(e) }

    suspend fun createProduct(product: Product): Result<String> = try {
        var result = Result.failure<String>(NullPointerException("id == null"))
            val id = product.id
            if (id != null) {
                val exists = productsCollection.document(id).get().await().exists()
                if (!exists) {
                    productsCollection.document(id).set(product).addOnCompleteListener {
                        result = if (it.isSuccessful)
                            Result.success("succ")
                        else
                            Result.failure(it.exception ?: Exception("null"))
                    }.await()
                }
                else {
                    result = Result.failure(Exception("already exists"))
                }
            }

        result
    } catch (e : Exception) { Result.failure(e) }

    suspend fun findProduct(productId : String) = try {
        productsCollection.document(productId).get().await().toObject(Product::class.java)
    } catch (e : Exception) { null }

    suspend fun updateProduct(productIdToUpdate : String, product: Product) : Result<String> = try {
        val deletedResult = deleteProduct(productIdToUpdate)
        val createdResult = createProduct(product)

        if (deletedResult.isFailure)
            Result.failure(deletedResult.exceptionOrNull() ?: Exception("deleted exc"))
        else if (createdResult.isFailure)
            Result.failure(deletedResult.exceptionOrNull() ?: Exception("created exc"))
        else
            Result.success("succ")
    } catch (e : Exception) { Result.failure(e) }

    suspend fun deleteProduct(productId : String) : Result<String> = try {
        withContext(Dispatchers.IO) {
            var result = Result.failure<String>(Exception(""))

            productsCollection.document(productId).delete().addOnCompleteListener {
                result =
                    if (it.isSuccessful) Result.success("succ") else Result.failure(Exception("failed"))
            }.await()

            result
        }
    } catch (e : Exception) { Result.failure(e) }

    suspend fun deleteProducts(
        productType: ProductType,
        volume : Double?,
        onError: (String) -> Unit
    ) = try {

        withContext(Dispatchers.IO) {
            var query = productsCollection
                .whereIn("type", listOf(productType.name))

            if (volume != null)
                query = query.whereEqualTo("volume", volume)

            val docIds = query.get().await().documents.asFlow()

            docIds.catch { e ->
                Log.d("ERROR_ERROR", "deleteProducts: $e ${e.message}")
            }.collect { ds ->
                productsCollection.document(ds.id).delete().await()
            }
        }


    } catch (e: Exception) {
        onError(e.message.toString())
    }

}
