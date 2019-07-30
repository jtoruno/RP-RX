package com.zimplifica.redipuntos.ui.paging

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.zimplifica.domain.entities.Transaction

abstract class BaseViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    private var mCurrentPosition: Int = 0

    protected abstract fun clear()

    open fun onBind(position: Int) {
        mCurrentPosition = position
        clear()
    }

    fun getCurrentPosition(): Int {
        return mCurrentPosition
    }
}