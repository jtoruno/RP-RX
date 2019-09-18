package com.zimplifica.redipuntos.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zimplifica.domain.entities.PaymentMethod
import com.zimplifica.redipuntos.R

class RecyclerCardPoints: androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object {
         val PAYMENT_METHOD = 5
         val ADD_VIEW = 6
    }
    private var paymentMethods = mutableListOf<PaymentMethod>()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return when(p1){
            PAYMENT_METHOD -> {
                val view = LayoutInflater.from(p0.context).inflate(R.layout.card_row_picker,p0,false)
                CardViewHolder(view)
            }
            ADD_VIEW -> {
                val view = LayoutInflater.from(p0.context).inflate(R.layout.card_add_row,p0,false)
                AddCardViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(p0.context).inflate(R.layout.card_row_picker,p0,false)
                CardViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val identifier = paymentMethods[position]
        return if (identifier.cardId == "ADD_VIEW"){
            ADD_VIEW
        }else{
            PAYMENT_METHOD
        }
    }

    override fun getItemCount(): Int {
       return paymentMethods.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            PAYMENT_METHOD -> {
                val p0 = holder as CardViewHolder
                val paymentMethod = paymentMethods[position]
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
            }
        }
    }

    /*
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
    }*/

    class CardViewHolder(itemView : View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView){
        val cardNumber : TextView = itemView.findViewById(R.id.cardNumtxtRecycler)
        val cardImage : ImageView = itemView.findViewById(R.id.cardImageImgrecycler)
        val cardIssuer : TextView = itemView.findViewById(R.id.cardTypetxtrecycler)
    }

    class AddCardViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

    }

    fun setPaymentMethods(list : List<PaymentMethod>){
        paymentMethods.clear()
        this.paymentMethods = list.toMutableList()
        paymentMethods.add(PaymentMethod("ADD_VIEW","","","",0.0,false))
    }
}