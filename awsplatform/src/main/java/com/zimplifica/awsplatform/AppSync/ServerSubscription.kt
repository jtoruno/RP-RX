package com.zimplifica.awsplatform.AppSync

import android.util.Log
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall
import com.amazonaws.rediPuntosAPI.GetNotificationsQuery
import com.amazonaws.rediPuntosAPI.SubscribeToEventsSubscription
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.internal.util.Cancelable
import com.zimplifica.domain.entities.RediSubscription
import com.zimplifica.domain.entities.Result
import com.zimplifica.domain.entities.ServerEvent
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import android.text.format.DateUtils.getRelativeTimeSpanString
import com.amazonaws.rediPuntosAPI.type.GetNotificationsInput
import java.lang.Exception

class ServerSubscription(val userId : String) : RediSubscription {

    private val appSyncClient = AppSyncClient.getClient(AppSyncClient.AppSyncClientMode.PRIVATE)
    private var events = PublishSubject.create<Result<ServerEvent>>()
    private var baseEvent = BehaviorSubject.create<Result<List<ServerEvent>>>()
    private var base2 = BehaviorSubject.createDefault(Result.success(mutableListOf<ServerEvent>()))
    private var subscription : Cancelable?

    init {
        subscription = initSync(userId)
    }

    private fun initSync(userId : String): Cancelable? {
        val s = SubscribeToEventsSubscription.builder().user(userId).build()
        val dInput = GetNotificationsInput.builder().limit(null).nextToken(null).build()
        val d = GetNotificationsQuery.builder().input(dInput).build()
        val bInput = GetNotificationsInput.builder().limit(null).nextToken(null).build()
        val b = GetNotificationsQuery.builder().input(bInput).build()
        return CustomSync(appSyncClient).sync(b,object : GraphQLCall.Callback<GetNotificationsQuery.Data>(){
            override fun onFailure(e: ApolloException) {
                Log.e("🔴","[ServerSubscription] [init] Error. Description: $e")
                baseEvent.onNext(Result.failure(e))
            }

            override fun onResponse(response: Response<GetNotificationsQuery.Data>) {
                Log.e("🔵","Base handler ${response.data()?.notifications?.items()?.size?:0}")
                val trx = response.data()?.notifications
                if (trx!=null){
                    val notifications = trx.items().map { n ->
                        val date = getRelativeTimeSpanString(n.createdAt().toLong())
                        return@map ServerEvent(n.id(),n.origin(),n.type(),n.title(),n.message(),date.toString(),n.data(),n.actionable(),n.triggered(),n.hidden())
                    }
                    baseEvent.onNext(Result.success(notifications))
                }
            }

        },s,object : AppSyncSubscriptionCall.Callback<SubscribeToEventsSubscription.Data>{
            override fun onFailure(e: ApolloException) {
                Log.e("\uD83D\uDD34","[Platform] [UserUseCase] [SubscribeToServerEvents] Error. Description: $e")
                events.onNext(Result.failure(e))            }

            override fun onResponse(response: Response<SubscribeToEventsSubscription.Data>) {
                val result = response.data()?.subscribeToEvents()
                if (result!=null){
                    val date = getRelativeTimeSpanString(result.createdAt().toLong())
                    val event = ServerEvent(result.id(),result.origin(),result.type(),result.title(),result.message(),date.toString(), result.data(),
                        result.actionable(),result.triggered(),result.hidden())
                    events.onNext(Result.success(event))
                }
                else{
                    Log.e("🔴", "[Platform] [UserUseCase] [SubscribeToServerEvents] InternalError")
                    events.onNext(Result.failure(Exception("SubscribeToServerEventsException")))
                }
            }

            override fun onCompleted() {
                Log.e("🚀", "onCompleted Subscription")
            }

        },d,object: GraphQLCall.Callback<GetNotificationsQuery.Data>(){
            override fun onFailure(e: ApolloException) {
                Log.e("🔴","[ServerSubscription] [init] Error. Description: $e")
                baseEvent.onNext(Result.failure(e))            }

            override fun onResponse(response: Response<GetNotificationsQuery.Data>) {
                Log.e("🔵","Delta handler ${response.data()?.notifications?.items()?.size?:0}")
                val trx = response.data()?.notifications
                if (trx!=null){
                    val notifications = trx.items().map { n ->
                        val date = getRelativeTimeSpanString(n.createdAt().toLong())
                        return@map ServerEvent(n.id(),n.origin(),n.type(),n.title(),n.message(),date.toString(),n.data(),n.actionable(),n.triggered(),n.hidden())
                    }
                    baseEvent.onNext(Result.success(notifications))
                }
            }
        },300)
    }

    override fun subscribeToEvents(): Observable<Result<ServerEvent>> {
        return events
    }

    override fun subscribeToBaseEvents(): Observable<Result<List<ServerEvent>>> {
        return baseEvent
    }

    override fun cancel() {
        subscription?.cancel()
    }
}