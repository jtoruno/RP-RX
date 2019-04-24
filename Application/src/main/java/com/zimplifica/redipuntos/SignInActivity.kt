package com.zimplifica.redipuntos

import android.arch.lifecycle.ViewModel
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.viewModels.SignInViewModel
import io.reactivex.android.schedulers.AndroidSchedulers

class SignInActivity : AppCompatActivity() {
    lateinit var  vm : SignInViewModel.ViewModel
    lateinit var signInBtn : Button
    lateinit var userEditText: EditText
    lateinit var passEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        this.supportActionBar?.title = "Iniciar Sesión"
        signInBtn = findViewById(R.id.sign_in_btn_screen)
        userEditText = findViewById(R.id.user_edit_text_sign_in)
        passEditText = findViewById(R.id.pass_edit_text_sign_in)
        vm = SignInViewModel.ViewModel()
        userEditText.onChange { vm.inputs.username(it) }
        passEditText.onChange { vm.inputs.password(it) }
        vm.outputs.signInButtonIsEnabled().observeOn(AndroidSchedulers.mainThread()).subscribe{setBtnEnabled(it)}

    }

    private fun setBtnEnabled(enabled : Boolean){
        signInBtn.isEnabled = enabled
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_help_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
