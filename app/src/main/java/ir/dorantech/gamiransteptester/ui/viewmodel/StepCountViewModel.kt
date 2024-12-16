package ir.dorantech.gamiransteptester.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dorantech.gamiransteptester.domain.usecase.StepCountUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StepCountViewModel @Inject constructor(
    private val stepCountUseCase: StepCountUseCase,
): ViewModel() {
    private val _stepCounterState = MutableStateFlow("")
    val stepCounterState: StateFlow<String> get() = _stepCounterState
    private var stepCounterJob: Job? = null

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