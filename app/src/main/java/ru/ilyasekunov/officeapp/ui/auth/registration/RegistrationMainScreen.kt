package ru.ilyasekunov.officeapp.ui.auth.registration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.ui.AnimatedLoadingScreen
import ru.ilyasekunov.officeapp.ui.auth.login.emailErrorMessage
import ru.ilyasekunov.officeapp.ui.auth.login.passwordErrorMessage
import ru.ilyasekunov.officeapp.ui.components.EmailTextField
import ru.ilyasekunov.officeapp.ui.components.PasswordTextField
import ru.ilyasekunov.officeapp.ui.components.rememberCircleClickEffectIndication
import ru.ilyasekunov.officeapp.ui.snackbarWithAction
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme
import ru.ilyasekunov.officeapp.validation.EmailValidationError
import ru.ilyasekunov.officeapp.validation.PasswordValidationError

@Composable
fun RegistrationMainScreen(
    registrationUiState: RegistrationUiState,
    onEmailValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onRepeatPasswordValueChange: (String) -> Unit,
    onRegisterButtonClick: () -> Unit,
    onNetworkErrorShown: () -> Unit,
    navigateToLoginScreen: () -> Unit
) {
    when {
        registrationUiState.isLoading -> AnimatedLoadingScreen()
        else -> RegistrationMainScreenContent(
            registrationUiState = registrationUiState,
            onEmailValueChange = onEmailValueChange,
            onPasswordValueChange = onPasswordValueChange,
            onRepeatPasswordValueChange = onRepeatPasswordValueChange,
            onRegisterButtonClick = onRegisterButtonClick,
            onNetworkErrorShown = onNetworkErrorShown,
            navigateToLoginScreen = navigateToLoginScreen,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun RegistrationMainScreenContent(
    registrationUiState: RegistrationUiState,
    onEmailValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onRepeatPasswordValueChange: (String) -> Unit,
    onRegisterButtonClick: () -> Unit,
    onNetworkErrorShown: () -> Unit,
    navigateToLoginScreen: () -> Unit,
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
                text = stringResource(R.string.registration),
                style = MaterialTheme.typography.displayMedium,
                fontSize = 36.sp,
                modifier = Modifier.padding(top = 150.dp, bottom = 75.dp)
            )
            RegistrationEmailTextField(
                email = registrationUiState.emailUiState.email,
                onEmailValueChange = onEmailValueChange,
                emailValidationError = registrationUiState.emailUiState.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            RegistrationPasswordTextField(
                password = registrationUiState.passwordUiState.password,
                passwordsDiffer = registrationUiState.passwordsDiffer,
                onPasswordValueChange = onPasswordValueChange,
                passwordPlaceholder = stringResource(R.string.password),
                passwordValidationError = registrationUiState.passwordUiState.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            RegistrationPasswordTextField(
                password = registrationUiState.repeatedPasswordUiState.password,
                passwordsDiffer = registrationUiState.passwordsDiffer,
                onPasswordValueChange = onRepeatPasswordValueChange,
                passwordPlaceholder = stringResource(R.string.repeat_password),
                passwordValidationError = registrationUiState.repeatedPasswordUiState.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
            Spacer(modifier = Modifier.height(50.dp))
            RegisterButton(onClick = onRegisterButtonClick)
            Spacer(modifier = Modifier.height(28.dp))
            LoginSection(onLoginClick = navigateToLoginScreen)
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
    ObserveNetworkError(
        registrationUiState = registrationUiState,
        snackbarHostState = snackbarHostState,
        onRetryClick = onRegisterButtonClick,
        onNetworkErrorShown = onNetworkErrorShown
    )
}

@Composable
private fun RegistrationEmailTextField(
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
private fun RegistrationPasswordTextField(
    password: String,
    passwordsDiffer: Boolean,
    onPasswordValueChange: (String) -> Unit,
    passwordPlaceholder: String,
    passwordValidationError: PasswordValidationError?,
    modifier: Modifier = Modifier
) {
    val passwordErrorMessage = if (passwordsDiffer) {
        stringResource(R.string.repeated_password_error_differ_from_password)
    } else {
        passwordValidationError?.let { passwordErrorMessage(it) }
    }
    PasswordTextField(
        value = password,
        errorMessage = passwordErrorMessage,
        onValueChange = onPasswordValueChange,
        placeholder = passwordPlaceholder,
        modifier = modifier
    )
}

@Composable
private fun RegisterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = MaterialTheme.shapes.large,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.register),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun LoginSection(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.have_account),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 14.sp
        )
        Text(
            text = stringResource(R.string.enter),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberCircleClickEffectIndication(
                    circleColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ),
                onClick = onLoginClick
            )
        )
    }
}

@Composable
private fun ObserveNetworkError(
    registrationUiState: RegistrationUiState,
    snackbarHostState: SnackbarHostState,
    onRetryClick: () -> Unit,
    onNetworkErrorShown: () -> Unit
) {
    val errorMessage = stringResource(R.string.error_connecting_to_server)
    val retryLabel = stringResource(R.string.retry)
    val currentOnNetworkErrorShown by rememberUpdatedState(onNetworkErrorShown)
    LaunchedEffect(Unit) {
        snapshotFlow { registrationUiState }
            .filter { it.isNetworkError }
            .collectLatest {
                snackbarHostState.snackbarWithAction(
                    message = errorMessage,
                    actionLabel = retryLabel,
                    onActionClick = onRetryClick,
                    duration = SnackbarDuration.Short
                )
                currentOnNetworkErrorShown()
            }
    }
}

@Preview
@Composable
private fun RegistrationMainScreenPreview() {
    OfficeAppTheme {
        Surface {
            RegistrationMainScreen(
                registrationUiState = RegistrationUiState(),
                onEmailValueChange = {},
                onPasswordValueChange = {},
                onRepeatPasswordValueChange = {},
                onRegisterButtonClick = {},
                onNetworkErrorShown = {},
                navigateToLoginScreen = {}
            )
        }
    }
}