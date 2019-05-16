package com.zimplifica.redipuntos.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.zimplifica.redipuntos.R

class SPSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spselection)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val amount = this.intent.extras.getFloat("amount")
        supportActionBar?.title = "₡ "+String.format("%,.0f", amount)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }
}
