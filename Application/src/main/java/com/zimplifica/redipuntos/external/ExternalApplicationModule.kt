package com.zimplifica.redipuntos.external

import com.zimplifica.redipuntos.ApplicationModule
import dagger.Module
import javax.inject.Singleton
import dagger.Provides




@Module(includes = [ApplicationModule::class])
object ExternalApplicationModule {

    @Provides
    @Singleton
    internal fun provideApiEndpoint(): String {
        return "ENDPOINT"
    }
}