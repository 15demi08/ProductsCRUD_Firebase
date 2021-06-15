package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.service.ProductClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), ProductAdapter.PACL {

    private lateinit var pc:ProductClient

    companion object {

        const val REQ_NEW = 1
        const val REQ_EDIT = 2

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pc = ProductClient()

        pc.getAll()
            .addOnSuccessListener {

                val manager = LinearLayoutManager(this)
                val productAdapter = ProductAdapter()

                productAdapter.clickListener = this
                productAdapter.products = it.toObjects(ProductClient.Product::class.java)

                findViewById<RecyclerView>(R.id.productList).apply {
                    setHasFixedSize(true)
                    layoutManager = manager
                    adapter = productAdapter
                }
            }
            .addOnFailureListener {
                showSnackbar(R.string.prodList_msg_unknownError)
            }

        findViewById<FloatingActionButton>(R.id.fabNewProduct).setOnClickListener{
            newProduct()
        }

    }

    private fun reloadItems(){

        Log.i("Product API", "loadItems()")

        pc.getAll()
            .addOnSuccessListener {
                (findViewById<RecyclerView>(R.id.productList).adapter as ProductAdapter).apply {
                    products = it.toObjects(ProductClient.Product::class.java)
                    notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                showSnackbar(R.string.prodList_msg_unknownError)
            }

    }

    private fun newProduct() = startActivityForResult( Intent(this, ProductForm::class.java).apply { action = "new" }, REQ_NEW )

    // Edit a product
    override fun onItemClicked(v:View, documentID:String) = startActivityForResult( Intent(this, ProductForm::class.java).apply { action = "edit" }.putExtra("productID", documentID), REQ_EDIT )

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == -1 ) {

            showSnackbar(R.string.prodList_msg_canceled)

        } else {

            when (requestCode) {

                REQ_NEW -> {

                    when (resultCode) {

                        0 -> showSnackbar(data?.getIntExtra("message", 0)!!)
                        1 -> showSnackbar(R.string.prodList_msg_saved)

                    }

                }
                REQ_EDIT -> {

                    when (resultCode) {

                        0 -> showSnackbar(data?.getIntExtra("message", 0)!!)
                        1 -> showSnackbar(R.string.prodList_msg_modified)
                        2 -> showSnackbar(R.string.prodList_msg_deleted)

                    }

                }

            }

            reloadItems()

        }

    }

    private fun showSnackbar( msg:Int ) = Snackbar.make(findViewById(R.id.layoutFABContainer), msg, Snackbar.LENGTH_SHORT ).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show()

}