package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.zimplifica.domain.entities.Transaction
import com.zimplifica.domain.entities.TransactionStatus
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.capitalizeWords
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.libs.utils.SharedPreferencesUtils
import com.zimplifica.redipuntos.viewModels.MovementDetailVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_movement_detail.*
import kotlinx.android.synthetic.main.app_bar_home.*
import java.text.SimpleDateFormat
import java.util.*

@RequiresActivityViewModel(MovementDetailVM.ViewModel::class)
class MovementDetailActivity : BaseActivity<MovementDetailVM.ViewModel>() {

    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movement_detail)
        supportActionBar?.title = getString(R.string.Payment_receipt)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        progressBar15.visibility = View.GONE
        mov_detail_info.setOnClickListener {  this.viewModel.inputs.paymentInfoButtonPressed() }
        compositeDisposable.add(this.viewModel.outputs.transactionUpdated().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                drawTransaction(it)
            })

        compositeDisposable.add(this.viewModel.outputs.paymentInfoButtonAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val list = mutableListOf <Pair<String,String>>()
                val issuer = (it.creditCard?.issuer?:"").toUpperCase()
                if(it.rediPuntos > 0.0){
                    list.add(Pair(getString(R.string.app_name),it.rediPuntos.toString()))
                }
                /*
                if(it.creditCardRewards> 0.0){
                    list.add(Pair(issuer + " **** " + it.creditCard?.cardNumber.toString(),"₡ "+String.format("%,.2f", it.rediPuntos)))
                }*/
                if(it.creditCardCharge>0.0){
                    list.add(Pair(issuer + " **** " + it.creditCard?.cardNumber.toString(),"₡ "+String.format("%,.2f", it.creditCardCharge)))
                }
                openDialog(list)
            })
    }

    private fun drawTransaction(transaction : Transaction){
        progressBar15.visibility = View.GONE
        generateQR(transaction.id)
        mov_detail_id.text = transaction.id
        mov_detail_commerce.text = transaction.transactionDetail.vendorName
        mov_detail_amount.text = "₡ "+String.format("%,.2f", transaction.total)
        val name = viewModel.environment.currentUser().getCurrentUser()?.nickname?: ""
        mov_detail_user_name.text = (name).toLowerCase().capitalizeWords()
        mov_detail_date.text = transaction.date
        mov_detail_hour.text = transaction.time
        mov_detail_rewards.text = "+ ₡ "+String.format("%,.2f", transaction.rewards)
        when(transaction.status){
            TransactionStatus.fail -> {
                mov_detail_status.text = getString(R.string.Transaction_rejected_title)
                mov_detail_ll.setBackgroundColor(getColor(R.color.red))
                mov_detail_msj.text = getString(R.string.Transaction_rejected_description)
                mov_detail_rewards.visibility = View.GONE
            }
            TransactionStatus.pending -> {
                progressBar15.visibility = View.VISIBLE
                mov_detail_status.text = getString(R.string.Transaction_pending_title)
                mov_detail_ll.setBackgroundColor(getColor(R.color.pendingColor))
                mov_detail_msj.text = getString(R.string.Transaction_pending_description)
            }
            TransactionStatus.success -> {
                mov_detail_status.text = getString(R.string.Transaction_successful_title)
                mov_detail_ll.setBackgroundColor(getColor(R.color.customGreen))
                mov_detail_msj.text = getString(R.string.Transaction_successful_description)
            }
        }
    }

    private fun openDialog(list : MutableList<Pair<String,String>>){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.Way_to_pay))
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(10,80,10,10)

        linearLayout.layoutParams = params
        for (item in list){
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val rowView = inflater.inflate(R.layout.payment_details_row, null)
            val method = rowView.findViewById<TextView>(R.id.payment_details_method)
            val amount = rowView.findViewById<TextView>(R.id.payment_details_amount)
            method.text = item.first
            amount.text = item.second
            linearLayout.addView(rowView)
        }
        builder.setView(linearLayout)
        builder.setPositiveButton(getString(R.string.Close),null)
        val dialog = builder.create()
        dialog.show()

    }

    private fun generateQR(id : String){
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(id, BarcodeFormat.QR_CODE, 200, 200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)

            mov_detail_qr_img.setImageBitmap(bitmap)
        }
        catch (e : WriterException){
            e.printStackTrace()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        navigateToMovements()
        finish()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    private fun navigateToMovements(){
        SharedPreferencesUtils.saveBooleanInSp(this,"nav_to_mov",true)
    }
}
