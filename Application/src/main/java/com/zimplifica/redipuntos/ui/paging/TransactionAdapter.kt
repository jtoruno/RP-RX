package com.zimplifica.redipuntos.ui.paging

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zimplifica.domain.entities.Transaction
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.zimplifica.domain.entities.TransactionStatus
import com.zimplifica.redipuntos.R


class TransactionAdapter(val callback : (Transaction) -> Unit) : RecyclerView.Adapter<BaseViewHolder>() {

    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private val VIEW_TYPE_HEADER = 2


    private var isLoaderVisible = false
    var list : MutableList<Transaction> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when(viewType){
            VIEW_TYPE_NORMAL -> {
                 ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.movement_row, parent, false))
            }
            VIEW_TYPE_LOADING -> {
                 FooterHolder(LayoutInflater.from(parent.context).inflate(R.layout.mov_loading, parent, false))
            }
            else -> FooterHolder(LayoutInflater.from(parent.context).inflate(R.layout.mov_loading, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == list.size - 1){
                VIEW_TYPE_LOADING
            } else{
                if(list[position].id == "header"){
                    VIEW_TYPE_HEADER
                }else{
                    VIEW_TYPE_NORMAL
                }
            }
        } else {
            if(list[position].id == "header"){
                VIEW_TYPE_HEADER
            }else{
                VIEW_TYPE_NORMAL
            }
        }
    }


    override fun getItemCount(): Int {
        return if (list == null) 0 else list.size
    }

    fun add(response: Transaction) {
        list.add(response)
        notifyItemInserted(list.size - 1)
    }

    fun addAll(postItems: List<Transaction>) {
        for (response in postItems) {
            add(response)
        }
        notifyDataSetChanged()
    }

    private fun remove(postItems: Transaction) {
        val position = list.indexOf(postItems)
        if (position > -1) {
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun addLoading() {
        isLoaderVisible = true
        add(Transaction())
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position = list.size - 1
        val item = getItem(position)
        if (item != null) {
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    fun getItem(position: Int): Transaction {
        return list[position]
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
        val context = holder.itemView.context
        when(holder.itemViewType){
            VIEW_TYPE_NORMAL -> {
                val itemHolder = holder as ViewHolder
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
            VIEW_TYPE_LOADING -> {
                val holderType = holder as FooterHolder

            }
        }
    }

    class ViewHolder(itemView : View) : BaseViewHolder(itemView){
        val image : ImageView = itemView.findViewById(R.id.mov_row_img)
        val vendorName : TextView = itemView.findViewById(R.id.mov_row_name)
        val type : TextView = itemView.findViewById(R.id.mov_row_type)
        val state : TextView = itemView.findViewById(R.id.mov_row_state)
        val amount : TextView = itemView.findViewById(R.id.mov_row_amount)
        val hour : TextView = itemView.findViewById(R.id.mov_row_hour)
        val rewards : TextView = itemView.findViewById(R.id.mov_row_rewards)


        override fun clear() {
        }

        override fun onBind(position: Int) {
            super.onBind(position)
        }

    }

    class FooterHolder(itemView : View) : BaseViewHolder(itemView){
        val mProgressBar : ProgressBar = itemView.findViewById(R.id.mov_loading_progressBar)
        override fun clear() {
        }
    }

    class HeaderHolder(itemView: View) : BaseViewHolder(itemView){
        val date : TextView = itemView.findViewById(R.id.mov_header_date)
        override fun clear() {
        }
    }
}