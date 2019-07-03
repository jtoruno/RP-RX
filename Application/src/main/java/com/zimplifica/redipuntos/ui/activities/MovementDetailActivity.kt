package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
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
        supportActionBar?.title = "Comprobante de Pago"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        progressBar15.visibility = View.GONE
        mov_detail_info.setOnClickListener {  this.viewModel.inputs.paymentInfoButtonPressed() }
        compositeDisposable.add(this.viewModel.outputs.transactionAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                drawTransaction(it)
            })

        compositeDisposable.add(this.viewModel.outputs.paymentInfoButtonAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val creditCard = it.creditCard ?: return@subscribe
                val list = mutableListOf <Pair<String,String>>()
                val issuer = (it.creditCard?.issuer?:"").toUpperCase()
                if(it.rediPuntos > 0.0){
                    list.add(Pair("RediPuntos",it.rediPuntos.toString()))
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
        mov_detail_description.text = transaction.description?:"Sin descripción"
        mov_detail_commerce.text = transaction.transactionDetail.vendorName
        mov_detail_amount.text = "₡ "+String.format("%,.2f", transaction.total)
        val firstName = viewModel.environment.currentUser().getCurrentUser()?.userFirstName ?: ""
        val lastName = viewModel.environment.currentUser().getCurrentUser()?.userLastName ?: ""
        mov_detail_user_name.text = ("$firstName $lastName").toLowerCase().capitalizeWords()
        mov_detail_date.text = transaction.date
        mov_detail_hour.text = transaction.time
        when(transaction.status){
            TransactionStatus.fail -> {
                mov_detail_status.text = "Transacción erronea"
                mov_detail_ll.setBackgroundColor(getColor(R.color.red))
                mov_detail_msj.text = "Pago erroneo, intente de nuevo"
            }
            TransactionStatus.pending -> {
                progressBar15.visibility = View.VISIBLE
                mov_detail_status.text = "Transacción pendiente"
                mov_detail_ll.setBackgroundColor(getColor(R.color.pendingColor))
                mov_detail_msj.text = "Pago pendiente"
            }
            TransactionStatus.success -> {
                mov_detail_status.text = "Transacción exitosa"
                mov_detail_ll.setBackgroundColor(getColor(R.color.customGreen))
                mov_detail_msj.text = "Pago aprobado y completado con éxito"
            }
        }
    }

    private fun openDialog(list : MutableList<Pair<String,String>>){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Forma de Pago")
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(10,50,10,10)

        linearLayout.layoutParams = params
        for (item in list){
            val textView = TextView(this)
            textView.text = item.first
            val paramsTxt = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            paramsTxt.setMargins(30,15,30,10)
            //textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.layoutParams = paramsTxt
            linearLayout.addView(textView)
            val textView2 = TextView(this)
            textView2.text = item.second
            textView2.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END
            textView2.layoutParams = paramsTxt
            linearLayout.addView(textView2)
        }
        builder.setView(linearLayout)
        builder.setPositiveButton("Continuar",null)
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
        finish()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}