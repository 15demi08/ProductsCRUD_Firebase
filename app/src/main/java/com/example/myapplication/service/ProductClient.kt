package com.example.myapplication.service

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProductClient {

    data class Product( var name:String? = null, var price:Double? = null, @DocumentId var id:String? = null )

    private val db = Firebase.firestore
    private val products = db.collection("products")

    /**
     * Returns all Products currently saved in the database
     */
    fun getAll() = products.get()

    /**
     * Returns a single Product, by its id. Fails with a message on error
     */
    fun getByID( id:String ) = products.document(id).get()

    /**
     * Creates a new Product in the database
     */
    fun new( product:Product ) = products.add(hashMapOf("name" to product.name, "price" to product.price))

    /**
     * Updates an existing Product in the database
     */
    fun update( product:Product ) = products.document(product.id!!).update(mapOf("name" to product.name, "price" to product.price))

    /**
     * Deletes a product from the Database
     */
    fun delete( id:String ): Task<Void> = products.document(id).delete()

}