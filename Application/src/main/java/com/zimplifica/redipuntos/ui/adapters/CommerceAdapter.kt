package com.zimplifica.redipuntos.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.zimplifica.domain.entities.Commerce
import com.zimplifica.redipuntos.R

class CommerceAdapter(val callback : (Commerce) -> Unit ) : androidx.recyclerview.widget.RecyclerView.Adapter<CommerceAdapter.CommerceVH>(){
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
        p0.cashBack.text = commerce.cashback.toString() + "%"
        p0.favoriteImg.setOnClickListener {
            p0.favoriteImg.setImageResource(R.drawable.ic_favorite_black_24dp)
            p0.favoriteImg.setColorFilter(p0.itemView.context.getColor(R.color.red))
        }
        Picasso.get().load(commerce.posterImage).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(p0.image)
        p0.itemView.setOnClickListener {
            callback(commerce)
        }

    }

    fun setCommerces(list : List<Commerce>){
        this.items = list
        notifyDataSetChanged()
    }

    class CommerceVH(itemView : View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView){
        val image : ImageView = itemView.findViewById(R.id.commerce_row_img)
        val name : TextView = itemView.findViewById(R.id.commerce_row_name)
        val cashBack : TextView = itemView.findViewById(R.id.commerce_row_cash_back)
        val favoriteImg : ImageView = itemView.findViewById(R.id.commerce_row_favorite)
    }
}