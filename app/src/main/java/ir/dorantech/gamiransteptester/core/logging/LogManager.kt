package ir.dorantech.gamiransteptester.core.logging

import androidx.compose.runtime.mutableStateListOf
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object LogManager {
    private val _logs = mutableStateListOf<String>()
    val logs: List<String> get() = _logs

    fun addLog(log: String) {
        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SS"))
        _logs.add("$time::$log")
    }
}