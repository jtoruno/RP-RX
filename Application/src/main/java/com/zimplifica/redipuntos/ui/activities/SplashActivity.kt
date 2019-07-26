package com.zimplifica.redipuntos.ui.activities

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
        this.viewModel.inputs.onCreate()
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

        compositeDisposable.add(this.viewModel.outputs.signedInAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()
            })
        compositeDisposable.add(this.viewModel.outputs.signedOutAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this, WalkThrough::class.java)
                startActivity(intent)
                finish()
            })
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
