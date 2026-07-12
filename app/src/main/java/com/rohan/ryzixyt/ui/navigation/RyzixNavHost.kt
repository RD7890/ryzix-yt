package com.rohan.ryzixyt.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rohan.ryzixyt.ui.history.HistoryScreen
import com.rohan.ryzixyt.ui.home.HomeScreen
import com.rohan.ryzixyt.ui.player.PlayerScreen

private data class TabItem(val destination: RyzixDestination, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

// Only two tabs: download/watch activity surfaces as real system notifications, not a screen.
private val tabs = listOf(
    TabItem(RyzixDestination.Home, "Home", Icons.Outlined.Home),
    TabItem(RyzixDestination.History, "History", Icons.Outlined.History),
)

@Composable
fun RyzixNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination

    val showBottomBar = tabs.any { currentRoute?.hierarchy?.any { dest -> dest.route == it.destination.route } == true }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        val selected = currentRoute?.hierarchy?.any { it.route == tab.destination.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = RyzixDestination.Home.route,
            modifier = androidx.compose.ui.Modifier.padding(padding),
        ) {
            composable(RyzixDestination.Home.route) {
                HomeScreen(
                    onVideoClick = { url ->
                        navController.navigate(RyzixDestination.Player.createRoute(url))
                    },
                )
            }
            composable(RyzixDestination.History.route) {
                HistoryScreen(
                    onVideoClick = { url ->
                        navController.navigate(RyzixDestination.Player.createRoute(url))
                    },
                )
            }
            composable(RyzixDestination.Player.route) { backStackEntry ->
                val encodedUrl = backStackEntry.arguments?.getString("videoUrl").orEmpty()
                val videoUrl = java.net.URLDecoder.decode(encodedUrl, "UTF-8")
                PlayerScreen(videoUrl = videoUrl, onBack = { navController.popBackStack() })
            }
        }
    }
}
