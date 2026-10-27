package com.atom.vpn.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.atom.vpn.ui.screens.dashboard.DashboardScreen
import com.atom.vpn.ui.screens.diagnostics.DiagnosticsScreen
import com.atom.vpn.ui.screens.favorites.FavoritesScreen
import com.atom.vpn.ui.screens.onboarding.OnboardingScreen
import com.atom.vpn.ui.screens.servers.ServersScreen
import com.atom.vpn.ui.screens.settings.SettingsScreen
import com.atom.vpn.ui.screens.statistics.StatisticsScreen

@Composable
fun AtomNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Dashboard.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 } },
        exitTransition = { fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { -it / 4 } },
        popEnterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -it / 4 } },
        popExitTransition = { fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { it / 4 } }
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onComplete = { navController.navigate(Screen.Dashboard.route) { popUpTo(0) } })
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToServers      = { navController.navigate(Screen.Servers.route) },
                onNavigateToStatistics   = { navController.navigate(Screen.Statistics.route) },
                onNavigateToSettings     = { navController.navigate(Screen.Settings.route) },
                onNavigateToDiagnostics  = { navController.navigate(Screen.Diagnostics.route) }
            )
        }
        composable(Screen.Servers.route) {
            ServersScreen(
                onBack = { navController.popBackStack() },
                onNavigateToDashboard = { navController.navigate(Screen.Dashboard.route) { launchSingleTop = true } }
            )
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Diagnostics.route) {
            DiagnosticsScreen(onBack = { navController.popBackStack() })
        }
    }
}
