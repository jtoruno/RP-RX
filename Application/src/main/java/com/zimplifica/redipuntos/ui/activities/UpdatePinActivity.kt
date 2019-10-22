package com.zimplifica.redipuntos.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.UpdatePinVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_update_pin.*

@RequiresActivityViewModel(UpdatePinVM.ViewModel::class)
class UpdatePinActivity : BaseActivity<UpdatePinVM.ViewModel>() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_pin)
        supportActionBar?.title = getString(R.string.Pin_update)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        update_pin_progress_bar.visibility = View.GONE

        //Inputs
        update_pin_phone_code.onChange { viewModel.inputs.verificationCodeChanged(it) }
        update_pin_field.onChange { viewModel.inputs.pinChanged(it) }
        update_pin_btn.setOnClickListener { viewModel.inputs.nextButtonPressed() }

        //Outputs
        compositeDisposable.add(viewModel.outputs.userInformationAction().observeOn(AndroidSchedulers.mainThread()).subscribe {
            val phoneNumber = it.userPhoneNumber
            update_pin_phone.text = getString(R.string.Verification_code_sent_to_number,phoneNumber)
        })

        compositeDisposable.add(viewModel.outputs.nextButtonEnabled().observeOn(AndroidSchedulers.mainThread()).subscribe{
            update_pin_btn.isEnabled = it
        })

        compositeDisposable.add(viewModel.outputs.showErrorAction().observeOn(AndroidSchedulers.mainThread()).subscribe {
            showDialog(getString(R.string.Sorry) ,it)
        })

        compositeDisposable.add(viewModel.outputs.loadingEnabled().observeOn(AndroidSchedulers.mainThread()).subscribe {
            if(it){
                update_pin_btn.isEnabled = false
                update_pin_progress_bar.visibility = View.VISIBLE
            }else{
                update_pin_progress_bar.visibility = View.GONE
                update_pin_btn.isEnabled = true
            }
        })

        compositeDisposable.add(viewModel.outputs.nextButtonAction().observeOn(AndroidSchedulers.mainThread()).subscribe {
            val returnIntent = Intent()
            returnIntent.putExtra("successful", it)
            setResult(Activity.RESULT_OK,returnIntent)
            finish()
        })


        viewModel.inputs.viewDidLoad()


    }

    private fun showDialog(title : String, message : String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.Close),null)
        val dialog = builder.create()
        dialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
