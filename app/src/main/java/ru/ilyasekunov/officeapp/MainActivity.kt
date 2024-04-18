package ru.ilyasekunov.officeapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.ilyasekunov.officeapp.navigation.BottomNavigationScreen
import ru.ilyasekunov.officeapp.navigation.MainGraphRoute
import ru.ilyasekunov.officeapp.navigation.auth.authGraph
import ru.ilyasekunov.officeapp.navigation.bottomNavigationDestinations
import ru.ilyasekunov.officeapp.navigation.mainGraph
import ru.ilyasekunov.officeapp.ui.LocalCoroutineScope
import ru.ilyasekunov.officeapp.ui.LocalCurrentNavigationBarScreen
import ru.ilyasekunov.officeapp.ui.LocalSnackbarHostState
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OfficeApp()
        }
    }
}

@Composable
fun OfficeApp() {
    OfficeAppTheme {
        val navController = rememberNavController()
        val currentBottomNavigationScreen = navController.currentBottomNavigationScreen()
        CompositionLocalProvider(
            values = arrayOf(
                LocalSnackbarHostState provides remember { SnackbarHostState() },
                LocalCurrentNavigationBarScreen provides currentBottomNavigationScreen,
                LocalCoroutineScope provides rememberCoroutineScope()
            )
        ) {
            NavHost(
                navController = navController,
                startDestination = MainGraphRoute
            ) {
                authGraph(navController)
                mainGraph(navController)
            }
        }
    }
}

@Composable
@SuppressLint("RestrictedApi")
private fun NavController.currentBottomNavigationScreen(): BottomNavigationScreen {
    val currentBackStackState by currentBackStack.collectAsStateWithLifecycle()
    val lastBottomNavigationEntryInStack = currentBackStackState.lastOrNull {
        bottomNavigationDestinations.any { screen -> screen.route == it.destination.route }
    }
    return bottomNavigationDestinations.find {
        it.route == lastBottomNavigationEntryInStack?.destination?.route
    } ?: BottomNavigationScreen.Home
}