package com.zimplifica.redipuntos

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.mocks.AuthenticationUseCase
import com.zimplifica.redipuntos.mocks.UserUseCase
import com.zimplifica.redipuntos.models.CurrentUser
import com.zimplifica.redipuntos.models.ServerSubscription
import com.zimplifica.redipuntos.services.AuthenticationService
import com.zimplifica.redipuntos.services.GlobalState
import com.zimplifica.redipuntos.services.UserService
import io.reactivex.annotations.NonNull
import org.junit.runner.RunWith
import org.junit.Before
import junit.framework.TestCase
import org.robolectric.annotation.Config
import org.robolectric.RuntimeEnvironment


@RunWith(RPGraddleTestRunner::class)
@Config(manifest = Config.NONE, sdk = [RPGraddleTestRunner.DEFAULT_SDK]/*,application = RPApplication::class*/,application = RPApplication::class)
abstract class RPTestCase : TestCase(){
    //private var application : RPApplication? = null
    private var application : Application? = null
    private var environment: Environment? = null
    @Before
    @Throws(Exception::class)
    public override fun setUp() {
        super.setUp()

        /*
        this.environment = application()!!.component()!!.environment().toBuilder()
            .webEndpoint("nnnnn")
            .sharedPreferences(sharedPref())
            .authenticationUseCase(authenticationService())
            .currentUser(CurrentUser)
            .userUseCase(userUseCaseService())
            .build()*/

        val environment = Environment.builder()
            .webEndpoint("nnnnn")
            .sharedPreferences(sharedPref())
            .authenticationUseCase(authenticationService())
            .currentUser(CurrentUser)
            .userUseCase(userUseCaseService())
            .serverSubscription(ServerSubscription)
            .build()
        this.environment = environment
    }

    @NonNull
    protected fun application(): Application? {
        if (this.application != null) {
            return this.application
        }
        this.application = RuntimeEnvironment.application
        //this.application = RuntimeEnvironment.application as RPApplication
        return this.application
    }
    private fun sharedPref(): SharedPreferences{
        return application()?.applicationContext?.getSharedPreferences("SP",Activity.MODE_PRIVATE)!!
    }

    private fun authenticationService(): AuthenticationService{
        val global = GlobalState(application()!!)
        return AuthenticationService(global,AuthenticationUseCase())
    }

    private fun userUseCaseService() : UserService{
        val global = GlobalState(application()!!)
        return UserService(UserUseCase(),global)
    }

    @NonNull
    protected fun context(): Context {
        return application()!!.applicationContext
    }

    @NonNull
    protected fun environment(): Environment? {
        return this.environment
    }
}