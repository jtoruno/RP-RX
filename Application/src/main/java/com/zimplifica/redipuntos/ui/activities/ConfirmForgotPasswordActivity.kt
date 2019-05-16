package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.ConfirmForgotPsswordVM
import io.reactivex.android.schedulers.AndroidSchedulers

@RequiresActivityViewModel(ConfirmForgotPsswordVM.ViewModel::class)
class ConfirmForgotPasswordActivity : BaseActivity<ConfirmForgotPsswordVM.ViewModel>() {

    lateinit var img1 : ImageView
    lateinit var img2 : ImageView
    lateinit var img3 : ImageView
    lateinit var img4 : ImageView
    lateinit var progressBar: ProgressBar
    lateinit var code : EditText
    lateinit var pass : EditText
    lateinit var nextBtn : Button

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_forgot_password)
        this.supportActionBar?.title = "Recuperar Contraseña"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        img1 = findViewById(R.id.forgot_img1)
        img2 = findViewById(R.id.forgot_img2)
        img3 = findViewById(R.id.forgot_img3)
        img4 = findViewById(R.id.forgot_img4)
        progressBar = findViewById(R.id.progressBar5)
        progressBar.visibility = View.GONE
        code = findViewById(R.id.editText2)
        code.onChange { this.viewModel.inputs.confirmationCodeTextChanged(it) }
        pass = findViewById(R.id.forgot_pass_edit_text)
        pass.onChange { this.viewModel.inputs.passwordChanged(it) }
        nextBtn = findViewById(R.id.forgot_change_btn)
        nextBtn.setOnClickListener { this.viewModel.inputs.nextButtonPressed() }

        this.viewModel.outputs.nextButtonEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe { nextBtn.isEnabled = it }
        this.viewModel.outputs.loadingEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it){
                    nextBtn.isEnabled = false
                    progressBar.visibility = View.VISIBLE
                }else{
                    nextBtn.isEnabled = true
                    progressBar.visibility = View.GONE
                }
            }
        this.viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showDialog("Lo sentimos",it.friendlyMessage)
            }
        this.viewModel.outputs.validPasswordCapitalLowerLetters().observeOn(AndroidSchedulers.mainThread()).subscribe {
            this.passOption(img1,it)
        }
        this.viewModel.outputs.validPasswordNumbers().observeOn(AndroidSchedulers.mainThread()).subscribe{
            this.passOption(img2,it)
        }
        this.viewModel.outputs.validPasswordSpecialCharacters().observeOn(AndroidSchedulers.mainThread()).subscribe {
            this.passOption(img3,it)
        }
        this.viewModel.outputs.validPasswordLenght().observeOn(AndroidSchedulers.mainThread()).subscribe {
            this.passOption(img4,it)
        }

        this.viewModel.outputs.passwordChangedAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Toast.makeText(this, "Modificación Exitosa \n Contraseña modificada correctamente. Por favor intente iniciar sesión nuevamente.", Toast.LENGTH_LONG).show()
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

    private fun passOption(image : ImageView, state: Boolean){
        if(state){
            image.setImageResource(R.drawable.icon_ok)
        }else{
            image.setImageResource(R.drawable.icon_close)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_help_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.help_action){
            this.showDialog("Ayuda","El código de verificación fue enviado al número de teléfono o correo electrónico.")
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
}