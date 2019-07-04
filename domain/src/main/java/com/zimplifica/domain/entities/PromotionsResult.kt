package com.zimplifica.domain.entities

import java.text.SimpleDateFormat
import java.util.*

class Location(val name: String, val latitude: Double, val longitude: Double)

class ShoppingHour(val open: Boolean, val openningHour: String,val closingHour: String)

class Schedule(val sun: ShoppingHour, val mon: ShoppingHour,val tue: ShoppingHour,val wed: ShoppingHour,val thu: ShoppingHour,val fri: ShoppingHour,val sat: ShoppingHour)

class Store(val id: String, val name: String,val  location: Location,val phoneNumber: String?, val schedule: Schedule?)

class Coupon(val beforeDiscount: Double, val afterDiscount: Double)

class Offer(val discount: Int)

class Category(val id: String, val name: String,val posterImage: String)

class Promotion(val id: String, val promotionType: String, val title: String, val description: String,val promotionImage: String,val commerceName: String,validFrom: String,validTo: String,
                val restrictions: String,val waysToUse: String,val stores: List<Store>,val coupon: Coupon?,val offer: Offer?,val website: String,val facebook: String,val whatsapp: String,
                val instagram: String){
    var validFrom :String
    var validTo: String
    init {
        val dateNow = Date()
        dateNow.time = validFrom.toLong()
        this.validFrom =  SimpleDateFormat("dd/MM/yyyy HH:mm").format(dateNow)
        dateNow.time = validTo.toLong()
        this.validTo = SimpleDateFormat("dd/MM/yyyy HH:mm").format(dateNow)
    }
}

class Commerce(val commerceId: String,val name: String,val posterImage: String,val promotions: List<Promotion>,val website: String,val facebook: String,val whatsapp: String,val instagram: String,val category: String?,val stores: List<Store>)

class CommercesResult(val commerces : List<Commerce>)

