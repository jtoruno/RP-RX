package com.zimplifica.redipuntos.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.zimplifica.domain.entities.Category
import com.zimplifica.redipuntos.R

class CategoryAdapter(val callback : (Category) -> Unit) : androidx.recyclerview.widget.RecyclerView.Adapter<CategoryAdapter.CategoryVH>() {
    private var items : List<Category> = listOf()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CategoryVH {
        return CategoryVH(LayoutInflater.from(p0.context).inflate(R.layout.category_row,p0,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: CategoryVH, p1: Int) {
        val category = items[p1]
        p0.name.text = category.name
        Picasso.get().load(category.posterImage).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(p0.image)
        p0.itemView.setOnClickListener {
            callback(category)
        }
    }

    fun setCategories(list : List<Category>){
        this.items = list
        notifyDataSetChanged()
    }

    class CategoryVH(itemView : View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView){
        val image : ImageView = itemView.findViewById(R.id.category_row_img)
        val name : TextView = itemView.findViewById(R.id.category_row_name)
    }
}