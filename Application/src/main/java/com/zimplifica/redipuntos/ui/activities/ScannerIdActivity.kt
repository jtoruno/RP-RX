package com.zimplifica.redipuntos.ui.activities

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.zimplifica.redipuntos.R
import kotlinx.android.synthetic.main.custom_bar_code.*

class ScannerIdActivity : AppCompatActivity() , DecoratedBarcodeView.TorchListener {
    lateinit var capture: CaptureManager
    lateinit var barcodeScannerView: DecoratedBarcodeView
    private var torchFlag = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner_id)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner)
        barcodeScannerView.setTorchListener(this)
        capture = CaptureManager(this,barcodeScannerView)
        capture.initializeFromIntent(intent,savedInstanceState)
        capture.decode()
        if (!hasFlash()){
            torch_bar_code.visibility = View.GONE
        }

        torch_bar_code.setOnClickListener {
            if (torchFlag){
                barcodeScannerView.setTorchOff()
            }else{
                barcodeScannerView.setTorchOn()
            }
            torchFlag=!torchFlag
        }
    }

    override fun onTorchOn() {
        torch_bar_code.setImageResource(R.drawable.ic_highlight_white_24dp)
    }

    override fun onTorchOff() {
        torch_bar_code.setImageResource(R.drawable.ic_highlight_black_24dp)
    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    /**
     * Check if the device's camera has a Flashlight.
     * @return true if there is Flashlight, otherwise false.
     */
    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

}
