package com.zimplifica.redipuntos.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.zimplifica.redipuntos.R

class ScannerIdActivity : AppCompatActivity() , DecoratedBarcodeView.TorchListener {
    private var capture: CaptureManager? = null
    private var barcodeScannerView: DecoratedBarcodeView? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner_id)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onTorchOn() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTorchOff() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
