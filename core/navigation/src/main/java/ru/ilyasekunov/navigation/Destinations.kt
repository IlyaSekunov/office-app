package ru.ilyasekunov.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NamedNavArgument
import ru.ilyasekunov.officeapp.core.navigation.R

open class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
)

sealed class BottomNavigationScreen(
    route: String,
    @DrawableRes val iconId: Int,
    @StringRes val labelId: Int
) : Screen(route) {
    data object Home : BottomNavigationScreen(
        route = "home",
        iconId = R.drawable.outline_home_24,
        labelId = R.string.core_navigation_home_screen_label
    )

    data object Favourite : BottomNavigationScreen(
        route = "favourite",
        iconId = R.drawable.outline_favorite_border_24,
        labelId = R.string.core_navigation_favourite_screen_label
    )

    data object MyOffice : BottomNavigationScreen(
        route = "my-office",
        iconId = R.drawable.outline_business_center_24,
        labelId = R.string.core_navigation_my_office_screen_label
    )

    data object Profile : BottomNavigationScreen(
        route = "profile",
        iconId = R.drawable.outline_person_24,
        labelId = R.string.core_navigation_profile_screen_label
    )
}

val bottomNavigationDestinations =
    listOf(
        BottomNavigationScreen.Home,
        BottomNavigationScreen.Favourite,
        BottomNavigationScreen.MyOffice,
        BottomNavigationScreen.Profile
    )