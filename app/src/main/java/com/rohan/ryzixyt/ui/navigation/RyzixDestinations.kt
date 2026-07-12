package com.rohan.ryzixyt.ui.navigation

sealed class RyzixDestination(val route: String) {
    data object Home : RyzixDestination("home")
    data object History : RyzixDestination("history")
    data object Notifications : RyzixDestination("notifications")
    data object Player : RyzixDestination("player/{videoUrl}") {
        fun createRoute(videoUrl: String): String {
            val encoded = java.net.URLEncoder.encode(videoUrl, "UTF-8")
            return "player/$encoded"
        }
    }
}
