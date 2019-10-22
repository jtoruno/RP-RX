package com.zimplifica.redipuntos.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zimplifica.redipuntos.R
import kotlinx.android.synthetic.main.activity_complete_payment.*

class CompletePaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_payment)
        supportActionBar?.title = getString(R.string.Payment_method)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        complete_payment_description.text = getString(R.string.Get_payment_method_description,getString(R.string.app_name))
        complete_payment_btn.setOnClickListener {
            val intent = Intent(this,AddPaymentActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }
}
