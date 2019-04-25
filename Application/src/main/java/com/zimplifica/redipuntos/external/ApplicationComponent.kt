package com.zimplifica.redipuntos.external

import com.zimplifica.redipuntos.ApplicationGraph
import com.zimplifica.redipuntos.internal.InternalApplicationModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [InternalApplicationModule::class])
public interface  ApplicationComponent : ApplicationGraph