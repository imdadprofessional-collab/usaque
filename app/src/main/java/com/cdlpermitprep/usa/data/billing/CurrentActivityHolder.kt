package com.cdlpermitprep.usa.data.billing

import android.app.Activity
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

/** Lets the billing flow (which requires an Activity) be launched from a ViewModel. */
@Singleton
class CurrentActivityHolder @Inject constructor() {
    private var ref: WeakReference<Activity>? = null

    fun set(activity: Activity) {
        ref = WeakReference(activity)
    }

    fun get(): Activity? = ref?.get()
}
