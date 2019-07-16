package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.MainViewModel
import io.reactivex.disposables.CompositeDisposable

@RequiresActivityViewModel(MainViewModel.ViewModel::class)
class MainActivity : BaseActivity<MainViewModel.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()

    lateinit var singInBtn : Button
    lateinit var signUpBtn : Button
    lateinit var helpBtn : ImageButton

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        singInBtn = findViewById(R.id.sign_in_btn)
        singInBtn.setOnClickListener {
            this.viewModel.inputs.signInButtonClicked()
        }
        signUpBtn = findViewById(R.id.sign_up_btn)
        signUpBtn.setOnClickListener {
            this.viewModel.inputs.signUpButtonClicked()
        }
        helpBtn = findViewById(R.id.imageButton)
        helpBtn.setOnClickListener { this.viewModel.inputs.helpButtonClicked() }

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

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
