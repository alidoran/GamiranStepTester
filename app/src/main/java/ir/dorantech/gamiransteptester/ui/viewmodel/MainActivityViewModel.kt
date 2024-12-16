package ir.dorantech.gamiransteptester.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dorantech.gamiransteptester.core.logging.LogManager
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val logManager: LogManager,
) : ViewModel() {

    fun addLogToList(log: String) {
        logManager.addLog(log)
    }
}