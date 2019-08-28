package com.zimplifica.redipuntos

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences
import androidx.annotation.NonNull
import com.zimplifica.awsplatform.useCases.UseCaseProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.models.CurrentUser
import com.zimplifica.redipuntos.models.ServerSubscription
import com.zimplifica.redipuntos.services.AuthenticationService
import com.zimplifica.redipuntos.services.GlobalState
import com.zimplifica.redipuntos.services.UserService


@Module
class ApplicationModule(@NonNull application: Application) {
    val application = application
    private val state = GlobalState(application.applicationContext)
    private val awsProvider =  UseCaseProvider(application.applicationContext)

    @Provides
    @Singleton
    fun provideEnvironment(
        @NonNull webEndpoint: String,
        @NonNull authenticationUseCase: AuthenticationService,
        @NonNull sharedPreferences: SharedPreferences,
        @NonNull currentUser: CurrentUser,
        @NonNull userUseCase: UserService,
        @NonNull serverSubscription: ServerSubscription
    ): Environment {

        return Environment.builder()
            .webEndpoint(webEndpoint)
            .authenticationUseCase(authenticationUseCase)
            .sharedPreferences(sharedPreferences)
            .currentUser(currentUser)
            .userUseCase(userUseCase)
            .serverSubscription(serverSubscription)
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

    @Provides
    @Singleton
    fun provideUserUseCase(): UserService{
        return UserService(awsProvider.makeUserUseCase(),state)
    }

    @Provides
    @Singleton
    fun provideServerSubscription() : ServerSubscription{
        return ServerSubscription
    }
    /*
    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return this.application
    }*/
}