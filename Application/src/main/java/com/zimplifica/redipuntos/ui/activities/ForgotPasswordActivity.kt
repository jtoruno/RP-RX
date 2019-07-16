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
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.ForgotPasswordViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

@RequiresActivityViewModel(ForgotPasswordViewModel.ViewModel::class)
class ForgotPasswordActivity : BaseActivity<ForgotPasswordViewModel.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()

    lateinit var progressBar: ProgressBar
    lateinit var nextBtn : Button
    lateinit var input : EditText

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        this.supportActionBar?.title = "Recuperar Contraseña"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        progressBar = findViewById(R.id.progressBar4)
        progressBar.visibility = View.GONE
        nextBtn = findViewById(R.id.forgot_next_btn)
        input = findViewById(R.id.forgot_username)
        input.onChange { this.viewModel.inputs.usernameChanged(it) }

        nextBtn.setOnClickListener { this.viewModel.inputs.nextButtonPressed() }
        compositeDisposable.add(this.viewModel.outputs.nextButtonEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                nextBtn.isEnabled = it
            })

        compositeDisposable.add(this.viewModel.outputs.loadingEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it){
                    nextBtn.isEnabled = false
                    progressBar.visibility = View.VISIBLE
                }else{
                    nextBtn.isEnabled = true
                    progressBar.visibility = View.GONE
                }
            })

        compositeDisposable.add(this.viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showDialog("Lo sentimos",it.friendlyMessage)
            })

        compositeDisposable.add(this.viewModel.outputs.forgotPasswordStatus().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this, ConfirmForgotPasswordActivity::class.java)
                startActivity(intent)
                finish()
            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_help_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.help_action){
            this.showDialog("Ayuda","Ingresar número de teléfono o correo electrónico registrado.")
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
