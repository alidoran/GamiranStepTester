package ir.dorantech.gamiransteptester.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ir.dorantech.gamiransteptester.ui.viewmodel.SensorViewModel

@Composable
fun SensorScreen(
    vm: SensorViewModel = hiltViewModel(),
    modifier: Modifier.Companion
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val stepCount = vm.stepCounterState.collectAsState()
        Text(text = "Step Count: ${stepCount.value}")
    }
}