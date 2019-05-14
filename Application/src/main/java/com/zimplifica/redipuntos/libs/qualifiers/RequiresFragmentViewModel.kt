package com.zimplifica.redipuntos.libs.qualifiers

import com.zimplifica.redipuntos.libs.FragmentViewModel
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@Inherited
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresFragmentViewModel(val value: KClass<out FragmentViewModel<*>>)