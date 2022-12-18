package com.reactnativecustomtimernotification

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager


class CustomTimerNotificationPackage : ReactPackage {
    override fun createNativeModules(
        reactContext: ReactApplicationContext): List<NativeModule> {
    val modules = ArrayList<NativeModule>()

    modules.add(CustomNotificationModule(reactContext))
    modules.add(CustomTimerNotificationModule(reactContext))
    return modules
}

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return emptyList()
    }
}
