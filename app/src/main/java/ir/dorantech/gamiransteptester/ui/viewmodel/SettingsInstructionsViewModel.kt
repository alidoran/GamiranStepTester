package ir.dorantech.gamiransteptester.ui.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dorantech.gamiransteptester.core.datastore.PreferencesHelper
import ir.dorantech.gamiransteptester.core.logging.LogManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsInstructionsViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val preferencesHelper: PreferencesHelper,
    private val logManager: LogManager,
) : ViewModel() {
    private val _isBatteryOptimizationDisabled = MutableStateFlow(false)
    val isBatteryOptimizationDisabled: StateFlow<Boolean> = _isBatteryOptimizationDisabled
    private val _isAllConditionsMet = MutableStateFlow(false)
    val isAllConditionsMet: StateFlow<Boolean> = _isAllConditionsMet
    private val _isAutoStartOpened = MutableStateFlow(false)
    val isAutoStartOpened: StateFlow<Boolean> = _isAutoStartOpened

    init {
        loadAutoStartStatus()
    }

    fun loadPermissionsGranted() {
        isAllConditionsMet()
    }

    private fun loadBatteryOptimizationStatus() {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        _isBatteryOptimizationDisabled.value =
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    private fun loadAutoStartStatus() {
        viewModelScope.launch {
            preferencesHelper.getAutoStartOpened().collect {
                _isAutoStartOpened.value = it
            }
        }
    }

    private fun isAllConditionsMet() {
        loadBatteryOptimizationStatus()
        _isAllConditionsMet.value = _isBatteryOptimizationDisabled.value && isAutoStartOpened.value
    }

    fun onAutoStartSetting(){
        viewModelScope.launch {
            preferencesHelper.setAutoStartOpened(true)
        }
        val intent = Intent()
        intent.setComponent(
            ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try{
            context.startActivity(intent)
        }catch (_: Exception){
            logManager.addLog("No need to open auto start")
            _isAutoStartOpened.value = true
            isAllConditionsMet()
        }
    }

    fun openBatteryOptimizationSettings() {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}