package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

@RequiresActivityViewModel(TakePhoneViewModel.ViewModel::class)
class TakePhoneActivity : BaseActivity<TakePhoneViewModel.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()
    lateinit var nextBtn : Button
    lateinit var phoneEditText: EditText
    lateinit var nicknameEditText: EditText

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_phone)
        this.supportActionBar?.title = "Registrar"
        nextBtn = findViewById(R.id.next_take_btn)
        phoneEditText = findViewById(R.id.phone_edit_text)
        nicknameEditText = findViewById(R.id.nickname_editText)

        phoneEditText.onChange { this.viewModel.inputs.phone(it) }
        nicknameEditText.onChange { this.viewModel.inputs.nicknameChanged(it) }

        nextBtn.setOnClickListener { this.viewModel.inputs.nextButtonClicked() }
        compositeDisposable.add(this.viewModel.outputs.startNextActivity().subscribe {
            val intent = Intent(this, PasswordActivity::class.java)
            intent.putExtra("SignUpUsernameModel",it)
            startActivity(intent)
        })
        compositeDisposable.add(this.viewModel.outputs.nextButtonIsEnabled().observeOn(AndroidSchedulers.mainThread()).subscribe {
            nextBtn.isEnabled = it
        })

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

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
