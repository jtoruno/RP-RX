package com.zimplifica.redipuntos.models

import android.content.Context
import android.content.Intent
import android.util.Log
import com.zimplifica.redipuntos.RPApplication
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.ui.activities.*
/*
class CompleteInfoNavigation(val context: Context, val environment: Environment) {
    enum class Step {
        completeCitizenInfo,completeEmail,verifyEmail,completePaymentMethod,none
    }

    init {
        val step = nextStep()
        show(step)
    }

    private fun nextStep() : Step{
        val userStatus = environment.currentUser().userConfirmationStatus() ?: return Step.none
        if(userStatus.shouldCompleteCitizenInfo){
            return Step.completeCitizenInfo
        }
        else if (userStatus.shouldCompleteEmail){
            return Step.completeEmail
        }
        else if(userStatus.shouldVerifyEmail){
            return if (userStatus.email!=null){
                Step.verifyEmail
            }else{
                Step.completeEmail
            }
        }
        else if(userStatus.shouldCompletePaymentMethod){
            return Step.completePaymentMethod
        }
        else{
            return Step.none
        }
    }

    private fun show(step: Step){
        val userStatus = environment.currentUser().userConfirmationStatus() ?: return
        when(step){
            Step.completeCitizenInfo -> {
                val intent = Intent(context, CitizenInfoActivity::class.java)
                context.startActivity(intent)
            }
            Step.completeEmail -> {
                val intent = Intent(context, CompleteEmailActivity::class.java)
                context.startActivity(intent)
            }
            Step.verifyEmail -> {
                val email = userStatus.email
                if(email!=null){
                    val intent = Intent(context, ConfirmEmailActivity::class.java)
                    intent.putExtra("email",email)
                    context.startActivity(intent)
                }
                else {
                    val intent = Intent(context, CompleteEmailActivity::class.java)
                    context.startActivity(intent)
                }
            }
            Step.completePaymentMethod ->{
                val intent = Intent(context,CompletePaymentActivity::class.java)
                context.startActivity(intent)

            }
            Step.none -> {
                Log.e("CompleteInfoNavigation","None to do")
            }
        }
    }

}*/


class ManagerNav private constructor(context: Context){
    private val context : Context = context
    private val environment : Environment?

    init {
        val app = context.applicationContext as RPApplication
        environment = app.component()?.environment()
    }

    companion object : SingletonHolder<ManagerNav,Context>(::ManagerNav)

    enum class Step {
        completeCitizenInfo,completeEmail,verifyEmail,completePaymentMethod,none
    }

    fun initNav(){
        val step = nextStep()
        show(step)
    }

    private fun nextStep() : ManagerNav.Step {
        val userStatus = environment?.currentUser()?.userConfirmationStatus() ?: return ManagerNav.Step.none
        if(userStatus.shouldCompleteCitizenInfo){
            return ManagerNav.Step.completeCitizenInfo
        }
        else if (userStatus.shouldCompleteEmail){
            return ManagerNav.Step.completeEmail
        }
        else if(userStatus.shouldVerifyEmail){
            return if (userStatus.email!=null){
                ManagerNav.Step.verifyEmail
            }else{
                ManagerNav.Step.completeEmail
            }
        }
        else if(userStatus.shouldCompletePaymentMethod){
            return ManagerNav.Step.completePaymentMethod
        }
        else{
            return ManagerNav.Step.none
        }
    }

    private fun show(step: ManagerNav.Step){
        val userStatus = environment?.currentUser()?.userConfirmationStatus() ?: return
        when(step){
            ManagerNav.Step.completeCitizenInfo -> {
                val intent = Intent(context, CitizenInfoActivity::class.java)
                context.startActivity(intent)
            }
            ManagerNav.Step.completeEmail -> {
                val intent = Intent(context, CompleteEmailActivity::class.java)
                context.startActivity(intent)
            }
            ManagerNav.Step.verifyEmail -> {
                val email = userStatus.email
                if(email!=null){
                    val intent = Intent(context, ConfirmEmailActivity::class.java)
                    intent.putExtra("email",email)
                    context.startActivity(intent)
                }
                else {
                    val intent = Intent(context, CompleteEmailActivity::class.java)
                    context.startActivity(intent)
                }
            }
            ManagerNav.Step.completePaymentMethod ->{
                val intent = Intent(context,CompletePaymentActivity::class.java)
                context.startActivity(intent)

            }
            ManagerNav.Step.none -> {
                Log.e("CompleteInfoNavigation","None to do")
            }
        }
    }

}