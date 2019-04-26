package com.zimplifica.redipuntos

import android.app.Application
import android.content.Context
import android.support.annotation.NonNull
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
        webEndpoint: String
    ): Environment {

        return Environment.builder()
            .webEndpoint(webEndpoint)
            .build()
    }


    @Provides
    @Singleton
    fun provideWebEndPoint(): String {
        return "EndPointString"
    }
    /*
    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return this.application
    }*/
}