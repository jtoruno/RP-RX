package com.zimplifica.redipuntos.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.zimplifica.domain.entities.Commerce
import com.zimplifica.redipuntos.R

class CommerceAdapter(val callback : (Commerce) -> Unit ) : RecyclerView.Adapter<CommerceAdapter.CommerceVH>(){
    private var items : List<Commerce> = listOf()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CommerceVH {
        return CommerceVH(LayoutInflater.from(p0.context).inflate(R.layout.commerce_row,p0,false))
    }

    override fun getItemCount(): Int {
         return items.size
    }

    override fun onBindViewHolder(p0: CommerceVH, p1: Int) {
        val commerce = items[p1]
        p0.name.text = commerce.name
        Picasso.get().load(commerce.posterImage).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(p0.image)
        p0.itemView.setOnClickListener {
            callback(commerce)
        }

    }

    fun setCommerces(list : List<Commerce>){
        this.items = list
        notifyDataSetChanged()
    }

    class CommerceVH(itemView : View) : RecyclerView.ViewHolder(itemView){
        val image : ImageView = itemView.findViewById(R.id.commerce_row_img)
        val name : TextView = itemView.findViewById(R.id.commerce_row_name)
    }
}