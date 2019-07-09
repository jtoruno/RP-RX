package com.zimplifica.redipuntos.ui.adapters

import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.zimplifica.domain.entities.Promotion
import com.zimplifica.redipuntos.R



class PromotionAdapter(val callback: (Promotion) -> Unit) : RecyclerView.Adapter<PromotionAdapter.PromotionVH>() {

    private var items : List<Promotion> = listOf()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PromotionVH {
        return PromotionVH(LayoutInflater.from(p0.context).inflate(R.layout.promotion_row,p0,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: PromotionVH, p1: Int) {
        val promotion = items[p1]
        Picasso.get().load(promotion.promotionImage).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(p0.image)
        p0.title.text = promotion.title
        p0.description.text = promotion.description
        if(promotion.offer!=null){
            if (promotion.offer?.discount == 0){
                p0.amount1.visibility = View.GONE
                p0.amount2.visibility = View.GONE
                p0.coupon.visibility = View.GONE
            }
            else{
                p0.coupon.text = "${promotion.offer?.discount.toString()}%"
                p0.amount1.visibility = View.GONE
                p0.amount2.visibility = View.GONE
            }
        }else{
            if (promotion.coupon!=null){
                p0.coupon.visibility = View.GONE
                p0.amount1.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG or p0.amount1.paintFlags
                p0.amount1.text = "₡"+String.format("%,.2f", promotion.coupon?.beforeDiscount)
                p0.amount2.text = "₡"+String.format("%,.2f", promotion.coupon?.afterDiscount)
            }
            else{
                p0.amount1.visibility = View.GONE
                p0.amount2.visibility = View.GONE
                p0.coupon.visibility = View.GONE
            }
        }
        p0.itemView.setOnClickListener {
            callback(promotion)
        }
    }

    fun setPromotions(list: List<Promotion>){
        items = list
        notifyDataSetChanged()
    }

    class PromotionVH(itemView : View) : RecyclerView.ViewHolder(itemView){
        val image : ImageView = itemView.findViewById(R.id.promotion_row_image)
        val title : TextView = itemView.findViewById(R.id.promotion_row_title)
        val description : TextView = itemView.findViewById(R.id.promotion_row_description)
        val amount1 : TextView = itemView.findViewById(R.id.promotion_row_amount1)
        val amount2 : TextView = itemView.findViewById(R.id.promotion_row_amount2)
        val coupon : TextView = itemView.findViewById(R.id.promotion_row_coupon)
    }
}