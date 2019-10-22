package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.biometric.BiometricManager
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.SignUpVerifyViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable


@RequiresActivityViewModel(SignUpVerifyViewModel.ViewModel::class)
class SignUpVerifyActivity : BaseActivity<SignUpVerifyViewModel.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()

    private var userName = ""
    //private var password = ""
    lateinit var progressBar: ProgressBar
    lateinit var verifyBtn : Button
    lateinit var resendTxt : TextView
    lateinit var code : EditText
    lateinit var checkBox: CheckBox
    lateinit var biometricLayout : LinearLayout

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_verify)
        this.supportActionBar?.title = getString(R.string.Sign_up)
        //password = this.intent.getStringExtra("password")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        progressBar = findViewById(R.id.progressBar3)
        verifyBtn = findViewById(R.id.confirm_code_btn)
        resendTxt = findViewById(R.id.resend_code_txt)
        code = findViewById(R.id.code_editText)
        checkBox = findViewById(R.id.sign_up_verify_check)
        biometricLayout = findViewById(R.id.sign_up_verify_biometric_layout)
        progressBar.visibility = View.GONE

        if(!biometricAuthAvailable()){
            biometricLayout.visibility = View.GONE
        }

        //Inputs
        code.onChange { this.viewModel.inputs.verificationCodeTextChanged(it) }
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.inputs.biometricAuthChanged(isChecked)
        }



        verifyBtn.setOnClickListener { this.viewModel.inputs.verificationButtonPressed() }
        resendTxt.setOnClickListener { this.viewModel.inputs.resendVerificationCodePressed() }

        compositeDisposable.add(this.viewModel.outputs.verificationButtonEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe { verifyBtn.isEnabled = it })

        compositeDisposable.add(this.viewModel.outputs.loadingEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if(it){
                    verifyBtn.isEnabled = false
                    progressBar.visibility = View.VISIBLE
                }else{
                    verifyBtn.isEnabled = true
                    progressBar.visibility = View.GONE
                }
            })

        compositeDisposable.add(this.viewModel.outputs.resendAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                this.showDialog(getString(R.string.Help),getString(R.string.Sign_up_resend_verification_code_result,this.viewModel.username))
            })

        compositeDisposable.add(this.viewModel.outputs.showError()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                verifyBtn.isEnabled = true
                progressBar.visibility = View.GONE
                showDialog(getString(R.string.Sorry),it.friendlyMessage)
            })


        //Cambiar con la pantalla
        compositeDisposable.add(this.viewModel.outputs.verifiedAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                //val intent = Intent(this, HomeActivity::class.java)
                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)
                //finish()
                finishAffinity()
                //showDialog("SIgnIn","Inicio de Sesión correcto")
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_help_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.help_action){
            this.showDialog(getString(R.string.Help),getString(R.string.Forgot_password_message,this.viewModel.username))
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

    private fun biometricAuthAvailable() : Boolean{
        val biometricManager = BiometricManager.from(this)
        when(biometricManager.canAuthenticate()){
            BiometricManager.BIOMETRIC_SUCCESS ->{
                Log.i("BiometricManager","App can authenticate using biometrics.")
                return true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->{
                Log.e("BiometricManager","No biometric features available on this device.")
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->{
                Log.e("BiometricManager","Biometric features are currently unavailable.")
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->{
                Log.e("BiometricManager","The user hasn't associated any biometric credentials " +
                        "with their account.")
                return false
            }else -> return false
        }
    }
}
