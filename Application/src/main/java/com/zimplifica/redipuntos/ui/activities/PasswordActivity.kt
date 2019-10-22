package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
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
import io.reactivex.disposables.CompositeDisposable
import java.util.*

@RequiresActivityViewModel(PasswordViewModel.ViewModel::class)
class PasswordActivity : BaseActivity<PasswordViewModel.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()

    lateinit var termnsTxt : TextView
    lateinit var passwordEditText: EditText
    lateinit var createAccountBtn : Button
    lateinit var img1 : ImageView
    lateinit var img2 : ImageView
    lateinit var img3 : ImageView
    lateinit var img4 : ImageView
    lateinit var progressBar: ProgressBar
    //private var phone = ""


    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        this.supportActionBar?.title = getString(R.string.Sign_up)
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
        //phone = this.intent.getStringExtra("phone")?:""

        //Log.e("Phone",phone)
        //this.viewModel.inputs.username(phone)
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


        compositeDisposable.add(this.viewModel.outputs.startTermsActivity().subscribe {
            val intent = Intent(this,TermsActivity::class.java)
            startActivity(intent)
        })

        compositeDisposable.add(this.viewModel.outputs.startPolicyActivity().subscribe {
            val intent = Intent(this,PrivacyActivity::class.java)
            startActivity(intent)
        })

        compositeDisposable.add(this.viewModel.outputs.signUpButtonEnabled().subscribe {
            this.buttonEnabled(it)
        })

        compositeDisposable.add(this.viewModel.outputs.validPasswordCapitalLowerLetters().subscribe {
            this.passOption(img1,it)
        })
        compositeDisposable.add(this.viewModel.outputs.validPasswordNumbers().subscribe{
            this.passOption(img2,it)
        })
        compositeDisposable.add(this.viewModel.outputs.validPasswordSpecialCharacters().subscribe {
            this.passOption(img3,it)
        })
        compositeDisposable.add(this.viewModel.outputs.validPasswordLenght().subscribe {
            this.passOption(img4,it)
        })

        createAccountBtn.setOnClickListener {
            Log.e("PSVM","CLicked")
            this.viewModel.inputs.signUpButtonPressed()
        }
        compositeDisposable.add(this.viewModel.outputs.loadingEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it){
                    createAccountBtn.isEnabled = false
                    progressBar.visibility = View.VISIBLE
                }else{
                    createAccountBtn.isEnabled = true
                    progressBar.visibility = View.GONE
                }
            })
        compositeDisposable.add(this.viewModel.outputs.showError()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showDialog(getString(R.string.Sorry),it.friendlyMessage)
            })

        compositeDisposable.add(this.viewModel.outputs.verifyPhoneNumberAction()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this,SignUpVerifyActivity::class.java)
                //intent.putExtra("password",it.password)
                intent.putExtra("SignUpModel",it)
                startActivity(intent)
            })
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
            this.showDialog(getString(R.string.Help),getString(R.string.Sign_Up_help_password_message))
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
