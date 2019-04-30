package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.extensions.text
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.TakePhoneViewModel

@RequiresActivityViewModel(TakePhoneViewModel.ViewModel::class)
class TakePhoneActivity : BaseActivity<TakePhoneViewModel.ViewModel>() {
    lateinit var nextBtn : Button
    lateinit var phoneEditText: EditText

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_phone)
        this.supportActionBar?.title = "Registrar"
        nextBtn = findViewById(R.id.next_take_btn)
        phoneEditText = findViewById(R.id.phone_edit_text)

        phoneEditText.onChange { this.viewModel.inputs.phone(it) }

        nextBtn.setOnClickListener { this.viewModel.inputs.nextButtonClicked() }
        this.viewModel.outputs.startNextActivity().subscribe {
            val intent = Intent(this, PasswordActivity::class.java)
            intent.putExtra("phone",phoneEditText.text())
            startActivity(intent)
        }
        this.viewModel.outputs.nextButtonIsEnabled().subscribe {
            nextBtn.isEnabled = it
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_help_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.help_action){
            this.showDialog("Ayuda","Tu número de telefono se utilizará para confirmar tu cuenta.")
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
