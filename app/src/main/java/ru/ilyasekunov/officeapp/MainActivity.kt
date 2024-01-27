package ru.ilyasekunov.officeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.ilyasekunov.officeapp.navigation.MainGraphRoute
import ru.ilyasekunov.officeapp.navigation.auth.authGraph
import ru.ilyasekunov.officeapp.navigation.mainGraph
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
    val navController = rememberNavController()
    OfficeAppTheme {
       NavHost(
            navController = navController,
            startDestination = MainGraphRoute,
            modifier = Modifier.imePadding()
        ) {
           authGraph(navController)
           mainGraph(navController)
        }
    }
}

