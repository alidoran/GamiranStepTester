package ir.dorantech.gamiransteptester.domain.model

sealed interface UseCaseResult<out T> {
    data class Success<T>(val data: T) : UseCaseResult<T>
    data class Error(val message: String) : UseCaseResult<Nothing>
}