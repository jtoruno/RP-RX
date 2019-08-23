package com.zimplifica.redipuntos.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.ui.adapters.SliderAdapter
import com.zimplifica.redipuntos.ui.adapters.VerifyWalkAdapter

class VerifyWalkThActivity : AppCompatActivity() {

    lateinit var mDots: Array<TextView>
    lateinit var mSlideViewPager: ViewPager
    lateinit var mDotLayout: LinearLayout
    private var mcurrentPage: Int = 0
    lateinit var mNextBtn : Button
    lateinit var mBackBtn : Button
    lateinit var mFinishBtn: Button
    lateinit var sliderAdapter: VerifyWalkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_walkth)
        supportActionBar?.title = "Verificar Identidad"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        mNextBtn = findViewById(R.id.verify_nextBtn)
        mBackBtn = findViewById(R.id.verify_backBtb)
        mFinishBtn = findViewById(R.id.verify_finishBtn)
        mSlideViewPager = findViewById(R.id.verify_walk_view_pager)
        mDotLayout = findViewById(R.id.verify_dotsLayout)
        sliderAdapter = VerifyWalkAdapter(this)
        mSlideViewPager.adapter = sliderAdapter
        addDotsIndicator(0)
        mSlideViewPager.addOnPageChangeListener(viewListener)

        mFinishBtn.setOnClickListener {
            val intent = Intent(this, JumioActivity::class.java)
            startActivity(intent)
            finish()
        }
        mNextBtn.setOnClickListener {
            mSlideViewPager.currentItem = mcurrentPage +1
        }
        mBackBtn.setOnClickListener {
            mSlideViewPager.currentItem = mcurrentPage -1
        }
    }

    private fun addDotsIndicator(position: Int){
        mDots = Array(5){ TextView(this) }
        mDotLayout.removeAllViews()
        var i = 0
        while (i<mDots.size){
            mDots[i].text = Html.fromHtml("&#8226;")
            mDots[i].textSize = 35F
            mDots[i].setTextColor(resources.getColor(android.R.color.white))
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(10, 0, 10, 0)
            mDots[i].layoutParams = params
            mDotLayout.addView(mDots[i])
            i++
        }
        if (mDots.isNotEmpty()){
            mDots[position].setTextColor(resources.getColor(R.color.colorAccent))
        }
    }

    private val viewListener = object : ViewPager.OnPageChangeListener{
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }
}
