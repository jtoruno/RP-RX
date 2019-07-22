package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.View
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.AccountEditEmailVM
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_account_edit_email.*
import io.reactivex.disposables.CompositeDisposable



@RequiresActivityViewModel(AccountEditEmailVM.ViewModel::class)
class AccountEditEmailActivity : BaseActivity<AccountEditEmailVM.ViewModel>(){
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_edit_email)
        supportActionBar?.title = "Editar Correo"
        progressBar14.visibility = View.GONE
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        account_edit_email_input.onChange { this.viewModel.inputs.emailValueChanged(it) }

        account_edit_email_btn.setOnClickListener {
            viewModel.inputs.verifyEmailPressed()
        }
        compositeDisposable.add(viewModel.outputs.emailAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                account_edit_email_input.setText(it)
            })


        compositeDisposable.add(viewModel.outputs.loadingEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if(it){
                    progressBar14.visibility = View.VISIBLE
                    account_edit_email_btn.isEnabled = false
                }else{
                    progressBar14.visibility = View.GONE
                    account_edit_email_btn.isEnabled = true
                }
            })

        compositeDisposable.add(viewModel.outputs.verifyButtonEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                account_edit_email_btn.isEnabled = it
            })

        compositeDisposable.add(viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showDialog("Lo sentimos", it)
            })

        compositeDisposable.add(viewModel.outputs.verifyEmailAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                val intent = Intent(this,AccountVerifyEmailActivity::class.java)
                intent.putExtra("email",it)
                startActivity(intent)
                finish()
            })

        viewModel.inputs.onCreated()
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
