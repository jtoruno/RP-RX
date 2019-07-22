package com.zimplifica.redipuntos.ui.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.zimplifica.domain.entities.Transaction
import com.zimplifica.redipuntos.R
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection
import com.amulyakhare.textdrawable.TextDrawable
import com.zimplifica.domain.entities.TransactionStatus


class MovSection internal constructor( internal val title: String, internal val list : List<Transaction>,internal val callback : (Transaction) -> Unit) :
    StatelessSection(R.layout.movement_header,R.layout.movement_row) {
    override fun getContentItemsTotal(): Int {
        return list.size
    }

    override fun onBindItemViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder?, position: Int) {
        val itemHolder = holder as ItemViewHolder
        val context = itemHolder.itemView.context
        val transaction = list[position]
        itemHolder.vendorName.text = transaction.transactionDetail.vendorName
        val initials = transaction.transactionDetail.vendorName
            .split(' ')
            .mapNotNull { it.firstOrNull()?.toString() }
            .reduce { acc, s -> acc + s }
        val drawable = TextDrawable.builder()
            .beginConfig()
            .width(70)
            .height(70)
            .bold()
            .endConfig()
            .buildRound(initials,context.getColor(R.color.colorPrimaryLight))
        itemHolder.image.setImageDrawable(drawable)
        itemHolder.type.text = "Pago en Sitio"
        itemHolder.hour.text = transaction.time
        itemHolder.amount.text = "₡ "+String.format("%,.2f", transaction.total)

        itemHolder.rewards.text = "+ ₡ "+String.format("%,.2f", transaction.rewards)

        when(transaction.status){
            TransactionStatus.success -> {
                itemHolder.state.text = "Pago exitoso"
                itemHolder.state.setTextColor(context.resources.getColor(R.color.customGreen,null))
            }
            TransactionStatus.pending -> {
                itemHolder.state.text = "Pago pendiente"
                itemHolder.state.setTextColor(context.resources.getColor(R.color.pendingColor,null))
                itemHolder.rewards.visibility = View.GONE
            }
            else -> {
                itemHolder.state.text = "Pago erroneo"
                itemHolder.state.setTextColor(context.resources.getColor(R.color.red,null))
                itemHolder.rewards.visibility = View.GONE
            }
        }
        itemHolder.itemView.setOnClickListener {
            callback(transaction)
        }

    }

    override fun getItemViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return ItemViewHolder(view)
    }

    override fun getHeaderViewHolder(view: View): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }

    override fun onBindHeaderViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder?) {
        val headerHolder = holder as HeaderViewHolder
        headerHolder.header.text = title
    }

    private class ItemViewHolder(rootView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(rootView){
        val image : ImageView = rootView.findViewById(R.id.mov_row_img)
        val vendorName : TextView = rootView.findViewById(R.id.mov_row_name)
        val type : TextView = rootView.findViewById(R.id.mov_row_type)
        val state : TextView = rootView.findViewById(R.id.mov_row_state)
        val amount : TextView = rootView.findViewById(R.id.mov_row_amount)
        val hour : TextView = rootView.findViewById(R.id.mov_row_hour)
        val rewards : TextView = rootView.findViewById(R.id.mov_row_rewards)
    }
    private class HeaderViewHolder(rootView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(rootView){
        var header : TextView = rootView.findViewById(R.id.mov_header_date)
    }
}