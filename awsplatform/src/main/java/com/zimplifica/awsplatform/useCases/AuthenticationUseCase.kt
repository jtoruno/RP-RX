package com.zimplifica.awsplatform.useCases

import android.util.Log
import com.amazonaws.AmazonServiceException
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.zimplifica.awsplatform.Utils.AWSErrorDecoder
import com.zimplifica.awsplatform.Utils.ErrorMappingHelper
import com.zimplifica.domain.entities.*
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.useCases.AuthenticationUseCase
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.lang.Exception

class AuthenticationUseCase : AuthenticationUseCase {


    override fun signIn(username: String, password: String): Observable<Result<SignInResult>> {
        val single = Single.create<Result<SignInResult>> create@{ single ->
            AWSMobileClient.getInstance()
                .signIn(username, password, null, object : Callback<com.amazonaws.mobile.client.results.SignInResult> {
                    override fun onResult(result: com.amazonaws.mobile.client.results.SignInResult?) {
                        if (result != null) {
                            Log.i("\uD83D\uDC9A", "SIgnInParameters " + result.parameters)
                            val state = result.signInState
                            val signInResult = SignInResult(state.name)
                            single.onSuccess(Result.success(signInResult))
                        }
                    }

                    override fun onError(e: Exception?) {
                        Log.e("\uD83D\uDD34", "Platform, AuthenticationUseCase,SignIn Error:", e)
                        //val error = AWSErrorDecoder.decodeSignInError(e)
                        val castedError = (e as? AmazonServiceException)?.let {
                            val casted = ErrorMappingHelper(it.errorCode,it.errorMessage, it)
                            return@let AWSErrorDecoder.decodeSignInError(casted)
                        } ?: kotlin.run {
                            return@run AWSErrorDecoder.decodeSignInError(e)
                        }

                        single.onSuccess(Result.failure(castedError))
                    }
                })
        }
        return single.toObservable()
    }

    override fun signUp(userId: String, username: String, password: String): Observable<Result<SignUpResult>> {
        val single = Single.create<Result<SignUpResult>> create@{ single ->
            AWSMobileClient.getInstance().signUp(userId,password, mapOf(Pair("phone_number",username)),null, object: Callback<com.amazonaws.mobile.client.results.SignUpResult>{
                override fun onResult(result: com.amazonaws.mobile.client.results.SignUpResult?) {
                    var state : SignUpState
                    if (result!=null){
                        if(!result.confirmationState){
                            state = SignUpState.unconfirmed
                            val result = SignUpResult(state,username,password)
                            single.onSuccess(Result.success(result))

                        }else{
                            state = SignUpState.confirmed
                            val result = SignUpResult(state,username,password)
                            single.onSuccess(Result.success(result))
                        }
                    }

                }

                override fun onError(e: Exception?) {
                    Log.e("\uD83D\uDD34", "Platform, AuthenticationUseCase,SignUp Error:", e)
                    //val error = AWSErrorDecoder.decodeSignUpError(e)
                    val castedError = (e as? AmazonServiceException)?.let {
                        val casted = ErrorMappingHelper(it.errorCode,it.errorMessage, it)
                        return@let AWSErrorDecoder.decodeSignUpError(casted)
                    } ?: kotlin.run {
                        return@run AWSErrorDecoder.decodeSignUpError(e)
                    }
                    single.onSuccess(Result.failure(castedError))
                }

            })
        }
        return single.toObservable()
    }

    override fun confirmSignUp(userId: String, verificationCode: String): Observable<Result<SignUpConfirmationResult>> {
        val single = Single.create<Result<SignUpConfirmationResult>> create@{ single ->
            AWSMobileClient.getInstance().confirmSignUp(userId, verificationCode, object : Callback<com.amazonaws.mobile.client.results.SignUpResult>{
                override fun onResult(result: com.amazonaws.mobile.client.results.SignUpResult?) {
                    var state : SignUpConfirmationState
                    if (result!=null){
                        if (!result.confirmationState){
                            state = SignUpConfirmationState.unconfirmed
                            val result = SignUpConfirmationResult(state)
                            single.onSuccess(Result.success(result))
                        }else{
                            state = SignUpConfirmationState.confirmed
                            val result = SignUpConfirmationResult(state)
                            single.onSuccess(Result.success(result))
                        }
                    }
                }

                override fun onError(e: Exception?) {
                    Log.e("\uD83D\uDD34", "Platform, AuthenticationUseCase,ConfirmSignUp Error:", e)
                    val error = AWSErrorDecoder.decodeSignUpError(e)
                    single.onSuccess(Result.failure(error))
                }
            })
        }
        return single.toObservable()
    }

    override fun resendVerificationCode(userId: String): Observable<Result<SignUpResendConfirmationResult>> {
        val single = Single.create<Result<SignUpResendConfirmationResult>> create@{ single ->
            AWSMobileClient.getInstance().resendSignUp(userId,object : Callback<com.amazonaws.mobile.client.results.SignUpResult>{
                override fun onResult(result: com.amazonaws.mobile.client.results.SignUpResult?) {
                    var state : SignUpResendConfirmationState
                    if(result!=null){
                        if(!result.confirmationState) {
                            state = SignUpResendConfirmationState.unconfirmed
                            val result = SignUpResendConfirmationResult(state)
                            single.onSuccess(Result.success(result))
                        }else {
                            state = SignUpResendConfirmationState.confirmed
                            val result = SignUpResendConfirmationResult(state)
                            single.onSuccess(Result.success(result))
                        }
                    }
                }

                override fun onError(e: Exception?) {
                    Log.e("\uD83D\uDD34", "Platform, AuthenticationUseCase,ResendVerificationCode Error:", e)
                    val error = AWSErrorDecoder.decodeSignUpError(e)
                    single.onSuccess(Result.failure(error))
                }

            })
        }
        return single.toObservable()
    }
}