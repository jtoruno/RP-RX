package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.models.ManagerNav
import com.zimplifica.redipuntos.viewModels.ConfirmEmailVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_confirm_email.*

@RequiresActivityViewModel(ConfirmEmailVM.ViewModel::class)

class ConfirmEmailActivity : BaseActivity<ConfirmEmailVM.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()

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
        compositeDisposable.add(this.viewModel.outputs.email().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                confirm_email_txt.text = "correo: $it"
            })

        compositeDisposable.add(this.viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showDialog("Lo sentimos", it)
            })

        compositeDisposable.add(this.viewModel.outputs.loading().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it){
                    confirm_email_btn.isEnabled = false
                    progressBar8.visibility = View.VISIBLE
                }else{
                    confirm_email_btn.isEnabled = true
                    progressBar8.visibility = View.GONE
                }
            })

        compositeDisposable.add(this.viewModel.outputs.isButtonEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe { confirm_email_btn.isEnabled = it })

        compositeDisposable.add(this.viewModel.outputs.showResendAlert().observeOn(AndroidSchedulers.mainThread())
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
            })

        compositeDisposable.add(this.viewModel.outputs.codeResent().observeOn(AndroidSchedulers.mainThread())
            .subscribe { Toast.makeText(this,"Código reenviado", Toast.LENGTH_SHORT).show() })

        compositeDisposable.add(this.viewModel.outputs.confirmedEmail().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Toast.makeText(this,"Correo Confirmado", Toast.LENGTH_SHORT).show()
                finish()
                ManagerNav.getInstance(this).initNav()
            })
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
