package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.zimplifica.domain.entities.UserStateResult
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.SplashViewModel
import io.reactivex.android.schedulers.AndroidSchedulers

@RequiresActivityViewModel(SplashViewModel.ViewModel::class)
class SplashActivity : BaseActivity<SplashViewModel.ViewModel>() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        this.viewModel.inputs.onCreate()

        this.viewModel.outputs.splashAction()
            .subscribe {
                when(it){
                    UserStateResult.signedIn -> {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else -> {
                        val intent = Intent(this, WalkThrough::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
    }
}
