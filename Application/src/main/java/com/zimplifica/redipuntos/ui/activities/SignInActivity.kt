package com.zimplifica.redipuntos.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.SignInViewModel
import io.reactivex.android.schedulers.AndroidSchedulers

@RequiresActivityViewModel(SignInViewModel.ViewModel::class)
class SignInActivity : BaseActivity<SignInViewModel.ViewModel>() {
    //lateinit var  vm : SignInViewModel.ViewModel
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
        //vm = SignInViewModel.ViewModel()
        userEditText.onChange { this.viewModel.inputs.username(it) }
        passEditText.onChange { this.viewModel.inputs.password(it) }
        //vm.outputs.signInButtonIsEnabled().observeOn(AndroidSchedulers.mainThread()).subscribe{setBtnEnabled(it)}
        this.viewModel.outputs.signInButtonIsEnabled().observeOn(AndroidSchedulers.mainThread()).subscribe { setBtnEnabled(it) }

        signInBtn.setOnClickListener {
            val intent = Intent(this, PasswordActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setBtnEnabled(enabled : Boolean){
        signInBtn.isEnabled = enabled
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_help_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
