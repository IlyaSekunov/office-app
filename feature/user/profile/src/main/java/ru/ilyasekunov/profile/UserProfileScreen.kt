package ru.ilyasekunov.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import ru.ilyasekunov.auth.registration.UserInfoFieldUiState
import ru.ilyasekunov.officeapp.feature.user.profile.R
import ru.ilyasekunov.ui.AnimatedLoadingScreen
import ru.ilyasekunov.ui.ErrorScreen
import ru.ilyasekunov.ui.LocalCurrentNavigationBarScreen
import ru.ilyasekunov.ui.LocalSnackbarHostState
import ru.ilyasekunov.ui.components.AsyncImageWithLoading
import ru.ilyasekunov.ui.components.BottomNavigationBar
import ru.ilyasekunov.ui.components.UpsidePullToRefreshContainer
import ru.ilyasekunov.ui.components.rememberCircleClickEffectIndication
import ru.ilyasekunov.ui.modifiers.shadow
import ru.ilyasekunov.ui.theme.OfficeAppTheme
import ru.ilyasekunov.officeapp.core.ui.R as coreUiResources

@Composable
fun UserProfileScreen(
    userProfileUiState: UserProfileUiState,
    onManageAccountClick: () -> Unit,
    onMyOfficeClick: () -> Unit,
    onMyIdeasClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onRetryUserLoadClick: () -> Unit,
    onPullToRefresh: CoroutineScope.() -> Job,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToAuthGraph: () -> Unit
) {
    val containerColor = MaterialTheme.colorScheme.background
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = LocalSnackbarHostState.current) },
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = LocalCurrentNavigationBarScreen.current,
                navigateToHomeScreen = navigateToHomeScreen,
                navigateToFavouriteScreen = navigateToFavouriteScreen,
                navigateToMyOfficeScreen = navigateToMyOfficeScreen
            )
        },
        containerColor = containerColor,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        when {
            isScreenLoading(userProfileUiState) -> AnimatedLoadingScreen()
            isErrorWhileLoading(userProfileUiState) -> {
                ErrorScreen(
                    message = stringResource(coreUiResources.string.core_ui_error_connecting_to_server),
                    onRetryButtonClick = onRetryUserLoadClick
                )
            }

            else -> {
                UserProfileContent(
                    userProfileUiState = userProfileUiState,
                    userInfoSectionContainerColor = containerColor,
                    onManageAccountClick = onManageAccountClick,
                    onMyOfficeClick = onMyOfficeClick,
                    onMyIdeasClick = onMyIdeasClick,
                    onLogoutClick = onLogoutClick,
                    onPullToRefresh = onPullToRefresh,
                    modifier = Modifier
                        .padding(bottom = paddingValues.calculateBottomPadding())
                        .fillMaxSize()
                )
            }
        }
    }
    ObserveIsLoggedOut(
        userProfileUiState = userProfileUiState,
        navigateToAuthGraph = navigateToAuthGraph
    )
}

@Composable
private fun UserProfileContent(
    userProfileUiState: UserProfileUiState,
    userInfoSectionContainerColor: Color,
    onManageAccountClick: () -> Unit,
    onMyOfficeClick: () -> Unit,
    onMyIdeasClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onPullToRefresh: CoroutineScope.() -> Job,
    modifier: Modifier = Modifier
) {
    UpsidePullToRefreshContainer(onRefreshTrigger = onPullToRefresh) {
        Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            UserInfoSection(
                name = userProfileUiState.name.value,
                surname = userProfileUiState.surname.value,
                photoUrl = userProfileUiState.photo.toString(),
                job = userProfileUiState.job.value,
                containerColor = userInfoSectionContainerColor,
                modifier = Modifier.fillMaxWidth()
            )
            Option(
                icon = painterResource(R.drawable.feature_user_profile_outline_manage_accounts_24),
                text = stringResource(R.string.feature_user_profile_manage_account),
                onClick = onManageAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
            )
            Option(
                icon = painterResource(R.drawable.feature_user_profile_outline_person_pin_circle_24),
                text = stringResource(coreUiResources.string.core_ui_my_office),
                onClick = onMyOfficeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
            )
            Option(
                icon = painterResource(R.drawable.feature_user_profile_outline_lightbulb_24),
                text = stringResource(coreUiResources.string.core_ui_my_ideas),
                onClick = onMyIdeasClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
            )
            HorizontalDivider(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )
            LogoutButton(
                onClick = onLogoutClick,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 30.dp)
            )
        }
    }
}

@Composable
private fun LogoutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.feature_user_profile_logout),
        style = MaterialTheme.typography.bodyMedium,
        fontSize = 15.sp,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberCircleClickEffectIndication(
                    circleColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ),
                onClick = onClick
            )
    )
}

@Composable
private fun ObserveIsLoggedOut(
    userProfileUiState: UserProfileUiState,
    navigateToAuthGraph: () -> Unit
) {
    val currentOnNavigateToAuthGraph by rememberUpdatedState(navigateToAuthGraph)
    LaunchedEffect(userProfileUiState) {
        if (userProfileUiState.isLoggedOut) {
            currentOnNavigateToAuthGraph()
        }
    }
}

@Composable
fun UserInfoSection(
    name: String,
    surname: String,
    photoUrl: String,
    job: String,
    containerColor: Color,
    modifier: Modifier = Modifier,
    contentTopPadding: Dp = 15.dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .shadow(blurRadius = 4.dp, cornerRadius = 20.dp)
            .clip(
                RoundedCornerShape(
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )
            )
            .background(containerColor)
    ) {
        AsyncImageWithLoading(
            model = photoUrl,
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = contentTopPadding)
                .size(width = 160.dp, height = 160.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.extraLarge
                )
        )
        Text(
            text = "$name $surname",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)
        )
        Text(
            text = job,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
    }
}

@Composable
private fun Option(
    icon: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(modifier)
    ) {
        Icon(
            painter = icon,
            contentDescription = "icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp
        )
    }
}

private fun isScreenLoading(userProfileUiState: UserProfileUiState) =
    userProfileUiState.isLoading

private fun isErrorWhileLoading(userProfileUiState: UserProfileUiState) =
    userProfileUiState.isErrorWhileUserLoading

@Preview
@Composable
private fun UserInfoSectionPreview() {
    OfficeAppTheme {
        Surface {
            UserInfoSection(
                name = "Дмитрий",
                surname = "Комарницкий",
                job = "Сотрудник Tinkoff",
                photoUrl = "",
                containerColor = MaterialTheme.colorScheme.background
            )
        }
    }
}

@Preview
@Composable
private fun UserProfileScreenPreview() {
    OfficeAppTheme {
        UserProfileScreen(
            userProfileUiState = UserProfileUiState(
                name = UserInfoFieldUiState("Дмитрий"),
                surname = UserInfoFieldUiState("Комарницкий"),
                job = UserInfoFieldUiState("Сотрудник Tinkoff")
            ),
            onManageAccountClick = {},
            onMyOfficeClick = {},
            onMyIdeasClick = {},
            onLogoutClick = {},
            onRetryUserLoadClick = {},
            onPullToRefresh = { Job() },
            navigateToHomeScreen = {},
            navigateToFavouriteScreen = {},
            navigateToMyOfficeScreen = {},
            navigateToAuthGraph = {}
        )
    }
}