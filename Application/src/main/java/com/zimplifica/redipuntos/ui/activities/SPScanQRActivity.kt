package com.zimplifica.redipuntos.ui.activities

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.google.zxing.Result
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.SPScanQRVM
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_spscan_qr.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.util.*

@RequiresActivityViewModel(SPScanQRVM.ViewModel::class)
class SPScanQRActivity : BaseActivity<SPScanQRVM.ViewModel>(), ZXingScannerView.ResultHandler {

    private  var ScannerVIew : ZXingScannerView ? = null
    private val PERMISSIONS_REQUEST_CAMERA = 100
    private var qrResult = ""

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_spscan_qr)
        request()

        close_qr.setOnClickListener {
            finish()
        }

        this.viewModel.outputs.getVendorInformationAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val returnIntent = Intent()
                returnIntent.putExtra("qr",it)
                setResult(Activity.RESULT_OK,returnIntent)
                finish()
            }

        this.viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.e("PBA",it)

                spscan_qr_ll.background = resources.getDrawable(android.R.color.holo_red_dark)
                spscan_qr_txt.text = it

                Handler().postDelayed({
                    spscan_qr_ll.background = resources.getDrawable(R.color.homeColorBtn)
                    spscan_qr_txt.text = "Escanear código del comercio"
                },2500)

            }

        spscan_qr_lottie.addAnimatorListener(object: Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                spscan_qr_lottie.visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
    }

    override fun onResume() {
        super.onResume()
        ScannerVIew?.setResultHandler(this)
        ScannerVIew?.startCamera()
    }

    override fun onPause() {
        super.onPause()
        ScannerVIew?.stopCamera()
    }

    override fun handleResult(p0: Result?) {
        if(p0!=null){

            Log.e("QR", p0.text)
            if(qrResult != p0.text){
                qrResult = p0.text
                spscan_qr_txt.text = "Validando"
                val vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibratorService.vibrate(200)
                this.viewModel.inputs.codeFound(qrResult)
            }
        }
        ScannerVIew?.resumeCameraPreview(this)
    }

    private fun request () {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSIONS_REQUEST_CAMERA)
        }else{
            //ScannerVIew = scaner_view
            ScannerVIew = ZXingScannerView(this)
            ScannerVIew = scaner_view
            //setContentView(ScannerVIew)
            ScannerVIew?.setResultHandler(this)
            ScannerVIew?.startCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CAMERA){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                ScannerVIew = ZXingScannerView(this)
                ScannerVIew = scaner_view
                //setContentView(ScannerVIew)
                ScannerVIew?.setResultHandler(this)
                ScannerVIew?.startCamera()
            }
            else{
                Toast.makeText(this,"No se dispone de permisos", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}
