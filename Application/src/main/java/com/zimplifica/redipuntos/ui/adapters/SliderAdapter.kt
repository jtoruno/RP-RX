package com.zimplifica.redipuntos.ui.adapters

import android.content.Context
import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.zimplifica.redipuntos.R

class SliderAdapter(val context: Context) : androidx.viewpager.widget.PagerAdapter() {
    var array = arrayListOf(R.string.walk1, R.string.walk2, R.string.walk3, R.string.walk4)
    var arraytext = arrayListOf(R.string.description1,R.string.description2, R.string.description3, R.string.description4)
    //var animations = arrayListOf(R.raw.confetti,R.raw.securitycheck, R.raw.servicespayment,R.raw.ballons)
    val images = arrayListOf(R.drawable.wt1,R.drawable.wt2,R.drawable.wt3,R.drawable.wt4)



    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1 as RelativeLayout

    }

    override fun getCount(): Int {
        return array.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = LayoutInflater.from(context)
        val view : View = layoutInflater.inflate(R.layout.walk_slide_layout, container, false)
        val header: TextView = view.findViewById(R.id.walk_title)
        val description: TextView = view.findViewById(R.id.walk_description)
        val image : ImageView = view.findViewById(R.id.walk_image)
        //val lottie : LottieAnimationView = view.findViewById(R.id.lottie_animation)
        image.setImageResource(images[position])
        header.setText(array[position])
        description.setText(arraytext[position])
        //lottie.setAnimation(animations[position])
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}