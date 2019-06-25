package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.MovementDetailVM
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_movement_detail.*
import java.text.SimpleDateFormat
import java.util.*

@RequiresActivityViewModel(MovementDetailVM.ViewModel::class)
class MovementDetailActivity : BaseActivity<MovementDetailVM.ViewModel>() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movement_detail)
        supportActionBar?.title = "Comprobante de Pago"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        mov_detail_info.setOnClickListener {  this.viewModel.inputs.paymentInfoButtonPressed() }
        this.viewModel.outputs.transactionAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                generateQR(it.orderId)
                mov_detail_id.text = it.orderId
                mov_detail_description.text = it.transactionDetail.description
                mov_detail_commerce.text = it.transactionDetail.sitePaymentItem?.vendorName ?: ""
                mov_detail_amount.text = "₡ "+String.format("%,.2f", it.total)
                val firstName = viewModel.environment.currentUser().getCurrentUser()?.userFirstName ?: ""
                val lastName = viewModel.environment.currentUser().getCurrentUser()?.userLastName ?: ""
                mov_detail_user_name.text = ("$firstName $lastName").capitalize()
                val date = Date()
                date.time = it.datetime.toLong()
                mov_detail_date.text = SimpleDateFormat("dd-MMM-yyyy").format(
                    date)
                mov_detail_hour.text = SimpleDateFormat("HH:mm").format(date)
            }
        //imageView11.elevation = 10F
        //imageView12.elevation = 10F

        this.viewModel.outputs.paymentInfoButtonAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val creditCard = it.creditCard ?: return@subscribe
                val list = mutableListOf <Pair<String,String>>()
                val issuer = (it.creditCard?.issuer?:"").toUpperCase()
                if(it.rediPuntos > 0.0){
                    list.add(Pair("RediPuntos",it.rediPuntos.toString()))
                }
                if(it.creditCardRewards> 0.0){
                    list.add(Pair(issuer + " **** " + it.creditCard?.cardNumber.toString(),"₡ "+String.format("%,.2f", it.rediPuntos)))
                }
                if(it.creditCardCharge>0.0){
                    list.add(Pair(issuer + " **** " + it.creditCard?.cardNumber.toString(),"₡ "+String.format("%,.2f", it.creditCardCharge)))
                }
                openDialog(list)
            }
    }

    fun openDialog(list : MutableList<Pair<String,String>>){
        val builder = AlertDialog.Builder(this)
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(10,10,10,10)

        linearLayout.layoutParams = params
        for (item in list){
            val textView = TextView(this)
            textView.text = item.first
            val paramsTxt = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            paramsTxt.setMargins(15,15,15,10)
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
        val dialog = builder.create()
        dialog.show()

    }

    private fun generateQR(id : String){
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(id, BarcodeFormat.QR_CODE, 200, 200)
            /*
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = R.color.colorAccent
                    }
                }
            }
            val bitmapGen = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmapGen.setPixels(pixels, 0, width, 0,0, width, height)*/

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
}
