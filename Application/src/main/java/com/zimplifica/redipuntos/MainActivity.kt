package com.zimplifica.redipuntos

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    lateinit var singInBtn : Button
    lateinit var signUpBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        singInBtn = findViewById(R.id.sign_in_btn)
        singInBtn.setOnClickListener {
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }
        signUpBtn = findViewById(R.id.sign_up_btn)
        signUpBtn.setOnClickListener {
            val intent = Intent(this, TakePhoneActivity::class.java)
            startActivity(intent)
        }
    }
}
