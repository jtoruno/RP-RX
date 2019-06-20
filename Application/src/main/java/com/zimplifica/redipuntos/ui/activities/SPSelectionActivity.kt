package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.AndroidException
import android.util.Log
import android.view.View
import com.jakewharton.rxbinding2.view.enabled
import com.zimplifica.domain.entities.Vendor
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.SPSelectionVM
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_spselection.*


@RequiresActivityViewModel(SPSelectionVM.ViewModel::class)
class SPSelectionActivity : BaseActivity<SPSelectionVM.ViewModel>() {
    private val REQUEST_CODE = 300


    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spselection)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        progressBar12.visibility = View.GONE
        sp_selection_chip.visibility = View.GONE
        sp_selection_txt.onChange { this.viewModel.inputs.descriptionTextFieldChanged(it) }
        sp_selection_btn.setOnClickListener { viewModel.inputs.nextButtonPressed() }
        sp_take_qr_code.setOnClickListener { viewModel.inputs.qrCameraButtonPressed() }


        //val amount = this.intent.extras.getFloat("amount")
        //supportActionBar?.title = "₡ "+String.format("%,.0f", amount)
        /*
        sp_take_qr_code.setOnClickListener {
            val intent = Intent(this, SPScanQRActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }*/
        viewModel.outputs.vendorInformationAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                sp_selection_chip.visibility = View.VISIBLE
                sp_selection_chip.text = it.name
            }

        viewModel.outputs.nextButtonLoadingIndicator().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if(it){
                    progressBar12.visibility = View.VISIBLE
                    sp_selection_btn.isEnabled = false
                    sp_selection_btn.alpha = 0.5F
                }else{
                    progressBar12.visibility = View.GONE
                    sp_selection_btn.isEnabled = true
                    sp_selection_btn.alpha = 1F
                }
            }

        viewModel.outputs.nextButtonEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                if(!it){
                    sp_selection_btn.isEnabled = false
                    sp_selection_btn.alpha = 0.5F
                }else{
                    sp_selection_btn.isEnabled = true
                    sp_selection_btn.alpha = 1F
                }

            }

        viewModel.outputs.qrCameraButtonAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                val intent = Intent(this, SPScanQRActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE)
            }

        supportActionBar?.title = "₡ "+String.format("%,.0f", viewModel.getAmount())

        viewModel.outputs.nextButtonAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                val intent = Intent(this,PaymentSelectionActivity::class.java)
                intent.putExtra("SPSelectionObject",it)
                intent.putExtra("amount",viewModel.getAmount())
                startActivity(intent)
                finish()
            }

        viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showDialog("Lo sentimos", it)
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val result = data?.getSerializableExtra("qr") as Vendor
            Log.e("result", result.name)
            this.viewModel.inputs.vendorInformation(result)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
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
