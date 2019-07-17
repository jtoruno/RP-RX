package com.zimplifica.redipuntos

import android.app.Application
import org.robolectric.annotation.Implements
import org.robolectric.shadows.ShadowApplication

@Implements(Application::class)
class ShadowApplication : ShadowApplication() {
}