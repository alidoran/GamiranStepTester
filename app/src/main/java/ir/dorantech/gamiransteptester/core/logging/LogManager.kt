package ir.dorantech.gamiransteptester.core.logging

import androidx.compose.runtime.mutableStateListOf

object LogManager {
    private val _logs = mutableStateListOf<String>()
    val logs: List<String> get() = _logs

    fun addLog(log: String) {
        _logs.add(log)
    }
}