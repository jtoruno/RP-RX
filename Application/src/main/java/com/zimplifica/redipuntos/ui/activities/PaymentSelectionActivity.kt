package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.biometric.BiometricPrompt
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.OnItemClickListener
import com.zimplifica.redipuntos.extensions.addOnItemClickListener
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.models.CheckAndPayModel
import com.zimplifica.redipuntos.ui.adapters.RecyclerCardPoints
import com.zimplifica.redipuntos.viewModels.PaymentSelectionVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_payment_selection.*
import kotlinx.android.synthetic.main.dialog_custom_card_picker.view.*
import java.util.concurrent.Executors

@RequiresActivityViewModel(PaymentSelectionVM.ViewModel::class)
class PaymentSelectionActivity : BaseActivity<PaymentSelectionVM.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()
    lateinit var adapter : RecyclerCardPoints
    private var applyAwards = false
    private val executor = Executors.newSingleThreadExecutor()

    //lateinit var paymentInformation: PaymentInformation
    lateinit var model : CheckAndPayModel

    companion object {
        val requestVerifyPin = 1600
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_selection)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""
        progressBar13.visibility = View.GONE

        adapter = RecyclerCardPoints()

        //adapter.setPaymentMethods(this.viewModel.getPaymentMethods())
        compositeDisposable.add(viewModel.outputs.checkAndPayModelAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                model = it
                reloadData()
            })

        payment_select_change_method.setOnClickListener {
            openCardPicker()
        }

        compositeDisposable.add(this.viewModel.outputs.reloadPaymentMethodsAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if(it!= null){
                    this.model = it
                }
                reloadData()
            })

        compositeDisposable.add(this.viewModel.outputs.applyRewards().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                this.applyAwards = it
                reloadData()
            })

        compositeDisposable.add(this.viewModel.outputs.paymentMethodChangedAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val cardIdentifier = it.issuer.toUpperCase() + " **** " + it.cardNumberWithMask
                ps_card_id_text.text = cardIdentifier
            })

        compositeDisposable.add(viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showDialog("Lo sentimos", it)
            })

        payment_s_checkBox.setOnCheckedChangeListener { _, isChecked ->
            this.viewModel.inputs.applyRewardsRowPressed(isChecked)
        }

        compositeDisposable.add(viewModel.outputs.finishPaymentProcessingAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this,MovementDetailActivity::class.java)
                intent.putExtra("transaction",it)
                startActivity(intent)
                finish()
            })

        compositeDisposable.add(viewModel.outputs.addPaymentMethodAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this,AddPaymentMethodActivity::class.java)
                startActivity(intent)
            })

        payment_selection_btn.setOnClickListener {  viewModel.inputs.nextButtonPressed() }

        compositeDisposable.add(viewModel.outputs.biometricAuthRequest().observeOn(AndroidSchedulers.mainThread()).subscribe {
            this.showBiometricPromp()
        })

        compositeDisposable.add(viewModel.outputs.pinSecurityCodeRequest().observeOn(AndroidSchedulers.mainThread()).subscribe {
            val intent = Intent(this,VerifyPinActivity::class.java)
            startActivityForResult(intent, requestVerifyPin)
        })

        compositeDisposable.add(viewModel.outputs.nextButtonLoadingIndicator().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if(it){
                    progressBar13.visibility = View.VISIBLE
                    payment_selection_btn.isEnabled = false
                    payment_selection_btn.alpha = 0.5F
                }else{
                    progressBar13.visibility = View.GONE
                    payment_selection_btn.isEnabled = true
                    payment_selection_btn.alpha = 1F
                }
            })

        payment_s_checkBox.isChecked = true

    }

    private fun reloadData(){
        adapter.setPaymentMethods(model.paymentMethods)

        //Vendor
        payment_selection_vendor_name.text = model.vendor.name
        payment_selection_vendor_place.text = model.vendor.address

        //
        payment_selection_amount.text = "₡ "+String.format("%,.2f", model.total)
        payment_s_subtotal.text = "₡ "+String.format("%,.2f", model.subtotal)

        //
        payment_s_fee.text = "₡ "+String.format("%,.2f", model.fee)
        payment_s_tax.text = "₡ "+String.format("%,.2f", model.tax)

        //
        payment_selection_redipoints.text = "Tienes "+String.format("%,.2f",model.rediPuntos)+" disponibles"
        payment_s_total.text =  "₡ "+String.format("%,.2f", model.chargeToApply())
        payment_s_rewards.text = "₡ "+String.format("%,.2f", model.rewardsToGet())
        val rediPuntosToApply = model.rediPuntosToApply()
        if(rediPuntosToApply>0){
            payment_s_reward_applied.text = "- ₡ "+String.format("%,.2f", rediPuntosToApply)
        }else{
            payment_s_reward_applied.text = "₡ "+String.format("%,.2f", 0.0)
        }
        //
        payment_s_fee.text = "₡ "+String.format("%,.2f", model.fee)
        payment_s_tax.text =  "${model.taxes} %"

        if (model.selectedPaymentMethod!=null){
            val cardIdentifier = model.selectedPaymentMethod?.issuer?.toUpperCase() + " **** " +  model.selectedPaymentMethod?.cardNumberWithMask
            ps_card_id_text.text = cardIdentifier
        }
    }

    private fun openCardPicker(){
        val alertDialog = AlertDialog.Builder(this,R.style.CustomDialogTheme)
        val row = layoutInflater.inflate(R.layout.dialog_custom_card_picker,null)
        val recyclerView = row.recyclerCardRow
        val manager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
        alertDialog.setView(row)
        alertDialog.setPositiveButton("Cerrar", null)
        alertDialog.setTitle("Seleccione una Tarjeta")
        val alert = alertDialog.create()
        val window = alert.window
        val wlp = window?.attributes
        wlp?.gravity = Gravity.BOTTOM
        window?.attributes = wlp
        alert.show()
        recyclerView.addOnItemClickListener(object : OnItemClickListener{
            override fun onItemClicked(position: Int, view: View) {
                val viewType = adapter.getItemViewType(position)
                if (viewType == RecyclerCardPoints.PAYMENT_METHOD){
                    viewModel.inputs.paymentMethodChanged(model.paymentMethods[position])
                }
                else{
                    viewModel.inputs.addPaymentMethodButtonPressed()
                }
                alert.dismiss()
            }
        })

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

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    private fun showBiometricPromp(){
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Confirmar pago")
            .setSubtitle("Por favor confirme su acción de pago")
            .setNegativeButtonText("Cancelar")
            .setConfirmationRequired(false)
            .build()

        val biometricPrompt = BiometricPrompt(this,executor,object : BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON){
                    //User clicked negative action
                }
                super.onAuthenticationError(errorCode, errString)
                Log.e("Biometric","Error")

            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.e("Biometric","Success")
                viewModel.biometricAuthStatusAction.onNext(Unit)

            }

            override fun onAuthenticationFailed() {

                super.onAuthenticationFailed()
                Log.e("Biometric","Failed")

            }
        })

        biometricPrompt.authenticate(promptInfo)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == requestVerifyPin && resultCode == Activity.RESULT_OK){
            val flag = data?.getBooleanExtra("successful",false)
            Log.e("PaymentSelection","Code $flag")
            if(flag == true){
                viewModel.pinSecurityCodeStatusAction.onNext(Unit)
            }
        }
    }
}
