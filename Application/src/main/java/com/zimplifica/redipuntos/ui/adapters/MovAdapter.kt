package com.zimplifica.redipuntos.ui.adapters

import android.annotation.SuppressLint
import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zimplifica.domain.entities.Transaction
import com.zimplifica.redipuntos.R
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection
import android.widget.Toast
import com.amulyakhare.textdrawable.TextDrawable
import com.zimplifica.domain.entities.TransactionStatus



/*
enum class RowType{
    HEADER,ITEM
}

data class ItemRow(var type : RowType,var transaction : Transaction?, var header : String?)

class DefaultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val viewMap: MutableMap<Int, View> = HashMap()

    init {
        findViewItems(itemView)
    }

    fun setText(@IdRes id: Int, text: String) {
        val view = (viewMap[id] ?: throw IllegalArgumentException("View for $id not found")) as? TextView ?: throw IllegalArgumentException("View for $id is not a TextView")
        view.text = text
    }

    fun getImage(@IdRes id: Int): ImageView {
        return (viewMap[id] ?: throw IllegalArgumentException("View for $id not found")) as? ImageView ?: throw IllegalArgumentException("View for $id is not a ImageView")
    }

    private fun findViewItems(itemView: View) {
        addToMap(itemView)
        if (itemView is ViewGroup) {
            val childCount = itemView.childCount
            (0 until childCount)
                .map { itemView.getChildAt(it) }
                .forEach { findViewItems(it) }
        }
    }

    private fun addToMap(itemView: View) {
        if (itemView.id == View.NO_ID) {
            itemView.id = View.generateViewId()
        }
        viewMap.put(itemView.id, itemView)
    }
}*/

class MovSection internal constructor( internal val title: String, internal val list : List<Transaction>,internal val callback : (Transaction) -> Unit) :
    StatelessSection(R.layout.movement_header,R.layout.movement_row) {
    override fun getContentItemsTotal(): Int {
        return list.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val itemHolder = holder as ItemViewHolder
        val context = itemHolder.itemView.context
        val transaction = list[position]
        itemHolder.vendorName.text = transaction.transactionDetail.sitePaymentItem?.vendorName?:""
        val initials = (transaction.transactionDetail.sitePaymentItem?.vendorName?:"")
            .split(' ')
            .mapNotNull { it.firstOrNull()?.toString() }
            .reduce { acc, s -> acc + s }
        val drawable = TextDrawable.builder()
            .beginConfig()
            .width(80)
            .bold()
            .height(80)
            .endConfig()
            .buildRound(initials,context.getColor(R.color.colorPrimaryLight))
        itemHolder.image.setImageDrawable(drawable)
        itemHolder.type.text = "Pago en Sitio"
        itemHolder.hour.text = transaction.time
        itemHolder.amount.text = "₡ "+String.format("%,.2f", transaction.total)
        when(transaction.status){
            TransactionStatus.success -> {
                itemHolder.state.text = "Pago exitoso"
                itemHolder.state.setTextColor(context.resources.getColor(R.color.customGreen,null))
            }
            TransactionStatus.pending -> {
                itemHolder.state.text = "Pago pendiente"
                itemHolder.state.setTextColor(context.resources.getColor(R.color.pendingColor,null))
            }
            else -> {
                itemHolder.state.text = "Pago erroneo"
                itemHolder.state.setTextColor(context.resources.getColor(R.color.red,null))
            }
        }
        itemHolder.itemView.setOnClickListener {
            callback(transaction)
        }

    }

    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        return ItemViewHolder(view)
    }

    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
        val headerHolder = holder as HeaderViewHolder
        headerHolder.header.text = title
    }

    private class ItemViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView){
        val image : ImageView = rootView.findViewById(R.id.mov_row_img)
        val vendorName : TextView = rootView.findViewById(R.id.mov_row_name)
        val type : TextView = rootView.findViewById(R.id.mov_row_type)
        val state : TextView = rootView.findViewById(R.id.mov_row_state)
        val amount : TextView = rootView.findViewById(R.id.mov_row_amount)
        val hour : TextView = rootView.findViewById(R.id.mov_row_hour)
    }
    private class HeaderViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView){
        var header : TextView = rootView.findViewById(R.id.mov_header_date)
    }

}
/*
class MovAdapter(private var list : MutableList<ItemRow>) : RecyclerView.Adapter<DefaultViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DefaultViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)

        val inflatedView : View = when (p1) {
            RowType.ITEM.ordinal -> layoutInflater.inflate(R.layout.movement_row, p0,false)
            else -> layoutInflater.inflate(R.layout.movement_header, p0,false)
        }
        return DefaultViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(p0: DefaultViewHolder, p1: Int) {
        val row = list[p1]
        if(row.type == RowType.ITEM){
            val transaction = row.transaction
        }else{
            row.header?.let { p0.setText(R.id.mov_header_date,it) }
        }
    }

    override fun getItemViewType(position: Int): Int = list[position].type.ordinal
}

/*
private class ContactsSection internal constructor(internal val title: String, internal val list: List<String>) :
    StatelessSection(
        SectionParameters.builder()
            .itemResourceId(R.layout.section_ex1_item)
            .headerResourceId(R.layout.section_ex1_header)
            .build()
    ) {

    override fun getContentItemsTotal(): Int {
        return list.size
    }

    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        return ItemViewHolder(view)
    }

    fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as ItemViewHolder

        val name = list[position]

        itemHolder.tvItem.text = name
        itemHolder.imgItem.setImageResource(if (name.hashCode() % 2 == 0) R.drawable.ic_face_black_48dp else R.drawable.ic_tag_faces_black_48dp)

        itemHolder.rootView.setOnClickListener {
            Toast.makeText(
                getContext(),
                String.format(
                    "Clicked on position #%s of Section %s",
                    sectionAdapter.getPositionInSection(itemHolder.adapterPosition),
                    title
                ),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }

    fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        val headerHolder = holder as HeaderViewHolder

        headerHolder.tvTitle.text = title
    }
}

private class HeaderViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {

    private val tvTitle: TextView

    init {

        tvTitle = view.findViewById(R.id.tvTitle)
    }
}

private class ItemViewHolder internal constructor(private val rootView: View) : RecyclerView.ViewHolder(rootView) {
    private val imgItem: ImageView
    private val tvItem: TextView

    init {
        imgItem = rootView.findViewById(R.id.imgItem)
        tvItem = rootView.findViewById(R.id.tvItem)
    }
}*/