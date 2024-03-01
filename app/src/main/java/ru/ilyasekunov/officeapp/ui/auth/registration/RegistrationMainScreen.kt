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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun RegistrationMainScreen(
    registrationUiState: RegistrationUiState,
    onEmailValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onRepeatPasswordValueChange: (String) -> Unit,
    onRegisterButtonClick: () -> Unit,
    navigateToLoginScreen: () -> Unit
) {
    if (registrationUiState.isLoading) {
        LoadingScreen()
    }  else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
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
            EmailTextField(
                value = registrationUiState.email,
                onValueChange = onEmailValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            PasswordTextField(
                value = registrationUiState.password,
                onValueChange = onPasswordValueChange,
                placeholder = stringResource(R.string.password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            PasswordTextField(
                value = registrationUiState.repeatedPassword,
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
        Text(
            text = stringResource(R.string.enter),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onLoginClick() }
        )
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
                onRegisterButtonClick = {}
            ) {}
        }
    }
}