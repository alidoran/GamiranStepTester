package ir.dorantech.gamiransteptester.core.broadcast.util

import com.google.android.gms.location.DetectedActivity
import ir.dorantech.gamiransteptester.core.broadcast.model.DetectActivityResult
import ir.dorantech.gamiransteptester.core.broadcast.model.DetectActivityResult.*

fun getDetectActivityModel(detectedActivity: DetectedActivity): DetectActivityResult {
    return when (detectedActivity.type) {
        DetectedActivity.IN_VEHICLE -> InVehicle(detectedActivity.confidence)
        DetectedActivity.ON_BICYCLE -> OnBicycle(detectedActivity.confidence)
        DetectedActivity.ON_FOOT -> OnFoot(detectedActivity.confidence)
        DetectedActivity.RUNNING -> Running(detectedActivity.confidence)
        DetectedActivity.WALKING -> Walking(detectedActivity.confidence)
        DetectedActivity.STILL -> Still(detectedActivity.confidence)
        DetectedActivity.TILTING -> Tilting(detectedActivity.confidence)
        else -> Unknown(detectedActivity.confidence)
    }
}