package com.zimplifica.redipuntos.ui.fragments


import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.amulyakhare.textdrawable.TextDrawable
import com.google.android.material.snackbar.Snackbar
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.UserInformationResult
import com.zimplifica.domain.entities.VerificationStatus

import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.capitalizeWords
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

@RequiresFragmentViewModel(AccountVM.ViewModel::class)
class ProfileFragment : BaseFragment<AccountVM.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()

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

        //Inputs
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
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finishAffinity()
        })

        compositeDisposable.add(viewModel.outputs.updateUserInfo().observeOn(AndroidSchedulers.mainThread()).subscribe {
            refreshUI(it)
            refreshProgressBar(it)
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
                        }
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
            Log.e("ProfileF",it.status.status.name)
            ManagerNav.getInstance(activity?.applicationContext!!).initNav()
        })

        viewModel.inputs.onCreate()
    }

    private fun refreshUI(user: UserInformationResult){
        val name1 = (user.userFirstName?:"").toLowerCase()
        val name2 = (user.userLastName?:"").toLowerCase()
        val completeName = ("$name1 $name2").capitalizeWords()
        if (completeName.isEmpty() || completeName == " "){
            profile_user_welcome.text = "¡Bienvenido Usuario Invitado!"
            setRoundImage(getUserInitials("Usuario Invitado"))
        }else{
            val formalName = (name1.split(" ").first() + " " + name2.split(" ").first()).capitalizeWords()
            setRoundImage(getUserInitials(formalName))
            profile_user_welcome.text = "¡Hola $formalName!"
        }

        val status = user.status.status
        when(status){
            VerificationStatus.VerifiedValid -> {
                profile_user_status.text = "Usuario verificado"
                profile_user_status_icon.setImageResource(R.drawable.ic_verified_user_black_24dp)
            }
            VerificationStatus.VerifiedInvalid -> {
                profile_user_status.text = "Usuario no verificado"
                profile_user_status_icon.setImageResource(R.drawable.ic_warning_black_24dp)
            }
            VerificationStatus.Verifying -> {
                profile_user_status.text = "Verificación en proceso"
                profile_user_status_icon.setImageResource(R.drawable.ic_sync_black_24dp )
            }
            else -> {
                profile_user_status.text = "Verificación pendiente"
                profile_user_status_icon.setImageResource(R.drawable.ic_warning_black_24dp)
            }
        }
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
                ProfileStep.VerifyIdentity -> {
                    val nextStepString = "Verificar Identidad"
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
        profile_actual_step_count.text = "Progreso en tu perfil ($progress de 3)"
        if(progress == 0){
            profile_progress_1.setBackgroundColor(resources.getColor(R.color.navDisableColor, null))
            profile_progress_2.setBackgroundColor(resources.getColor(R.color.navDisableColor, null))
            profile_progress_3.setBackgroundColor(resources.getColor(R.color.navDisableColor, null))
        }
        if(progress == 1){
            profile_progress_1.setBackgroundColor(resources.getColor(R.color.colorAccent, null))
        }
        if(progress == 2){
            profile_progress_1.setBackgroundColor(resources.getColor(R.color.colorAccent, null))
            profile_progress_2.setBackgroundColor(resources.getColor(R.color.colorAccent, null))

        }
        if(progress == 3){
            profile_progress_text.visibility = View.GONE
            profile_complete_account_info.visibility = View.GONE
            profile_progress_1.setBackgroundColor(resources.getColor(R.color.colorAccent, null))
            profile_progress_2.setBackgroundColor(resources.getColor(R.color.colorAccent, null))
            profile_progress_3.setBackgroundColor(resources.getColor(R.color.colorAccent, null))
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
        val firstInitial = splitedUsername[0].first()
        val secondInitial = splitedUsername[1].first()
        return "$firstInitial$secondInitial"

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

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }


}
