package ir.dorantech.gamiransteptester.ui.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.core.services.StepCountingService
import ir.dorantech.gamiransteptester.util.Const
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceCounterViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val logManager: LogManager
) : ViewModel() {

    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount.asStateFlow()

    private val _serviceData: MutableStateFlow<Int?> = MutableStateFlow(0)
    val serviceData: StateFlow<Int?> get() = _serviceData

    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as StepCountingService.StepCountingBinder
            val stepCountingService = binder.getService()
            isBound = true
            viewModelScope.launch {
                stepCountingService.counterState.collect {
                    _stepCount.value = it
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            _serviceData.value = null
        }
    }

    fun startBroadcastAndSensorService() {
        val intent = Intent(context, StepCountingService::class.java)
        startForegroundService(context, intent)
    }

    fun stopService() {
        val intent = Intent(context, StepCountingService::class.java)
        context.stopService(intent)
    }

    fun setStepCount(count: Int) {
        _stepCount.value = count
    }

    fun bindService() {
        val intent = Intent(context, StepCountingService::class.java)
        // Use applicationContext to avoid context leaks
        context.applicationContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        if (isBound) {
            // Unbind the service using applicationContext to prevent leaking the activity context
            context.applicationContext.unbindService(serviceConnection)
            isBound = false
        }
    }
}
