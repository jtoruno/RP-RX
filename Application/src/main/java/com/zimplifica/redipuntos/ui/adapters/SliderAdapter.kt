package com.zimplifica.redipuntos.ui.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.zimplifica.redipuntos.R

class SliderAdapter(val context: Context) : PagerAdapter() {
    var array = arrayListOf(R.string.walk1, R.string.walk2, R.string.walk3, R.string.walk4)
    var arraytext = arrayListOf(R.string.description1,R.string.description2, R.string.description3, R.string.description4)



    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1 as RelativeLayout

    }

    override fun getCount(): Int {
        return array.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = LayoutInflater.from(context)
        val view : View = layoutInflater.inflate(R.layout.slide_layout, container, false)
        val header: TextView = view.findViewById(R.id.headerTxtView)
        val description: TextView = view.findViewById(R.id.descriptionTxt)

        header.setText(array[position])
        description.setText(arraytext[position])
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}