package com.zimplifica.redipuntos.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zimplifica.domain.entities.ServerEvent
import com.zimplifica.redipuntos.R

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.NotificationVH>() {
    private var notifications : List<ServerEvent> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationVH {
        return NotificationVH(LayoutInflater.from(parent.context).inflate(R.layout.notifications_row,parent,false))
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: NotificationVH, position: Int) {
        val notification = notifications[position]
        holder.title.text = notification.title
        holder.description.text = notification.message
        holder.time.text = notification.createdAt

    }

    class NotificationVH(itemView : View): RecyclerView.ViewHolder(itemView){
        val title : TextView = itemView.findViewById(R.id.notification_row_title)
        val description : TextView = itemView.findViewById(R.id.notification_row_description)
        val time : TextView = itemView.findViewById(R.id.notification_row_time)
    }

    fun setNotifications(list : List<ServerEvent>){
        notifications = list
        notifyDataSetChanged()
    }
}