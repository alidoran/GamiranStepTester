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
    onRecordingApiClick: () -> Unit,
    onUserActivityClick: () -> Unit,
    onLocationPermissionClick: () -> Unit,
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
            onClick = onRecordingApiClick
        ) {
            Text(text = "Recording API")
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
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onStepCounterClick = {},
        onRecordingApiClick = {},
        onUserActivityClick = {},
        onLocationPermissionClick = {},
    )
}