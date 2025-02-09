package ir.dorantech.gamiransteptester.util

import com.google.android.gms.location.DetectedActivity

enum class ActivityType(val value: Int) {
    WALKING(DetectedActivity.WALKING),
    RUNNING(DetectedActivity.RUNNING),
    CYCLING(DetectedActivity.ON_BICYCLE),
    STILL(DetectedActivity.STILL),
    TILTING(DetectedActivity.TILTING),
    IN_VEHICLE(DetectedActivity.IN_VEHICLE),
    DRIVING(DetectedActivity.ON_FOOT),
    UNKNOWN(DetectedActivity.UNKNOWN);

    companion object {
        fun fromInt(value: Int): ActivityType {
            return entries.find { it.value == value } ?: UNKNOWN
        }
    }
}