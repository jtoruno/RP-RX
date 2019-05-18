package com.zimplifica.redipuntos.ui.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.zimplifica.redipuntos.R
import kotlinx.android.synthetic.main.activity_spselection.*

class SPSelectionActivity : AppCompatActivity() {
    private val REQUEST_CODE = 300


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spselection)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val amount = this.intent.extras.getFloat("amount")
        supportActionBar?.title = "₡ "+String.format("%,.0f", amount)

        sp_take_qr_code.setOnClickListener {
            val intent = Intent(this, SPScanQRActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val result = data?.getStringExtra("qr")
            Log.e("result", result)
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
