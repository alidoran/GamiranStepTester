package ir.dorantech.gamiransteptester.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dorantech.gamiransteptester.core.datastore.PreferencesHelper
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.core.services.StepCountingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceCounterViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val logManager: LogManager,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {

    init {
        setStepCount()
    }

    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount.asStateFlow()

    fun startBroadcastAndSensorService() {
        val intent = Intent(context, StepCountingService::class.java)
        startForegroundService(context, intent)
    }

    fun stopService() {
        val intent = Intent(context, StepCountingService::class.java)
        context.stopService(intent)
    }

    private fun setStepCount() = viewModelScope.launch {
        preferencesHelper.getTotalSteps().collect {
            _stepCount.emit(it)
        }
    }
}
