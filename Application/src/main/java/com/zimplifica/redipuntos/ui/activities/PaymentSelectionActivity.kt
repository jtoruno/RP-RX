package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
import android.view.View
import com.zimplifica.domain.entities.PaymentInformation
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.OnItemClickListener
import com.zimplifica.redipuntos.extensions.addOnItemClickListener
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.ui.adapters.RecyclerCardPoints
import com.zimplifica.redipuntos.viewModels.PaymentSelectionVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_payment_selection.*
import kotlinx.android.synthetic.main.dialog_custom_card_picker.view.*

@RequiresActivityViewModel(PaymentSelectionVM.ViewModel::class)
class PaymentSelectionActivity : BaseActivity<PaymentSelectionVM.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()
    lateinit var adapter : RecyclerCardPoints
    private var applyAwards = false

    lateinit var paymentInformation: PaymentInformation

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_selection)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""
        progressBar13.visibility = View.GONE
        textView29.visibility = View.INVISIBLE

        adapter = RecyclerCardPoints()

        adapter.setPaymentMethods(this.viewModel.getPaymentMethods())

        this.viewModel.inputs.paymentMethodChanged(viewModel.getPaymentMethods().first())


        compositeDisposable.add(this.viewModel.outputs.showVendor().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                payment_selection_vendor_name.text = it.name
                payment_selection_vendor_place.text = it.address
            })

        payment_selection_amount.text = "₡ "+String.format("%,.2f", viewModel.getAmount())
        payment_s_subtotal.text = "₡ "+String.format("%,.2f", viewModel.getAmount())

        payment_select_change_method.setOnClickListener {
            openCardPicker()
        }

        compositeDisposable.add(this.viewModel.outputs.paymentInformationChangedAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                this.paymentInformation = it
                updateData()
            })

        compositeDisposable.add(this.viewModel.outputs.applyRewards().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                this.applyAwards = it
                updateData()
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

        payment_selection_order.text = ""

        payment_s_checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            this.viewModel.inputs.applyRewardsRowPressed(isChecked)
        }

        compositeDisposable.add(viewModel.outputs.finishPaymentProcessingAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this,MovementDetailActivity::class.java)
                intent.putExtra("transaction",it)
                startActivity(intent)
                finish()
            })

        payment_selection_btn.setOnClickListener {  viewModel.inputs.nextButtonPressed() }

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

        payment_s_description.onChange {
            viewModel.inputs.descriptionTextFieldChanged(it)
            if(!it.isNullOrBlank()){
                textView29.visibility = View.VISIBLE
            }
            else{
                textView29.visibility = View.INVISIBLE
            }
        }

        payment_s_checkBox.isChecked = true


    }

    private fun updateData(){
        Log.e("PaymentSelection","Update Data")
        payment_s_fee.text = "₡ "+String.format("%,.2f", paymentInformation.fee)
        payment_s_tax.text = "₡ "+String.format("%,.2f", paymentInformation.tax)
        /*
        payment_s_reward_applied.text = if (applyAwards){
            "- ₡ "+String.format("%,.2f", paymentInformation.rediPoints)
        }else{
            "₡ "+String.format("%,.2f",0.0)
        }*/
        val rediPuntosToApply = if (applyAwards){
            if(paymentInformation.total-paymentInformation.rediPoints < 0.0){
                paymentInformation.total
            }else{
                paymentInformation.rediPoints
            }
        } else { 0.0 }
        payment_s_reward_applied.text = if (applyAwards){
            "- ₡ "+String.format("%,.2f", rediPuntosToApply)
        }else{
            "₡ "+String.format("%,.2f",0.0)
        }


        payment_selection_redipoints.text = "Tienes "+paymentInformation.rediPoints+" pts"

        val total = paymentInformation.total - rediPuntosToApply
        payment_s_total.text = "₡ "+String.format("%,.2f", total)
        payment_s_rewards.text = viewModel.getOrder().rewards.toString() + " pts"

    }

    private fun openCardPicker(){
        val alertDialog = AlertDialog.Builder(this,R.style.CustomDialogTheme)
        val row = layoutInflater.inflate(R.layout.dialog_custom_card_picker,null)
        val recyclerView = row.recyclerCardRow
        val manager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
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
                viewModel.inputs.paymentMethodChanged(viewModel.getPaymentMethods()[position])
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
}
