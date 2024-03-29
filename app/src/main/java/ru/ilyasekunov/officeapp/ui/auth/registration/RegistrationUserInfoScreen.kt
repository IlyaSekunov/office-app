package ru.ilyasekunov.officeapp.ui.auth.registration

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.ui.ErrorScreen
import ru.ilyasekunov.officeapp.ui.LoadingScreen
import ru.ilyasekunov.officeapp.ui.components.NavigateBackArrow
import ru.ilyasekunov.officeapp.ui.components.OfficePicker
import ru.ilyasekunov.officeapp.ui.components.PhotoPicker
import ru.ilyasekunov.officeapp.ui.components.UserInfoTextField
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme
import ru.ilyasekunov.officeapp.validation.UserInfoValidationError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationUserInfoScreen(
    registrationUiState: RegistrationUiState,
    availableOfficesUiState: AvailableOfficesUiState,
    onPhotoPickerClick: () -> Unit,
    onNameValueChange: (String) -> Unit,
    onSurnameValueChange: (String) -> Unit,
    onJobValueChange: (String) -> Unit,
    onOfficeChange: (Office) -> Unit,
    onSaveButtonClick: () -> Unit,
    onRetryButtonClick: () -> Unit,
    navigateBack: () -> Unit,
    navigateToHomeScreen: () -> Unit
) {
    when {
        registrationUiState.isLoading || availableOfficesUiState.isLoading -> LoadingScreen()
        availableOfficesUiState.isErrorWhileLoading -> {
            ErrorScreen(
                message = stringResource(R.string.error_connecting_to_server),
                onRetryButtonClick = onRetryButtonClick
            )
        }

        else -> {
            val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
                state = rememberTopAppBarState(),
                snapAnimationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                    visibilityThreshold = null
                )
            )
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.top_bar_register_screen_title),
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 20.sp
                            )
                        },
                        navigationIcon = {
                            NavigateBackArrow(onClick = navigateBack)
                        },
                        scrollBehavior = topAppBarScrollBehavior,
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            scrolledContainerColor = MaterialTheme.colorScheme.background
                        )
                    )
                },
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .padding(it)
                ) {
                    PhotoPicker(
                        selectedPhoto = registrationUiState.userInfoRegistrationUiState.photo,
                        onPhotoPickerClick = onPhotoPickerClick,
                        modifier = Modifier
                            .size(180.dp)
                    )
                    Spacer(modifier = Modifier.height(36.dp))

                    val nameError = registrationUiState.userInfoRegistrationUiState.name.error
                    val nameErrorMessage = if (nameError != null) {
                        userInfoFieldErrorMessage(nameError)
                    } else null
                    UserInfoTextField(
                        value = registrationUiState.userInfoRegistrationUiState.name.value,
                        label = stringResource(R.string.name),
                        errorMessage = nameErrorMessage,
                        placeholder = stringResource(R.string.your_name),
                        onValueChange = onNameValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    val surnameError = registrationUiState.userInfoRegistrationUiState.surname.error
                    val surnameErrorMessage = if (surnameError != null) {
                        userInfoFieldErrorMessage(surnameError)
                    } else null
                    UserInfoTextField(
                        value = registrationUiState.userInfoRegistrationUiState.surname.value,
                        label = stringResource(R.string.surname),
                        errorMessage = surnameErrorMessage,
                        placeholder = stringResource(R.string.your_surname),
                        onValueChange = onSurnameValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(30.dp))

                    val jobError = registrationUiState.userInfoRegistrationUiState.job.error
                    val jobErrorMessage = if (jobError != null) {
                        userInfoFieldErrorMessage(jobError)
                    } else null
                    UserInfoTextField(
                        value = registrationUiState.userInfoRegistrationUiState.job.value,
                        label = stringResource(R.string.job),
                        errorMessage = jobErrorMessage,
                        placeholder = stringResource(R.string.your_job),
                        onValueChange = onJobValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    OfficePicker(
                        officeList = availableOfficesUiState.availableOffices,
                        initialSelectedOffice = registrationUiState.userInfoRegistrationUiState.currentOffice!!,
                        officeWidth = 170.dp,
                        officeHeight = 180.dp,
                        onOfficeChange = onOfficeChange
                    )
                    Spacer(modifier = Modifier.height(45.dp))
                    EndRegistrationButton(onClick = onSaveButtonClick)
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    // Observe isRegistrationSuccess to navigate to home screen
    val currentNavigateToHomeScreen by rememberUpdatedState(navigateToHomeScreen)
    LaunchedEffect(registrationUiState) {
        if (registrationUiState.isRegistrationSuccess) {
            currentNavigateToHomeScreen()
        }
    }
}

@Composable
fun EndRegistrationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.end_registration),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp
        )
    }
}

@Composable
fun userInfoFieldErrorMessage(error: UserInfoValidationError) =
    when (error) {
        UserInfoValidationError.BLANK -> stringResource(R.string.user_info_field_error_is_blank)
    }

@Preview
@Composable
fun RegistrationUserInfoScreenPreview() {
    OfficeAppTheme {
        RegistrationUserInfoScreen(
            registrationUiState = RegistrationUiState(),
            availableOfficesUiState = AvailableOfficesUiState(),
            onPhotoPickerClick = {},
            onNameValueChange = {},
            onSurnameValueChange = {},
            onJobValueChange = {},
            onOfficeChange = {},
            onSaveButtonClick = {},
            navigateBack = {},
            onRetryButtonClick = {},
            navigateToHomeScreen = {}
        )
    }
}