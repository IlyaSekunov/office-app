package ru.ilyasekunov.officeapp.ui.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.ui.auth.EmailTextField
import ru.ilyasekunov.officeapp.ui.auth.PasswordTextField
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@Composable
fun LoginScreen(onRegisterClick: () -> Unit) {
    val loginViewModel = viewModel<LoginViewModel>()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(190.dp))
        Text(
            text = stringResource(R.string.login),
            style = MaterialTheme.typography.displayLarge,
            fontSize = 36.sp
        )
        Spacer(modifier = Modifier.height(75.dp))
        EmailTextField(
            value = loginViewModel.loginUiState.email,
            onValueChange = loginViewModel::updateEmail,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp)
        )
        Spacer(modifier = Modifier.height(30.dp))
        PasswordTextField(
            value = loginViewModel.loginUiState.password,
            onValueChange = loginViewModel::updatePassword,
            placeholder = "Пароль",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        LoginButton(onClick = loginViewModel::login)
        Spacer(modifier = Modifier.height(28.dp))
        RegisterSection(onRegisterClick = onRegisterClick)
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
            style = MaterialTheme.typography.titleSmall,
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
            style = MaterialTheme.typography.labelMedium,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = stringResource(R.string.enter),
            style = MaterialTheme.typography.labelMedium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onRegisterClick() }
        )
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    OfficeAppTheme {
        Surface {
            LoginScreen(onRegisterClick = {})
        }
    }
}