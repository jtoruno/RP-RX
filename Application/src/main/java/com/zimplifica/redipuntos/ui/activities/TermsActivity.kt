package com.zimplifica.redipuntos.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.zimplifica.redipuntos.R

class TermsActivity : AppCompatActivity() {

    lateinit var webView : WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)
        webView = findViewById(R.id.webViewTermsAndConditions)
        webView.settings.javaScriptEnabled = true

        webView.loadUrl("https://zimplerp.netlify.com/legal/terms/")


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.Terms_and_conditions)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }
}
