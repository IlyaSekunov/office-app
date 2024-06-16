package ru.ilyasekunov.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NamedNavArgument

open class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
)

open class BottomNavigationScreen(
    route: String,
    @DrawableRes val iconId: Int,
    @StringRes val labelId: Int
) : Screen(route)