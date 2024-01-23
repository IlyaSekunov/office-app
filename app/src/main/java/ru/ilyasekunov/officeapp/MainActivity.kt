package ru.ilyasekunov.officeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.ilyasekunov.officeapp.navigation.auth.AuthGraphRoute
import ru.ilyasekunov.officeapp.navigation.auth.authGraph
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OfficeApp() {
    val navController = rememberNavController()
    OfficeAppTheme {
        Surface(
            modifier = Modifier
                .safeDrawingPadding()
                .imePadding()
                .imeNestedScroll()
        ) {
            NavHost(
                navController = navController,
                startDestination = AuthGraphRoute
            ) {
                authGraph(navController)
            }
        }
    }
}

