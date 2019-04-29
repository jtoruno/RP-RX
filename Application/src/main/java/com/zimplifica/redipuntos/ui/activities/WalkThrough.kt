package com.zimplifica.redipuntos.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.ui.adapters.SliderAdapter

class WalkThrough : AppCompatActivity() {
    lateinit var mDots: Array<TextView>
    lateinit var mSlideViewPager: ViewPager
    lateinit var mDotLayout: LinearLayout
    var mcurrentPage: Int = 0
    lateinit var mNextBtn : Button
    lateinit var mBackBtn : Button
    lateinit var mFinishBtn: Button
    lateinit var closeBtn : Button
    lateinit var sliderAdapter: SliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walkthrough)

        //Buttons
        mNextBtn = findViewById(R.id.nextBtn)
        mBackBtn = findViewById(R.id.backBtb)
        mFinishBtn = findViewById(R.id.finishBtn)
        closeBtn = findViewById(R.id.CloseBtnWalktr)
        mSlideViewPager = findViewById(R.id.slideViewPager)
        mDotLayout = findViewById(R.id.dotsLayout)
        sliderAdapter = SliderAdapter(this)
        mSlideViewPager.adapter = sliderAdapter
        addDotsIndicator(0)
        mSlideViewPager.addOnPageChangeListener(viewListener)

        mNextBtn.setOnClickListener {
            mSlideViewPager.currentItem = mcurrentPage +1
        }
        mBackBtn.setOnClickListener {
            mSlideViewPager.currentItem = mcurrentPage -1
        }
    }

    fun addDotsIndicator(position: Int){
        mDots = Array(4){ TextView(this) }
        mDotLayout.removeAllViews()
        var i = 0
        while (i<mDots.size){
            mDots[i].text = Html.fromHtml("&#8226;")
            mDots[i].textSize = 35F
            mDots[i].setTextColor(resources.getColor(R.color.colorAccent))
            mDotLayout.addView(mDots[i])
            i++
        }
        if (mDots.isNotEmpty()){
            mDots[position].setTextColor(resources.getColor(android.R.color.white))
        }
    }

    val viewListener = object : ViewPager.OnPageChangeListener{
        override fun onPageScrollStateChanged(p0: Int) {}

        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

        override fun onPageSelected(p0: Int) {
            addDotsIndicator(p0)
            mcurrentPage = p0
            if(p0==0){
                mNextBtn.isEnabled = true
                mBackBtn.isEnabled = false
                mFinishBtn.isEnabled = false
                mBackBtn.visibility = View.INVISIBLE
                mFinishBtn.visibility = View.INVISIBLE
                mNextBtn.visibility =  View.VISIBLE
                mNextBtn.text = "Siguiente"
                mBackBtn.text = ""

            }else if (p0 == mDots.size -1){
                mNextBtn.isEnabled = true
                mBackBtn.isEnabled = true
                mBackBtn.visibility = View.VISIBLE
                mFinishBtn.visibility = View.VISIBLE

                //mNextBtn.setText("Continuar")
                mNextBtn.isEnabled = false
                mNextBtn.visibility =  View.INVISIBLE
                mFinishBtn.isEnabled = true
                mBackBtn.text = "Anterior"
            }
            else{
                mNextBtn.isEnabled = true
                mBackBtn.isEnabled = true
                mBackBtn.visibility = View.VISIBLE
                mNextBtn.visibility =  View.VISIBLE

                mFinishBtn.visibility = View.INVISIBLE
                mFinishBtn.isEnabled = false

                mNextBtn.text = "Siguiente"
                mBackBtn.text = "Anterior"
            }


        }

    }
}
