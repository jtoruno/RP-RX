package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.jakewharton.rxbinding2.view.visibility
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.SignInViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

@RequiresActivityViewModel(SignInViewModel.ViewModel::class)
class SignInActivity : BaseActivity<SignInViewModel.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()
    //lateinit var  vm : SignInViewModel.ViewModel
    lateinit var signInBtn : Button
    lateinit var userEditText: EditText
    lateinit var passEditText: EditText
    lateinit var progressBar : ProgressBar

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        this.supportActionBar?.title = getString(R.string.Sign_in)
        signInBtn = findViewById(R.id.sign_in_btn_screen)
        userEditText = findViewById(R.id.user_edit_text_sign_in)
        passEditText = findViewById(R.id.pass_edit_text_sign_in)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE
        //vm = SignInViewModel.ViewModel()
        userEditText.onChange { this.viewModel.inputs.username(it) }
        passEditText.onChange { this.viewModel.inputs.password(it) }
        //vm.outputs.signInButtonIsEnabled().observeOn(AndroidSchedulers.mainThread()).subscribe{setBtnEnabled(it)}
        this.viewModel.outputs.signInButtonIsEnabled().observeOn(AndroidSchedulers.mainThread()).subscribe { setBtnEnabled(it) }

        signInBtn.setOnClickListener {this.viewModel.inputs.signInButtonPressed()}
        this.viewModel.outputs.print().subscribe {
            Log.e("PBA",it)
        }
        compositeDisposable.add(this.viewModel.outputs.loadingEnabled()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
            if (it){
                signInBtn.isEnabled = false
                progressBar.visibility = View.VISIBLE
            }else{
                signInBtn.isEnabled = true
                progressBar.visibility = View.GONE
            }
        })
        compositeDisposable.add(this.viewModel.outputs.showError().subscribe {
            Log.e("Error Message Activity",it.friendlyMessage)
            val intent = Intent(this, SignInFailureActivity::class.java)
            startActivity(intent)
        })

        compositeDisposable.add(this.viewModel.outputs.signedInAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                //val intent = Intent(this, HomeActivity::class.java)
                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)
                finish()
            })
    }

    private fun setBtnEnabled(enabled : Boolean){
        signInBtn.isEnabled = enabled
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_help_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.help_action){
            this.showDialog(getString(R.string.Help),getString(R.string.Sign_in_help_message))
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
