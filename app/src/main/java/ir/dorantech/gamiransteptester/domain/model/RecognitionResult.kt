package ir.dorantech.gamiransteptester.domain.model

sealed class RecognitionResult {
    data object Success : RecognitionResult()
    data class Error(val message: String) : RecognitionResult()
}