package com.zimplifica.redipuntos

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.support.annotation.NonNull

import com.zimplifica.awsplatform.useCases.UseCaseProvider
import com.zimplifica.domain.useCases.AuthenticationUseCase


import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.CurrentUser
import com.zimplifica.redipuntos.services.AuthenticationService
import com.zimplifica.redipuntos.services.GlobalState


@Module
class ApplicationModule(@NonNull application: Application) {
    val application = application
    private val state = GlobalState(application.applicationContext)

    @Provides
    @Singleton
    fun provideEnvironment(
        @NonNull webEndpoint: String,
        @NonNull authenticationUseCase: AuthenticationService,
        @NonNull sharedPreferences: SharedPreferences,
        @NonNull currentUser: CurrentUser
    ): Environment {

        return Environment.builder()
            .webEndpoint(webEndpoint)
            .authenticationUseCase(authenticationUseCase)
            .sharedPreferences(sharedPreferences)
            .currentUser(currentUser)
            .build()
    }


    @Provides
    @Singleton
    fun provideWebEndPoint(): String {
        return "EndPointString"
    }

    @Provides
    @Singleton
    fun provideAuthenticationUseCase() : AuthenticationService {
        val awsProvider =  UseCaseProvider(application.applicationContext)
        return AuthenticationService(state,awsProvider.makeAuthenticationUseCase())
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences("SP", Activity.MODE_PRIVATE)
        //return PreferenceManager.getDefaultSharedPreferences(this.application)
    }

    @Provides
    @Singleton
    fun provideCurrentUser(): CurrentUser{
        return CurrentUser
    }

    /*
    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return this.application
    }*/
}