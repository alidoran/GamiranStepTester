package ir.dorantech.gamiransteptester.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.fitness.LocalRecordingClient
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dorantech.gamiransteptester.domain.usecase.StepCountUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StepTesterViewModel @Inject constructor(
    private val stepCountUseCase: StepCountUseCase
) : ViewModel() {
    private val _onPermissionResultState = MutableStateFlow(false)
    val onPermissionResultState: StateFlow<Boolean> get() = _onPermissionResultState
    private var stepCounterJob: Job? = null

    private val _stepCounterState = MutableStateFlow<String>("")
    val stepCounterState: StateFlow<String> get() = _stepCounterState

    fun onPermissionResult(result: Boolean) {
        _onPermissionResultState.value = result
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
}