package ru.ilyasekunov.officeapp.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.ilyasekunov.officeapp.R

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object RegistrationMain : Screen("registration-main")
    data object RegistrationUserInfo : Screen("registration-user-info")
    data object UserManageAccount : Screen("user-manage-account")
}

sealed class BottomNavigationScreen(
    route: String,
    @DrawableRes val iconId: Int,
    @StringRes val labelId: Int
) : Screen(route) {
    data object Home : BottomNavigationScreen(
        route = "home",
        iconId = R.drawable.outline_home_24,
        labelId = R.string.home_screen_label
    )

    data object Favourite : BottomNavigationScreen(
        route = "favourite",
        iconId = R.drawable.outline_favorite_border_24,
        labelId = R.string.favourite_screen_label
    )

    data object MyOffice : BottomNavigationScreen(
        route = "my-office",
        iconId = R.drawable.outline_business_center_24,
        labelId = R.string.my_office_screen_label
    )

    data object Profile : BottomNavigationScreen(
        route = "profile",
        iconId = R.drawable.outline_person_24,
        labelId = R.string.profile_screen_label
    )
}

val bottomNavigationDestinations =
    listOf(
        BottomNavigationScreen.Home,
        BottomNavigationScreen.Favourite,
        BottomNavigationScreen.MyOffice,
        BottomNavigationScreen.Profile
    )

const val AuthGraphRoute = "auth"
const val MainGraphRoute = "app"