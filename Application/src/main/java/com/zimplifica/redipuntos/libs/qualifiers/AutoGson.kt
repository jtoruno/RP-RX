package com.zimplifica.redipuntos.libs.qualifiers

import java.lang.annotation.ElementType
import java.lang.annotation.ElementType.*
import java.lang.annotation.RetentionPolicy
/**
 * Marks an {@link AutoValue @AutoValue}-annotated type for proper Gson serialization.
 * <p>
 * This annotation is needed because the {@linkplain Retention retention} of {@code @AutoValue}
 * does not allow reflection at runtime.
 */
@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
annotation class AutoGson