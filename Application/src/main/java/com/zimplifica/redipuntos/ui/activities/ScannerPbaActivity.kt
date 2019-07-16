package com.zimplifica.redipuntos.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.models.IdCardCR
import com.zimplifica.redipuntos.models.Person
import me.dm7.barcodescanner.zbar.ZBarScannerView
import me.dm7.barcodescanner.zxing.ZXingScannerView



class ScannerPbaActivity : AppCompatActivity() , ZXingScannerView.ResultHandler{
     fun handleResult(rawResult: me.dm7.barcodescanner.zbar.Result?) {
        Log.v("ScanPba", rawResult?.contents) // Prints scan results
        Log.v("ScanPba", rawResult?.barcodeFormat.toString()) // Prints the scan format (qrcode, pdf417 etc.)
        if(rawResult!=null){
            try {
                val result = rawResult.contents
                val p: Person
                val d = ByteArray(result.length)
                for(i in d.indices){
                    d[i] = result.codePointAt(i).toByte()
                }
                p = IdCardCR.parse(d)!!
                Log.e("PBA",p.toString())
            }catch (e: Exception) {
                Log.i("CitizenInfo","Error with the parser",e)
                Toast.makeText(this, "Error: No se pudo hacer la transformación del código QR", Toast.LENGTH_LONG).show()
            }
        }
        //mScannerView?.resumeCameraPreview(this)
    }

    private var mScannerView: ZXingScannerView? = null
    private val PERMISSIONS_REQUEST_CAMERA = 100

    override fun handleResult(p0: Result?) {
// Do something with the result here
        Log.v("ScanPba", p0?.text) // Prints scan results
        Log.v("ScanPba", p0?.barcodeFormat?.name) // Prints the scan format (qrcode, pdf417 etc.)
        if(p0!=null){
            try {
                val result = p0.text
                val p: Person
                val d = ByteArray(result.length)
                for(i in d.indices){
                    d[i] = result.codePointAt(i).toByte()
                }
                p = IdCardCR.parse(d)!!
                Log.e("PBA",p.toString())
            }catch (e: Exception) {
                Log.i("CitizenInfo","Error with the parser",e)
                Toast.makeText(this, "Error: No se pudo hacer la transformación del código QR", Toast.LENGTH_LONG).show()
            }
        }

        // If you would like to resume scanning, call this method below:
        //mScannerView?.resumeCameraPreview(this);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)


        mScannerView =  ZXingScannerView(this)
        //mScannerView?.startCamera(1)// Programmatically initialize the scanner view
        setContentView(mScannerView)
        //setContentView(R.layout.activity_scanner_pba)
        val formats = mutableListOf<BarcodeFormat>()
        formats.add(BarcodeFormat.PDF_417)
        formats.add(BarcodeFormat.DATA_MATRIX)
        formats.add(BarcodeFormat.QR_CODE)
        mScannerView?.setFormats(formats)
        mScannerView?.setAutoFocus(true)
        mScannerView?.setAspectTolerance(0.5f)
    }

    override fun onResume() {
        super.onResume()
        mScannerView?.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView?.startCamera()
    }

    override fun onPause() {
        super.onPause()
        mScannerView?.stopCamera()           // Stop camera on pause

    }

}
