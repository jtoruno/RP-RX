package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.SignInFailureViewModel
import io.reactivex.android.schedulers.AndroidSchedulers

@RequiresActivityViewModel(SignInFailureViewModel.ViewModel::class)
class SignInFailureActivity : BaseActivity<SignInFailureViewModel.ViewModel>() {

    lateinit var forgotBtn : Button
    lateinit var signUpBtn : Button

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_failure)

        this.supportActionBar?.title = "Iniciar Sesión"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        forgotBtn = findViewById(R.id.forgot_fail_btn)
        signUpBtn = findViewById(R.id.sign_up_fail_btn)

        forgotBtn.setOnClickListener { this.viewModel.inputs.forgotPasswordButtonPressed() }
        signUpBtn.setOnClickListener { this.viewModel.inputs.signUpButtonPressed() }

        this.viewModel.outputs.signUpButton().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this, TakePhoneActivity::class.java)
                startActivity(intent)
                finish()
            }
        this.viewModel.outputs.forgotPasswordButton().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this, ForgotPasswordActivity::class.java)
                startActivity(intent)
                finish()
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
