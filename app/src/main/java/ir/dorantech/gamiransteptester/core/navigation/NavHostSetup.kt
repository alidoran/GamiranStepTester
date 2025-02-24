package ir.dorantech.gamiransteptester.core.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch

@SuppressLint("InlinedApi")
@Composable
fun NavHostSetup(
    modifier: Modifier = Modifier,
    vm: SettingsInstructionsViewModel = hiltViewModel(),
    onRequestPermission: (permissionList: Array<Permission>) -> Unit,
) {
    val navController = rememberNavController()
    var targetDestination by remember { mutableStateOf<NavRoute?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(vm.neededPermissions) {
        vm.loadAllNeededConditionsMet()
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavRoute.Home
    ) {
        coroutineScope.launch { vm.loadAllNeededConditionsMet() }
        composable<NavRoute.Home> {
            HomeScreen(
                onStepCounterClick = {
                    coroutineScope.launch {
                        vm.updateNeededPermissions(arrayOf(Permission.ACTIVITY_RECOGNITION))
                        targetDestination = NavRoute.StepCounter
                    }
                },
                onUserActivityClick = {
                    coroutineScope.launch {
                        vm.updateNeededPermissions(arrayOf(Permission.ACTIVITY_RECOGNITION))
                        targetDestination = NavRoute.UserActivity
                    }
                },
                onLocationPermissionClick = {
                    coroutineScope.launch {
                        vm.updateNeededPermissions(
                            arrayOf(
                                Permission.ACCESS_COARSE_LOCATION,
                                Permission.ACCESS_FINE_LOCATION
                            )
                        )
                        targetDestination = NavRoute.AppSettings
                    }
                },
                onSensorClick = {
                    navController.navigate(NavRoute.Sensor)
                },
                onRunningCounterClick = {
                    coroutineScope.launch {
                        vm.updateNeededPermissions(
                            arrayOf(
                                Permission.ACTIVITY_RECOGNITION,
                                Permission.BATTERY_OPTIMIZATION,
                                Permission.AUTO_START,
                                Permission.POST_NOTIFICATIONS,
                            )
                        )
                        targetDestination = NavRoute.ServiceCounter
                    }
                },
                modifier = Modifier,
            )
        }
        composable<NavRoute.StepCounter> { StepCountScreen(modifier = Modifier) }
        composable<NavRoute.UserActivity> { UserActivityScreen(modifier = Modifier) }
        composable<NavRoute.Sensor> { SensorScreen(modifier = Modifier) }
        composable<NavRoute.ServiceCounter> { ServiceCounterScreen(modifier = Modifier) }
        composable<NavRoute.AppSettings> {
            SettingsInstructionsScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onRequestPermission = onRequestPermission,
            )
        }
    }

    LaunchedEffect(targetDestination) {
        if (targetDestination != null) {
            if (vm.isAllConditionsMet.value) navController.navigate(targetDestination!!)
            else navController.navigate(NavRoute.AppSettings)
            targetDestination = null
        }
    }
}
