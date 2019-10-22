package com.zimplifica.redipuntos.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.CreatePinVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_create_pin.*


@RequiresActivityViewModel(CreatePinVM.ViewModel::class)
class CreatePinActivity : BaseActivity<CreatePinVM.ViewModel>() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_pin)
        supportActionBar?.title = getString(R.string.Pin_create)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        create_pin_progress_bar.visibility = View.GONE

        ///Inputs
        create_pin_code.onChange { viewModel.inputs.pinChanged(it) }
        create_pin_btn.setOnClickListener { viewModel.inputs.nextButtonPressed() }

        //Outputs
        compositeDisposable.add(viewModel.outputs.nextButtonEnabled().observeOn(AndroidSchedulers.mainThread()).subscribe {
            create_pin_btn.isEnabled = it
        })

        compositeDisposable.add(viewModel.outputs.showErrorAction().observeOn(AndroidSchedulers.mainThread()).subscribe {
            showDialog(getString(R.string.Sorry), it)
        })

        compositeDisposable.add(viewModel.outputs.loadingEnabled().observeOn(AndroidSchedulers.mainThread()).subscribe {
            if(it){
                create_pin_btn.isEnabled = false
                create_pin_progress_bar.visibility = View.VISIBLE
            }else{
                create_pin_progress_bar.visibility = View.GONE
                create_pin_btn.isEnabled = true
            }
        })

        compositeDisposable.add(viewModel.outputs.nextButtonAction().observeOn(AndroidSchedulers.mainThread()).subscribe {
            val returnIntent = Intent()
            returnIntent.putExtra("successful", it)
            setResult(Activity.RESULT_OK,returnIntent)
            finish()
        })

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
