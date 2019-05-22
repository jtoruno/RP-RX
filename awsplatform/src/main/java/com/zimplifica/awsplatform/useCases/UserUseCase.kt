package com.zimplifica.awsplatform.useCases

import android.content.Context
import android.util.Log
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers
import com.amazonaws.rediPuntosAPI.GetUserQuery
import com.amazonaws.rediPuntosAPI.GetVendorQuery
import com.amazonaws.rediPuntosAPI.UpdatePersonalInfoMutation
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.zimplifica.awsplatform.AppSync.AppSyncClient
import com.zimplifica.awsplatform.AppSync.CacheOperations
import com.zimplifica.domain.entities.*
import com.zimplifica.domain.useCases.UserUseCase
import io.reactivex.Observable
import io.reactivex.Single
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class UserUseCase : UserUseCase {

    private val appSyncClient = AppSyncClient.getClient()
    private val cacheOperations = CacheOperations()

    override fun getUserInformation(useCache: Boolean): Observable<Result<UserInformationResult>> {
        val single = Single.create<Result<UserInformationResult>> create@{ single->
            val query = GetUserQuery.builder().build()
            val cachePolicy =  if(useCache){
                AppSyncResponseFetchers.CACHE_FIRST
            }else{
                AppSyncResponseFetchers.CACHE_AND_NETWORK
            }
            this.appSyncClient.query(query).responseFetcher(cachePolicy).enqueue(object: GraphQLCall.Callback<GetUserQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [GetUserInformation] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<GetUserQuery.Data>) {
                    if(response.data()!=null){
                        val user = response.data()!!.user
                        val paymentMethods : List<PaymentMethod> = response.data()!!.user.paymentMethods().map { p ->
                            return@map PaymentMethod(p.cardId(),p.cardNumber(),p.expirationDate(),p.issuer(),p.rewards(),p.automaticRedemption())
                        }

                        val userObj = UserInformationResult(user.username(),user.identityNumber(),user.firstName(),user.lastName(),
                            user.birthdate(),user.email(),user.phoneNumber(),user.phoneNumberVerified(),user.emailVerified(),null,user.rewards(),
                            paymentMethods)
                        single.onSuccess(Result.success(userObj))
                    }
                }
            })
        }
        return single.toObservable()
    }

    override fun updateUserInfo(citizen: CitizenInput): Observable<Result<Citizen>> {
        val single = Single.create<Result<Citizen>> create@{ single ->
            val mutation = UpdatePersonalInfoMutation.builder()
                .username(citizen.citizenId)
                .firstName(citizen.firstName)
                .lastName(citizen.lastName)
                .birthdate(citizen.birthDate)
                .identityNumber(citizen.citizenId)
                .build()
            this.appSyncClient.mutate(mutation).enqueue(object: GraphQLCall.Callback<UpdatePersonalInfoMutation.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [UpdateUserInfo] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<UpdatePersonalInfoMutation.Data>) {
                    val result = response.data()?.updatePersonalInfo()
                    if(result!=null){
                        val citizen = Citizen(result.lastName(),result.firstName(), Date(result.birthdate()) ,result.identityNumber())
                        cacheOperations.updateCitizen(citizen)
                        single.onSuccess(Result.success(citizen))
                    }
                }

            })
        }
        return single.toObservable()
    }

    override fun getVendorInformation(vendorId: String): Observable<Result<Vendor>> {
        val single = Single.create<Result<Vendor>> create@{ single ->
            val query = GetVendorQuery.builder().vendorId(vendorId).build()
            this.appSyncClient.query(query).enqueue(object :GraphQLCall.Callback<GetVendorQuery.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.e("\uD83D\uDD34", "[Platform] [UserUseCase] [getVendorInformation] Error.",e)
                    single.onSuccess(Result.failure(e))
                }

                override fun onResponse(response: Response<GetVendorQuery.Data>) {
                    val result = response.data()?.vendor
                    if(result!=null){
                        val vendor = Vendor(result.pk(),result.name(),result.address())
                        single.onSuccess(Result.success(vendor))
                    }
                }

            })
        }
        return single.toObservable()
    }

}