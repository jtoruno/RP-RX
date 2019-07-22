package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.AccountVerifyEmailVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_account_verify_email.*

@RequiresActivityViewModel(AccountVerifyEmailVM.ViewModel::class)
class AccountVerifyEmailActivity : BaseActivity<AccountVerifyEmailVM.ViewModel>() {

    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_verify_email)
        supportActionBar?.title = "Verificar Correo"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        progressBar16.visibility = View.GONE

        account_verify_code.onChange { viewModel.inputs.codeValueChanged(it) }
        account_verify_btn.setOnClickListener {
            viewModel.inputs.verifyCodePressed()
        }
        compositeDisposable.add(viewModel.outputs.validCode().observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                account_verify_btn.isEnabled = it
            })

        compositeDisposable.add(viewModel.outputs.loadingEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if(it){
                    progressBar16.visibility = View.VISIBLE
                    account_verify_btn.isEnabled = false
                }else{
                    progressBar16.visibility = View.GONE
                    account_verify_btn.isEnabled = true
                }
            })

        compositeDisposable.add(viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showDialog("Lo sentimos", it)
            })

        compositeDisposable.add(viewModel.outputs.verifyCodeAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Toast.makeText(this,"Correo electrónico confirmado correctamente.",Toast.LENGTH_SHORT).show()
                finish()
            })

        compositeDisposable.add(viewModel.outputs.emailAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                account_verify_email.text = it
            })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
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
