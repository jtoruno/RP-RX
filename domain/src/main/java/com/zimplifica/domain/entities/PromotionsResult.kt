package com.zimplifica.domain.entities

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Location(val name: String, val latitude: Double, val longitude: Double) : Serializable

class ShoppingHour(val open: Boolean, val openningHour: String,val closingHour: String) : Serializable

class Schedule(val sun: ShoppingHour, val mon: ShoppingHour,val tue: ShoppingHour,val wed: ShoppingHour,val thu: ShoppingHour,val fri: ShoppingHour,val sat: ShoppingHour) : Serializable

class Store(val id: String, val name: String,val  location: Location,val phoneNumber: String?, val schedule: Schedule?) : Serializable

class Coupon(val beforeDiscount: Double, val afterDiscount: Double) : Serializable

class Offer(val discount: Int) : Serializable

class Category(val id: String, val name: String,val posterImage: String) : Serializable

class Promotion(val id: String, val promotionType: String, val title: String, val description: String,val promotionImage: String,val commerceName: String,validFrom: String,validTo: String,
                val restrictions: String,val waysToUse: String,val stores: List<Store>,val coupon: Coupon?,val offer: Offer?,val website: String,val facebook: String,val whatsapp: String,
                val instagram: String): Serializable {

    var validFrom :String
    var validTo: String
    init {
        this.validFrom = formatLongDate(validFrom)
        this.validTo = formatLongDate(validTo)
    }

    private fun formatLongDate(dateValue : String) : String{
        val hourString = dateValue.split("T").last()
        val date = dateValue.split("T").first()
        val final = date.replace("-","/")
        return "$final $hourString"
    }
}

class Commerce(val commerceId: String,val name: String,val posterImage: String,val promotions: List<Promotion>,val website: String,val facebook: String,val whatsapp: String,val instagram: String,val category: String?,val stores: List<Store>) : Serializable

class CommercesResult(val commerces : List<Commerce>) : Serializable

