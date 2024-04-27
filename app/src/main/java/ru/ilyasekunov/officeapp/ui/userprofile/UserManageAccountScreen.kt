package ru.ilyasekunov.officeapp.ui.userprofile

import android.net.Uri
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
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
import kotlinx.coroutines.CoroutineScope
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.ui.AnimatedLoadingScreen
import ru.ilyasekunov.officeapp.ui.ErrorScreen
import ru.ilyasekunov.officeapp.ui.LocalCoroutineScope
import ru.ilyasekunov.officeapp.ui.LocalCurrentNavigationBarScreen
import ru.ilyasekunov.officeapp.ui.LocalSnackbarHostState
import ru.ilyasekunov.officeapp.ui.changesSavedSuccessfullySnackbar
import ru.ilyasekunov.officeapp.ui.components.BottomNavigationBar
import ru.ilyasekunov.officeapp.ui.components.NavigateBackArrow
import ru.ilyasekunov.officeapp.ui.components.OfficePicker
import ru.ilyasekunov.officeapp.ui.components.PhotoPicker
import ru.ilyasekunov.officeapp.ui.components.UserJobTextField
import ru.ilyasekunov.officeapp.ui.components.UserNameTextField
import ru.ilyasekunov.officeapp.ui.components.UserSurnameTextField
import ru.ilyasekunov.officeapp.ui.networkErrorSnackbar
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@Composable
fun UserManageAccountScreen(
    userManageAccountUiState: UserManageAccountUiState,
    onAttachImage: (Uri?) -> Unit,
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
    when {
        isScreenLoading(userManageAccountUiState) -> AnimatedLoadingScreen()
        isErrorWhileLoading(userManageAccountUiState) -> {
            ErrorScreen(
                message = stringResource(R.string.error_connecting_to_server),
                onRetryButtonClick = onRetryLoadProfileClick
            )
        }

        else -> UserManageAccountScreenContent(
            userManageAccountUiState = userManageAccountUiState,
            onAttachImage = onAttachImage,
            onNameValueChange = onNameValueChange,
            onSurnameValueChange = onSurnameValueChange,
            onJobValueChange = onJobValueChange,
            onOfficeChange = onOfficeChange,
            onSaveButtonClick = onSaveButtonClick,
            onRetrySaveClick = onRetrySaveClick,
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToProfileScreen = navigateToProfileScreen,
            navigateBack = navigateBack,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserManageAccountScreenContent(
    userManageAccountUiState: UserManageAccountUiState,
    onAttachImage: (Uri?) -> Unit,
    onNameValueChange: (String) -> Unit,
    onSurnameValueChange: (String) -> Unit,
    onJobValueChange: (String) -> Unit,
    onOfficeChange: (Office) -> Unit,
    onSaveButtonClick: () -> Unit,
    onRetrySaveClick: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        snapAnimationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = null
        )
    )
    val snackbarHostState = LocalSnackbarHostState.current
    Scaffold(
        topBar = {
            UserManageAccountTopAppBar(
                navigateBack = navigateBack,
                scrollBehavior = topAppBarScrollBehavior
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = LocalCurrentNavigationBarScreen.current,
                navigateToHomeScreen = navigateToHomeScreen,
                navigateToFavouriteScreen = navigateToFavouriteScreen,
                navigateToMyOfficeScreen = navigateToMyOfficeScreen,
                navigateToProfileScreen = navigateToProfileScreen
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        val mutableUserProfileUiState = userManageAccountUiState.mutableUserProfileUiState
        val currentUserProfileUiState = userManageAccountUiState.currentUserProfileUiState
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            PhotoPicker(
                selectedPhoto = mutableUserProfileUiState.photo,
                onSelectedPhoto = onAttachImage,
                modifier = Modifier.size(180.dp)
            )
            UserNameTextField(
                name = mutableUserProfileUiState.name.value,
                onNameValueChange = onNameValueChange,
                nameValidationError = mutableUserProfileUiState.name.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 22.dp, bottom = 20.dp)
            )
            UserSurnameTextField(
                surname = mutableUserProfileUiState.surname.value,
                onSurnameValueChange = onSurnameValueChange,
                surnameValidationError = mutableUserProfileUiState.surname.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
            UserJobTextField(
                job = mutableUserProfileUiState.job.value,
                onJobValueChange = onJobValueChange,
                jobValidationError = mutableUserProfileUiState.job.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 20.dp)
            )
            OfficePicker(
                officeList = userManageAccountUiState.availableOfficesUiState.availableOffices,
                initialSelectedOffice = mutableUserProfileUiState.currentOffice!!,
                officeWidth = 170.dp,
                officeHeight = 180.dp,
                onOfficeChange = onOfficeChange
            )
            SaveButton(
                onClick = onSaveButtonClick,
                isEnabled = currentUserProfileUiState != mutableUserProfileUiState,
                modifier = Modifier
                    .padding(top = 45.dp, bottom = 30.dp)
                    .size(width = 200.dp, height = 40.dp)
            )
        }
    }
    ObserveStateChanges(
        userManageAccountUiState = userManageAccountUiState,
        snackbarHostState = snackbarHostState,
        coroutineScope = LocalCoroutineScope.current,
        onRetrySaveClick = onRetrySaveClick,
        navigateToProfileScreen = navigateToProfileScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserManageAccountTopAppBar(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.manage_account),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp
            )
        },
        navigationIcon = { NavigateBackArrow(onClick = navigateBack) },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        ),
        modifier = modifier
    )
}

