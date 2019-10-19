package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
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
import io.reactivex.disposables.CompositeDisposable

@RequiresActivityViewModel(ConfirmForgotPsswordVM.ViewModel::class)
class ConfirmForgotPasswordActivity : BaseActivity<ConfirmForgotPsswordVM.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()


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
        this.supportActionBar?.title = getString(R.string.Forgot_password)
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

        compositeDisposable.add(this.viewModel.outputs.nextButtonEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe { nextBtn.isEnabled = it })

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
                showDialog(getString(R.string.Sorry),it)
            })

        compositeDisposable.add(this.viewModel.outputs.validPasswordCapitalLowerLetters().observeOn(AndroidSchedulers.mainThread()).subscribe {
            this.passOption(img1,it)
        })
        compositeDisposable.add(this.viewModel.outputs.validPasswordNumbers().observeOn(AndroidSchedulers.mainThread()).subscribe{
            this.passOption(img2,it)
        })
        compositeDisposable.add(this.viewModel.outputs.validPasswordSpecialCharacters().observeOn(AndroidSchedulers.mainThread()).subscribe {
            this.passOption(img3,it)
        })
        compositeDisposable.add(this.viewModel.outputs.validPasswordLenght().observeOn(AndroidSchedulers.mainThread()).subscribe {
            this.passOption(img4,it)
        })

        compositeDisposable.add(this.viewModel.outputs.passwordChangedAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Toast.makeText(this, "Modificación Exitosa \n Contraseña modificada correctamente. Por favor intente iniciar sesión nuevamente.", Toast.LENGTH_LONG).show()
                finish()
            })

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
            this.showDialog(getString(R.string.Help),"El código de verificación fue enviado al número de teléfono")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog(title : String, message : String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.Close),null)
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
