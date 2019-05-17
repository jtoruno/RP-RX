package com.zimplifica.redipuntos

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.zimplifica.redipuntos.libs.Environment
import com.zimplifica.redipuntos.mocks.AuthenticationUseCase
import io.reactivex.annotations.NonNull
import org.junit.runner.RunWith
import org.junit.Before
import junit.framework.TestCase
import org.junit.Ignore
import org.robolectric.annotation.Config
import org.robolectric.RuntimeEnvironment


@RunWith(RPGraddleTestRunner::class)
@Config(manifest = Config.NONE, sdk = [RPGraddleTestRunner.DEFAULT_SDK],application = TestRPApp::class)
abstract class RPTestCase : TestCase(){
    private var application : TestRPApp? = null
    private var environment: Environment? = null
    @Before
    @Throws(Exception::class)
    public override fun setUp() {
        super.setUp()

        this.environment = application()!!.component().environment().toBuilder()
            .webEndpoint("nnnnn")
            .sharedPreferences(sharedPref())
            .authenticationUseCase(AuthenticationUseCase())
            .build()
    }

    @NonNull
    protected fun application(): RPApplication? {
        if (this.application != null) {
            return this.application
        }

        this.application = RuntimeEnvironment.application as TestRPApp
        return this.application
    }
    private fun sharedPref(): SharedPreferences{
        return application!!.getSharedPreferences("SP",Activity.MODE_PRIVATE)
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