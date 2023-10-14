package com.example.goldparfumadmin.repository

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.goldparfumadmin.model.Order
import com.example.goldparfumadmin.model.OrderProduct
import com.example.goldparfumadmin.model.Product
import com.example.goldparfumadmin.utils.ProductType
import com.example.goldparfumadmin.utils.OrderStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


class FireRepository @Inject constructor(private val queryProducts : Query) {


    private val productsCollection = FirebaseFirestore.getInstance()
        .collection("products")

    private val blackListCollection = FirebaseFirestore.getInstance()
        .collection("black_list")

    private val ordersProductsCollection = FirebaseFirestore.getInstance()
        .collection("orders_products")

    private val ordersCollection = FirebaseFirestore.getInstance()
        .collection("orders")



    private suspend inline fun <reified T> Query.queryToFlow() : Flow<T> {
        return this.get().await().toObjects(T::class.java).asFlow()
    }

    suspend fun getActiveOrders() : Flow<Order> {
        return ordersCollection
            .whereEqualTo("status", OrderStatus.Accepted.name)
            .queryToFlow()
    }

    suspend fun updateActiveOrders() {
        val activeOrdersIds = ordersCollection
            .whereEqualTo("status", OrderStatus.Accepted.name)
            .get().await().map { it.id }

        activeOrdersIds.forEach { activeOrderId ->
            ordersCollection.document(activeOrderId).update("status", OrderStatus.Delivering)
                .await()
        }
    }

    suspend fun findOrders(orderNumber: String) : List<Order> {
        return ordersCollection.whereEqualTo("number", orderNumber).get().await().toObjects(Order::class.java)
//        return when (orders.size()){
//            0 -> Pair(emptyList(), "Ошибка.\nЗаказ с таким номером не существует.")
//            1 -> return Pair(listOf(orders.first().toObject(Order::class.java)), "")
//            else -> Pair(orders.toObjects(Order::class.java), "Существует несколько заказов с подобным номером")
//        }
    }

    suspend fun setOrderStatusExplicitly(orderId : String, orderStatus : OrderStatus) : Pair<Boolean, String> {
        var isSuccessful = false
        var message = ""
        if (!ordersCollection.document(orderId).get().await().exists()) return Pair(false, "Ошибка.\nЗаказ не существует")

        ordersCollection.document(orderId).update("status", orderStatus.name)
            .addOnCompleteListener {
                isSuccessful = it.isSuccessful
                if (!isSuccessful) message = "Ошибка.\n ${it.exception?.message}"
            }
            .await()
        return Pair(isSuccessful, message)
    }

    suspend fun getOrderProducts(orderId : String) : List<OrderProduct> {
        return ordersProductsCollection.whereEqualTo("order_id", orderId)
            .get()
            .addOnCompleteListener{
                Log.d("RESULT_TEST", "getOrderProducts: ${it.isSuccessful} ${it.exception} ${it.exception?.message}")
            }
            .await().toObjects(OrderProduct::class.java)
    }

    suspend fun addUserToBlackList(phoneNumber : String) : Pair<Boolean, String> {
        var isSuccessful = false

        val exists = blackListCollection.document(phoneNumber).get().await().exists()
        if (exists) return Pair(false, "Ошибка.\nПользователь уже в чёрном списке")

        var message = ""
        blackListCollection.document(phoneNumber).set(mapOf<String,String>()).addOnCompleteListener {
                isSuccessful = it.isSuccessful
                message = if (isSuccessful) "" else "Ошибка"
            Log.d("ERROR_ERROR", "addUserToBlackList: $message")
        }.await()
        return Pair(isSuccessful, message)
    }

    suspend fun deleteUserToBlackList(phoneNumber : String) : Pair<Boolean, String> {
        var isSuccessful = false
        var message = ""

        blackListCollection.document(phoneNumber).delete().addOnCompleteListener {
            if (it.isSuccessful)
                isSuccessful = true
            else
                message = it.exception?.message.toString()
        }.await()

        return Pair(isSuccessful, message)
    }

    suspend fun createProduct(product: Product): Pair<MutableState<Boolean>, MutableState<String>> {
        Log.d("LONG_OPER_SAVE_TEST", "createProduct: 1")
        val isSuccess = Pair(mutableStateOf(false), mutableStateOf(""))
            val id = product.id
            if (id != null) {
                if (!productsCollection.document(id).get().await().exists()) {
                    //Log.d("LONG_OPER_SAVE_TEST", "createProduct: 2")
                    productsCollection.document(id).set(product).addOnCompleteListener {
                        if (it.isSuccessful)
                            isSuccess.first.value = true
                        else {
                            isSuccess.first.value = false
                            isSuccess.second.value = it.exception?.message.toString()
                        }
                    }.await()
                }
                else {
                    //Log.d("LONG_OPER_SAVE_TEST", "createProduct: 4")
                    isSuccess.first.value = false
                   // isSuccess.second.value = "Подобный продукт уже существует"
                }
            }

        //Log.d("LONG_OPER_SAVE_TEST", "createProduct: 5")
        return isSuccess
    }

    suspend fun findProduct(productId : String) = flow {
        val product = productsCollection.document(productId)
            .get().await().toObject(Product::class.java)
        emit(product)
    }

    suspend fun updateProduct(productIdToUpdate : String, product: Product) : MutableState<Boolean> {
        val deleted = deleteProduct(productIdToUpdate)
        val created = createProduct(product)

        return mutableStateOf(deleted.value && created.first.value)
    }

    suspend fun deleteProduct(productId : String) : MutableState<Boolean> {
        val isSuccess = mutableStateOf(false)
        withContext(Dispatchers.IO) {
            productsCollection.document(productId).delete().addOnCompleteListener {
                isSuccess.value = it.isSuccessful
            }.await()
        }
        return isSuccess
    }

    suspend fun deleteProducts(
        productType: ProductType,
        volumes : List<String>,
        onSuccess: () -> Unit) :  Pair<MutableState<Boolean>, MutableState<Int>>  {

        val state: Pair<MutableState<Boolean>, MutableState<Int>> =
            Pair(mutableStateOf(false), mutableStateOf(0))

        withContext(Dispatchers.IO) {
            val docIds = productsCollection
                .whereIn("type", listOf(productType.name))
                .apply {
                    if (volumes.isNotEmpty())
                        this.whereIn("volume", volumes)
                    else
                        this
                }
                .get().await().documents.asFlow()

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
