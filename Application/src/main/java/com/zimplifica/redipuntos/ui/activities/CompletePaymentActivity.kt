package com.zimplifica.redipuntos.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.zimplifica.redipuntos.R

class CompletePaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_payment)
        supportActionBar?.title = "Método de Pago"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }
}
