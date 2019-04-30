package com.zimplifica.redipuntos

import android.content.Context
import com.zimplifica.redipuntos.libs.Environment
import io.reactivex.annotations.NonNull
import org.junit.runner.RunWith
import org.junit.Before
import junit.framework.TestCase
import org.robolectric.annotation.Config
import org.robolectric.RuntimeEnvironment



@RunWith(RPGraddleTestRunner::class)
@Config(manifest = Config.NONE, sdk = [RPGraddleTestRunner.DEFAULT_SDK],application = RPApplication::class)
abstract class RPTestCase : TestCase(){
    private var application : RPApplication? = null
    private var environment: Environment? = null
    @Before
    @Throws(Exception::class)
    public override fun setUp() {
        super.setUp()

        this.environment = application()!!.component().environment().toBuilder()
            .webEndpoint("nnnnn")
            .build()
    }

    @NonNull
    protected fun application(): RPApplication? {
        if (this.application != null) {
            return this.application
        }

        this.application = RuntimeEnvironment.application as RPApplication
        return this.application
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