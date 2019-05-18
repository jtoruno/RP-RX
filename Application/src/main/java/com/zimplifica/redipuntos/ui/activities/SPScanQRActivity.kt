package com.zimplifica.redipuntos.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.google.zxing.Result
import com.zimplifica.redipuntos.R
import kotlinx.android.synthetic.main.activity_spscan_qr.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class SPScanQRActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    lateinit var ScannerVIew : ZXingScannerView
    private val PERMISSIONS_REQUEST_CAMERA = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_spscan_qr)
        //request()
        ScannerVIew = scaner_view
        request()

        close_qr.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        ScannerVIew.setResultHandler(this)
        ScannerVIew.startCamera()
    }

    override fun onPause() {
        super.onPause()
        ScannerVIew.stopCamera()
    }

    override fun handleResult(p0: Result?) {
        if(p0!=null){
            Log.e("QR", p0.text)
            val returnIntent = Intent()
            returnIntent.putExtra("qr",p0.text)
            setResult(Activity.RESULT_OK,returnIntent)
            finish()
        }
    }

    private fun request () {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSIONS_REQUEST_CAMERA)
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            //ScannerVIew = scaner_view
            //ScannerVIew = ZXingScannerView(this)
            //setContentView(ScannerVIew)
            ScannerVIew.setResultHandler(this)
            ScannerVIew.startCamera()
        }
    }
}
