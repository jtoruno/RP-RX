package com.zimplifica.redipuntos.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.FirstVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

@RequiresActivityViewModel(FirstVM.ViewModel::class)
class FirstActivity : BaseActivity<FirstVM.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        compositeDisposable.add(this.viewModel.outputs.signedInAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)
                finishAffinity()
            })
        compositeDisposable.add(this.viewModel.outputs.signedOutAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this, WalkThrough::class.java)
                startActivity(intent)
                finish()
            })
        viewModel.inputs.onCreate()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
