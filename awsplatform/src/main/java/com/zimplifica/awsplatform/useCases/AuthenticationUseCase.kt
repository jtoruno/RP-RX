package com.zimplifica.awsplatform.useCases

import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.zimplifica.awsplatform.Utils.AWSErrorDecoder
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.SignUpError
//import com.zimplifica.domain.Entities.Result
import com.zimplifica.domain.entities.SignUpResult
import com.zimplifica.domain.entities.SignUpState
import com.zimplifica.domain.useCases.AuthenticationUseCase
import io.reactivex.Observable
import io.reactivex.Single
import java.lang.Exception

class AuthenticationUseCase : AuthenticationUseCase {
    override fun signUp(userId: String, username: String, password: String): Observable<Result<SignUpResult, SignUpError>> {
        val single = Single.create<Result<SignUpResult, SignUpError>> create@{ single ->
            AWSMobileClient.getInstance().signUp(userId,password, mapOf(Pair("phone_number",username)),null, object: Callback<com.amazonaws.mobile.client.results.SignUpResult>{
                override fun onResult(result: com.amazonaws.mobile.client.results.SignUpResult?) {
                    var state : SignUpState
                    if (result!=null){
                        if(!result.confirmationState){
                            state = SignUpState.unconfirmed
                            val result = SignUpResult(state,username,password)
                            single.onSuccess(Result.sucess(result))

                        }else{
                            state = SignUpState.confirmed
                            val result = SignUpResult(state,username,password)
                            single.onSuccess(Result.sucess(result))
                        }
                    }

                }

                override fun onError(e: Exception?) {
                    //Log.e("\uD83D\uDD34", "Platform, AuthenticationUseCase,SignUp Error:", e)
                    Log.e("Error", "Platform, AuthenticationUseCase,SignUp Error:", e)
                    val error = AWSErrorDecoder.decodeSignUpError(e)
                    single.onSuccess(Result.error(error))
                }

            })
        }
        return single.toObservable()
    }
}