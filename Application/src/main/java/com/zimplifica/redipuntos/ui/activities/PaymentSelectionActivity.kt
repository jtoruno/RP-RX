package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.zimplifica.redipuntos.models.CheckAndPayModel
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

    //lateinit var paymentInformation: PaymentInformation
    lateinit var model : CheckAndPayModel

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

        compositeDisposable.add(viewModel.outputs.addPaymentMethodAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this,AddPaymentMethodActivity::class.java)
                startActivity(intent)
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
        val manager = androidx.recyclerview.widget.LinearLayoutManager(
            this,
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
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
}
