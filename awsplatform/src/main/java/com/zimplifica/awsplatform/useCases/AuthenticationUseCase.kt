package com.zimplifica.awsplatform.useCases

import android.util.Log
import com.amazonaws.AmazonServiceException
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserState
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.client.results.ForgotPasswordState
import com.amazonaws.rediPuntosAPI.InitPhoneVerificationMutation
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.zimplifica.awsplatform.AppSync.AppSyncClient
import com.zimplifica.awsplatform.AppSync.CacheOperations
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

    private val appSyncClient = AppSyncClient.getClient()
    private val cacheOperations : CacheOperations = CacheOperations()

    override fun verifyPhoneNumber(phoneNumber: String): Observable<Result<Boolean>> {
        val single = Single.create<Result<Boolean>> create@{ single ->
            val verificationRequest = InitPhoneVerificationMutation.builder()
                .username("@")
                .phoneNumber(phoneNumber)
                .build()
            this.appSyncClient.mutate(verificationRequest).enqueue(object : GraphQLCall.Callback<InitPhoneVerificationMutation.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "Platform, AuthenticationUseCase, verifyPhoneNumber Error:", e)
                    single.onSuccess(Result.failure(AWSErrorDecoder.decodeSignUpError(e)))

                }

                override fun onResponse(response: Response<InitPhoneVerificationMutation.Data>) {
                    Log.i("🔵","Verification code sent")
                    single.onSuccess(Result.success(true))
                }

            })
        }
        return single.toObservable()
    }

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

    override fun signUp(userId: String, username: String, password: String, verificationCode: String): Observable<Result<SignUpResult>> {
        val single = Single.create<Result<SignUpResult>> create@{ single ->
            AWSMobileClient.getInstance().signUp(userId,password, mapOf(Pair("phone_number",username)), mapOf(Pair("verificationCode",verificationCode)), object: Callback<com.amazonaws.mobile.client.results.SignUpResult>{
                override fun onResult(result: com.amazonaws.mobile.client.results.SignUpResult?) {
                    var state : SignUpState
                    if (result!=null){
                        if(!result.confirmationState){
                            state = SignUpState.unconfirmed
                            val result1 = SignUpResult(state,username,password)
                            single.onSuccess(Result.success(result1))

                        }else{
                            state = SignUpState.confirmed
                            val result1 = SignUpResult(state,username,password)
                            single.onSuccess(Result.success(result1))
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
    /*
    override fun confirmSignUp(userId: String, verificationCode: String): Observable<Result<SignUpConfirmationResult>> {
        val single = Single.create<Result<SignUpConfirmationResult>> create@{ single ->
            AWSMobileClient.getInstance().confirmSignUp(userId, verificationCode, object : Callback<com.amazonaws.mobile.client.results.SignUpResult>{
                override fun onResult(result: com.amazonaws.mobile.client.results.SignUpResult?) {
                    var state : SignUpConfirmationState
                    if (result!=null){
                        if (!result.confirmationState){
                            state = SignUpConfirmationState.unconfirmed
                            val result1 = SignUpConfirmationResult(state)
                            single.onSuccess(Result.success(result1))
                        }else{
                            state = SignUpConfirmationState.confirmed
                            val result1 = SignUpConfirmationResult(state)
                            single.onSuccess(Result.success(result1))
                        }
                    }
                }

                override fun onError(e: Exception?) {
                    Log.e("\uD83D\uDD34", "Platform, AuthenticationUseCase,ConfirmSignUp Error:", e)
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
    }*/

    override fun resendVerificationCode(userId: String): Observable<Result<SignUpResendConfirmationResult>> {
        val single = Single.create<Result<SignUpResendConfirmationResult>> create@{ single ->
            AWSMobileClient.getInstance().resendSignUp(userId,object : Callback<com.amazonaws.mobile.client.results.SignUpResult>{
                override fun onResult(result: com.amazonaws.mobile.client.results.SignUpResult?) {
                    var state : SignUpResendConfirmationState
                    if(result!=null){
                        if(!result.confirmationState) {
                            state = SignUpResendConfirmationState.unconfirmed
                            val result1 = SignUpResendConfirmationResult(state)
                            single.onSuccess(Result.success(result1))
                        }else {
                            state = SignUpResendConfirmationState.confirmed
                            val result1 = SignUpResendConfirmationResult(state)
                            single.onSuccess(Result.success(result1))
                        }
                    }
                }

                override fun onError(e: Exception?) {
                    Log.e("\uD83D\uDD34", "Platform, AuthenticationUseCase,ResendVerificationCode Error:", e)
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

    override fun forgotPassword(username: String): Observable<Result<ForgotPasswordResult>> {
        val single = Single.create<Result<ForgotPasswordResult>> create@{single->
            AWSMobileClient.getInstance().forgotPassword(username, object: Callback<com.amazonaws.mobile.client.results.ForgotPasswordResult>{
                override fun onResult(result: com.amazonaws.mobile.client.results.ForgotPasswordResult?) {
                    if(result!=null){
                        Log.i("ForgotPassword", "Info ${result.parameters.destination}")
                        var forgotState : com.zimplifica.domain.entities.ForgotPasswordState = com.zimplifica.domain.entities.ForgotPasswordState.confirmationCodeSent
                        var deliveryMedium : UserCodeDeliveryMedium = UserCodeDeliveryMedium.unknown
                        when(result.state){
                            ForgotPasswordState.CONFIRMATION_CODE -> forgotState = com.zimplifica.domain.entities.ForgotPasswordState.confirmationCodeSent
                            ForgotPasswordState.DONE -> forgotState = com.zimplifica.domain.entities.ForgotPasswordState.done
                        }
                        when(result.parameters.deliveryMedium){
                            "some"-> deliveryMedium = UserCodeDeliveryMedium.sms
                            "none"-> deliveryMedium = UserCodeDeliveryMedium.unknown
                        }

                        val userCodeDeliveryDetails = UserCodeDeliveryDetails(deliveryMedium,result.parameters.destination, result.parameters.attributeName)
                        val forgotPasswordResult = ForgotPasswordResult(forgotState,userCodeDeliveryDetails)
                        single.onSuccess(Result.success(forgotPasswordResult))
                    }
                }

                override fun onError(e: Exception?) {
                    Log.e("\uD83D\uDD34", "Platform, AuthenticationUseCase,ForgotPassword Error:", e)
                    val castedError = (e as? AmazonServiceException)?.let {
                        val casted = ErrorMappingHelper(it.errorCode,it.errorMessage, it)
                        return@let AWSErrorDecoder.decodeForgotPasswordError(casted)
                    } ?: kotlin.run {
                        return@run AWSErrorDecoder.decodeForgotPasswordError(e)
                    }
                    single.onSuccess(Result.failure(castedError))
                }

            })
        }
        return single.toObservable()
    }

    override fun confirmForgotPassword(username: String, confirmationCode: String, newPassword: String): Observable<Result<ForgotPasswordResult>> {
        val single = Single.create<Result<ForgotPasswordResult>> create@{ single ->
            AWSMobileClient.getInstance().confirmForgotPassword(newPassword,confirmationCode, object : Callback<com.amazonaws.mobile.client.results.ForgotPasswordResult>{
                override fun onResult(result: com.amazonaws.mobile.client.results.ForgotPasswordResult?) {
                    if(result!=null){
                        var forgotState : com.zimplifica.domain.entities.ForgotPasswordState = com.zimplifica.domain.entities.ForgotPasswordState.confirmationCodeSent
                        var deliveryMedium : UserCodeDeliveryMedium = UserCodeDeliveryMedium.unknown
                        when(result.state){
                            ForgotPasswordState.CONFIRMATION_CODE -> forgotState = com.zimplifica.domain.entities.ForgotPasswordState.confirmationCodeSent
                            ForgotPasswordState.DONE -> forgotState = com.zimplifica.domain.entities.ForgotPasswordState.done
                        }
                        when(result.parameters.deliveryMedium){
                            "some"-> deliveryMedium = UserCodeDeliveryMedium.sms
                            "none"-> deliveryMedium = UserCodeDeliveryMedium.unknown
                        }

                        val userCodeDeliveryDetails = UserCodeDeliveryDetails(deliveryMedium,result.parameters.destination, result.parameters.attributeName)
                        val forgotPasswordResult = ForgotPasswordResult(forgotState,userCodeDeliveryDetails)
                        single.onSuccess(Result.success(forgotPasswordResult))
                    }
                }

                override fun onError(e: Exception?) {
                    Log.e("\uD83D\uDD34", "Platform, AuthenticationUseCase,confirmForgotPassword Error:", e)
                    val castedError = (e as? AmazonServiceException)?.let {
                        val casted = ErrorMappingHelper(it.errorCode,it.errorMessage, it)
                        return@let AWSErrorDecoder.decodeForgotPasswordError(casted)
                    } ?: kotlin.run {
                        return@run AWSErrorDecoder.decodeForgotPasswordError(e)
                    }
                    single.onSuccess(Result.failure(castedError))
                }

            })
        }
        return single.toObservable()
    }

    override fun getCurrentUserState(): Observable<UserStateResult> {
        val single = Single.create<UserStateResult> create@{ single ->
            var state : UserStateResult = UserStateResult.signedOut
            val actual = AWSMobileClient.getInstance().currentUserState().userState
            Log.e("UserState", actual.toString())
            state = when(actual){
                UserState.SIGNED_IN -> UserStateResult.signedIn
                UserState.SIGNED_OUT, UserState.GUEST, UserState.UNKNOWN, UserState.SIGNED_OUT_FEDERATED_TOKENS_INVALID,
                UserState.SIGNED_OUT_USER_POOLS_TOKENS_INVALID -> UserStateResult.signedOut
            }
            Log.e("UserState", state.toString())
            single.onSuccess(state)
        }
        return single.toObservable()
    }

    override fun signOut(): Observable<Result<UserStateResult>> {
        val single = Single.create<Result<UserStateResult>> create@{ single ->
            AWSMobileClient.getInstance().signOut()
            single.onSuccess(Result.success(UserStateResult.signedOut))
        }
        return single.toObservable()
    }

    override fun updateUserAttributes(attributes: Map<String, String>): Observable<Result<Boolean>> {
        val single = Single.create<Result<Boolean>> create@{ single ->
            AWSMobileClient.getInstance().updateUserAttributes(attributes,object: Callback<List<com.amazonaws.mobile.client.results.UserCodeDeliveryDetails>>{
                override fun onResult(result: List<com.amazonaws.mobile.client.results.UserCodeDeliveryDetails>?) {
                    val email = attributes["email"] ?: ""
                    cacheOperations.updateEmail(email)
                    single.onSuccess(Result.success(true))
                }

                override fun onError(e: Exception?) {
                    Log.e("\uD83D\uDD34", "Platform, AuthenticationUseCase, updateUserAttributes Error:", e)
                    val castedError = (e as? AmazonServiceException)?.let {
                        val casted = ErrorMappingHelper(it.errorCode,it.errorMessage, it)
                        return@let AWSErrorDecoder.decodeForgotPasswordError(casted)
                    } ?: kotlin.run {
                        return@run AWSErrorDecoder.decodeForgotPasswordError(e)
                    }
                    single.onSuccess(Result.failure(castedError))
                }

            })
        }
        return single.toObservable()
    }

    override fun verifyEmail(): Observable<Result<String>> {
        val single = Single.create<Result<String>> create@{ single->
            AWSMobileClient.getInstance().verifyUserAttribute("email",object : Callback<com.amazonaws.mobile.client.results.UserCodeDeliveryDetails>{
                override fun onResult(result: com.amazonaws.mobile.client.results.UserCodeDeliveryDetails?) {
                    if(result?.destination != null){
                        single.onSuccess(Result.success(result.destination))
                    }else{
                        val error = Exception("[Platform] [AuthenticationUseCase] [VerifyEmail]")
                        single.onSuccess(Result.failure(error))
                    }
                }

                override fun onError(e: Exception?) {
                    Log.e("\uD83D\uDD34", "Platform, AuthenticationUseCase, verifyEmail Error:", e)
                    val castedError = (e as? AmazonServiceException)?.let {
                        val casted = ErrorMappingHelper(it.errorCode,it.errorMessage, it)
                        return@let AWSErrorDecoder.decodeForgotPasswordError(casted)
                    } ?: kotlin.run {
                        return@run AWSErrorDecoder.decodeForgotPasswordError(e)
                    }
                    single.onSuccess(Result.failure(castedError))
                }

            })
        }
        return single.toObservable()
    }

    override fun confirmEmail(verificationCode: String): Observable<Result<Boolean>> {
        val single = Single.create<Result<Boolean>> create@{single->
            AWSMobileClient.getInstance().confirmVerifyUserAttribute("email",verificationCode,object : Callback<Void>{
                override fun onResult(result: Void?) {
                    cacheOperations.updateEmailStatus(true)
                    single.onSuccess(Result.success(true))
                }

                override fun onError(e: Exception?) {
                    Log.e("\uD83D\uDD34", "Platform, AuthenticationUseCase, confirmEmail Error:", e)
                    val castedError = (e as? AmazonServiceException)?.let {
                        val casted = ErrorMappingHelper(it.errorCode,it.errorMessage, it)
                        return@let AWSErrorDecoder.decodeForgotPasswordError(casted)
                    } ?: kotlin.run {
                        return@run AWSErrorDecoder.decodeForgotPasswordError(e)
                    }
                    single.onSuccess(Result.failure(castedError))
                }

            })
        }
        return single.toObservable()
    }
}