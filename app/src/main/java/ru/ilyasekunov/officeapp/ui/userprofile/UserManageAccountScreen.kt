package ru.ilyasekunov.officeapp.ui.userprofile

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import ru.ilyasekunov.officeapp.navigation.BottomNavigationScreen
import ru.ilyasekunov.officeapp.ui.ErrorScreen
import ru.ilyasekunov.officeapp.ui.LoadingScreen
import ru.ilyasekunov.officeapp.ui.auth.registration.userInfoFieldErrorMessage
import ru.ilyasekunov.officeapp.ui.components.BottomNavigationBar
import ru.ilyasekunov.officeapp.ui.components.NavigateBackArrow
import ru.ilyasekunov.officeapp.ui.components.OfficePicker
import ru.ilyasekunov.officeapp.ui.components.PhotoPicker
import ru.ilyasekunov.officeapp.ui.components.UserInfoTextField
import ru.ilyasekunov.officeapp.ui.networkErrorSnackbar
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManageAccountScreen(
    userManageAccountUiState: UserManageAccountUiState,
    onPhotoPickerClick: () -> Unit,
    onNameValueChange: (String) -> Unit,
    onSurnameValueChange: (String) -> Unit,
    onJobValueChange: (String) -> Unit,
    onOfficeChange: (Office) -> Unit,
    onSaveButtonClick: () -> Unit,
    onRetrySaveClick: () -> Unit,
    onRetryLoadProfileClick: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentUserProfileUiState = userManageAccountUiState.currentUserProfileUiState
    val availableOfficesUiState = userManageAccountUiState.availableOfficesUiState
    when {
        userManageAccountUiState.isLoading -> LoadingScreen()
        currentUserProfileUiState.isErrorWhileUserLoading || availableOfficesUiState.isErrorWhileLoading -> {
            ErrorScreen(
                message = stringResource(R.string.error_connecting_to_server),
                onRetryButtonClick = onRetryLoadProfileClick
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
                                text = stringResource(R.string.manage_account),
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
                bottomBar = {
                    BottomNavigationBar(
                        selectedScreen = BottomNavigationScreen.Profile,
                        navigateToHomeScreen = navigateToHomeScreen,
                        navigateToFavouriteScreen = navigateToFavouriteScreen,
                        navigateToMyOfficeScreen = navigateToMyOfficeScreen,
                        navigateToProfileScreen = navigateToProfileScreen
                    )
                },
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            ) { paddingValues ->
                val mutableUserProfileUiState = userManageAccountUiState.mutableUserProfileUiState
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    PhotoPicker(
                        selectedPhoto = mutableUserProfileUiState.photo,
                        onPhotoPickerClick = onPhotoPickerClick,
                        modifier = Modifier.size(180.dp)
                    )
                    Spacer(modifier = Modifier.height(22.dp))

                    val nameError = mutableUserProfileUiState.name.error
                    val nameErrorMessage = if (nameError != null) {
                        userInfoFieldErrorMessage(nameError)
                    } else null
                    UserInfoTextField(
                        value = mutableUserProfileUiState.name.value,
                        errorMessage = nameErrorMessage,
                        label = stringResource(R.string.name),
                        placeholder = stringResource(R.string.your_name),
                        onValueChange = onNameValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    val surnameError = mutableUserProfileUiState.surname.error
                    val surnameErrorMessage = if (surnameError != null) {
                        userInfoFieldErrorMessage(surnameError)
                    } else null
                    UserInfoTextField(
                        value = mutableUserProfileUiState.surname.value,
                        errorMessage = surnameErrorMessage,
                        label = stringResource(R.string.surname),
                        placeholder = stringResource(R.string.your_surname),
                        onValueChange = onSurnameValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    val jobError = mutableUserProfileUiState.job.error
                    val jobErrorMessage = if (jobError != null) {
                        userInfoFieldErrorMessage(jobError)
                    } else null
                    UserInfoTextField(
                        value = mutableUserProfileUiState.job.value,
                        errorMessage = jobErrorMessage,
                        label = stringResource(R.string.job),
                        placeholder = stringResource(R.string.your_job),
                        onValueChange = onJobValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    OfficePicker(
                        officeList = availableOfficesUiState.availableOffices,
                        initialSelectedOffice = mutableUserProfileUiState.currentOffice!!,
                        officeWidth = 170.dp,
                        officeHeight = 180.dp,
                        onOfficeChange = onOfficeChange
                    )
                    Spacer(modifier = Modifier.height(45.dp))
                    SaveButton(
                        onClick = onSaveButtonClick,
                        isEnabled = currentUserProfileUiState != mutableUserProfileUiState,
                        modifier = Modifier.size(width = 200.dp, height = 40.dp)
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    ObserveChangesSavingError(
        userManageAccountUiState = userManageAccountUiState,
        snackbarHostState = snackbarHostState,
        onActionPerformedClick = onRetrySaveClick
    )

    ObserveIsChangesSaved(
        userManageAccountUiState = userManageAccountUiState,
        navigateToProfileScreen = navigateToProfileScreen
    )
}

@Composable
private fun ObserveChangesSavingError(
    userManageAccountUiState: UserManageAccountUiState,
    snackbarHostState: SnackbarHostState,
    onActionPerformedClick: () -> Unit
) {
    val retryLabel = stringResource(R.string.retry)
    val serverErrorMessage = stringResource(R.string.error_connecting_to_server)
    val currentOnActionPerformedClick by rememberUpdatedState(onActionPerformedClick)
    LaunchedEffect(userManageAccountUiState) {
        if (userManageAccountUiState.isChangesSavingError) {
            networkErrorSnackbar(
                snackbarHostState = snackbarHostState,
                duration = SnackbarDuration.Short,
                message = serverErrorMessage,
                retryLabel = retryLabel,
                onRetryClick = currentOnActionPerformedClick
            )
        }
    }
}

@Composable
private fun ObserveIsChangesSaved(
    userManageAccountUiState: UserManageAccountUiState,
    navigateToProfileScreen: () -> Unit
) {
    val currentNavigateToProfileScreen by rememberUpdatedState(navigateToProfileScreen)
    LaunchedEffect(userManageAccountUiState) {
        if (userManageAccountUiState.isChangesSaved) {
            currentNavigateToProfileScreen()
        }
    }
}

@Composable
fun SaveButton(
    onClick: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        enabled = isEnabled,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.save),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp
        )
    }
}

@Preview
@Composable
fun UserManageAccountScreenPreview() {
    OfficeAppTheme {
        UserManageAccountScreen(
            userManageAccountUiState = UserManageAccountUiState(),
            onNameValueChange = {},
            onSurnameValueChange = {},
            onJobValueChange = {},
            onOfficeChange = {},
            onPhotoPickerClick = {},
            onSaveButtonClick = {},
            onRetrySaveClick = {},
            onRetryLoadProfileClick = {},
            navigateToHomeScreen = {},
            navigateToFavouriteScreen = {},
            navigateToMyOfficeScreen = {},
            navigateToProfileScreen = {},
            navigateBack = {}
        )
    }
}