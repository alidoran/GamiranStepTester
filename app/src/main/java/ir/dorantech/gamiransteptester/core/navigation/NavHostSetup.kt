package ir.dorantech.gamiransteptester.core.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ir.dorantech.gamiransteptester.ui.screen.HomeScreen
import ir.dorantech.gamiransteptester.ui.screen.ServiceCounterScreen
import ir.dorantech.gamiransteptester.ui.screen.SensorScreen
import ir.dorantech.gamiransteptester.ui.screen.SettingsInstructionsScreen
import ir.dorantech.gamiransteptester.ui.screen.StepCountScreen
import ir.dorantech.gamiransteptester.ui.screen.UserActivityScreen
import ir.dorantech.gamiransteptester.ui.viewmodel.NavHostViewmodel
import ir.dorantech.gamiransteptester.ui.viewmodel.SettingsInstructionsViewModel

@SuppressLint("InlinedApi")
@Composable
fun NavHostSetup(
    modifier: Modifier = Modifier,
    vmNav: NavHostViewmodel = hiltViewModel(),
    vmSettings: SettingsInstructionsViewModel = hiltViewModel(),
    onRequestPermission: (permissionList: Array<String>) -> Unit,
) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavRoute.Home
    ) {
        composable<NavRoute.Home> {
            val activityRecognitionPermission =
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION)
            val locationPermission = arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            )
            HomeScreen(
                onStepCounterClick = {
                    if (vmNav.checkPermissionsGranted(activityRecognitionPermission))
                        navController.navigate(NavRoute.StepCounter)
                    else onRequestPermission(activityRecognitionPermission)
                },
                onUserActivityClick = {
                    if (vmNav.checkPermissionsGranted(activityRecognitionPermission))
                        navController.navigate(NavRoute.UserActivity)
                    else onRequestPermission(activityRecognitionPermission)
                },
                onLocationPermissionClick = {
                    if (!vmNav.checkPermissionsGranted(locationPermission))
                        onRequestPermission(locationPermission)
                },
                onSensorClick = {
                    navController.navigate(NavRoute.Sensor)
                },
                onRunningCounterClick = {
                    if (vmNav.checkPermissionsGranted(activityRecognitionPermission)) {
                        vmSettings.loadPermissionsGranted()
                        if (!vmSettings.isBatteryOptimizationDisabled.value || !vmSettings.isAutoStartOpened.value)
                            navController.navigate(NavRoute.AppSettings)
                        else navController.navigate(NavRoute.ServiceCounter)
                    } else onRequestPermission(activityRecognitionPermission)
                },
                modifier = Modifier,
            )
        }
        composable<NavRoute.StepCounter> {
            StepCountScreen(
                modifier = Modifier
            )
        }
        composable<NavRoute.UserActivity> {
            UserActivityScreen(
                modifier = Modifier
            )
        }
        composable<NavRoute.Sensor> {
            SensorScreen(
                modifier = Modifier,
            )
        }
        composable<NavRoute.ServiceCounter> {
            ServiceCounterScreen(
                modifier = Modifier,
            )
        }
        composable<NavRoute.AppSettings> {
            SettingsInstructionsScreen(
                onNextClick = { navController.navigate(NavRoute.ServiceCounter) },
            )
        }
    }
}


