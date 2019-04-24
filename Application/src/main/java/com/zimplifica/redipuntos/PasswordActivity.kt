package com.zimplifica.redipuntos

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Menu
import android.view.View
import android.widget.TextView

class PasswordActivity : AppCompatActivity() {

    lateinit var termnsTxt : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        this.supportActionBar?.title = "Registrar"

        termnsTxt = findViewById(R.id.termsSignUpPresenterTxt)
        //Clickeable text
        val ss = SpannableString(resources.getString(R.string.signUpTermsText))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }

        val clickableSpanPrivacity = object : ClickableSpan() {
            override fun onClick(textView: View) {

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }
        ss.setSpan(clickableSpan,40,62, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(clickableSpanPrivacity,69,92, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        termnsTxt.text = ss
        termnsTxt.movementMethod = LinkMovementMethod.getInstance()
        termnsTxt.highlightColor = Color.TRANSPARENT

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_help_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
