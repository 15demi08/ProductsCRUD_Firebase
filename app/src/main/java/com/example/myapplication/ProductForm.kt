package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.myapplication.service.ProductClient
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProductForm : AppCompatActivity() {

    var currentProduct: ProductClient.Product? = null
    lateinit var pc:ProductClient

    lateinit var btnDelete:Button
    lateinit var lblProductID:TextView
    lateinit var txtvProductID:TextView
    lateinit var txteProductName:EditText
    lateinit var txteProductPrice:EditText
    lateinit var lblProductFormTitle:TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_form)

        pc = ProductClient()

        initScreen()

    }

    private fun initScreen() {

        btnDelete = findViewById(R.id.btnDelete)
        lblProductID = findViewById(R.id.lblProductID)
        txtvProductID = findViewById(R.id.txtvProductID)
        txteProductName = findViewById(R.id.txteProductName)
        txteProductPrice = findViewById(R.id.txteProductPrice)
        lblProductFormTitle = findViewById(R.id.lblProductFormTitle)

        when( intent.action ){

            "new" -> {
                btnDelete.visibility = Button.GONE
                lblProductID.visibility = TextView.GONE
                txtvProductID.visibility = TextView.GONE
            }
            "edit" -> {

                pc.getByID(intent.getStringExtra("productID")!!)
                    .addOnSuccessListener { product ->
                        currentProduct = product.toObject(ProductClient.Product::class.java)
                        txtvProductID.text = currentProduct?.id
                        txteProductName.setText(currentProduct?.name)
                        txteProductPrice.setText(currentProduct?.price.toString())
                    }
                    .addOnFailureListener {
                        result(0, Intent().putExtra("message", it.toString()))
                    }

            }

        }

        var titlePieceID = if(intent.action == "new") R.string.prodForm_titlePiece_new else R.string.prodForm_titlePiece_edit

        // R.string.prodForm_title is a String with Placeholders: '%1$s %2$s'. %1 and %2 are indexes, $s denotes String. Result is "[New|Edit] Product", localized
        lblProductFormTitle.text = getString(R.string.prodForm_title, getString(titlePieceID), getString(R.string.prodForm_titlePiece_product)  )

    }

    fun cancel( v:View ){

        this.result(-1)

    }

    fun save( v:View ){

        when( intent.action ){

            "new" -> {

                pc.new(
                    ProductClient.Product(
                        txteProductName.text.toString(),
                        txteProductPrice.text.toString().toDouble()
                    )
                )
                    .addOnSuccessListener { result(1) }
                    .addOnFailureListener { result(0, Intent().putExtra("message", R.string.prodList_msg_unknownError)) }

            }
            "edit" -> {

                currentProduct?.apply {
                    name = txteProductName.text.toString()
                    price = txteProductPrice.text.toString().toDouble()
                }

                pc.update(currentProduct!!)
                    .addOnSuccessListener { result(1) }
                    .addOnFailureListener { result(0, Intent().putExtra("message", R.string.prodList_msg_unknownError)) }


            }

        }



    }

    fun delete( v:View ){

        pc.delete(currentProduct!!.id!!)
            .addOnSuccessListener { result(2) }
            .addOnFailureListener { result(0, Intent().putExtra("message", R.string.prodList_msg_unknownError)) }

    }

    private fun result( code:Int, data:Intent? = null ){

        if(data is Intent) setResult(code, data)
        else setResult(code)

        finish()

    }

}