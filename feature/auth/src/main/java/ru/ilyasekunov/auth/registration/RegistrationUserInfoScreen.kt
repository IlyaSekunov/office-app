package ru.ilyasekunov.auth.registration

import android.net.Uri
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import ru.ilyasekunov.model.Office
import ru.ilyasekunov.ui.AnimatedLoadingScreen
import ru.ilyasekunov.ui.ErrorScreen
import ru.ilyasekunov.ui.LocalCoroutineScope
import ru.ilyasekunov.ui.LocalSnackbarHostState
import ru.ilyasekunov.ui.components.NavigateBackArrow
import ru.ilyasekunov.ui.components.OfficePicker
import ru.ilyasekunov.ui.components.PhotoPicker
import ru.ilyasekunov.ui.components.UserJobTextField
import ru.ilyasekunov.ui.components.UserNameTextField
import ru.ilyasekunov.ui.components.UserSurnameTextField
import ru.ilyasekunov.ui.snackbarWithAction
import ru.ilyasekunov.ui.theme.OfficeAppTheme
import ru.ilyasekunov.officeapp.core.ui.R.string as coreUiStrings
import ru.ilyasekunov.officeapp.feature.auth.R.string as featureAuthStrings

@Composable
fun RegistrationUserInfoScreen(
    registrationUiState: RegistrationUiState,
    availableOfficesUiState: AvailableOfficesUiState,
    onAttachImage: (Uri?) -> Unit,
    onNameValueChange: (String) -> Unit,
    onSurnameValueChange: (String) -> Unit,
    onJobValueChange: (String) -> Unit,
    onOfficeChange: (Office) -> Unit,
    onSaveButtonClick: () -> Unit,
    onNetworkErrorShown: () -> Unit,
    onRetryButtonClick: () -> Unit,
    navigateBack: () -> Unit,
    navigateToHomeScreen: () -> Unit
) {
    when {
        isScreenLoading(registrationUiState, availableOfficesUiState) -> AnimatedLoadingScreen()
        isErrorWhileLoading(availableOfficesUiState) -> {
            ErrorScreen(
                message = stringResource(coreUiStrings.core_ui_error_connecting_to_server),
                onRetryButtonClick = onRetryButtonClick
            )
        }

        else -> RegistrationUserInfoScreenContent(
            registrationUiState = registrationUiState,
            availableOfficesUiState = availableOfficesUiState,
            onAttachImage = onAttachImage,
            onNameValueChange = onNameValueChange,
            onSurnameValueChange = onSurnameValueChange,
            onJobValueChange = onJobValueChange,
            onOfficeChange = onOfficeChange,
            onSaveButtonClick = onSaveButtonClick,
            onNetworkErrorShown = onNetworkErrorShown,
            navigateBack = navigateBack,
            navigateToHomeScreen = navigateToHomeScreen,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationUserInfoScreenContent(
    registrationUiState: RegistrationUiState,
    availableOfficesUiState: AvailableOfficesUiState,
    onAttachImage: (Uri?) -> Unit,
    onNameValueChange: (String) -> Unit,
    onSurnameValueChange: (String) -> Unit,
    onJobValueChange: (String) -> Unit,
    onOfficeChange: (Office) -> Unit,
    onSaveButtonClick: () -> Unit,
    onNetworkErrorShown: () -> Unit,
    navigateBack: () -> Unit,
    navigateToHomeScreen: () -> Unit,
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
            RegistrationMainScreenTopAppBar(
                navigateBack = navigateBack,
                scrollBehavior = topAppBarScrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
            .imePadding()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            PhotoPicker(
                selectedPhoto = registrationUiState.userInfoRegistrationUiState.photo,
                onSelectedPhoto = onAttachImage,
                modifier = Modifier.size(180.dp)
            )
            Spacer(modifier = Modifier.height(36.dp))
            UserNameTextField(
                name = registrationUiState.userInfoRegistrationUiState.name.value,
                onNameValueChange = onNameValueChange,
                nameValidationError = registrationUiState.userInfoRegistrationUiState.name.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            UserSurnameTextField(
                surname = registrationUiState.userInfoRegistrationUiState.surname.value,
                onSurnameValueChange = onSurnameValueChange,
                surnameValidationError = registrationUiState.userInfoRegistrationUiState.surname.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            UserJobTextField(
                job = registrationUiState.userInfoRegistrationUiState.job.value,
                onJobValueChange = onJobValueChange,
                jobValidationError = registrationUiState.userInfoRegistrationUiState.job.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            OfficePicker(
                officeList = availableOfficesUiState.availableOffices,
                initialSelectedOffice = registrationUiState.userInfoRegistrationUiState.currentOffice!!,
                officeWidth = 170.dp,
                officeHeight = 180.dp,
                onOfficeChange = onOfficeChange
            )
            EndRegistrationButton(
                onClick = onSaveButtonClick,
                modifier = Modifier.padding(top = 45.dp, bottom = 30.dp)
            )
        }
    }
    ObserveStateChanges(
        registrationUiState = registrationUiState,
        snackbarHostState = snackbarHostState,
        coroutineScope = LocalCoroutineScope.current,
        onRetryButtonClick = onSaveButtonClick,
        onNetworkErrorShown = onNetworkErrorShown,
        navigateToHomeScreen = navigateToHomeScreen
    )
}

@Composable
private fun ObserveStateChanges(
    registrationUiState: RegistrationUiState,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onRetryButtonClick: () -> Unit,
    onNetworkErrorShown: () -> Unit,
    navigateToHomeScreen: () -> Unit
) {
    ObserveIsNetworkError(
        registrationUiState = registrationUiState,
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope,
        onRetryButtonClick = onRetryButtonClick,
        onNetworkErrorShown = onNetworkErrorShown
    )
    ObserveIsRegistrationSuccess(
        registrationUiState = registrationUiState,
        navigateToHomeScreen = navigateToHomeScreen
    )
}

@Composable
private fun ObserveIsNetworkError(
    registrationUiState: RegistrationUiState,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onRetryButtonClick: () -> Unit,
    onNetworkErrorShown: () -> Unit
) {
    val currentOnRetryButtonClick by rememberUpdatedState(onRetryButtonClick)
    val currentOnNetworkErrorShown by rememberUpdatedState(onNetworkErrorShown)
    val errorMessage = stringResource(featureAuthStrings.feature_auth_error_while_registration)
    val retryLabel = stringResource(coreUiStrings.core_ui_retry)

    LaunchedEffect(Unit) {
        snapshotFlow { registrationUiState }
            .filter { it.isNetworkError }
            .collectLatest {
                coroutineScope.launch {
                    snackbarHostState.snackbarWithAction(
                        message = errorMessage,
                        actionLabel = retryLabel,
                        onActionClick = currentOnRetryButtonClick,
                        duration = SnackbarDuration.Short
                    )
                }
                currentOnNetworkErrorShown()
            }
    }
}

@Composable
private fun ObserveIsRegistrationSuccess(
    registrationUiState: RegistrationUiState,
    navigateToHomeScreen: () -> Unit
) {
    val currentNavigateToHomeScreen by rememberUpdatedState(navigateToHomeScreen)
    LaunchedEffect(registrationUiState) {
        if (registrationUiState.isRegistrationSuccess) {
            currentNavigateToHomeScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegistrationMainScreenTopAppBar(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(featureAuthStrings.feature_auth_top_bar_register_screen_title),
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
private fun EndRegistrationButton(
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
            text = stringResource(featureAuthStrings.feature_auth_end_registration),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp
        )
    }
}

private fun isScreenLoading(
    registrationUiState: RegistrationUiState,
    availableOfficesUiState: AvailableOfficesUiState,
) = registrationUiState.isLoading || availableOfficesUiState.isLoading

private fun isErrorWhileLoading(
    availableOfficesUiState: AvailableOfficesUiState,
) = availableOfficesUiState.isErrorWhileLoading

@Preview
@Composable
private fun RegistrationUserInfoScreenPreview() {
    OfficeAppTheme {
        RegistrationUserInfoScreen(
            registrationUiState = RegistrationUiState(),
            availableOfficesUiState = AvailableOfficesUiState(),
            onAttachImage = {},
            onNameValueChange = {},
            onSurnameValueChange = {},
            onJobValueChange = {},
            onOfficeChange = {},
            onSaveButtonClick = {},
            onNetworkErrorShown = {},
            navigateBack = {},
            onRetryButtonClick = {},
            navigateToHomeScreen = {}
        )
    }
}