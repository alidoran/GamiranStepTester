package ir.dorantech.gamiransteptester.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ir.dorantech.gamiransteptester.ui.screen.HomeScreen
import ir.dorantech.gamiransteptester.ui.screen.StepCountScreen
import ir.dorantech.gamiransteptester.ui.theme.GamiranStepTesterTheme
import ir.dorantech.gamiransteptester.viewmodel.StepTesterViewModel

@Composable
fun NavHostSetup(
    modifier: Modifier = Modifier,
    onRequestPermission: () -> Unit = {},
) {
    val navController = rememberNavController()
    val vm: StepTesterViewModel = hiltViewModel()
    val permissionResultState = vm.onPermissionResultState.collectAsState()
    if (permissionResultState.value) {
        navController.navigate(NavRoute.StepCounter)
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavRoute.Home
    ) {
        composable<NavRoute.Home> {
            HomeScreen(
                modifier = Modifier,
                onStepCounterClick = onRequestPermission,
                onRecordingApiClick = {},
            )
        }
        composable<NavRoute.StepCounter> {
            StepCountScreen(
                onBackClick = { navController.navigate(NavRoute.Home) },
                modifier = Modifier
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GamiranStepTesterTheme {
        NavHostSetup()
    }
}