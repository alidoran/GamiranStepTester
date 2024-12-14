package ir.dorantech.gamiransteptester.navigation

import kotlinx.serialization.Serializable

interface NavRoute {
    @Serializable
    data object Home : NavRoute

    @Serializable
    data object StepCounter : NavRoute
}