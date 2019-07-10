package com.zimplifica.redipuntos.ui.adapters

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.zimplifica.domain.entities.Location
import com.zimplifica.domain.entities.Store
import com.zimplifica.redipuntos.R

class LocationAdapter(val callback : (Location) -> Unit) : RecyclerView.Adapter<LocationAdapter.LocationVH>(){
    var items : List<Store> = listOf()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): LocationVH {
        return LocationVH(LayoutInflater.from(p0.context).inflate(R.layout.location_row,p0,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: LocationVH, p1: Int) {
        val store = items[p1]
        p0.place.text = store.location.name
        val number = store.phoneNumber?:"No disponible"
        p0.phone.text = "Llamar: $number"
        if(store.phoneNumber.isNullOrEmpty()){
            p0.phoneLL.visibility = View.GONE
        }


        p0.phoneLL.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:"+store.phoneNumber)
            p0.itemView.context.startActivity(intent)
        }
        p0.itemView.setOnClickListener {
            callback(store.location)
        }
    }

    fun setStores(list : List<Store>){
        items = list
        notifyDataSetChanged()
    }


    class LocationVH(itemView : View): RecyclerView.ViewHolder(itemView){
        val place : TextView = itemView.findViewById(R.id.location_row_place)
        val phone : TextView = itemView.findViewById(R.id.location_row_phone)
        val schedule : TextView = itemView.findViewById(R.id.location_row_schedule)
        val phoneLL : LinearLayout = itemView.findViewById(R.id.linearLayout10)
        val scheduleLL : LinearLayout = itemView.findViewById(R.id.linearLayout11)
    }
}