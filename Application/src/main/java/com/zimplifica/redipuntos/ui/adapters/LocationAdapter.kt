package com.zimplifica.redipuntos.ui.adapters

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.zimplifica.domain.entities.Location
import com.zimplifica.domain.entities.Schedule
import com.zimplifica.domain.entities.ShoppingHour
import com.zimplifica.domain.entities.Store
import com.zimplifica.redipuntos.R
import java.util.*

class LocationAdapter(val callback : (Location) -> Unit, val callback2 : (Schedule)-> Unit) : RecyclerView.Adapter<LocationAdapter.LocationVH>(){
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
        if(store.schedule == null){
            p0.schedule.text = "No disponible"
        }else{
            handleSchedule(store.schedule,p0)
        }
        p0.scheduleLL.setOnClickListener {
            if(store.schedule!=null){
                callback2(store.schedule!!)
            }
        }
    }

    private fun handleSchedule(schedule: Schedule?,p0 : LocationVH){
        if(schedule== null){
            return
        }
        val weekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        var shoppingDay : ShoppingHour = ShoppingHour(0,false,"","")
        when(weekDay){
            1 -> {shoppingDay = schedule.sun}
            2 -> {shoppingDay = schedule.mon}
            3 -> {shoppingDay = schedule.tue}
            4 -> {shoppingDay = schedule.wed}
            5 -> {shoppingDay = schedule.thu}
            6 -> {shoppingDay = schedule.fri}
            7 -> {shoppingDay = schedule.sat}
        }
        p0.schedule.text = "Cerrado"
        p0.scheduleIcon.setColorFilter(p0.itemView.context.getColor(R.color.red))


        val currentHour =  Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)

        val opennigTime = shoppingDay.openningHour.split(":")
        val closingTime = shoppingDay.closingHour.split(":")
        val openingHour = opennigTime.first().toInt()
        val closingHour = closingTime.first().toInt()
        if(openingHour <= currentHour && currentHour <= closingHour){
            if(currentHour == openingHour || currentHour == closingHour){
                val openingMinute = opennigTime.last().toInt()
                val closingMinute = closingTime.last().toInt()
                if(openingMinute <= currentMinute && currentMinute < closingMinute){
                    p0.schedule.text = "Cerrará pronto"
                    p0.scheduleIcon.setColorFilter(p0.itemView.context.getColor(R.color.pendingColor))
                }
            }else{
                p0.schedule.text = "Abierto ahora"
                p0.scheduleIcon.setColorFilter(p0.itemView.context.getColor(R.color.pendingColor))
            }
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
        val scheduleIcon : ImageView = itemView.findViewById(R.id.location_row_schedule_icon)
    }
}