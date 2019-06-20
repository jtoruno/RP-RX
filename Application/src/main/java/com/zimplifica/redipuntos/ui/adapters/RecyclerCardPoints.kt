package com.zimplifica.redipuntos.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zimplifica.domain.entities.PaymentMethod
import com.zimplifica.redipuntos.R

class RecyclerCardPoints: RecyclerView.Adapter<RecyclerCardPoints.CardViewHolder>(){
    private var paymentMethods = mutableListOf<PaymentMethod>()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CardViewHolder {
       val view = LayoutInflater.from(p0.context).inflate(R.layout.card_row_picker,p0,false)
        return CardViewHolder(view)
    }

    override fun getItemCount(): Int {
       return paymentMethods.size
    }

    override fun onBindViewHolder(p0: CardViewHolder, p1: Int) {
        val paymentMethod = paymentMethods[p1]
        p0.cardIssuer.text = paymentMethod.issuer.toUpperCase()
        p0.cardNumber.text = "**** " + paymentMethod.cardNumberWithMask
        if(paymentMethod.issuer.toLowerCase() == "amex"){
            p0.cardImage.setImageResource(R.drawable.amex)
        }
        if(paymentMethod.issuer.toLowerCase() == "visa"){
            p0.cardImage.setImageResource(R.drawable.visa2)
        }
        if(paymentMethod.issuer.toLowerCase() == "mastercard"){
            p0.cardImage.setImageResource(R.drawable.mastercard)
        }
        /*
        p0.itemView.setOnClickListener {
            callBack(paymentMethod)
        }*/
    }

    class CardViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val cardNumber : TextView = itemView.findViewById(R.id.cardNumtxtRecycler)
        val cardImage : ImageView = itemView.findViewById(R.id.cardImageImgrecycler)
        val cardIssuer : TextView = itemView.findViewById(R.id.cardTypetxtrecycler)
    }

    fun setPaymentMethods(list : List<PaymentMethod>){
        this.paymentMethods = list.toMutableList()
    }
}