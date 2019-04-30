package com.zimplifica.redipuntos.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
        this.viewModel.outputs.print().subscribe {
            Log.e("PBA",it)
        }

    }

    private fun setBtnEnabled(enabled : Boolean){
        signInBtn.isEnabled = enabled
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_help_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.help_action){
            this.showDialog("Ayuda","Ingrese su usuario (número de teléfono o correo electrónico) y contraseña")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog(title : String, message : String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Cerrar",null)
        val dialog = builder.create()
        dialog.show()
    }
}
