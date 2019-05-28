package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.AndroidException
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.models.IdCardCR
import com.zimplifica.redipuntos.models.Person
import com.zimplifica.redipuntos.viewModels.CitizenInfoVM
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_citizen_info.*
import java.util.*

@RequiresActivityViewModel(CitizenInfoVM.ViewModel::class)
class CitizenInfoActivity : BaseActivity<CitizenInfoVM.ViewModel>() {

    private var TYPES: Collection<String> = Arrays.asList("PDF417")

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_citizen_info)
        this.supportActionBar?.title = "Información Básica"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        citizen_next_btn.setOnClickListener {
            this.viewModel.inputs.nextButtonPressed()
        }

        this.viewModel.outputs.startScanActivity().observeOn(AndroidSchedulers.mainThread())
            .subscribe { scanBarcode() }

        this.viewModel.outputs.startNextActivity().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                //Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                val intent = Intent(this,ConfirmCitizenInfoActivity::class.java)
                intent.putExtra("citizen",it)
                startActivity(intent)
                finish()
            }
    }

    private fun scanBarcode() {
        IntentIntegrator(this)
            .setOrientationLocked(false)
            //.addExtra("", Intents.Scan.INVERTED_SCAN)
            .setCaptureActivity(ScannerIdActivity::class.java)
            .setDesiredBarcodeFormats(TYPES)
            .initiateScan()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Log.d("Citizen", "Scaneo cancelado")
                //Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                try {
                    val p: Person

                    //Workarround debido a que result.getRawBytes() devuelve un array nulo
                    val d = ByteArray(result.contents.length)
                    for (i in d.indices) {
                        d[i] = result.contents.codePointAt(i).toByte()
                    }
                    p = IdCardCR.parse(d)!!
                    this.viewModel.inputs.personInfo(p)
                    //Toast.makeText(this, p.toString(), Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Log.i("CitizenInfo","Error with the parser",e)
                    Toast.makeText(this, "Error: No se pudo hacer la transformación del código QR", Toast.LENGTH_LONG).show()
                }

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
