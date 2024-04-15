package ru.ilyasekunov.officeapp.ui.auth.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.ui.LoadingScreen
import ru.ilyasekunov.officeapp.ui.auth.login.emailErrorMessage
import ru.ilyasekunov.officeapp.ui.auth.login.passwordErrorMessage
import ru.ilyasekunov.officeapp.ui.components.CircleClickEffect
import ru.ilyasekunov.officeapp.ui.components.EmailTextField
import ru.ilyasekunov.officeapp.ui.components.PasswordTextField
import ru.ilyasekunov.officeapp.ui.networkErrorSnackbar
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@Composable
fun RegistrationMainScreen(
    registrationUiState: RegistrationUiState,
    onEmailValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onRepeatPasswordValueChange: (String) -> Unit,
    onRegisterButtonClick: () -> Unit,
    navigateToLoginScreen: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    when {
        registrationUiState.isLoading -> LoadingScreen()
        else -> {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { paddingValues ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .padding(paddingValues)
                        .background(color = MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(150.dp))
                    Text(
                        text = stringResource(R.string.registration),
                        style = MaterialTheme.typography.displayMedium,
                        fontSize = 36.sp
                    )
                    Spacer(modifier = Modifier.height(75.dp))

                    val emailErrorMessage = if (registrationUiState.emailUiState.error != null) {
                        emailErrorMessage(error = registrationUiState.emailUiState.error)
                    } else null
                    EmailTextField(
                        value = registrationUiState.emailUiState.email,
                        errorMessage = emailErrorMessage,
                        onValueChange = onEmailValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    val passwordErrorMessage = when {
                        registrationUiState.passwordsDiffer -> {
                            stringResource(R.string.repeated_password_error_differ_from_password)
                        }

                        registrationUiState.passwordUiState.error != null -> {
                            passwordErrorMessage(error = registrationUiState.passwordUiState.error)
                        }

                        else -> null
                    }
                    PasswordTextField(
                        value = registrationUiState.passwordUiState.password,
                        errorMessage = passwordErrorMessage,
                        onValueChange = onPasswordValueChange,
                        placeholder = stringResource(R.string.password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    val repeatedPasswordErrorMessage = when {
                        registrationUiState.passwordsDiffer -> {
                            stringResource(R.string.repeated_password_error_differ_from_password)
                        }

                        registrationUiState.repeatedPasswordUiState.error != null -> {
                            passwordErrorMessage(error = registrationUiState.repeatedPasswordUiState.error)
                        }

                        else -> null
                    }
                    PasswordTextField(
                        value = registrationUiState.repeatedPasswordUiState.password,
                        errorMessage = repeatedPasswordErrorMessage,
                        onValueChange = onRepeatPasswordValueChange,
                        placeholder = stringResource(R.string.repeat_password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                    RegisterButton(onClick = onRegisterButtonClick)
                    Spacer(modifier = Modifier.height(28.dp))
                    LoginSection(onLoginClick = navigateToLoginScreen)
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }

    ObserveNetworkError(
        registrationUiState = registrationUiState,
        snackbarHostState = snackbarHostState,
        onRetryClick = onRegisterButtonClick
    )
}

@Composable
fun ObserveNetworkError(
    registrationUiState: RegistrationUiState,
    snackbarHostState: SnackbarHostState,
    onRetryClick: () -> Unit
) {
    val errorMessage = stringResource(R.string.error_connecting_to_server)
    val retryLabel = stringResource(R.string.retry)
    LaunchedEffect(registrationUiState) {
        if (registrationUiState.isNetworkError) {
            networkErrorSnackbar(
                snackbarHostState = snackbarHostState,
                coroutineScope = this,
                duration = SnackbarDuration.Short,
                message = errorMessage,
                retryLabel = retryLabel,
                onRetryClick = onRetryClick
            )
        }
    }
}

@Composable
fun RegisterButton(
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
fun LoginSection(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(R.string.have_account),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(3.dp))
        CircleClickEffect(
            onClick = onLoginClick,
            circleColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        ) {
            Text(
                text = stringResource(R.string.enter),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
fun RegistrationMainScreenPreview() {
    OfficeAppTheme {
        Surface {
            RegistrationMainScreen(
                registrationUiState = RegistrationUiState(),
                onEmailValueChange = {},
                onPasswordValueChange = {},
                onRepeatPasswordValueChange = {},
                onRegisterButtonClick = {},
                navigateToLoginScreen = {}
            )
        }
    }
}