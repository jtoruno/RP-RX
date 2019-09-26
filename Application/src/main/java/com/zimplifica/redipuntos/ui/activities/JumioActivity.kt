package com.zimplifica.redipuntos.ui.activities

import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.jumio.MobileSDK
import com.jumio.core.enums.JumioCameraPosition
import com.jumio.core.enums.JumioDataCenter
import com.jumio.core.exceptions.MissingPermissionException
import com.jumio.core.exceptions.PlatformNotSupportedException
import com.jumio.nv.NetverifyDocumentData
import com.jumio.nv.NetverifySDK
import com.jumio.nv.data.document.NVDocumentType
import com.jumio.nv.data.document.NVDocumentVariant
import com.zimplifica.redipuntos.BuildConfig
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.models.ManagerNav
import com.zimplifica.redipuntos.viewModels.JumioScanVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_jumio.*
import java.util.concurrent.TimeUnit

@RequiresActivityViewModel(JumioScanVM.ViewModel::class)
class JumioActivity : BaseActivity<JumioScanVM.ViewModel>() {

    companion object {
        private val TAG = "JumioSDK_Netverify"
        private val PERMISSION_REQUEST_CODE_NETVERIFY = 303
        private val PRESELECTED_COUNTRY = "CRI"
    }

    private var apiToken: String = BuildConfig.JUMIO_API_TOKEN
    private var apiSecret: String = BuildConfig.JUMIO_API_SECRET
    private val callbackBaseUrl = BuildConfig.JUMIO_CALLBACK_URL
    private lateinit var netverifySDK: NetverifySDK
    private val NETVERIFY_DATACENTER = JumioDataCenter.US

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jumio)
        this.supportActionBar?.title = "Información Básica"
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //supportActionBar!!.setDisplayShowHomeEnabled(true)

        jumio_waiting_animation.repeatCount = 2
        jumio_waiting_animation.addAnimatorListener(object: Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                viewModel.inputs.scanActivity()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })

        compositeDisposable.add(viewModel.outputs.openScan().observeOn(AndroidSchedulers.mainThread()).subscribe {
            scanAction()
        })

        compositeDisposable.add(viewModel.outputs.finishVerificationOutput().observeOn(AndroidSchedulers.mainThread()).subscribe {
            netverifySDK.destroy()
            if (it){

            }else{
                finish()
                val intent = Intent(this, VerifyWalkThActivity::class.java)
                startActivity(intent)
            }
        })

        /*
        compositeDisposable.add(viewModel.outputs.stepCompleted().observeOn(AndroidSchedulers.mainThread()).subscribe {
            finish()
            ManagerNav.getInstance(this).handleNextStep()
        })

        compositeDisposable.add(viewModel.outputs.stepError().observeOn(AndroidSchedulers.mainThread()).subscribe {
            Toast.makeText(this, "Error de lectura, intente de nuevo.",Toast.LENGTH_LONG).show()
        })*/
    }

    private fun initializeNetverifySDK(){
        try{
            if (!NetverifySDK.isSupportedPlatform(this))
                Log.w(TAG, "Device not supported")

            if (NetverifySDK.isRooted(this))
                Log.w(TAG, "Device is rooted")

            netverifySDK = NetverifySDK.create(this, apiToken, apiSecret, NETVERIFY_DATACENTER)

            netverifySDK.setEnableVerification(true)
            netverifySDK.setEnableIdentityVerification(true)
            netverifySDK.setPreselectedCountry(PRESELECTED_COUNTRY)
            val listOfDocTypes = arrayListOf<NVDocumentType>()
            listOfDocTypes.add(NVDocumentType.IDENTITY_CARD)
            netverifySDK.setPreselectedDocumentTypes(listOfDocTypes)
            netverifySDK.setPreselectedDocumentVariant(NVDocumentVariant.PLASTIC)
            val url = callbackBaseUrl + viewModel.environment.currentUser().getCurrentUser()?.id
            netverifySDK.setCallbackUrl(url)
            netverifySDK.setCameraPosition(JumioCameraPosition.BACK)


        } catch (e: PlatformNotSupportedException) {
            Log.e(TAG, "Error in initializeNetverifySDK: ", e)
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        } catch (e1: NullPointerException) {
            Log.e(TAG, "Error in initializeNetverifySDK: ", e1)
        }
    }

    private fun scanAction(){
        initializeNetverifySDK()
        if(checkPermissions(PERMISSION_REQUEST_CODE_NETVERIFY)){
            try {
                if (::netverifySDK.isInitialized) {
                    startActivityForResult(netverifySDK.intent, NetverifySDK.REQUEST_CODE)
                }
            } catch (e: MissingPermissionException) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkPermissions(requestCode: Int): Boolean {
        return if (!MobileSDK.hasAllRequiredPermissions(this)) {
            //Acquire missing permissions.
            val mp = MobileSDK.getMissingPermissions(this)

            ActivityCompat.requestPermissions(this, mp, requestCode)
            //The result is received in onRequestPermissionsResult.
            false
        } else {
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NetverifySDK.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val scanReference = data?.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE)
                val documentData = data?.getParcelableExtra<Parcelable>(NetverifySDK.EXTRA_SCAN_DATA) as? NetverifyDocumentData
                val mrzData = documentData?.mrzData
                Log.e(TAG, documentData.toString())
                viewModel.inputs.finishVerification(true)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                val errorMessage = data?.getStringExtra(NetverifySDK.EXTRA_ERROR_MESSAGE)
                val errorCode = data?.getStringExtra(NetverifySDK.EXTRA_ERROR_CODE)
                Log.e(TAG, errorMessage?:"")
                viewModel.inputs.finishVerification(false)
            }

            //At this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
            //internal resources can be freed.
            //netverifySDK.destroy()
        }
    }

    /*
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }
    */
}
