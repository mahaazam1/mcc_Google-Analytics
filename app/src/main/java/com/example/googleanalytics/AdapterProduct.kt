package com.example.googleanalytics

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.product_item.view.*

class AdapterProduct(val activity: Activity,
                      val data: ArrayList<ModelCategory>,
                      val onClick: OnClickItem
) : RecyclerView.Adapter<AdapterProduct.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var id = itemView.id
        var productName = itemView.name_product

        var productCard = itemView.product_card
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterProduct.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category =data[position]
        holder.productName.text = category.products?.get(position)?.get("nameP")
        holder.productCard.setOnClickListener {
            onClick.onClick(holder.adapterPosition)
        }
    }


    interface OnClickItem {
        fun onClick(position: Int)
    }
}