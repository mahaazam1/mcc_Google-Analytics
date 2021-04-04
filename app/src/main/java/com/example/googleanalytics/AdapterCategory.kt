package com.example.googleanalytics

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.category_item.view.*

class AdapterCategory(val activity: Activity,
                   val data: ArrayList<ModelCategory>,
                   val onClick: OnClickItem
) : RecyclerView.Adapter<AdapterCategory.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var id = itemView.id
        var categoryName = itemView.btn_category

        var categoryCard = itemView.category_card
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterCategory.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category =data[position]
        holder.categoryName.text = category.name
        holder.categoryName.setOnClickListener {
            Toast.makeText(activity, "adapter ${category.id}", Toast.LENGTH_LONG).show()
            onClick.onClick(category)
        }
    }


    interface OnClickItem {
        fun onClick(modelCategory:ModelCategory)
    }
}
