package com.atom.vpn.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding   : Screen("onboarding")
    object Dashboard    : Screen("dashboard")
    object Servers      : Screen("servers")
    object Statistics   : Screen("statistics")
    object Settings     : Screen("settings")
    object Favorites    : Screen("favorites")
    object Diagnostics  : Screen("diagnostics")
    data class ServerDetail(val serverId: String = "{serverId}") : Screen("server_detail/{serverId}") {
        fun route(id: String) = "server_detail/$id"
    }
}
