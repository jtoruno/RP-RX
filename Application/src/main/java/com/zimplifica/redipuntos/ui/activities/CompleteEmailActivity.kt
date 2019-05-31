package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.CompleteEmailVM
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_complete_email.*

@RequiresActivityViewModel(CompleteEmailVM.ViewModel::class)
class CompleteEmailActivity : BaseActivity<CompleteEmailVM.ViewModel>() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_email)
        supportActionBar?.title = "Correo electrónico"
        progressBar7.visibility = View.GONE
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        complete_email_input.onChange { this.viewModel.inputs.emailChanged(it) }
        complete_emain_btn.setOnClickListener {
            this.viewModel.inputs.verifyEmailPressed()
        }
        this.viewModel.outputs.isEmailValid().observeOn(AndroidSchedulers.mainThread())
            .subscribe { complete_emain_btn.isEnabled = it }

        this.viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showDialog("Lo sentimos", it)
            }

        this.viewModel.outputs.loading().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it){
                    complete_emain_btn.isEnabled = false
                    progressBar7.visibility = View.VISIBLE
                }else{
                    complete_emain_btn.isEnabled = true
                    progressBar7.visibility = View.GONE
                }
            }

        this.viewModel.outputs.emailSuccessfullyConfirmed().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this, ConfirmEmailActivity::class.java)
                intent.putExtra("email",it)
                startActivity(intent)
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
