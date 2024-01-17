package ru.ilyasekunov.officeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import ru.ilyasekunov.officeapp.ui.auth.login.LoginScreen
import ru.ilyasekunov.officeapp.ui.auth.registration.RegistrationScreen
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OfficeAppTheme {
                Surface(
                    modifier = Modifier.safeDrawingPadding()
                ) {
                    LoginScreen {}
                }
            }
        }
    }
}