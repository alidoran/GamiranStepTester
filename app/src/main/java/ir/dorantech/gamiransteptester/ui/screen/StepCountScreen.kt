package ir.dorantech.gamiransteptester.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import ir.dorantech.gamiransteptester.ui.viewmodel.StepTesterViewModel

@Composable
fun StepCountScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val vm: StepTesterViewModel = hiltViewModel()
        val stepCountState = vm.stepCounterState.collectAsState()
        val status = rememberSaveable { mutableStateOf("Stop")}

        Text(text = status.value)
        Text(text = stepCountState.value)

        Button(
            onClick = {
                status.value = "Counting"
                vm.startStepCounter()
            },
        ) {
            Text("Start Counting")
        }
        Button(
            onClick = {
                status.value = "Stop"
                vm.stopStepCounter()
            },
        ) {
            Text("Stop Counting")
        }
        Button(
            onClick = onBackClick
        ) {
            Text("Back to Sensors")
        }
    }
}

@Preview
@Composable
fun StepCountScreenPreview() {
    StepCountScreen(
        onBackClick = {}
    )
}