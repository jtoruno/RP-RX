package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.PasswordViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*

@RequiresActivityViewModel(PasswordViewModel.ViewModel::class)
class PasswordActivity : BaseActivity<PasswordViewModel.ViewModel>() {

    lateinit var termnsTxt : TextView
    lateinit var passwordEditText: EditText
    lateinit var createAccountBtn : Button
    lateinit var img1 : ImageView
    lateinit var img2 : ImageView
    lateinit var img3 : ImageView
    lateinit var img4 : ImageView
    lateinit var progressBar: ProgressBar
    private var phone = ""


    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        this.supportActionBar?.title = "Registrar"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        passwordEditText = findViewById(R.id.passTxtTempPass)
        createAccountBtn = findViewById(R.id.create_account_btn)
        img1 = findViewById(R.id.imageView10)
        img2 = findViewById(R.id.imageView20)
        img3 = findViewById(R.id.imageView21)
        img4 = findViewById(R.id.imageView22)
        progressBar = findViewById(R.id.progressBar2)
        progressBar.visibility = View.GONE
        phone = this.intent.getStringExtra("phone")?:""

        Log.e("Phone",phone)
        this.viewModel.inputs.username(phone)
        passwordEditText.onChange { this.viewModel.inputs.password(it) }

        /////////////////////////////
        termnsTxt = findViewById(R.id.termsSignUpPresenterTxt)
        //Clickeable text
        val ss = SpannableString(resources.getString(R.string.signUpTermsText))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                this@PasswordActivity.viewModel.inputs.termsAndConditionsButtonPressed()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }

        val clickableSpanPrivacy = object : ClickableSpan() {
            override fun onClick(textView: View) {
                this@PasswordActivity.viewModel.inputs.privacyPolicyButtonPressed()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }
        ss.setSpan(clickableSpan,40,62, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(clickableSpanPrivacy,69,92, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        termnsTxt.text = ss
        termnsTxt.movementMethod = LinkMovementMethod.getInstance()
        termnsTxt.highlightColor = Color.TRANSPARENT
        /////////////////////////////


        this.viewModel.outputs.startTermsActivity().subscribe {
            val intent = Intent(this,TermsActivity::class.java)
            startActivity(intent)
        }

        this.viewModel.outputs.startPolicyActivity().subscribe {
            val intent = Intent(this,PrivacyActivity::class.java)
            startActivity(intent)
        }

        this.viewModel.outputs.signUpButtonEnabled().subscribe {
            this.buttonEnabled(it)
        }

        this.viewModel.outputs.validPasswordCapitalLowerLetters().subscribe {
            this.passOption(img1,it)
        }
        this.viewModel.outputs.validPasswordNumbers().subscribe{
            this.passOption(img2,it)
        }
        this.viewModel.outputs.validPasswordSpecialCharacters().subscribe {
            this.passOption(img3,it)
        }
        this.viewModel.outputs.validPasswordLenght().subscribe {
            this.passOption(img4,it)
        }

        createAccountBtn.setOnClickListener {
            this.viewModel.inputs.signUpButtonPressed()
        }
        this.viewModel.outputs.loadingEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it){
                    createAccountBtn.isEnabled = false
                    progressBar.visibility = View.VISIBLE
                }else{
                    createAccountBtn.isEnabled = true
                    progressBar.visibility = View.GONE
                }
            }
        this.viewModel.outputs.showError()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showDialog("Lo sentimos",it.friendlyMessage)
            }

        this.viewModel.outputs.signedUpAction()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this,SignUpVerifyActivity::class.java)
                startActivity(intent)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_help_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }

    private fun buttonEnabled(state : Boolean){
        createAccountBtn.isEnabled = state
    }

    private fun passOption(image : ImageView, state: Boolean){
        if(state){
            image.setImageResource(R.drawable.icon_ok)
        }else{
            image.setImageResource(R.drawable.icon_close)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.help_action){
            this.showDialog("Ayuda","Ingrese una contraseña válida. \nLa contraseña debe contener entre 8 y 20 caracteres, 1 letra mayúscula, 1 letra minúscula, 1 caracter numérico y 1 caracter especial (!\$#@_-.+)")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog(title : String, message : String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Cerrar",null)
        val dialog = builder.create()
        dialog.show()
    }
}
