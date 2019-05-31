package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.ConfirmEmailVM
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_confirm_email.*

@RequiresActivityViewModel(ConfirmEmailVM.ViewModel::class)

class ConfirmEmailActivity : BaseActivity<ConfirmEmailVM.ViewModel>() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_email)
        supportActionBar?.title = "Verificación"
        progressBar8.visibility = View.GONE
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        confirm_email_btn.setOnClickListener {
            this.viewModel.inputs.confirmEmailButtonPressed()
        }
        confirm_email_input.onChange { this.viewModel.inputs.verificationCodeChanged(it) }
        confirm_email_resend.setOnClickListener {
            this.viewModel.inputs.resendCodeButtonPressed()
        }
        this.viewModel.outputs.email().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                confirm_email_txt.text = "correo: $it"
            }

        this.viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showDialog("Lo sentimos", it)
            }

        this.viewModel.outputs.loading().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it){
                    confirm_email_btn.isEnabled = false
                    progressBar8.visibility = View.VISIBLE
                }else{
                    confirm_email_btn.isEnabled = true
                    progressBar8.visibility = View.GONE
                }
            }

        this.viewModel.outputs.isButtonEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe { confirm_email_btn.isEnabled = it }

        this.viewModel.outputs.showResendAlert().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Alerta")
                builder.setMessage("¿Deseas que se reenvíe el código de verificación?")
                builder.setPositiveButton("Reenviar"){
                    _,_ ->
                    this.viewModel.inputs.confirmResendButtonPressed()
                }
                builder.setNegativeButton("Cancelar", null)
                val dialog = builder.create()
                dialog.show()
            }

        this.viewModel.outputs.codeResent().observeOn(AndroidSchedulers.mainThread())
            .subscribe { Toast.makeText(this,"Código reenviado", Toast.LENGTH_SHORT).show() }

        this.viewModel.outputs.confirmedEmail().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Toast.makeText(this,"Correo Confirmado", Toast.LENGTH_SHORT).show()
                finish()
            }

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
}
