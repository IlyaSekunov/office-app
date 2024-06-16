package ru.ilyasekunov.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.ilyasekunov.officeapp.core.ui.R
import ru.ilyasekunov.officeapp.ui.components.UserInfoTextField
import ru.ilyasekunov.validation.UserInfoValidationError

@Composable
fun UserNameTextField(
    name: String,
    onNameValueChange: (String) -> Unit,
    nameValidationError: UserInfoValidationError?,
    modifier: Modifier = Modifier
) {
    val nameErrorMessage = nameValidationError?.let { userInfoFieldErrorMessage(it) }
    UserInfoTextField(
        value = name,
        label = stringResource(R.string.core_ui_name),
        errorMessage = nameErrorMessage,
        placeholder = stringResource(R.string.core_ui_your_name),
        onValueChange = onNameValueChange,
        modifier = modifier
    )
}

@Composable
fun UserSurnameTextField(
    surname: String,
    onSurnameValueChange: (String) -> Unit,
    surnameValidationError: UserInfoValidationError?,
    modifier: Modifier = Modifier
) {
    val surnameErrorMessage = surnameValidationError?.let { userInfoFieldErrorMessage(it) }
    UserInfoTextField(
        value = surname,
        label = stringResource(R.string.core_ui_surname),
        errorMessage = surnameErrorMessage,
        placeholder = stringResource(R.string.core_ui_your_surname),
        onValueChange = onSurnameValueChange,
        modifier = modifier
    )
}

@Composable
fun UserJobTextField(
    job: String,
    onJobValueChange: (String) -> Unit,
    jobValidationError: UserInfoValidationError?,
    modifier: Modifier = Modifier
) {
    val jobErrorMessage = jobValidationError?.let { userInfoFieldErrorMessage(it) }
    UserInfoTextField(
        value = job,
        label = stringResource(R.string.core_ui_job),
        errorMessage = jobErrorMessage,
        placeholder = stringResource(R.string.core_ui_your_job),
        onValueChange = onJobValueChange,
        modifier = modifier
    )
}

@Composable
private fun userInfoFieldErrorMessage(error: UserInfoValidationError) =
    when (error) {
        UserInfoValidationError.BLANK -> stringResource(R.string.user_info_field_error_is_blank)
    }