package com.zimplifica.redipuntos

import android.app.Application
import android.content.Context
import android.support.annotation.NonNull
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(@NonNull application: Application) {
    val application = application

    @Provides
    @Singleton
    fun providePackageName(application: Application): String {
        return application.packageName
    }

    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return this.application
    }
}