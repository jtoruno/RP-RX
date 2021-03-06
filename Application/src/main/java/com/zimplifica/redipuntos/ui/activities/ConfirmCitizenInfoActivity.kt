package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.models.ManagerNav
import com.zimplifica.redipuntos.viewModels.ConfirmCitizenInfoVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_confirm_citizen_info.*

@RequiresActivityViewModel(ConfirmCitizenInfoVM.ViewModel::class)
class ConfirmCitizenInfoActivity : BaseActivity<ConfirmCitizenInfoVM.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_citizen_info)
        progressBar6.visibility = View.GONE
        this.supportActionBar?.title = "Confirmar identitdad"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        compositeDisposable.add(this.viewModel.outputs.printData().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                citizen_name_text.setText(it.nombre)
                val lastName = "${it.apellido1} ${it.apellido2}"
                citizen_last_name_text.setText(lastName)
                citizen_birth_date_text.setText(it.fechaNacimiento)
                citizen_id_number_text.setText(transformUserId(it.cedula))
            })

        next_btn_confirm_citizen.setOnClickListener {
            this.viewModel.inputs.nextButtonPressed()
        }

        compositeDisposable.add(this.viewModel.outputs.loadingEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it){
                    next_btn_confirm_citizen.isEnabled = false
                    progressBar6.visibility = View.VISIBLE
                }else{
                    next_btn_confirm_citizen.isEnabled = true
                    progressBar6.visibility = View.GONE
                }
            })
        compositeDisposable.add(this.viewModel.outputs.citizenInformationConfirmed().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Toast.makeText(this,"Completada la información personal correctamente", Toast.LENGTH_LONG).show()
                finish()
                ManagerNav.getInstance(this).initNav()
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }

    private fun transformUserId(userIdString : String?) : String {
        if (userIdString == null){return ""}
        return userIdString.substring(0,1) + "-" + userIdString.substring(1,5) + "-" + userIdString.substring(5,userIdString.length)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
