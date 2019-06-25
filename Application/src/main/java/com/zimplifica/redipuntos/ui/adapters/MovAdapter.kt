package com.zimplifica.redipuntos.ui.adapters

import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zimplifica.domain.entities.Transaction
import com.zimplifica.redipuntos.R


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
}


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