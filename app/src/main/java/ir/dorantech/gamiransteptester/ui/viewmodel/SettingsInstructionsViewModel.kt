package ir.dorantech.gamiransteptester.ui.viewmodel

import android.content.Context
import android.os.PowerManager
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsInstructionsViewModel @Inject constructor(
    @ApplicationContext val context: Context,
) : ViewModel() {
    private val _isBatteryOptimizationDisabled = MutableStateFlow(false)
    val isBatteryOptimizationDisabled: StateFlow<Boolean> = _isBatteryOptimizationDisabled
    private val _isAllConditionsMet = MutableStateFlow(false)
    val isAllConditionsMet: StateFlow<Boolean> = _isAllConditionsMet

    fun loadPermissionsGranted() {
        isBatteryOptimizationDisabled()
        isAllConditionsMet()
    }

    private fun isBatteryOptimizationDisabled() {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        _isBatteryOptimizationDisabled.value =
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    private fun isAllConditionsMet() {
        _isAllConditionsMet.value = isBatteryOptimizationDisabled.value
    }
}