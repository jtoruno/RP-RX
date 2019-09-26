package com.zimplifica.redipuntos.models

import android.content.Context
import android.content.Intent
import android.util.Log
import com.zimplifica.redipuntos.RPApplication
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.libs.utils.ProfileStep
import com.zimplifica.redipuntos.libs.utils.ValidationService
import com.zimplifica.redipuntos.ui.activities.*


class ManagerNav private constructor(private val context: Context){
    private val environment : Environment?

    init {
        val app = context.applicationContext as RPApplication
        environment = app.component()?.environment()
    }

    companion object : SingletonHolder<ManagerNav,Context>(::ManagerNav)

    /*
    enum class Step {
        completeCitizenInfo,completeEmail,verifyEmail,completePaymentMethod,none
    }*/

    fun initNav(){
        val step = nextStep()
        show(step)
    }

    fun handleNextStep(){
        show(nextStep())
    }

    private fun nextStep() : ProfileStep? {
        val userStatus = environment?.currentUser()?.getCurrentUser()
        return ValidationService.getNextStepToCompleteProfile(userStatus)
    }

    private fun show(step: ProfileStep?){
        if (step == null)return
        val userStatus = environment?.currentUser()?.getCurrentUser() ?: return
        when(step){
            ProfileStep.Email -> {
                val intent = Intent(context, CompleteEmailActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            ProfileStep.VerifyEmail -> {
                val email = userStatus.userEmail
                if(email!=null){
                    val intent = Intent(context, ConfirmEmailActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("email",email)
                    context.startActivity(intent)
                }
                else {
                    val intent = Intent(context, CompleteEmailActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
            }
            ProfileStep.PaymentMethod ->{
                val intent = Intent(context,CompletePaymentActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)

            }
            else -> {
                Log.e("CompleteInfoNavigation","None to do")
            }
        }
    }

}