package ir.dorantech.gamiransteptester.ui.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dorantech.gamiransteptester.core.datastore.PreferencesHelper
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.core.model.Permission
import ir.dorantech.gamiransteptester.domain.usecase.PermissionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsInstructionsViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val preferencesHelper: PreferencesHelper,
    private val logManager: LogManager,
    private val permissionUseCase: PermissionUseCase,
) : ViewModel() {
    private val _isBatteryOptimizationDisabled = MutableStateFlow(false)
    val isBatteryOptimizationDisabled: StateFlow<Boolean> = _isBatteryOptimizationDisabled
    private val _isActivityRecognitionEnabled = MutableStateFlow(false)
    val isActivityRecognitionEnabled: StateFlow<Boolean> = _isActivityRecognitionEnabled
    private val _isCoarseLocationEnabled = MutableStateFlow(false)
    val isCoarseLocationEnabled: StateFlow<Boolean> = _isCoarseLocationEnabled
    private val _isFineLocationEnabled = MutableStateFlow(false)
    val isFineLocationEnabled: StateFlow<Boolean> = _isFineLocationEnabled
    private val _isAllConditionsMet = MutableStateFlow(false)
    val isAllConditionsMet: StateFlow<Boolean> = _isAllConditionsMet
    private val _isAutoStartOpened = MutableStateFlow(false)
    val isAutoStartOpened: StateFlow<Boolean> = _isAutoStartOpened
    private lateinit var neededPermissions: Array<Permission>

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

    private fun loadActivityRecognitionStatus() {
        _isActivityRecognitionEnabled.value =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                permissionUseCase.checkPermissionsGranted(
                    arrayOf(Permission.ACTIVITY_RECOGNITION)
                )
            else true
    }

    private fun loadCoarseLocationStatus(){
        _isCoarseLocationEnabled.value =
                permissionUseCase.checkPermissionsGranted(
                    arrayOf(Permission.ACCESS_COARSE_LOCATION))
    }

    private fun loadFineLocationStatus(){
        _isFineLocationEnabled.value =
            permissionUseCase.checkPermissionsGranted(
                arrayOf(Permission.ACCESS_COARSE_LOCATION))
    }

    fun loadAllNeededConditionsMet(permissionList: Array<Permission>) {
        val statusMap = mapOf(
            Permission.ACTIVITY_RECOGNITION to ::loadActivityRecognitionStatus,
            Permission.ACCESS_COARSE_LOCATION to ::loadCoarseLocationStatus,
            Permission.ACCESS_FINE_LOCATION to ::loadFineLocationStatus,
            Permission.BATTERY_OPTIMIZATION to ::loadBatteryOptimizationStatus,
            Permission.AUTO_START to ::loadAutoStartStatus,
        )

        permissionList.forEach { permission ->
            statusMap[permission]?.invoke()
        }

        _isAllConditionsMet.value = permissionList.all { permission ->
            when (permission) {
                Permission.ACCESS_COARSE_LOCATION -> _isCoarseLocationEnabled.value
                Permission.ACCESS_FINE_LOCATION -> _isFineLocationEnabled.value
                Permission.ACTIVITY_RECOGNITION -> _isActivityRecognitionEnabled.value
                Permission.BATTERY_OPTIMIZATION -> _isBatteryOptimizationDisabled.value
                Permission.AUTO_START -> _isAutoStartOpened.value
            }
        }
    }

    fun onAutoStartSetting() {
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
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            logManager.addLog("No need to open auto start")
            _isAutoStartOpened.value = true
            loadAllNeededConditionsMet(neededPermissions)
        }
    }

    fun openBatteryOptimizationSettings() {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun toastMessage(message: String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}