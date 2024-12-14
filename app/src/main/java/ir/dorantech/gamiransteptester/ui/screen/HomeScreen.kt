package ir.dorantech.gamiransteptester.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(
    modifier: Modifier,
    onStepCounterClick: () -> Unit,
    onRecordingApiClick: () -> Unit,
    ) {
    Column (
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
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
    }
}