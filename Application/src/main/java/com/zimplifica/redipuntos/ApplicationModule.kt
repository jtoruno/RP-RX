package com.zimplifica.redipuntos

import android.app.Application
import android.content.Context
import android.support.annotation.NonNull

import com.zimplifica.awsplatform.useCases.UseCaseProvider
import com.zimplifica.domain.useCases.AuthenticationUseCase


import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import com.zimplifica.redipuntos.libs.Environment


@Module
class ApplicationModule(@NonNull application: Application) {
    val application = application

    @Provides
    @Singleton
    fun provideEnvironment(
        @NonNull webEndpoint: String,
        @NonNull authenticationUseCase: AuthenticationUseCase
    ): Environment {

        return Environment.builder()
            .webEndpoint(webEndpoint)
            .authenticationUseCase(authenticationUseCase)
            .build()
    }


    @Provides
    @Singleton
    fun provideWebEndPoint(): String {
        return "EndPointString"
    }

    @Provides
    @Singleton
    fun provideAuthenticationUseCase() : AuthenticationUseCase {
        val awsProvider =  UseCaseProvider(application.applicationContext)
        return awsProvider.makeAuthenticationUseCase()
    }


    /*
    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return this.application
    }*/
}