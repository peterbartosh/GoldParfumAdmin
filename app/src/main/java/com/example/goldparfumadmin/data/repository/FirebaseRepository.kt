package com.example.goldparfumadmin.data.repository

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
        suspend fun getIsBlockedForMaintenance() : Boolean? {
            return withContext(Dispatchers.IO) {
                FirebaseFirestore.getInstance()
                    .collection("main_app_blocker")
                    .document("blocker_id")
                    .get()
                    .addOnCompleteListener {
                        Log.d(
                            "AOIJIO",
                            "getIsBlockedForMaintenance: ${it.isSuccessful}"
                        )
                    }
                    .await().getBoolean("is_blocked_for_maintenance")
            }
        }

        suspend fun setIsBlockedForMaintenance(newValue: Boolean) {
            withContext(Dispatchers.IO) {
                FirebaseFirestore.getInstance()
                    .collection("main_app_blocker")
                    .document("blocker_id")
                    .update("is_blocked_for_maintenance", newValue)
                    .addOnCompleteListener {
                        Log.d(
                            "AOIJIO",
                            "setIsBlockedForMaintenance: ${it.isSuccessful}"
                        )
                    }
                    .await()
            }
        }
    }

    private suspend inline fun <reified T> Query.queryToFlow() : Flow<T> {
        return this.get().await().toObjects(T::class.java).asFlow()
    }

    suspend fun getActiveOrders() : Flow<Order> {
        return ordersCollection
            .whereEqualTo("status", OrderStatus.Accepted.name)
            .queryToFlow()
    }

    suspend fun updateActiveOrders() : Result<String> {

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

        return if (result1.isFailure) result1
        else if (result2.isFailure) result2
        else Result.success("")
    }

    suspend fun findOrders(orderNumber: String) : List<Order> {
        return ordersCollection.whereEqualTo("number", orderNumber).get().await().toObjects(Order::class.java)
    }

    suspend fun setOrderStatusExplicitly(orderId : String, orderStatus : OrderStatus) : Result<String> {
        var result = Result.success("")

        if (!ordersCollection.document(orderId).get().await().exists())
            return Result.failure(Exception("Ошибка.\nЗаказ не существует"))

        ordersCollection.document(orderId).update("status", orderStatus.name)
            .addOnCompleteListener {task ->
                result = if (task.isSuccessful)
                    Result.success("")
                else {
                    Log.d("ERROR_ERROR", "addUserToBlackList: ${task.exception} ${task.exception?.message}")
                    Result.failure(task.exception ?: Exception("null exception"))
                }
            }
            .await()
        return result
    }

    suspend fun getOrderProducts(orderId : String) : List<OrderProduct> {
        return ordersProductsCollection.whereEqualTo("order_id", orderId)
            .get()
            .addOnCompleteListener{
                Log.d("RESULT_TEST", "getOrderProducts: ${it.isSuccessful} ${it.exception} ${it.exception?.message}")
            }
            .await().toObjects(OrderProduct::class.java)
    }

    suspend fun addUserToBlackList(phoneNumber : String) : Result<String> {
        var result = Result.success("")

        val exists = blackListCollection.document(phoneNumber).get().await().exists()
        if (exists) return Result.failure(Exception("Ошибка.\nПользователь уже в чёрном списке"))

        blackListCollection.document(phoneNumber).set(mapOf<String,String>()).addOnCompleteListener {task ->
                result = if (task.isSuccessful)
                    Result.success("")
                else {
                    Log.d("ERROR_ERROR", "addUserToBlackList: ${task.exception} ${task.exception?.message}")
                    Result.failure(task.exception ?: Exception("null exception"))
                }
        }.await()

        return result
    }

    suspend fun deleteUserFromBlackList(phoneNumber : String) : Result<String> {
        var result = Result.success("")

        val exists = blackListCollection.document(phoneNumber).get().await().exists()
        if (!exists) return Result.failure(Exception("Ошибка.\nПользователь не найден чёрном списке"))

        blackListCollection.document(phoneNumber).delete().addOnCompleteListener {task ->
            result = if (task.isSuccessful)
                Result.success("")
            else {
                Log.d("ERROR_ERROR", "addUserToBlackList: ${task.exception} ${task.exception?.message}")
                Result.failure(task.exception ?: Exception("null exception"))
            }
        }.await()

        return result
    }

    suspend fun createProduct(product: Product): Result<String> {
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

        return result
    }

    suspend fun findProduct(productId : String) =
        productsCollection.document(productId).get().await().toObject(Product::class.java)


    suspend fun updateProduct(productIdToUpdate : String, product: Product) : Result<String> {
        val deletedResult = deleteProduct(productIdToUpdate)
        val createdResult = createProduct(product)

        return if (deletedResult.isFailure)
            Result.failure(deletedResult.exceptionOrNull() ?: Exception("deleted exc"))
        else if (createdResult.isFailure)
            Result.failure(deletedResult.exceptionOrNull() ?: Exception("created exc"))
        else
            Result.success("succ")
    }

    suspend fun deleteProduct(productId : String) : Result<String> {
        var result = Result.failure<String>(Exception(""))

        productsCollection.document(productId).delete().addOnCompleteListener {
            result = if (it.isSuccessful) Result.success("succ") else Result.failure(Exception("failed"))
        }.await()

        return result
    }

    suspend fun deleteProducts(
        productType: ProductType,
        volume : Double?,
        onSuccess: () -> Unit
    ) :  Pair<MutableState<Boolean>, MutableState<Int>> {

        val state: Pair<MutableState<Boolean>, MutableState<Int>> =
            Pair(mutableStateOf(false), mutableStateOf(0))

        withContext(Dispatchers.IO) {
            var query = productsCollection
                .whereIn("type", listOf(productType.name))

            if (volume != null)
                query = query.whereEqualTo("volume", volume)

            val docIds = query.get().await().documents.asFlow()

            docIds.catch {
                state.second.value++
                state.first.value = true
            }.collect { ds ->
                productsCollection.document(ds.id).delete().addOnCompleteListener {
                    if (!it.isSuccessful) {
                        state.second.value++
                        state.first.value = true
                    }
                }
            }
        }

        return state

    }

}
