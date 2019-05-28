package com.zimplifica.redipuntos.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.zxing.integration.android.IntentIntegrator
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.CitizenInfoVM
import java.util.*

@RequiresActivityViewModel(CitizenInfoVM.ViewModel::class)
class CitizenInfoActivity : BaseActivity<CitizenInfoVM.ViewModel>() {

    private var TYPES: Collection<String> = Arrays.asList("PDF417")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_citizen_info)
        this.supportActionBar?.title = "Información Básica"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        fun scanBarcode(view: View) {
            IntentIntegrator(this)
                .setOrientationLocked(false)
                //.addExtra("", Intents.Scan.INVERTED_SCAN)
                .setCaptureActivity(ScannerIdActivity::class.java)
                .setDesiredBarcodeFormats(TYPES)
                .initiateScan()
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
