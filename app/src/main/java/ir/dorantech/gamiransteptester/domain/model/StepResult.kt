package ir.dorantech.gamiransteptester.domain.model

sealed class StepResult {
    data class Success(val steps: Long) : StepResult()
    data class Error(val message: String) : StepResult()
}