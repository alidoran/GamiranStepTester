package ir.dorantech.gamiransteptester.core.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ir.dorantech.gamiransteptester.core.model.Permission
import ir.dorantech.gamiransteptester.ui.screen.HomeScreen
import ir.dorantech.gamiransteptester.ui.screen.ServiceCounterScreen
import ir.dorantech.gamiransteptester.ui.screen.SensorScreen
import ir.dorantech.gamiransteptester.ui.screen.SettingsInstructionsScreen
import ir.dorantech.gamiransteptester.ui.screen.StepCountScreen
import ir.dorantech.gamiransteptester.ui.screen.UserActivityScreen
import ir.dorantech.gamiransteptester.ui.viewmodel.SettingsInstructionsViewModel

@SuppressLint("InlinedApi")
@Composable
fun NavHostSetup(
    modifier: Modifier = Modifier,
    vmSettings: SettingsInstructionsViewModel = hiltViewModel(),
    onRequestPermission: (permissionList: Array<Permission>) -> Unit,
) {
    val navController = rememberNavController()
    var neededPermissions: Array<Permission> = emptyArray()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavRoute.Home
    ) {
        composable<NavRoute.Home> {
            HomeScreen(
                onStepCounterClick = {
                    neededPermissions = arrayOf(Permission.ACTIVITY_RECOGNITION)
                    vmSettings.loadAllNeededConditionsMet(neededPermissions)
                    if (vmSettings.isAllConditionsMet.value)
                        navController.navigate(NavRoute.StepCounter)
                    else navController.navigate(NavRoute.AppSettings)
                },
                onUserActivityClick = {
                    neededPermissions = arrayOf(Permission.ACTIVITY_RECOGNITION)
                    vmSettings.loadAllNeededConditionsMet(neededPermissions)
                    if (vmSettings.isAllConditionsMet.value)
                        navController.navigate(NavRoute.UserActivity)
                    else navController.navigate(NavRoute.AppSettings)
                },
                onLocationPermissionClick = {
                    neededPermissions = arrayOf(
                        Permission.ACCESS_COARSE_LOCATION,
                        Permission.ACCESS_FINE_LOCATION,
                    )
                    vmSettings.loadAllNeededConditionsMet(neededPermissions)
                    if (vmSettings.isAllConditionsMet.value)
                        vmSettings.toastMessage("All permissions granted")
                    else navController.navigate(NavRoute.AppSettings)
                },
                onSensorClick = {
                    navController.navigate(NavRoute.Sensor)
                },
                onRunningCounterClick = {
                    neededPermissions = arrayOf(
                        Permission.ACTIVITY_RECOGNITION,
                        Permission.BATTERY_OPTIMIZATION,
                        Permission.AUTO_START
                    )
                    vmSettings.loadAllNeededConditionsMet(neededPermissions)
                    if (vmSettings.isAllConditionsMet.value)
                        navController.navigate(NavRoute.ServiceCounter)
                    else navController.navigate(NavRoute.AppSettings)
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
                onBackClick = { navController.popBackStack() },
                neededPermissions = neededPermissions,
                onRequestPermission = onRequestPermission,
            )
        }
    }
}