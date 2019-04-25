package com.zimplifica.redipuntos.libs.qualifiers

import java.lang.annotation.Inherited
import com.zimplifica.redipuntos.libs.ActivityViewModel
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass


@Inherited
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresActivityViewModel(val value: KClass<out ActivityViewModel<*>>)