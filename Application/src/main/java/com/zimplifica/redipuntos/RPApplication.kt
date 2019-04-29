package com.zimplifica.redipuntos

import android.app.Application
import com.zimplifica.redipuntos.external.ApplicationComponent
import com.zimplifica.redipuntos.external.DaggerApplicationComponent




open class RPApplication: Application() {

    lateinit var component: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        component = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()

        component().inject(this)

    }

    fun component(): ApplicationComponent {
        return this.component
    }
}