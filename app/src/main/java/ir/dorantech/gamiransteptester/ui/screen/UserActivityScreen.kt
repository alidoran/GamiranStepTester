package ir.dorantech.gamiransteptester.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import ir.dorantech.gamiransteptester.ui.viewmodel.UserActivityViewModel

@Composable
fun UserActivityScreen(
    vm: UserActivityViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        val activity = vm.userActivityState.collectAsState()
        Text(text = "User Activity is: ${activity.value}")

        Button(
            onClick = {
                vm.requestActivityTransitionUpdates()
            }
        ) {
            Text(text = "Load User Activity")
        }
    }
}

@Preview
@Composable
fun UserActivityScreenPreview() {
    UserActivityScreen()
}