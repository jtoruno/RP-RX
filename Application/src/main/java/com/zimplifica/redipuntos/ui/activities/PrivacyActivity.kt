package com.zimplifica.redipuntos.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.zimplifica.redipuntos.R

class PrivacyActivity : AppCompatActivity() {

    lateinit var webView : WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Políticas de Privacidad"

        webView = findViewById(R.id.webViewPrivacy)

        webView.settings.javaScriptEnabled = true
        webView.loadUrl("https://zimplerp.netlify.com/legal/privacy-policy/")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }
}