@Composable
private fun ObserveStateChanges(
    userManageAccountUiState: UserManageAccountUiState,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onRetrySaveClick: () -> Unit,
    navigateToProfileScreen: () -> Unit
) {
    ObserveChangesSavingError(
        userManageAccountUiState = userManageAccountUiState,
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope,
        onActionPerformedClick = onRetrySaveClick
    )
    ObserveIsChangesSaved(
        userManageAccountUiState = userManageAccountUiState,
        coroutineScope = coroutineScope,
        snackbarHostState = snackbarHostState,
        navigateToProfileScreen = navigateToProfileScreen
    )
}

@Composable
private fun ObserveChangesSavingError(
    userManageAccountUiState: UserManageAccountUiState,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onActionPerformedClick: () -> Unit
) {
    val retryLabel = stringResource(R.string.retry)
    val serverErrorMessage = stringResource(R.string.error_connecting_to_server)
    val currentOnActionPerformedClick by rememberUpdatedState(onActionPerformedClick)
    LaunchedEffect(userManageAccountUiState) {
        if (userManageAccountUiState.isChangesSavingError) {
            networkErrorSnackbar(
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope,
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
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    navigateToProfileScreen: () -> Unit
) {
    val currentNavigateToProfileScreen by rememberUpdatedState(navigateToProfileScreen)
    val message = stringResource(R.string.profile_settings_changes_saved_succesfully)
    LaunchedEffect(userManageAccountUiState) {
        if (userManageAccountUiState.isChangesSaved) {
            changesSavedSuccessfullySnackbar(
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope,
                message = message,
                duration = SnackbarDuration.Short
            )
            currentNavigateToProfileScreen()
        }
    }
}

@Composable
private fun SaveButton(
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

private fun isErrorWhileLoading(userManageAccountUiState: UserManageAccountUiState): Boolean {
    val currentUserProfileUiState = userManageAccountUiState.currentUserProfileUiState
    val availableOfficesUiState = userManageAccountUiState.availableOfficesUiState
    return currentUserProfileUiState.isErrorWhileUserLoading || availableOfficesUiState.isErrorWhileLoading
}

private fun isScreenLoading(userManageAccountUiState: UserManageAccountUiState) =
    userManageAccountUiState.isLoading

@Preview
@Composable
private fun UserManageAccountScreenPreview() {
    OfficeAppTheme {
        UserManageAccountScreen(
            userManageAccountUiState = UserManageAccountUiState(),
            onNameValueChange = {},
            onSurnameValueChange = {},
            onJobValueChange = {},
            onOfficeChange = {},
            onAttachImage = {},
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