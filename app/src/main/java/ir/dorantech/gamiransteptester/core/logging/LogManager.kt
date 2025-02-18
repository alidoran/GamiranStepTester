package ir.dorantech.gamiransteptester.core.logging

import androidx.compose.runtime.mutableStateListOf
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LogManager {
    private const val MAX_LOGS = 10_000
    private val _logs = mutableStateListOf<String>()
    val logs: List<String> get() = _logs.reversed()

    fun addLog(log: String) {
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd HH:mm:ss"))
        val callerClass = Throwable().stackTrace[1].className.split(".").last()
        val formattedLog = "$time $callerClass => $log \n"
        synchronized(this) {
            if (_logs.size >= MAX_LOGS) _logs.removeAt(0)
            _logs.add(formattedLog)
        }
    }
}