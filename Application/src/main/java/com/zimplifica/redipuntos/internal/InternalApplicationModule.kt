package com.zimplifica.redipuntos.internal

import javax.inject.Singleton
import dagger.Provides
import android.content.SharedPreferences
import android.support.annotation.NonNull
import com.zimplifica.redipuntos.ApplicationModule
import dagger.Module


@Module(includes = [ApplicationModule::class])
class InternalApplicationModule {
    @Provides
    @Singleton
    internal fun provideApiEndpoint(@NonNull baseUrl:String): String {
        return baseUrl + "Internal"
    }
}