package ir.dorantech.gamiransteptester.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import ir.dorantech.gamiransteptester.ui.viewmodel.ServiceCounterViewModel

@Composable
fun ServiceCounterScreen(
    vm: ServiceCounterViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val stepCount = vm.stepCount.collectAsState()
        Text("Step Count: ${stepCount.value}")

        Button(
            onClick = {
                vm.startBroadcastAndSensorService()
            }
        ) {
            Text("Start BroadcastReceiver + StepSensor")
        }

        Button(
            onClick = {
                vm.stopService()
            }) {
            Text("Stop Service")
        }
    }
}

@Composable
@Preview
fun RunningCounterScreenPreview() {
    ServiceCounterScreen()
}