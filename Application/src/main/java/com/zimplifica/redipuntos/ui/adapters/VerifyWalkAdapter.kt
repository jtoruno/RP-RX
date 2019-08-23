package com.zimplifica.redipuntos.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.airbnb.lottie.LottieAnimationView
import com.zimplifica.redipuntos.R

class VerifyWalkAdapter(val context : Context) : PagerAdapter() {

    private val titles = arrayListOf(R.string.verify_walk_title1,R.string.verify_walk_title2,R.string.verify_walk_title3,R.string.verify_walk_title4,R.string.verify_walk_title5)
    private val descriptions = arrayListOf(R.string.verify_walk_descrip1,R.string.verify_walk_descrip2,R.string.verify_walk_descrip3,R.string.verify_walk_descrip4,R.string.verify_walk_descrip5)
    private val animations = arrayListOf(R.raw.emoji,R.raw.scan,R.raw.lamp,R.raw.scanner,R.raw.server)

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as RelativeLayout
    }

    override fun getCount(): Int {
        return titles.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = LayoutInflater.from(context)
        val view : View = layoutInflater.inflate(R.layout.slide_layout, container, false)
        val header: TextView = view.findViewById(R.id.headerTxtView)
        val description: TextView = view.findViewById(R.id.descriptionTxt)
        val lottie : LottieAnimationView = view.findViewById(R.id.lottie_animation)

        header.setText(titles[position])
        description.setText(descriptions[position])
        lottie.setAnimation(animations[position])
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}