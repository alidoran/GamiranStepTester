package ir.dorantech.gamiransteptester.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Activity.RECEIVER_NOT_EXPORTED
import android.app.PendingIntent
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dorantech.gamiransteptester.core.broadcast.ActivityRecognitionReceiver
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.domain.model.RecognitionResult
import ir.dorantech.gamiransteptester.domain.usecase.ActivityRecognitionRequestUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserActivityViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val receiver: ActivityRecognitionReceiver,
    private val activityRecognitionRequestUseCase: ActivityRecognitionRequestUseCase,
    private val pendingIntent: PendingIntent,
    private val logManager: LogManager,
) : ViewModel() {
    private val _userActivityState = MutableStateFlow("Not Response Yet")
    val userActivityState: StateFlow<String> get() = _userActivityState

    fun requestActivityTransitionUpdates() {
        viewModelScope.launch {
            activityRecognitionRequestUseCase.invoke(pendingIntent).collect {
                when (it) {
                    is RecognitionResult.Success -> {
                        logManager.addLog("Activity transition updates requested")
                        registerBroadcast()
                        _userActivityState.value = "Loading..."
                        handleActivityTransitionEvents()
                    }

                    is RecognitionResult.Error -> {
                        logManager.addLog("Activity transition updates request failed")
                        _userActivityState.value = it.message
                    }
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag", "InlinedApi")
    private fun registerBroadcast() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ActivityRecognitionReceiver.INTENT_ACTION)

        when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            false -> context.registerReceiver(receiver, intentFilter)
            true -> context.registerReceiver(
                receiver,
                intentFilter,
                RECEIVER_NOT_EXPORTED
            )
        }
    }

    private suspend fun handleActivityTransitionEvents() {
        receiver.eventsFlow.collect { event ->
            logManager.addLog("Activity transition event: ${event.transitionType}")
            _userActivityState.value = event.transitionType.toString()
        }
    }
}