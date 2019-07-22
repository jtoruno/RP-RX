package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.LaunchViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

@RequiresActivityViewModel(LaunchViewModel.ViewModel::class)
class LaunchActivity : BaseActivity<LaunchViewModel.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        this.viewModel.inputs.onCreate()

        compositeDisposable.add(this.viewModel.outputs.nextScreen().delay(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this@LaunchActivity,HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()
            })
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
