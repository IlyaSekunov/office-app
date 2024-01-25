package ru.ilyasekunov.officeapp.navigation

import androidx.annotation.StringRes

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Favourite : Screen("favourite")
    data object MyOffice : Screen("my-office")
    data object Profile : Screen("profile")
    data object Login : Screen("login")
    data object RegistrationMain : Screen("registration-main")
    data object RegistrationUserInfo : Screen("registration-user-info")
}