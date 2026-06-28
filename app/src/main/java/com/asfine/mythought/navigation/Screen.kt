package com.asfine.mythought.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Write : Screen("write")
    data object History : Screen("history")
    data object Profile : Screen("profile")
    data object Detail : Screen("detail/{id}") {
        fun createRoute(id: String) = "detail/$id"
    }
}