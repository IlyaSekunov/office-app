package ru.ilyasekunov.officeapp.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import ru.ilyasekunov.officeapp.R

sealed class Screen(val route: String, val arguments: List<NamedNavArgument> = emptyList()) {
    data object Login : Screen("login")
    data object RegistrationMain : Screen("registration-main")
    data object RegistrationUserInfo : Screen("registration-user-info")
    data object UserManageAccount : Screen("user-manage-account")
    data object FiltersScreen : Screen("filters")
    data object SuggestIdea : Screen("suggest-idea")
    data object EditIdea : Screen(
        route = "edit-idea/{postId}",
        arguments = listOf(
            navArgument("postId") { type = NavType.LongType }
        )
    )

    data object IdeaAuthor : Screen(
        route = "idea-author/{authorId}",
        arguments = listOf(
            navArgument("authorId") { type = NavType.LongType }
        )
    )

    data object IdeaDetails : Screen(
        route = "idea-details/{postId}?initiallyScrollToComments={initiallyScrollToComments}",
        arguments = listOf(
            navArgument("postId") { type = NavType.LongType },
            navArgument("initiallyScrollToComments") {
                type = NavType.BoolType
                defaultValue = false
            }
        )
    )
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

val destinationsWithBottomNavBar =
    listOf(
        Screen.UserManageAccount,
        Screen.FiltersScreen,
        Screen.SuggestIdea,
        Screen.EditIdea,
        Screen.IdeaAuthor
    ) + bottomNavigationDestinations

const val AuthGraphRoute = "auth-graph"
const val MainGraphRoute = "app-graph"