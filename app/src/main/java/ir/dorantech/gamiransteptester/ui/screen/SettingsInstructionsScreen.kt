package ir.dorantech.gamiransteptester.ui.screen

import android.annotation.SuppressLint
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import ir.dorantech.gamiransteptester.core.model.Permission
import ir.dorantech.gamiransteptester.ui.viewmodel.SettingsInstructionsViewModel
import kotlinx.coroutines.launch

@SuppressLint("InlinedApi")
@Composable
fun SettingsInstructionsScreen(
    vm: SettingsInstructionsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onRequestPermission: (permissionList: Array<Permission>) -> Unit,
) {
    val neededPermissions = vm.neededPermissions
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Disable Battery Optimization",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "For optimal app performance, please follow the steps:",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Go to Battery Optimization settings and exclude this app from battery optimizations.",
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (neededPermissions.contains(Permission.BATTERY_OPTIMIZATION)) {
            val isBatteryOptimizationDisabled = vm.isBatteryOptimizationDisabled.collectAsState()
            Button(
                onClick = { vm.openBatteryOptimizationSettings() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isBatteryOptimizationDisabled.value,
            ) {
                Text(
                    text = if (isBatteryOptimizationDisabled.value) "Battery Optimization is disabled"
                    else "Go to Battery Optimization Settings"
                )
            }
        }

        if (neededPermissions.contains(Permission.AUTO_START)) {
            val isAutoStartOpened = vm.isAutoStartOpened.collectAsState()
            Button(
                onClick = { coroutineScope.launch { vm.onAutoStartSetting() } },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isAutoStartOpened.value,
            ) {
                Text(text = "Go to Auto Start Settings")
            }
        }

        if (
            neededPermissions.contains(Permission.ACTIVITY_RECOGNITION) &&
            VERSION.SDK_INT >= VERSION_CODES.Q
        ) {
            val isActivityRecognitionEnabled = vm.isActivityRecognitionEnabled.collectAsState()
            Button(
                onClick = { onRequestPermission(arrayOf(Permission.ACTIVITY_RECOGNITION)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isActivityRecognitionEnabled.value,
            ) {
                Text(text = "Go to Activity Recognition Settings")
            }
        }

        if (neededPermissions.contains(Permission.ACCESS_COARSE_LOCATION)) {
            val isCoarseLocationEnabled = vm.isCoarseLocationEnabled.collectAsState()
            Button(
                onClick = { onRequestPermission(arrayOf(Permission.ACCESS_COARSE_LOCATION)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isCoarseLocationEnabled.value,
            ) {
                Text(text = "Go to Coarse Location Settings")
            }
        }

        if (neededPermissions.contains(Permission.ACCESS_FINE_LOCATION)) {
            val isFineLocationEnabled = vm.isFineLocationEnabled.collectAsState()
            Button(
                onClick = { onRequestPermission(arrayOf(Permission.ACCESS_FINE_LOCATION)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isFineLocationEnabled.value,
            ) {
                Text(text = "Go to Fine Location Settings")
            }
        }

        if (
            neededPermissions.contains(Permission.POST_NOTIFICATIONS) &&
            VERSION.SDK_INT >= VERSION_CODES.TIRAMISU
        ) {
            val isPostNotificationsEnabled = vm.isPostNotificationsEnabled.collectAsState()
            Button(
                onClick = { onRequestPermission(arrayOf(Permission.POST_NOTIFICATIONS)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isPostNotificationsEnabled.value,
            ) {
                Text(text = "Go to Post Notifications Settings")
            }
        }

        val isAllConditionsMet = vm.isAllConditionsMet.collectAsState()
        Button(
            onClick = { onBack() },
            modifier = Modifier.fillMaxWidth(),
            enabled = isAllConditionsMet.value,
        ) {
            Text(text = "Back")
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                coroutineScope.launch { vm.loadAllNeededConditionsMet() }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
@Preview
fun SettingsInstructionsScreenPreview() {
    SettingsInstructionsScreen(
        onRequestPermission = {},
        onBack = {},
    )
}