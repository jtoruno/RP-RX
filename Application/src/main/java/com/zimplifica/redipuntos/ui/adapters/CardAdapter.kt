package com.zimplifica.redipuntos.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.zimplifica.domain.entities.PaymentMethod
import com.zimplifica.redipuntos.R

class CardAdapter(val context : Context, val callBack : (PaymentMethod) -> Unit) : RecyclerView.Adapter<CardAdapter.CardVH>() {
    private var items : List<PaymentMethod> = listOf()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CardVH {
        return CardVH(LayoutInflater.from(p0.context).inflate(R.layout.custom_card_row,p0,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: CardVH, p1: Int) {
        val item = items[p1]
        p0.cardNumber.text = "**** **** **** " + item.cardNumberWithMask
        p0.cardissuer.text = item.issuer.toUpperCase()

        if(item.issuer.toLowerCase() == "amex"){
            p0.cardImage.setImageResource(R.drawable.amex)
            p0.cardNumber.text = "**** ****** *" + item.cardNumberWithMask
        }
        if(item.issuer.toLowerCase() == "visa"){
            p0.cardImage.setImageResource(R.drawable.visa2)
        }
        if(item.issuer.toLowerCase() == "mastercard"){
            p0.cardImage.setImageResource(R.drawable.mastercard)
        }
        if(item.issuer.toLowerCase() == "redipuntos"){
            p0.cardCloseBtn.visibility = View.GONE
            //p0.cardImage.background =  context.resources.getDrawable(android.R.color.black)
            p0.cardImage.setImageResource(R.drawable.cutmypic)
            p0.cardNumber.text = item.rewards.toString()
        }
        p0.cardCloseBtn.setOnClickListener {
            callBack(item)
        }

    }

    fun setPaymentMethods(list : List<PaymentMethod>){
        this.items = list.reversed()
        notifyDataSetChanged()
    }

    class CardVH(itemView : View) : RecyclerView.ViewHolder(itemView){
        var cardImage : ImageView = itemView.findViewById(R.id.card_row_img)
        var cardNumber : TextView = itemView.findViewById(R.id.card_row_number)
        var cardissuer : TextView = itemView.findViewById(R.id.card_row_issuer)
        var cardCloseBtn : ImageButton = itemView.findViewById(R.id.card_row_close)
    }
}