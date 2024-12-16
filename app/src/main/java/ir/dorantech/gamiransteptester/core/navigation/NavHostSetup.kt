package ir.dorantech.gamiransteptester.core.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ir.dorantech.gamiransteptester.ui.screen.HomeScreen
import ir.dorantech.gamiransteptester.ui.screen.StepCountScreen
import ir.dorantech.gamiransteptester.ui.screen.UserActivityScreen
import ir.dorantech.gamiransteptester.ui.viewmodel.NavHostViewmodel

@SuppressLint("InlinedApi")
@Composable
fun NavHostSetup(
    modifier: Modifier = Modifier,
    vm: NavHostViewmodel = hiltViewModel(),
    onRequestPermission: (permissionList: Array<String>) -> Unit,
) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavRoute.Home
    ) {
        composable<NavRoute.Home> {
            val stepCounterPermission = arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION)
            val userActivityPermission = arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION)
            val locationPermission = arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            )
            HomeScreen(
                onStepCounterClick = {
                    if (vm.checkPermissionsGranted(stepCounterPermission.toList()))
                        navController.navigate(NavRoute.StepCounter)
                    else onRequestPermission(stepCounterPermission)
                },
                onUserActivityClick = {
                    if (vm.checkPermissionsGranted(userActivityPermission.toList()))
                        navController.navigate(NavRoute.UserActivity)
                    else onRequestPermission(userActivityPermission)
                },
                onLocationPermissionClick = {
                    if (!vm.checkPermissionsGranted(locationPermission.toList()))
                        onRequestPermission(locationPermission)
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
    }
}