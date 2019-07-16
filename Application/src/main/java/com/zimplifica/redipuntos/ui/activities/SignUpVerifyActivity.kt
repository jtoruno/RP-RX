package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.SignUpVerifyViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable


@RequiresActivityViewModel(SignUpVerifyViewModel.ViewModel::class)
class SignUpVerifyActivity : BaseActivity<SignUpVerifyViewModel.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()

    private var userName = ""
    //private var password = ""
    lateinit var progressBar: ProgressBar
    lateinit var verifyBtn : Button
    lateinit var resendTxt : TextView
    lateinit var code : EditText

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_verify)
        this.supportActionBar?.title = "Registrar"
        //password = this.intent.getStringExtra("password")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        progressBar = findViewById(R.id.progressBar3)
        verifyBtn = findViewById(R.id.confirm_code_btn)
        resendTxt = findViewById(R.id.resend_code_txt)
        code = findViewById(R.id.code_editText)
        progressBar.visibility = View.GONE

        code.onChange { this.viewModel.inputs.verificationCodeTextChanged(it) }

        verifyBtn.setOnClickListener { this.viewModel.inputs.verificationButtonPressed() }
        resendTxt.setOnClickListener { this.viewModel.inputs.resendVerificationCodePressed() }

        compositeDisposable.add(this.viewModel.outputs.verificationButtonEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe { verifyBtn.isEnabled = it })

        compositeDisposable.add(this.viewModel.outputs.loadingEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if(it){
                    verifyBtn.isEnabled = false
                    progressBar.visibility = View.VISIBLE
                }else{
                    verifyBtn.isEnabled = true
                    progressBar.visibility = View.GONE
                }
            })

        compositeDisposable.add(this.viewModel.outputs.resendAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                this.showDialog("Confirmación","El código de verificación de 6 dígitos fue reenviado a: ${this.viewModel.username}")
            })

        compositeDisposable.add(this.viewModel.outputs.showError()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                verifyBtn.isEnabled = true
                progressBar.visibility = View.GONE
                showDialog("Lo sentimos",it.friendlyMessage)
            })


        //Cambiar con la pantalla
        compositeDisposable.add(this.viewModel.outputs.verifiedAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                //val intent = Intent(this, HomeActivity::class.java)
                val intent = Intent(this, LaunchActivity::class.java)
                startActivity(intent)
                finish()
                //showDialog("SIgnIn","Inicio de Sesión correcto")
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_help_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.help_action){
            this.showDialog("Ayuda","El código de verificación de 6 dígitos fue enviado a: ${this.viewModel.username}")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog(title : String, message : String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Cerrar",null)
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
