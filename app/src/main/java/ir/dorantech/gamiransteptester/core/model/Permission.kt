package ir.dorantech.gamiransteptester.core.model

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("InlinedApi")
@Serializable
enum class Permission( val androidName: String) {
    ACTIVITY_RECOGNITION(android.Manifest.permission.ACTIVITY_RECOGNITION),
    ACCESS_COARSE_LOCATION(android.Manifest.permission.ACCESS_COARSE_LOCATION),
    ACCESS_FINE_LOCATION(android.Manifest.permission.ACCESS_FINE_LOCATION),
    BATTERY_OPTIMIZATION(android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS),
    AUTO_START("android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"),
    POST_NOTIFICATIONS(android.Manifest.permission.POST_NOTIFICATIONS),
}