package com.zimplifica.redipuntos.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.utils.SharedPreferencesUtils
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Sobre Zimplifica"
        about_zimplifica_web_view.settings.javaScriptEnabled = true
        about_zimplifica_web_view.loadUrl("https://www.zimplifica.com/")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        //navigateToMovements()
        finish()
    }
    /*
    private fun navigateToMovements(){
        SharedPreferencesUtils.saveBooleanInSp(this,"nav_to_mov",true)
    }*/
}
