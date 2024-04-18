package ru.ilyasekunov.officeapp.ui.userprofile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.Job
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.ui.ErrorScreen
import ru.ilyasekunov.officeapp.ui.LoadingScreen
import ru.ilyasekunov.officeapp.ui.LocalCurrentNavigationBarScreen
import ru.ilyasekunov.officeapp.ui.LocalSnackbarHostState
import ru.ilyasekunov.officeapp.ui.auth.registration.UserInfoFieldUiState
import ru.ilyasekunov.officeapp.ui.components.BasicPullToRefreshContainer
import ru.ilyasekunov.officeapp.ui.components.BottomNavigationBar
import ru.ilyasekunov.officeapp.ui.components.rememberCircleClickEffectIndication
import ru.ilyasekunov.officeapp.ui.modifiers.shadow
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@Composable
fun UserProfileScreen(
    userProfileUiState: UserProfileUiState,
    onManageAccountClick: () -> Unit,
    onMyOfficeClick: () -> Unit,
    onMyIdeasClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onRetryUserLoadClick: () -> Unit,
    onPullToRefresh: suspend () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToAuthGraph: () -> Unit
) {
    if (userProfileUiState.isLoading) {
        LoadingScreen()
    } else {
        val containerColor = MaterialTheme.colorScheme.background
        Scaffold(
            containerColor = containerColor,
            snackbarHost = { SnackbarHost(hostState = LocalSnackbarHostState.current) },
            bottomBar = {
                BottomNavigationBar(
                    selectedScreen = LocalCurrentNavigationBarScreen.current,
                    navigateToHomeScreen = navigateToHomeScreen,
                    navigateToFavouriteScreen = navigateToFavouriteScreen,
                    navigateToMyOfficeScreen = navigateToMyOfficeScreen
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            if (userProfileUiState.isErrorWhileUserLoading) {
                ErrorScreen(
                    message = stringResource(R.string.error_connecting_to_server),
                    onRetryButtonClick = onRetryUserLoadClick
                )
            } else {
                UserProfileContent(
                    userProfileUiState = userProfileUiState,
                    userInfoSectionContainerColor = containerColor,
                    onManageAccountClick = onManageAccountClick,
                    onMyOfficeClick = onMyOfficeClick,
                    onMyIdeasClick = onMyIdeasClick,
                    onLogoutClick = onLogoutClick,
                    onPullToRefresh = onPullToRefresh,
                    modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
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
fun UserProfileContent(
    userProfileUiState: UserProfileUiState,
    userInfoSectionContainerColor: Color,
    onManageAccountClick: () -> Unit,
    onMyOfficeClick: () -> Unit,
    onMyIdeasClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onPullToRefresh: suspend () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicPullToRefreshContainer(onRefreshTrigger = onPullToRefresh) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            UserInfoSection(
                name = userProfileUiState.name.value,
                surname = userProfileUiState.surname.value,
                photoUrl = userProfileUiState.photo.toString(),
                job = userProfileUiState.job.value,
                containerColor = userInfoSectionContainerColor,
                modifier = Modifier.fillMaxWidth()
            )
            Option(
                icon = painterResource(R.drawable.outline_manage_accounts_24),
                text = stringResource(R.string.manage_account),
                onClick = onManageAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
            )
            Option(
                icon = painterResource(R.drawable.outline_person_pin_circle_24),
                text = stringResource(R.string.my_office),
                onClick = onMyOfficeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
            )
            Option(
                icon = painterResource(R.drawable.outline_lightbulb_24),
                text = stringResource(R.string.my_ideas),
                onClick = onMyIdeasClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
            )
            HorizontalDivider(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.log_out),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberCircleClickEffectIndication(
                            circleColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                        onClick = onLogoutClick
                    )
            )
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
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
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(photoUrl)
            .size(coil.size.Size.ORIGINAL)
            .build()
    )
    val imageShape = MaterialTheme.shapes.extraLarge
    val imageBorderColor = MaterialTheme.colorScheme.primary
    val imageBorderShape = MaterialTheme.shapes.extraLarge
    val imageModifier = remember {
        Modifier
            .statusBarsPadding()
            .padding(top = contentTopPadding)
            .size(width = 160.dp, height = 160.dp)
            .clip(imageShape)
            .border(
                width = 1.dp,
                color = imageBorderColor,
                shape = imageBorderShape
            )
    }
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
        when (imagePainter.state) {
            is AsyncImagePainter.State.Loading -> {
                Box(
                    modifier = imageModifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                        .shimmer()
                )
            }

            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = imagePainter,
                    contentDescription = "user_photo",
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier
                )
            }

            else -> {
                NoPhoto(
                    modifier = imageModifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "$name $surname",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = job,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun Option(
    icon: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onClick() }
            .then(modifier)
    ) {
        Icon(
            painter = icon,
            contentDescription = "icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp
        )
    }
}

@Composable
fun NoPhoto(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.no_photo),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 20.sp
        )
    }
}

@Preview
@Composable
fun NoPhotoPreview() {
    OfficeAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            NoPhoto(modifier = Modifier.size(100.dp))
        }
    }
}

@Preview
@Composable
fun UserInfoSectionPreview() {
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
fun UserProfileScreenPreview() {
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