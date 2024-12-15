package ir.dorantech.gamiransteptester.ui.viewmodel

import android.app.PendingIntent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dorantech.gamiransteptester.core.broadcast.ActivityRecognitionReceiver
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.domain.model.RecognitionResult
import ir.dorantech.gamiransteptester.domain.usecase.ActivityRecognitionRequestUseCase
import ir.dorantech.gamiransteptester.domain.usecase.StepCountUseCase
import ir.dorantech.gamiransteptester.core.navigation.NavRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class StepTesterViewModel @Inject constructor(
    private val stepCountUseCase: StepCountUseCase,
    private val receiver: ActivityRecognitionReceiver,
    private val activityRecognitionRequestUseCase: ActivityRecognitionRequestUseCase,
    private val pendingIntent: PendingIntent,
    private val logManager: LogManager

) : ViewModel() {
    private val _onPermissionResultState = MutableStateFlow(false)
    val onPermissionResultState: StateFlow<Boolean> get() = _onPermissionResultState
    private val _chosenScreen = MutableStateFlow<NavRoute?>(NavRoute.Home)
    val chosenScreen: StateFlow<NavRoute?> get() = _chosenScreen
    private val _stepCounterState = MutableStateFlow("")
    val stepCounterState: StateFlow<String> get() = _stepCounterState
    private val _userActivityState = MutableStateFlow("Not Response Yet")
    val userActivityState: StateFlow<String> get() = _userActivityState

    private var stepCounterJob: Job? = null

    fun setPermissionResult(result: Boolean) {
        _onPermissionResultState.value = result
    }

    fun setChosenScreen(screen: NavRoute?) {
        _chosenScreen.value = screen
    }

    fun startStepCounter() {
        stepCounterJob = viewModelScope.launch {
            stepCountUseCase.invoke().collect {
                _stepCounterState.value = it
            }
        }
    }

    fun stopStepCounter() {
        stepCounterJob?.cancel()
    }

    fun requestActivityTransitionUpdates() = viewModelScope.launch {
        activityRecognitionRequestUseCase.invoke(pendingIntent).collect {
            when (it) {
                is RecognitionResult.Success -> {
                    addLogToList("Activity transition updates requested")
                    handleActivityTransitionEvents()
                }
                is RecognitionResult.Error -> {
                    addLogToList("Activity transition updates request failed")
                    _userActivityState.value = it.message
                }
            }
        }
    }

    private suspend fun handleActivityTransitionEvents() {
        receiver.eventsFlow.collect { event ->
            addLogToList("Activity transition event: ${event.transitionType}")
            _userActivityState.value = event.transitionType.toString()
        }
    }

    fun addLogToList(log: String) {
        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SS"))
        logManager.addLog("$time::$log")
    }
}