package ir.dorantech.gamiransteptester.domain.model

data class RecognitionEvent(
    val activityType: String,
    val confidence: Int
)