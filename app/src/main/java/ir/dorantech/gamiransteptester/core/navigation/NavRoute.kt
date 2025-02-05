package ir.dorantech.gamiransteptester.core.navigation

import kotlinx.serialization.Serializable

interface NavRoute {
    @Serializable
    data object Home : NavRoute

    @Serializable
    data object StepCounter : NavRoute

    @Serializable
    data object UserActivity : NavRoute

    @Serializable
    data object Sensor : NavRoute
}