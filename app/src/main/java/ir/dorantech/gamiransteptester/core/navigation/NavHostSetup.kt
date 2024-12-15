package ir.dorantech.gamiransteptester.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ir.dorantech.gamiransteptester.ui.screen.HomeScreen
import ir.dorantech.gamiransteptester.ui.screen.StepCountScreen
import ir.dorantech.gamiransteptester.ui.screen.UserActivityScreen
import ir.dorantech.gamiransteptester.ui.viewmodel.StepTesterViewModel

@Composable
fun NavHostSetup(
    modifier: Modifier = Modifier,
    onRequestPermission: (NavRoute?, permissionList: Array<String>) -> Unit,
) {
    val navController = rememberNavController()
    val vm: StepTesterViewModel = hiltViewModel()
    val permissionResultState by vm.onPermissionResultState.collectAsState()
    val chosenScreen by vm.chosenScreen.collectAsState()

    LaunchedEffect(permissionResultState) {
        if (permissionResultState) {
            chosenScreen?.let { navController.navigate(it) }
            vm.setPermissionResult(false)
        }
    }


    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavRoute.Home
    ) {
        composable<NavRoute.Home> {
            HomeScreen(
                onStepCounterClick = {
                    onRequestPermission(
                        NavRoute.StepCounter,
                        arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION)
                    )
                },
                onRecordingApiClick = {},
                onUserActivityClick = {
                    onRequestPermission(
                        NavRoute.UserActivity,
                        arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION)
                    )
                },
                onLocationPermissionClick = {
                    onRequestPermission(
                        null,
                        arrayOf(
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ),
                    )
                },
                modifier = Modifier,
            )
        }
        composable<NavRoute.StepCounter> {
            StepCountScreen(
                onBackClick = { navController.navigate(NavRoute.Home) },
                modifier = Modifier
            )
        }
        composable<NavRoute.UserActivity> {
            UserActivityScreen(
                modifier = Modifier
            )
        }
    }
}