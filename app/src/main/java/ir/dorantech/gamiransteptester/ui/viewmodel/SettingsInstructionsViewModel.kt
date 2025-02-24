package ir.dorantech.gamiransteptester.ui.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dorantech.gamiransteptester.core.datastore.PreferencesHelper
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.core.model.Permission
import ir.dorantech.gamiransteptester.domain.usecase.PermissionUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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
    private val _isPostNotificationsEnabled = MutableStateFlow(false)
    val isPostNotificationsEnabled: StateFlow<Boolean> = _isPostNotificationsEnabled
    private val _isAutoStartOpened = MutableStateFlow(false)
    val isAutoStartOpened: StateFlow<Boolean> = _isAutoStartOpened
    var neededPermissions: Array<Permission> = emptyArray()
        private set

    private fun loadBatteryOptimizationStatus(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    private fun loadAutoStartStatus(): Flow<Boolean> = preferencesHelper.getAutoStartOpened()

    private fun loadActivityRecognitionStatus() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionUseCase.checkPermissionsGranted(arrayOf(Permission.ACTIVITY_RECOGNITION))
        } else true


    private fun loadCoarseLocationStatus() =
        permissionUseCase.checkPermissionsGranted(arrayOf(Permission.ACCESS_COARSE_LOCATION))


    private fun loadFineLocationStatus() =
        permissionUseCase.checkPermissionsGranted(arrayOf(Permission.ACCESS_FINE_LOCATION))


    private fun loadPostNotificationsStatus() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionUseCase.checkPermissionsGranted(arrayOf(Permission.POST_NOTIFICATIONS))
        } else true


    suspend fun loadAllNeededConditionsMet() = coroutineScope {
        val statusMap: Map<Permission, suspend () -> Unit> = mapOf(
            Permission.ACTIVITY_RECOGNITION to {
                val status = loadActivityRecognitionStatus()
                _isActivityRecognitionEnabled.emit(status)
            },
            Permission.ACCESS_COARSE_LOCATION to {
                val status = loadCoarseLocationStatus()
                _isCoarseLocationEnabled.emit(status)
            },
            Permission.ACCESS_FINE_LOCATION to {
                val status = loadFineLocationStatus()
                _isFineLocationEnabled.emit(status)
            },
            Permission.BATTERY_OPTIMIZATION to {
                val status = loadBatteryOptimizationStatus()
                _isBatteryOptimizationDisabled.emit(status)
            },
            Permission.AUTO_START to {
                val status = loadAutoStartStatus().first()
                _isAutoStartOpened.emit(status)
            },
            Permission.POST_NOTIFICATIONS to {
                val status = loadPostNotificationsStatus()
                _isPostNotificationsEnabled.emit(status)
            },
        )

        neededPermissions.mapNotNull { permission ->
            statusMap[permission]?.let { async { it() } }
        }.awaitAll()

        _isAllConditionsMet.emit(
            neededPermissions.isNotEmpty() && neededPermissions.all { permission ->
                when (permission) {
                    Permission.ACCESS_COARSE_LOCATION -> _isCoarseLocationEnabled.value
                    Permission.ACCESS_FINE_LOCATION -> _isFineLocationEnabled.value
                    Permission.ACTIVITY_RECOGNITION -> _isActivityRecognitionEnabled.value
                    Permission.BATTERY_OPTIMIZATION -> _isBatteryOptimizationDisabled.value
                    Permission.AUTO_START -> _isAutoStartOpened.value
                    Permission.POST_NOTIFICATIONS -> _isPostNotificationsEnabled.value
                }
            })
    }

    suspend fun onAutoStartSetting() {
        coroutineScope {
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
            coroutineScope {
                _isAutoStartOpened.emit(true)
            }
        }
    }

    fun openBatteryOptimizationSettings() {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    suspend fun updateNeededPermissions(
        neededPermissions: Array<Permission>,
    ) {
        this@SettingsInstructionsViewModel.neededPermissions = neededPermissions
        loadAllNeededConditionsMet()
    }
}