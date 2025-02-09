package ir.dorantech.gamiransteptester.core.model

interface ResultCallback<T> {
    fun onSuccess(data: T)
    fun onError(error: String)
}