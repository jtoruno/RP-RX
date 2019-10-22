package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.View
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.CompleteEmailVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_complete_email.*

@RequiresActivityViewModel(CompleteEmailVM.ViewModel::class)
class CompleteEmailActivity : BaseActivity<CompleteEmailVM.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_email)
        supportActionBar?.title = getString(R.string.Email)
        progressBar7.visibility = View.GONE
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        textView18.text = getString(R.string.Get_email_description,getString(R.string.app_name))

        complete_email_input.onChange { this.viewModel.inputs.emailChanged(it) }
        complete_emain_btn.setOnClickListener {
            this.viewModel.inputs.verifyEmailPressed()
        }
        compositeDisposable.add(this.viewModel.outputs.isEmailValid().observeOn(AndroidSchedulers.mainThread())
            .subscribe { complete_emain_btn.isEnabled = it })

        compositeDisposable.add(this.viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showDialog(getString(R.string.Sorry), it)
            })

        compositeDisposable.add(this.viewModel.outputs.loading().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it){
                    complete_emain_btn.isEnabled = false
                    progressBar7.visibility = View.VISIBLE
                }else{
                    complete_emain_btn.isEnabled = true
                    progressBar7.visibility = View.GONE
                }
            })

        compositeDisposable.add(this.viewModel.outputs.emailSuccessfullyConfirmed().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this, ConfirmEmailActivity::class.java)
                intent.putExtra("email",it)
                startActivity(intent)
                finish()
            })
    }

    private fun showDialog(title : String, message : String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.Close),null)
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
