package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.libs.utils.SharedPreferencesUtils
import com.zimplifica.redipuntos.ui.adapters.SliderAdapter
import com.zimplifica.redipuntos.viewModels.WalkThroughViewModel
import io.reactivex.disposables.CompositeDisposable


@RequiresActivityViewModel(WalkThroughViewModel.ViewModel::class)
class WalkThrough : BaseActivity<WalkThroughViewModel.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()
    lateinit var mDots: Array<TextView>
    lateinit var mSlideViewPager: androidx.viewpager.widget.ViewPager
    lateinit var mDotLayout: LinearLayout
    var mcurrentPage: Int = 0
    lateinit var sliderAdapter: SliderAdapter

    lateinit var singInBtn : Button
    lateinit var signUpBtn : Button
    lateinit var helpBtn : ImageButton

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walkthrough)
        /*
        val flag = SharedPreferencesUtils.getBooleanInSp(this, "walkthrough")
        if(flag){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }*/

        singInBtn = findViewById(R.id.walk_sign_in_btn)
        signUpBtn = findViewById(R.id.walk_sign_up_btn)
        helpBtn = findViewById(R.id.walk_help_btn)

        //Buttons
        mSlideViewPager = findViewById(R.id.slideViewPager)
        mDotLayout = findViewById(R.id.dotsLayout)
        sliderAdapter = SliderAdapter(this)
        mSlideViewPager.adapter = sliderAdapter
        addDotsIndicator(0)
        mSlideViewPager.addOnPageChangeListener(viewListener)


        singInBtn.setOnClickListener {
            this.viewModel.inputs.signInButtonClicked()
        }
        signUpBtn.setOnClickListener {
            this.viewModel.inputs.signUpButtonClicked()
        }
        helpBtn.setOnClickListener {
            this.viewModel.inputs.helpButtonClicked()
        }

        compositeDisposable.add(this.viewModel.outputs.startSignUpActivity().subscribe {
            val intent = Intent(this, TakePhoneActivity::class.java)
            startActivity(intent)
        })
        compositeDisposable.add(this.viewModel.outputs.startSignInActivity().subscribe {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        })
        compositeDisposable.add(this.viewModel.outputs.startHelpActivity().subscribe{
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
        })
    }


    fun addDotsIndicator(position: Int){
        mDots = Array(4){ TextView(this) }
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

    val viewListener = object : androidx.viewpager.widget.ViewPager.OnPageChangeListener{
        override fun onPageScrollStateChanged(p0: Int) {}

        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

        override fun onPageSelected(p0: Int) {
            addDotsIndicator(p0)
            mcurrentPage = p0
            /*
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
            }*/
        }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
