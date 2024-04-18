package ru.ilyasekunov.officeapp.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineScope
import ru.ilyasekunov.officeapp.navigation.BottomNavigationScreen

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No Snackbar Host State provided")
}
val LocalCurrentNavigationBarScreen = compositionLocalOf<BottomNavigationScreen> {
    error("No Current Navigation Bar Screen")
}
val LocalCoroutineScope = staticCompositionLocalOf<CoroutineScope> {
    error("No Coroutine Scope provided")
}