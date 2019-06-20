package com.zimplifica.redipuntos.extensions

import android.support.v7.widget.RecyclerView
import android.view.View

interface OnItemClickListener {
    fun onItemClicked(position: Int, view: View)
}

fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {
    this.addOnChildAttachStateChangeListener(object: RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewDetachedFromWindow(p0: View) {
            p0.setOnClickListener(null)
        }

        override fun onChildViewAttachedToWindow(p0: View) {
            p0.setOnClickListener {
                val holder = getChildViewHolder(p0)
                onClickListener.onItemClicked(holder.adapterPosition, p0)
            }
        }
    })
}