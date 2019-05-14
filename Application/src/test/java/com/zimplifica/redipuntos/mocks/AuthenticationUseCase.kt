package com.zimplifica.redipuntos.mocks

import android.util.Log
import com.zimplifica.awsplatform.Utils.AWSErrorDecoder
import com.zimplifica.domain.entities.*
import com.zimplifica.domain.useCases.AuthenticationUseCase
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import java.lang.Exception
import com.zimplifica.domain.entities.Result
import kotlin.math.sin

class AuthenticationUseCase : AuthenticationUseCase {

    override fun getCurrentUserState(): Observable<Result<UserStateResult>> {
        return Observable.create create@{ observer ->
            val response = UserStateResult.signedOut
            observer.onNext(Result.success(response))
        }
    }

    override fun signOut(): Observable<Result<UserStateResult>> {
        return Observable.create<Result<UserStateResult>> create@{ observer ->
            val signOutResult = UserStateResult.signedOut
            val result = Result.success(signOutResult)
            observer.onNext(result)
        }
    }

    override fun signIn(username: String, password: String): Observable<Result<SignInResult>> {
        print(username)
       return Observable.create { single ->
            if((username == "+50688889999" || username == "zimple@zimple.com") && password == "123Jose_"){
                val signInResult = SignInResult("signedIn")
                val result = Result.success(signInResult)
                single.onNext(result)
                single.onComplete()
            } else{
                if((username == "+50688554433" || username == "0a64b9c5-0f9e-4439-86af-4b7756e07ee7") && password == "123Zimplista_"){
                    val error = SignInError.userNotConfirmed
                    print(error)
                    single.onNext(Result.failure(error))
                    single.onComplete()
                }
                else{
                    val error = SignInError.invalidCredentials
                    print("Error"+error)
                    val thr : Throwable = Throwable("Invalid")
                    val result = Result.failure(error)

                    //print(result.isFailure)

                    single.onNext(result)
                    single.onComplete()
                }

            }
        }
    }

    override fun signUp(userId: String, username: String, password: String): Observable<Result<SignUpResult>> {
        val single = Single.create<Result<SignUpResult>> create@{ single ->
            if((username == "+50688889999" || username == "zimple@zimple.com") && password == "123Jose_"){
                val signUpResult = SignUpResult(SignUpState.unconfirmed, username, password)
                single.onSuccess(Result.success(signUpResult))
            }
            else{
                val error = SignUpError.usernameExistsException
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }

    override fun confirmSignUp(userId: String, verificationCode: String): Observable<Result<SignUpConfirmationResult>> {
        val single = Single.create<Result<SignUpConfirmationResult>> create@{ single ->
            if(userId == "E621E1F8-C36C-495A-93FC-0C247A3E6E5F" && verificationCode == "123456"){
                val signUpResult = SignUpConfirmationResult(SignUpConfirmationState.unconfirmed)
                single.onSuccess(Result.success(signUpResult))
            }
            else{
                val error = SignUpError.usernameExistsException
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }

    override fun resendVerificationCode(userId: String): Observable<Result<SignUpResendConfirmationResult>> {
        val single = Single.create<Result<SignUpResendConfirmationResult>> create@{ single ->
            if(userId == "E621E1F8-C36C-495A-93FC-0C247A3E6E5F"){
                val signUpResult = SignUpResendConfirmationResult(SignUpResendConfirmationState.unconfirmed)
                single.onSuccess(Result.success(signUpResult))
            }
            else{
                val error = SignUpError.tooManyRequestsException
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }


    override fun forgotPassword(username: String): Observable<Result<ForgotPasswordResult>> {
        val single = Single.create<Result<ForgotPasswordResult>> create@{ single ->
            if(username == "+50688889999" || username == "zimple@zimplifica.com"){
                val userCodeDeliveryDetails = UserCodeDeliveryDetails(UserCodeDeliveryMedium.sms,"+50688889999","phone_number")
                val forgotPasswordResult = ForgotPasswordResult(ForgotPasswordState.confirmationCodeSent, userCodeDeliveryDetails)
                single.onSuccess(Result.success(forgotPasswordResult))
            }else{
                val error = ForgotPasswordError.userNotFound
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }

    override fun confirmForgotPassword(
        username: String,
        confirmationCode: String,
        newPassword: String
    ): Observable<Result<ForgotPasswordResult>> {
        val single = Single.create<Result<ForgotPasswordResult>> create@{ single ->
            if(username == "+50688889999" && newPassword == "123Jose_"){
                val userCodeDeliveryDetails = UserCodeDeliveryDetails(UserCodeDeliveryMedium.sms,"+50688889999","phone_number")
                val forgotPasswordResult = ForgotPasswordResult(ForgotPasswordState.done, userCodeDeliveryDetails)
                single.onSuccess(Result.success(forgotPasswordResult))
            }else{
                val error = ForgotPasswordError.userNotFound
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }

    override fun updateUserAttributes(attributes: Map<String, String>): Observable<Result<Boolean>> {
        val single = Single.create<Result<Boolean>> create@{single->
            val email = attributes["email"]
            if(email != null  &&  email == "dsanchez@zimplifica"){
                single.onSuccess(Result.success(true))
            }else{
                val error = Exception("XXXX")
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }

    override fun verifyEmail(): Observable<Result<String>> {
        val single = Single.create<Result<String>> create@{single->
            single.onSuccess(Result.success("j****@g****.com"))
        }
        return single.toObservable()
    }

    override fun confirmEmail(verificationCode: String): Observable<Result<Boolean>> {
        val single= Single.create<Result<Boolean>> create@{single->
            if (verificationCode == "949494"){
                single.onSuccess(Result.success(true))
            }else{
                val error = Exception("xxxx")
                single.onSuccess(Result.failure(error))
            }
        }
        return single.toObservable()
    }
}