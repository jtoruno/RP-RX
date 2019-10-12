package com.zimplifica.redipuntos.ui.fragments


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.DialogFragment
import com.amulyakhare.textdrawable.TextDrawable
import com.google.android.material.snackbar.Snackbar
import com.zimplifica.domain.entities.PinRequestMode
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.domain.entities.VerificationStatus

import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseFragment
import com.zimplifica.redipuntos.libs.qualifiers.RequiresFragmentViewModel
import com.zimplifica.redipuntos.libs.utils.ProfileStep
import com.zimplifica.redipuntos.libs.utils.ValidationService
import com.zimplifica.redipuntos.models.ManagerNav
import com.zimplifica.redipuntos.ui.activities.*
import com.zimplifica.redipuntos.viewModels.AccountVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.concurrent.Executor

@RequiresFragmentViewModel(AccountVM.ViewModel::class)
class ProfileFragment : BaseFragment<AccountVM.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()
    private val executor = Executor{}

    companion object {
        val requestCreatePin = 1200
        val requestUpdatePin = 1300
        val requestVerifyPin = 1400
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        // Inflate the layout for this fragment
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        //VerifyBiometricAvailable
        if(!biometricAuthAvailable()){
            profile_touch_id.visibility = View.GONE
        }


        //Inputs
        profile_pin.setOnClickListener {
            viewModel.inputs.pinButtonPressed()
        }

        profile_account_info.setOnClickListener {
            viewModel.inputs.accountInformationSelected()
        }

        profile_complete_account_info.setOnClickListener {
            viewModel.inputs.completeAccountInfoButtonPressed()
        }

        profile_privacy.setOnClickListener {
            viewModel.inputs.privacyPolicyButtonPressed()
        }

        profile_about_us.setOnClickListener {
            viewModel.inputs.aboutUsButtonPressed()
        }

        profile_terms_and_conditions.setOnClickListener {
            viewModel.inputs.termsAndConditionsButtonPressed()
        }

        profile_change_password.setOnClickListener {
            viewModel.inputs.changePasswordButtonPressed()
        }

        profile_touch_id.setOnClickListener {
            //showBiometricPromp()
            val state = profile_touch_id_switch.isChecked
            viewModel.inputs.biometricAuthChanged(!state)
        }


        /*
        profile_change_password.setOnClickListener {
            class MyDialogFragment2 : DialogFragment() {
                override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                    val inflater = activity?.layoutInflater
                    val view = inflater?.inflate(R.layout.dialog_rate_commerce,null)
                    val smileRate = view?.findViewById<SmileRating>(R.id.smile_rating)
                    smileRate?.setOnSmileySelectionListener { smiley, _ ->
                        when(smiley){
                            SmileRating.BAD ->{
                                Log.e("Smile","2")
                            }
                            SmileRating.GOOD ->{
                                Log.e("Smile","4")
                            }
                            SmileRating.GREAT ->{
                                Log.e("Smile","5")
                            }
                            SmileRating.OKAY ->{
                                Log.e("Smile","3")
                            }
                            SmileRating.TERRIBLE ->{
                                Log.e("Smile","1")
                            }
                        }
                    }
                    return androidx.appcompat.app.AlertDialog.Builder(activity!!)
                        .setTitle("Comercio")
                        .setView(view)
                        .setNegativeButton("Saltar", null)
                        .create()
                }
            }
            MyDialogFragment2().show(fragmentManager!!,"pbadialog")
        }*/

        profile_log_out.setOnClickListener {
            class MyDialogFragment : DialogFragment() {
                override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                    return androidx.appcompat.app.AlertDialog.Builder(activity!!)
                        .setTitle("Cerrar Sesión")
                        .setMessage("¿Desea salir de la aplicación?")
                        .setPositiveButton("Aceptar"){
                            _,_ -> viewModel.inputs.signOutButtonPressed()
                        }
                        .setNegativeButton("Cancelar", null)
                        .create()
                }
            }
            MyDialogFragment().show(fragmentManager!!,"signOutAlert")
        }

        compositeDisposable.add(viewModel.outputs.aboutUsButton().observeOn(AndroidSchedulers.mainThread()).subscribe {
            val intent = Intent(activity, AboutActivity::class.java)
            startActivity(intent)
        })

        compositeDisposable.add(viewModel.outputs.privacyPolicyButton().observeOn(AndroidSchedulers.mainThread()).subscribe {
            val intent = Intent(activity, PrivacyActivity::class.java)
            startActivity(intent)
        })

        compositeDisposable.add(viewModel.outputs.termsAndConditionsButton().observeOn(AndroidSchedulers.mainThread()).subscribe{
            val intent = Intent(activity, TermsActivity::class.java)
            startActivity(intent)
        })

        compositeDisposable.add(viewModel.outputs.signOutAction().observeOn(AndroidSchedulers.mainThread()).subscribe {
            val intent = Intent(activity, WalkThrough::class.java)
            startActivity(intent)
            activity?.finishAffinity()
        })

        compositeDisposable.add(viewModel.outputs.updateUserInfo().observeOn(AndroidSchedulers.mainThread()).subscribe {
            refreshUI(it)
            refreshProgressBar(it)
        })

        compositeDisposable.add(viewModel.outputs.biometricAuthEnabled().observeOn(AndroidSchedulers.mainThread()).subscribe {
            profile_touch_id_switch.isChecked = viewModel.biometricAuthValue()
        })

        compositeDisposable.add(viewModel.outputs.showBiometricAuthActivationAlert().observeOn(AndroidSchedulers.mainThread()).subscribe {
            class MyDialogFragment : DialogFragment() {
                override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                    return androidx.appcompat.app.AlertDialog.Builder(activity!!)
                        .setTitle("Alerta")
                        .setMessage("¿Deseas utilizar Autenticación Biométrica en vez de tú código PIN?")
                        .setPositiveButton("Aceptar"){
                                _,_ -> viewModel.inputs.biometricAuthChangeAccepted(it)
                        }
                        .setNegativeButton("Cancelar", null)
                        .create()
                }
            }
            MyDialogFragment().show(fragmentManager!!,"BiometricActivationAlert")
        })

        compositeDisposable.add(viewModel.outputs.showUpdatePinAlert().observeOn(AndroidSchedulers.mainThread()).subscribe {
            class MyDialogFragment : DialogFragment() {
                override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                    return androidx.appcompat.app.AlertDialog.Builder(activity!!)
                        .setTitle("PIN Encontrado")
                        .setMessage("Actualmente existe un PIN para tu cuenta. ¿Deseas actualizarlo? Un código de confirmación será enviado a tu número de teléfono.")
                        .setPositiveButton("Aceptar"){
                                _,_ -> viewModel.inputs.showUpdatePinScreen()
                        }
                        .setNegativeButton("Cancelar", null)
                        .create()
                }
            }
            MyDialogFragment().show(fragmentManager!!,"updatePinAlert")
        })

        compositeDisposable.add(viewModel.outputs.changePasswordButtonAction().observeOn(AndroidSchedulers.mainThread()).subscribe {
            class MyDialogFragment : DialogFragment() {
                override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                    return androidx.appcompat.app.AlertDialog.Builder(activity!!)
                        .setTitle("Alerta")
                        .setMessage("¿Estás seguro que deseas cambiar tu contraseña? Un código de confirmación será enviado a tu número de teléfono.")
                        .setPositiveButton("Aceptar"){
                                _,_ -> viewModel.inputs.goToChangePasswordScreen()
                        }
                        .setNegativeButton("Cancelar", null)
                        .create()
                }
            }
            MyDialogFragment().show(fragmentManager!!,"changePasswordAlert")
        })

        compositeDisposable.add(viewModel.outputs.pinButtonAction().observeOn(AndroidSchedulers.mainThread()).subscribe {
            startPinActivity(it)
        })

        compositeDisposable.add(viewModel.outputs.verifyPinSecurityCode().observeOn(AndroidSchedulers.mainThread()).subscribe {
            startPinActivity(PinRequestMode.VERIFY)
        })

        compositeDisposable.add(viewModel.outputs.goToChangePasswordScreenAction().observeOn(AndroidSchedulers.mainThread()).subscribe {
            val intent = Intent(activity,ChangePasswordActivity::class.java)
            startActivity(intent)
        })

        compositeDisposable.add(viewModel.outputs.showAlert().observeOn(AndroidSchedulers.mainThread()).subscribe {
            when(it){
                is Result.success -> {
                    val status = it.value
                    when(status){
                        VerificationStatus.Verifying -> {
                            showAlert("Verificando","Tu verificación se encuentra en proceso, por favor espera mientras te notificamos el resultado")
                        }
                        VerificationStatus.VerifiedInvalid -> {
                            showAlert("Lo sentimos","No hemos podido verificar tu identidad, póngase en contacto con soporte@zimplifica.com para solucionar este problema")
                        }else -> {}
                    }
                }
                is Result.failure -> {
                    Log.e("AccountFragment","Result Failure")
                    showAlert("Es una pena","Tenemos problemas de comunicación, asegúrate de tener conexión a internet y luego intenta de nuevo.")
                }
            }
        })

        compositeDisposable.add(viewModel.outputs.accountInformationAction().subscribe {
            if(it){
                val intent = Intent(activity, AccountInfoActivity::class.java)
                startActivity(intent)
            }else{
                val snackBar = Snackbar.make(view!!,"Primero debe verificar su cuenta",Snackbar.LENGTH_SHORT)
                snackBar.show()
            }
        })

        compositeDisposable.add(viewModel.outputs.completeAccountInfoAction().subscribe {
            ManagerNav.getInstance(activity?.applicationContext!!).initNav()
        })

        viewModel.inputs.onCreate()
    }

    private fun refreshUI(user: UserInformationResult){
        val name = user.nickname
        if (name==null){
            profile_user_welcome.text = "¡Bienvenido Usuario Invitado!"
            setRoundImage(getUserInitials("Usuario Invitado"))
        }else{

            setRoundImage(getUserInitials(name))
            profile_user_welcome.text = "¡Hola $name!"
        }

        profile_user_status.text = "Usuario verificado"
        profile_user_status_icon.setImageResource(R.drawable.ic_verified_user_black_24dp)
    }

    private fun refreshProgressBar(user: UserInformationResult){
        val stepsCompleted = ValidationService.getCompletedStepsCount(user)
        val nextStep = ValidationService.getNextStepToCompleteProfile(user)
        if(nextStep!=null){
            when(nextStep){
                ProfileStep.PhoneNumber -> {
                    val nextStepString = "Añadir teléfono"
                    profile_next_step.text = nextStepString

                }
                ProfileStep.Email -> {
                    val nextStepString = "Añadir Email"
                    profile_next_step.text = nextStepString
                }
                ProfileStep.VerifyEmail -> {
                    val nextStepString = "Verificar Email"
                    profile_next_step.text = nextStepString
                }
                ProfileStep.PaymentMethod -> {
                    val nextStepString = "Añadir método de pago"
                    profile_next_step.text = nextStepString
                }
                ProfileStep.Unknown -> {
                    val nextStepString = ""
                    profile_next_step.text = nextStepString
                }
            }
        }
        showProgressBar(stepsCompleted)
    }

    private fun showProgressBar(progress : Int){
        profile_actual_step_count.text = "Progreso en tu perfil ($progress de 2)"
        if(progress == 0){
            profile_progress_1.setBackgroundColor(resources.getColor(R.color.navDisableColor, null))
            profile_progress_2.setBackgroundColor(resources.getColor(R.color.navDisableColor, null))
            //profile_progress_3.setBackgroundColor(resources.getColor(R.color.navDisableColor, null))
        }
        if(progress == 1){
            profile_progress_1.setBackgroundColor(resources.getColor(R.color.colorAccent, null))
        }
        /*
        if(progress == 2){
            profile_progress_1.setBackgroundColor(resources.getColor(R.color.colorAccent, null))
            profile_progress_2.setBackgroundColor(resources.getColor(R.color.colorAccent, null))

        }*/
        if(progress == 2){
            profile_progress_text.visibility = View.GONE
            profile_complete_account_info.visibility = View.GONE
            profile_progress_1.setBackgroundColor(resources.getColor(R.color.colorAccent, null))
            profile_progress_2.setBackgroundColor(resources.getColor(R.color.colorAccent, null))
            //profile_progress_3.setBackgroundColor(resources.getColor(R.color.colorAccent, null))
        }
    }

    private fun setRoundImage(initials : String){
        val drawable = TextDrawable.builder()
            .beginConfig()
            .width(70)
            .height(70)
            .bold()
            .endConfig()
            .buildRound(initials,activity!!.getColor(R.color.colorPrimaryLight))
        profile_user_initials.setImageDrawable(drawable)

    }

    private fun getUserInitials(username : String) : String {
        val splitedUsername = username.toUpperCase().split(" ")
        var response = ""
        if(splitedUsername.isEmpty()){
            return response
        }
        return if (splitedUsername.size <= 1) {
            splitedUsername[0].first().toString()
        } else {
            val firstInitial = splitedUsername[0].first()
            val secondInitial = splitedUsername[1].first()
            "$firstInitial$secondInitial"
        }


    }

    private fun showAlert(title : String, message: String){
        class MyDialogFragment2 : DialogFragment() {
            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                return androidx.appcompat.app.AlertDialog.Builder(activity!!)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Cerrar",null)
                    .create()
            }
        }
        MyDialogFragment2().show(fragmentManager!!,"showAlert")
    }

    private fun startPinActivity(pinRequestMode: PinRequestMode){
        when(pinRequestMode){
            PinRequestMode.CREATE -> {
                val intent = Intent(context, CreatePinActivity::class.java)
                startActivityForResult(intent,requestCreatePin)
            }
            PinRequestMode.UPDATE -> {
                val intent = Intent(context, UpdatePinActivity::class.java)
                startActivityForResult(intent, requestUpdatePin)
            }
            PinRequestMode.VERIFY -> {
                val intent = Intent(context, VerifyPinActivity::class.java)
                startActivityForResult(intent, requestVerifyPin)
            }

        }
    }

    private fun biometricAuthAvailable() : Boolean{
        val biometricManager = BiometricManager.from(activity!!)
        when(biometricManager.canAuthenticate()){
            BiometricManager.BIOMETRIC_SUCCESS ->{
                Log.i("BiometricManager","App can authenticate using biometrics.")
                return true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->{
                Log.e("BiometricManager","No biometric features available on this device.")
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->{
                Log.e("BiometricManager","Biometric features are currently unavailable.")
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->{
                Log.e("BiometricManager","The user hasn't associated any biometric credentials " +
                        "with their account.")
                return false
            }else -> return false
        }
    }

    private fun showBiometricPromp(){
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Confirmar pago")
            .setSubtitle("Por favor confirme su acción de pago")
            .setNegativeButtonText("Cancelar")
            .setConfirmationRequired(false)
            .build()

        val biometricPrompt = BiometricPrompt(activity!!,executor,object : BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON){
                    //User clicked negative action
                    Toast.makeText(activity,"Cancel",Toast.LENGTH_SHORT).show()
                }
                super.onAuthenticationError(errorCode, errString)

            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                Toast.makeText(activity,"Success",Toast.LENGTH_SHORT).show()

                super.onAuthenticationSucceeded(result)

            }

            override fun onAuthenticationFailed() {
                Toast.makeText(activity,"Fail",Toast.LENGTH_SHORT).show()

                super.onAuthenticationFailed()

            }
        })

        biometricPrompt.authenticate(promptInfo)

    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == requestCreatePin && resultCode == Activity.RESULT_OK){
            val flag = data?.getBooleanExtra("successful",false)
            Log.e("AccountVM","Code $flag")
            if(flag == true){
               showAlert("PIN Actualizado","Código de seguridad actualizado correctamente.")
            }
        }
        if(requestCode == requestUpdatePin && resultCode == Activity.RESULT_OK){
            val flag = data?.getBooleanExtra("successful",false)
            Log.e("AccountVM","Code $flag")
            if(flag == true){
                showAlert("PIN Actualizado","Código de seguridad actualizado correctamente.")
            }
        }
        if(requestCode == requestVerifyPin && resultCode == Activity.RESULT_OK){
            val flag = data?.getBooleanExtra("successful",false)
            Log.e("AccountVM","Code $flag")
            if(flag == true){
                viewModel.pinSecurityCodeStatusAction.onNext(Unit)
            }
        }
    }
}
