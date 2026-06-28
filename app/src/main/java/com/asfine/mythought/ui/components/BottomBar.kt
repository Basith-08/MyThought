package com.asfine.mythought.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.asfine.mythought.navigation.Screen

@Composable
fun BottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Screen.Dashboard.route,
            onClick = {
                if (currentRoute != Screen.Dashboard.route) {
                    onNavigate(Screen.Dashboard.route)
                }
            },
            icon = {
                Icon(
                    Icons.Outlined.Home,
                    null
                )
            },
            label = {
                Text("Home")
            }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Write.route,
            onClick = {
                if (currentRoute != Screen.Write.route) {
                    onNavigate(Screen.Write.route)
                }
            },
            icon = {
                Icon(
                    Icons.Outlined.EditNote,
                    null
                )
            },
            label = {
                Text("Tulis")
            }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.History.route,
            onClick = {
                if (currentRoute != Screen.History.route) {
                    onNavigate(Screen.History.route)
                }
            },
            icon = {
                Icon(
                    Icons.AutoMirrored.Outlined.MenuBook,
                    null
                )
            },
            label = {
                Text("Riwayat")
            }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Profile.route,
            onClick = {
                if (currentRoute != Screen.Profile.route) {
                    onNavigate(Screen.Profile.route)
                }
            },
            icon = {
                Icon(
                    Icons.Outlined.Person,
                    null
                )
            },
            label = {
                Text("Profil")
            }
        )
    }
}