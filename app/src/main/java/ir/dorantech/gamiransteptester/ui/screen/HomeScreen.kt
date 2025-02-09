package ir.dorantech.gamiransteptester.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(
    onStepCounterClick: () -> Unit,
    onUserActivityClick: () -> Unit,
    onSensorClick: () -> Unit,
    onLocationPermissionClick: () -> Unit,
    onRunningCounterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = onStepCounterClick
        ) {
            Text(text = "StepCounter")
        }
        Button(
            onClick = onUserActivityClick
        ) {
            Text(text = "User Activity")
        }
        Button(
            onClick = onLocationPermissionClick
        ) {
            Text(text = "Get Location Permissions")
        }
        Button(
            onClick = { onSensorClick() }
        ) {
            Text("Sensor Event Listener")
        }
        Button(
            onClick = { onRunningCounterClick() }
        ) {
            Text("Running counter")
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onStepCounterClick = {},
        onUserActivityClick = {},
        onLocationPermissionClick = {},
        onSensorClick = {},
        onRunningCounterClick = {},

    )
}