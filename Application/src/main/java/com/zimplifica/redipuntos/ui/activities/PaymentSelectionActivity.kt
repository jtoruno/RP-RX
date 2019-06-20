package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.OnItemClickListener
import com.zimplifica.redipuntos.extensions.addOnItemClickListener
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.ui.adapters.RecyclerCardPoints
import com.zimplifica.redipuntos.viewModels.PaymentSelectionVM
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_payment_selection.*
import kotlinx.android.synthetic.main.dialog_custom_card_picker.view.*

@RequiresActivityViewModel(PaymentSelectionVM.ViewModel::class)
class PaymentSelectionActivity : BaseActivity<PaymentSelectionVM.ViewModel>() {
    lateinit var adapter : RecyclerCardPoints

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_selection)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Pago"

        adapter = RecyclerCardPoints()

        adapter.setPaymentMethods(this.viewModel.getPaymentMethods())

        this.viewModel.inputs.paymentMethodChanged(viewModel.getPaymentMethods().first())


        this.viewModel.outputs.showVendor().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                payment_selection_vendor_name.text = it.name
                payment_selection_vendor_place.text = it.address
            }

        payment_selection_amount.text = "₡ "+String.format("%,.2f", viewModel.getAmount())

        payment_select_change_method.setOnClickListener {
            openCardPicker()
        }

        this.viewModel.outputs.paymentInformationChangedAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {

            }

        this.viewModel.outputs.paymentMethodChangedAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val cardIdentifier = it.issuer.toUpperCase() + " **** " + it.cardNumberWithMask
                ps_card_id_text.text = cardIdentifier
            }

        viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showDialog("Lo sentimos", it)
            }

        payment_selection_order.text = viewModel.getOrder().item.description
        payment_selection_redipoints.text = "Tienes "+viewModel.rediPoints()+" pts"



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
}
