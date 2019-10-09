package com.zimplifica.redipuntos.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.ChangePasswordVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_change_password.*

@RequiresActivityViewModel(ChangePasswordVM.ViewModel::class)
class ChangePasswordActivity : BaseActivity<ChangePasswordVM.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        supportActionBar?.title = "Cambiar Contraseña"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        change_password_progress_bar.visibility = View.GONE

        change_password_pass_input.onChange { viewModel.inputs.newPasswordChanged(it) }
        change_password_code_input.onChange { viewModel.inputs.verificationCodeChange(it) }
        change_password_btn.setOnClickListener { viewModel.inputs.changePasswordButtonPressed() }

        compositeDisposable.add(viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread()).subscribe {
            showDialog("Lo sentimos", it)
        })

        compositeDisposable.add(viewModel.outputs.loadingEnabled().observeOn(AndroidSchedulers.mainThread()).subscribe {
            if(it){
                change_password_btn.isEnabled = false
                change_password_progress_bar.visibility = View.VISIBLE
            }else{
                change_password_btn.isEnabled = true
                change_password_progress_bar.visibility = View.GONE
            }
        })

        compositeDisposable.add(viewModel.outputs.changePasswordAction().observeOn(AndroidSchedulers.mainThread()).subscribe {
            Toast.makeText(this, "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show()
            finish()
        })

        compositeDisposable.add(viewModel.outputs.changePasswordButtonEnabled().observeOn(AndroidSchedulers.mainThread()).subscribe {
            change_password_btn.isEnabled = it
        })

        compositeDisposable.add(viewModel.outputs.validPasswordCapitalLowerLetters().observeOn(AndroidSchedulers.mainThread()).subscribe {
            passOption(imageView10, it)
        })

        compositeDisposable.add(viewModel.outputs.validPasswordLength().observeOn(AndroidSchedulers.mainThread()).subscribe {
            passOption(imageView22, it)
        })
        compositeDisposable.add(viewModel.outputs.validPasswordNumbers().observeOn(AndroidSchedulers.mainThread()).subscribe {
            passOption(imageView20, it)
        })
        compositeDisposable.add(viewModel.outputs.validPasswordSpecialCharacters().observeOn(AndroidSchedulers.mainThread()).subscribe {
            passOption(imageView21, it)
        })
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

    private fun showDialog(title : String, message : String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Cerrar",null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun passOption(image : ImageView, state: Boolean){
        if(state){
            image.setImageResource(R.drawable.icon_ok)
        }else{
            image.setImageResource(R.drawable.icon_close)
        }
    }
}
