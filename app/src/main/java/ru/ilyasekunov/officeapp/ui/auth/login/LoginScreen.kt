package ru.ilyasekunov.officeapp.ui.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import ru.ilyasekunov.officeapp.ui.components.rememberCircleClickEffectIndication
import ru.ilyasekunov.officeapp.ui.loginErrorSnackbar
import ru.ilyasekunov.officeapp.ui.networkErrorSnackbar
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
    when {
        loginUiState.isLoading -> LoadingScreen()
        else -> LoginScreenContent(
            loginUiState = loginUiState,
            onEmailValueChange = onEmailValueChange,
            onPasswordValueChange = onPasswordValueChange,
            onLoginButtonClick = onLoginButtonClick,
            navigateToRegistrationMainScreen = navigateToRegistrationMainScreen,
            navigateToHomeScreen = navigateToHomeScreen,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun LoginScreenContent(
    loginUiState: LoginUiState,
    onEmailValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onLoginButtonClick: () -> Unit,
    navigateToRegistrationMainScreen: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.imePadding()
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.login),
                style = MaterialTheme.typography.displayMedium,
                fontSize = 36.sp,
                modifier = Modifier.padding(top = 190.dp, bottom = 75.dp)
            )
            LoginEmailTextField(
                email = loginUiState.emailUiState.email,
                onEmailValueChange = onEmailValueChange,
                emailValidationError = loginUiState.emailUiState.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            LoginPasswordTextField(
                password = loginUiState.passwordUiState.password,
                onPasswordValueChange = onPasswordValueChange,
                passwordValidationError = loginUiState.passwordUiState.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
            LoginButton(
                onClick = onLoginButtonClick,
                modifier = Modifier.padding(top = 40.dp, bottom = 28.dp)
            )
            RegisterSection(
                onRegisterClick = navigateToRegistrationMainScreen,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }
    }
    ObserveLoginStateChanges(
        loginUiState = loginUiState,
        snackbarHostState = snackbarHostState,
        onLoginButtonClick = onLoginButtonClick,
        navigateToHomeScreen = navigateToHomeScreen
    )
}

@Composable
private fun LoginEmailTextField(
    email: String,
    onEmailValueChange: (String) -> Unit,
    emailValidationError: EmailValidationError?,
    modifier: Modifier = Modifier
) {
    val emailErrorMessage = emailValidationError?.let { emailErrorMessage(it) }
    EmailTextField(
        value = email,
        errorMessage = emailErrorMessage,
        onValueChange = onEmailValueChange,
        modifier = modifier
    )
}

@Composable
private fun LoginPasswordTextField(
    password: String,
    onPasswordValueChange: (String) -> Unit,
    passwordValidationError: PasswordValidationError?,
    modifier: Modifier = Modifier
) {
    val passwordErrorMessage = passwordValidationError?.let { passwordErrorMessage(it) }
    PasswordTextField(
        value = password,
        errorMessage = passwordErrorMessage,
        onValueChange = onPasswordValueChange,
        placeholder = stringResource(R.string.password),
        modifier = modifier
    )
}

@Composable
private fun LoginButton(
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
private fun RegisterSection(
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
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberCircleClickEffectIndication(
                    circleColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ),
                onClick = onRegisterClick
            )
        )
    }
}

@Composable
private fun ObserveLoginStateChanges(
    loginUiState: LoginUiState,
    snackbarHostState: SnackbarHostState,
    onLoginButtonClick: () -> Unit,
    navigateToHomeScreen: () -> Unit
) {
    ObserveIsLoggedIn(
        loginUiState = loginUiState,
        navigateToHomeScreen = navigateToHomeScreen
    )
    ObserveCredentialsValid(
        loginUiState = loginUiState,
        snackbarHostState = snackbarHostState
    )
    ObserveIsNetworkError(
        loginUiState = loginUiState,
        snackbarHostState = snackbarHostState,
        onRetryClick = onLoginButtonClick
    )
}

@Composable
private fun ObserveIsLoggedIn(
    loginUiState: LoginUiState,
    navigateToHomeScreen: () -> Unit
) {
    val currentNavigateToHomeScreen by rememberUpdatedState(navigateToHomeScreen)
    LaunchedEffect(loginUiState) {
        if (loginUiState.isLoggedIn) {
            currentNavigateToHomeScreen()
        }
    }
}

@Composable
private fun ObserveCredentialsValid(
    loginUiState: LoginUiState,
    snackbarHostState: SnackbarHostState
) {
    val loginErrorMessage = stringResource(R.string.incorrect_login_or_password)
    LaunchedEffect(loginUiState) {
        if (loginUiState.credentialsInvalid) {
            loginErrorSnackbar(
                snackbarHostState = snackbarHostState,
                coroutineScope = this,
                message = loginErrorMessage
            )
        }
    }
}

@Composable
private fun ObserveIsNetworkError(
    loginUiState: LoginUiState,
    snackbarHostState: SnackbarHostState,
    onRetryClick: () -> Unit
) {
    val errorMessage = stringResource(R.string.error_connecting_to_server)
    val retryLabel = stringResource(R.string.retry)
    LaunchedEffect(loginUiState) {
        if (loginUiState.isNetworkError) {
            networkErrorSnackbar(
                snackbarHostState = snackbarHostState,
                coroutineScope = this,
                duration = SnackbarDuration.Long,
                message = errorMessage,
                retryLabel = retryLabel,
                onRetryClick = onRetryClick
            )
        }
    }
}

@Composable
fun emailErrorMessage(error: EmailValidationError) =
    when (error) {
        EmailValidationError.BLANK -> stringResource(R.string.email_error_is_blank)
        EmailValidationError.NOT_EMAIL_PATTERN -> stringResource(R.string.email_error_is_not_pattern)
        EmailValidationError.UNAVAILABLE -> stringResource(R.string.email_is_unavailable)
    }

@Composable
fun passwordErrorMessage(error: PasswordValidationError) =
    when (error) {
        PasswordValidationError.BLANK -> stringResource(R.string.password_error_is_blank)
        PasswordValidationError.TOO_SHORT -> stringResource(R.string.password_error_too_short)
        PasswordValidationError.NO_SPEC_SYMBOLS -> stringResource(R.string.password_error_no_spec_symbols)
        PasswordValidationError.NO_CAPITAL_LETTER -> stringResource(R.string.password_error_no_capital_letter)
    }

@Preview
@Composable
private fun LoginScreenPreview() {
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