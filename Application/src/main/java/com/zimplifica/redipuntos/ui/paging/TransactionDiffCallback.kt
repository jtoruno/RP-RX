package com.zimplifica.redipuntos.ui.paging

import androidx.recyclerview.widget.DiffUtil
import com.zimplifica.domain.entities.Transaction

class TransactionDiffCallback(private val oldList : List<Transaction>, private val newList : List<Transaction>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id && oldList[oldItemPosition].date == newList[newItemPosition].date
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldTransaction = oldList[oldItemPosition]
        val newTransaction = newList[newItemPosition]
        return oldTransaction.id == newTransaction.id && oldTransaction.date == newTransaction.date
    }
}