package com.zimplifica.redipuntos.ui.activities

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.snackbar.Snackbar
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.SplashViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.concurrent.TimeUnit

@RequiresActivityViewModel(SplashViewModel.ViewModel::class)
class SplashActivity : BaseActivity<SplashViewModel.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        splash_back_btn.visibility = View.GONE
        splash_retry_btn.visibility = View.GONE
        splash_loading.visibility = View.GONE
        splash_retry_btn.setOnClickListener {
            viewModel.inputs.retryButtonPressed()
        }
        splash_back_btn.setOnClickListener {
            viewModel.inputs.backButtonPressed()
        }
        splash_animation.addAnimatorListener(object: Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                this@SplashActivity.viewModel.inputs.finishAnimation()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })

        compositeDisposable.add(this.viewModel.outputs.didFinishWithError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                splash_back_btn.visibility = View.VISIBLE
                splash_retry_btn.visibility = View.VISIBLE
                Log.e("SpashActivity", "Error with Splash")
                showSnackBar()
            })

        compositeDisposable.add(this.viewModel.outputs.finishLoadingUserInfo().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()
            })
        /*
        compositeDisposable.add(this.viewModel.outputs.signedOutAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this, WalkThrough::class.java)
                startActivity(intent)
                finish()
            })*/

        compositeDisposable.add(viewModel.outputs.retryLoading().observeOn(AndroidSchedulers.mainThread()).subscribe {
            splash_retry_btn.isEnabled = !it
            if(splash_retry_btn.visibility == View.VISIBLE){
                if(it){
                    splash_loading.visibility = View.VISIBLE
                }else{
                    splash_loading.visibility = View.GONE
                }
            }
        })


        compositeDisposable.add(viewModel.outputs.backToWelcome().observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            })

        this.viewModel.inputs.onCreate()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    private fun showSnackBar(){
        val parentView = findViewById<View>(android.R.id.content)
        val snackbar = Snackbar.make(parentView,"Tenemos problemas de comunicación, asegurate de tener conexión a internet y luego intenta de nuevo.",Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(getColor(R.color.red))
        val params = snackbar.view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        snackbar.view.layoutParams = params
        snackbar.show()

    }
}
