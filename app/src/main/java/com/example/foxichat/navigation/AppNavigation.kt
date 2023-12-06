package com.example.foxichat.navigation

enum class Screen {
    HOME,
    SIGNUP,
}
sealed class NavigationItem(val route: String) {
    object Home : NavigationItem(Screen.HOME.name)
    object Login : NavigationItem(Screen.SIGNUP.name)
}