package ru.ilyasekunov.officeapp.ui.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.ui.LoadingScreen
import ru.ilyasekunov.officeapp.ui.components.EmailTextField
import ru.ilyasekunov.officeapp.ui.components.PasswordTextField
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme
import ru.ilyasekunov.officeapp.validation.EmailValidationError
import ru.ilyasekunov.officeapp.validation.PasswordValidationError

@Composable
fun LoginScreen(
    loginUiState: LoginUiState,
    onEmailValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onLoginButtonClick: () -> Unit,
    navigateToRegistrationMainScreen: () -> Unit,
    navigateToHomeScreen: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    if (loginUiState.isLoading) {
        LoadingScreen()
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding()
                    .background(color = MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(190.dp))
                Text(
                    text = stringResource(R.string.login),
                    style = MaterialTheme.typography.displayMedium,
                    fontSize = 36.sp
                )
                Spacer(modifier = Modifier.height(75.dp))

                val emailErrorMessage = if (loginUiState.emailUiState.error != null) {
                    emailErrorMessage(error = loginUiState.emailUiState.error)
                } else null
                EmailTextField(
                    value = loginUiState.emailUiState.email,
                    errorMessage = emailErrorMessage,
                    onValueChange = onEmailValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp)
                )
                Spacer(modifier = Modifier.height(30.dp))

                val passwordErrorMessage = if (loginUiState.passwordUiState.error != null) {
                    passwordErrorMessage(error = loginUiState.passwordUiState.error)
                } else null
                PasswordTextField(
                    value = loginUiState.passwordUiState.password,
                    errorMessage = passwordErrorMessage,
                    onValueChange = onPasswordValueChange,
                    placeholder = stringResource(R.string.password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp)
                )
                Spacer(modifier = Modifier.height(40.dp))
                LoginButton(onClick = onLoginButtonClick)
                Spacer(modifier = Modifier.height(28.dp))
                RegisterSection(onRegisterClick = navigateToRegistrationMainScreen)
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }

    // Observe isLoggedIn to navigate to home screen after successful logging
    val currentNavigateToHomeScreen by rememberUpdatedState(navigateToHomeScreen)
    LaunchedEffect(loginUiState) {
        if (loginUiState.isLoggedIn) {
            currentNavigateToHomeScreen()
        }
    }

    ObserveCredentialsValid(
        loginUiState = loginUiState,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun ObserveCredentialsValid(
    loginUiState: LoginUiState,
    snackbarHostState: SnackbarHostState
) {
    val loginErrorMessage = stringResource(R.string.incorrect_login_or_password)
    LaunchedEffect(loginUiState) {
        if (loginUiState.credentialsInvalid) {
            loginErrorSnackbar(
                snackbarHostState = snackbarHostState,
                message = loginErrorMessage
            )
        }
    }
}

@Composable
fun LoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = MaterialTheme.shapes.large,
        contentPadding = PaddingValues(start = 50.dp, end = 50.dp),
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.enter),
            style = MaterialTheme.typography.labelLarge,
            fontSize = 16.sp
        )
    }
}

@Composable
fun RegisterSection(
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(R.string.no_account),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = stringResource(R.string.register),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onRegisterClick() }
        )
    }
}

@Composable
fun emailErrorMessage(error: EmailValidationError) =
    when (error) {
        EmailValidationError.BLANK -> stringResource(R.string.email_error_is_blank)
        EmailValidationError.NOT_EMAIL_PATTERN -> stringResource(R.string.email_error_is_not_pattern)
    }

@Composable
fun passwordErrorMessage(error: PasswordValidationError) =
    when (error) {
        PasswordValidationError.BLANK -> stringResource(R.string.password_error_is_blank)
        PasswordValidationError.TOO_SHORT -> stringResource(R.string.password_error_too_short)
        PasswordValidationError.NO_SPEC_SYMBOLS -> stringResource(R.string.password_error_no_spec_symbols)
        PasswordValidationError.NO_CAPITAL_LETTER -> stringResource(R.string.password_error_no_capital_letter)
    }

private suspend fun loginErrorSnackbar(
    snackbarHostState: SnackbarHostState,
    message: String,
    duration: SnackbarDuration = SnackbarDuration.Short,
    withDismissAction: Boolean = true
) = snackbarHostState.showSnackbar(
    message = message,
    withDismissAction = withDismissAction,
    duration = duration
)

@Preview
@Composable
fun LoginScreenPreview() {
    OfficeAppTheme {
        Surface {
            LoginScreen(
                loginUiState = LoginUiState(),
                onEmailValueChange = {},
                onPasswordValueChange = {},
                onLoginButtonClick = {},
                navigateToRegistrationMainScreen = {},
                navigateToHomeScreen = {}
            )
        }
    }
}