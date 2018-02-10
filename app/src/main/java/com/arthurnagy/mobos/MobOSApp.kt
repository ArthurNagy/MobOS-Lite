package com.arthurnagy.mobos

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric

class MobOSApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(Fabric.Builder(this)
                .kits(Crashlytics.Builder()
                        .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                        .build())
                .build())
    }

}