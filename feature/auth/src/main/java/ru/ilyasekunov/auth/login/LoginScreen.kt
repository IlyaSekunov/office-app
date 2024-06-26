package ru.ilyasekunov.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import ru.ilyasekunov.ui.AnimatedLoadingScreen
import ru.ilyasekunov.ui.components.EmailTextField
import ru.ilyasekunov.ui.components.PasswordTextField
import ru.ilyasekunov.ui.components.rememberCircleClickEffectIndication
import ru.ilyasekunov.ui.snackbarWithAction
import ru.ilyasekunov.ui.theme.OfficeAppTheme
import ru.ilyasekunov.validation.EmailValidationError
import ru.ilyasekunov.validation.PasswordValidationError
import ru.ilyasekunov.officeapp.core.ui.R.string as coreUiStrings
import ru.ilyasekunov.officeapp.feature.auth.R.string as featureAuthStrings

@Composable
fun LoginScreen(
    loginUiState: LoginUiState,
    onEmailValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onLoginButtonClick: () -> Unit,
    onCredentialsInvalidShown: () -> Unit,
    onNetworkErrorShown: () -> Unit,
    navigateToRegistrationMainScreen: () -> Unit,
    navigateToHomeScreen: () -> Unit
) {
    when {
        loginUiState.isLoading -> AnimatedLoadingScreen()
        else -> LoginScreenContent(
            loginUiState = loginUiState,
            onEmailValueChange = onEmailValueChange,
            onPasswordValueChange = onPasswordValueChange,
            onLoginButtonClick = onLoginButtonClick,
            onCredentialsInvalidShown = onCredentialsInvalidShown,
            onNetworkErrorShown = onNetworkErrorShown,
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
    onCredentialsInvalidShown: () -> Unit,
    onNetworkErrorShown: () -> Unit,
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
                text = stringResource(featureAuthStrings.feature_auth_login),
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
        onCredentialsInvalidShown = onCredentialsInvalidShown,
        onNetworkErrorShown = onNetworkErrorShown,
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
        placeholder = stringResource(featureAuthStrings.feature_auth_password),
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
            text = stringResource(featureAuthStrings.feature_auth_enter),
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
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = modifier
    ) {
        Text(
            text = stringResource(featureAuthStrings.feature_auth_no_account),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 14.sp
        )
        Text(
            text = stringResource(featureAuthStrings.feature_auth_register),
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
    onCredentialsInvalidShown: () -> Unit,
    onNetworkErrorShown: () -> Unit,
    navigateToHomeScreen: () -> Unit
) {
    ObserveIsLoggedIn(
        loginUiState = loginUiState,
        navigateToHomeScreen = navigateToHomeScreen
    )
    ObserveCredentialsInvalid(
        loginUiState = loginUiState,
        snackbarHostState = snackbarHostState,
        onCredentialsInvalidShown = onCredentialsInvalidShown
    )
    ObserveIsNetworkError(
        loginUiState = loginUiState,
        snackbarHostState = snackbarHostState,
        onRetryClick = onLoginButtonClick,
        onNetworkErrorShown = onNetworkErrorShown
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
private fun ObserveCredentialsInvalid(
    loginUiState: LoginUiState,
    snackbarHostState: SnackbarHostState,
    onCredentialsInvalidShown: () -> Unit
) {
    val loginErrorMessage = stringResource(featureAuthStrings.feature_auth_incorrect_login_or_password)
    val currentOnCredentialsInvalidShown by rememberUpdatedState(onCredentialsInvalidShown)

    LaunchedEffect(Unit) {
        snapshotFlow { loginUiState }
            .filter { it.credentialsInvalid }
            .collectLatest {
                snackbarHostState.showSnackbar(
                    message = loginErrorMessage,
                    withDismissAction = true
                )
                currentOnCredentialsInvalidShown()
            }
    }
}

@Composable
private fun ObserveIsNetworkError(
    loginUiState: LoginUiState,
    snackbarHostState: SnackbarHostState,
    onRetryClick: () -> Unit,
    onNetworkErrorShown: () -> Unit
) {
    val errorMessage = stringResource(coreUiStrings.core_ui_error_connecting_to_server)
    val retryLabel = stringResource(coreUiStrings.core_ui_retry)

    LaunchedEffect(Unit) {
        snapshotFlow { loginUiState }
            .filter { it.isNetworkError }
            .collectLatest {
                snackbarHostState.snackbarWithAction(
                    message = errorMessage,
                    actionLabel = retryLabel,
                    onActionClick = onRetryClick,
                    duration = SnackbarDuration.Short
                )
                onNetworkErrorShown()
            }
    }
}

@Composable
fun emailErrorMessage(error: EmailValidationError) =
    when (error) {
        EmailValidationError.BLANK -> stringResource(featureAuthStrings.feature_auth_email_error_is_blank)
        EmailValidationError.NOT_EMAIL_PATTERN -> stringResource(featureAuthStrings.feature_auth_email_error_is_not_pattern)
        EmailValidationError.UNAVAILABLE -> stringResource(featureAuthStrings.feature_auth_email_is_unavailable)
    }

@Composable
fun passwordErrorMessage(error: PasswordValidationError) =
    when (error) {
        PasswordValidationError.BLANK -> stringResource(featureAuthStrings.feature_auth_password_error_is_blank)
        PasswordValidationError.TOO_SHORT -> stringResource(featureAuthStrings.feature_auth_password_error_too_short)
        PasswordValidationError.NO_SPEC_SYMBOLS -> stringResource(featureAuthStrings.feature_auth_password_error_no_spec_symbols)
        PasswordValidationError.NO_CAPITAL_LETTER -> stringResource(featureAuthStrings.feature_auth_password_error_no_capital_letter)
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
                onCredentialsInvalidShown = {},
                onNetworkErrorShown = {},
                navigateToRegistrationMainScreen = {},
                navigateToHomeScreen = {}
            )
        }
    }
}