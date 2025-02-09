package ir.dorantech.gamiransteptester.core.model

sealed interface ResultModel<out T> {
    data class Success<T>(val data: T) : ResultModel<T>
    data class Error(val message: String) : ResultModel<Nothing>
}