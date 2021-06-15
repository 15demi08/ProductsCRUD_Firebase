package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.service.ProductClient

class ProductAdapter:RecyclerView.Adapter<ProductAdapter.PAVH>() {

    var clickListener:PACL? = null
    lateinit var products:List<ProductClient.Product>

    class PAVH:RecyclerView.ViewHolder { //ProductAdapterViewHolder

        var txtvName:TextView
        var txtvID:TextView
        var txtvPrice:TextView

        constructor( v:View ):super(v) {

            txtvName = v.findViewById(R.id.itemLabel_Name)
            txtvID = v.findViewById(R.id.itemLabel_id)
            txtvPrice = v.findViewById(R.id.itemLabel_Price)

        }

    }

    override fun onCreateViewHolder( parent:ViewGroup, viewType:Int):PAVH {

        val view = LayoutInflater.from(parent.context).inflate( R.layout.product_list_item, parent, false )
        return PAVH(view)

    }

    override fun onBindViewHolder( holder:PAVH, position:Int) {

        var product = products[position]

        holder.txtvName.text = product.name
        holder.txtvID.text = product.id
        holder.txtvPrice.text = "$${product.price}"
        holder.itemView.setOnClickListener {
            clickListener?.onItemClicked(holder.itemView, product.id!!)
        }

    }

    override fun getItemCount(): Int = products.size

    interface PACL { // ProductAdapterClickListener
        fun onItemClicked( v:View, documentID:String )
    }

}
