package ir.dorantech.gamiransteptester.ui.screen

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import ir.dorantech.gamiransteptester.ui.viewmodel.SettingsInstructionsViewModel

@Composable
fun SettingsInstructionsScreen(
    vm: SettingsInstructionsViewModel = hiltViewModel(),
    onNextClick: () -> Unit,
) {
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

        val isAutoStartOpened = vm.isAutoStartOpened.collectAsState()
        Button(
            onClick = { vm.onAutoStartSetting() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isAutoStartOpened.value,
        ) {
            Text(text = "Go to Auto Start Settings")
        }

        val isAllConditionsMet = vm.isAllConditionsMet.collectAsState()
        Button(
            onClick = onNextClick,
            enabled = isAllConditionsMet.value,
        ) {
            Text(text = "Next")
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                vm.loadPermissionsGranted()
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
        onNextClick = {},
    )
}