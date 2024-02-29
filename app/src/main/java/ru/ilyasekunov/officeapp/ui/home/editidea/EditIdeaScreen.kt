package ru.ilyasekunov.officeapp.ui.home.editidea

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.navigation.BottomNavigationScreen
import ru.ilyasekunov.officeapp.ui.LoadingScreen
import ru.ilyasekunov.officeapp.ui.components.BottomNavigationBar
import ru.ilyasekunov.officeapp.ui.home.suggestidea.EditIdeaSection
import ru.ilyasekunov.officeapp.ui.home.suggestidea.SuggestIdeaTopBar
import ru.ilyasekunov.officeapp.ui.networkErrorSnackbar

@Composable
fun EditIdeaScreen(
    editIdeaUiState: EditIdeaUiState,
    onTitleValueChange: (String) -> Unit,
    onIdeaBodyValueChange: (String) -> Unit,
    onRemoveImageClick: (image: AttachedImage) -> Unit,
    onPublishClick: () -> Unit,
    onAttachImagesButtonClick: () -> Unit,
    onRetryClick: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    if (editIdeaUiState.isLoading) {
        LoadingScreen()
    } else {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                SuggestIdeaTopBar(
                    onCloseClick = navigateBack,
                    onPublishClick = onPublishClick,
                    modifier = Modifier.padding(start = 10.dp, end = 15.dp)
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            bottomBar = {
                BottomNavigationBar(
                    selectedScreen = BottomNavigationScreen.Home,
                    navigateToHomeScreen = navigateToHomeScreen,
                    navigateToFavouriteScreen = navigateToFavouriteScreen,
                    navigateToMyOfficeScreen = navigateToMyOfficeScreen,
                    navigateToProfileScreen = navigateToProfileScreen
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.editing),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(start = 20.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))

                val writingIdeaSectionShape = MaterialTheme.shapes.large.copy(
                    bottomStart = CornerSize(0.dp),
                    bottomEnd = CornerSize(0.dp)
                )
                val writingIdeaSectionBorderWidth = 1.dp
                EditIdeaSection(
                    title = editIdeaUiState.title,
                    content = editIdeaUiState.content,
                    attachedImages = editIdeaUiState.attachedImages,
                    onTitleValueChange = onTitleValueChange,
                    onIdeaBodyValueChange = onIdeaBodyValueChange,
                    onRemoveImageClick = onRemoveImageClick,
                    onAttachImagesButtonClick = onAttachImagesButtonClick,
                    modifier = Modifier
                        .weight(1f)
                        .clip(writingIdeaSectionShape)
                        .offset(y = writingIdeaSectionBorderWidth)
                        .border(
                            width = writingIdeaSectionBorderWidth,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = writingIdeaSectionShape
                        )
                )
            }
        }
    }

    ObserveNetworkError(
        editIdeaUiState = editIdeaUiState,
        snackbarHostState = snackbarHostState,
        onActionPerformedClick = onRetryClick
    )

    ObserveIsPublished(
        editIdeaUiState = editIdeaUiState,
        navigateToHomeScreen = navigateToHomeScreen
    )
}

@Composable
private fun ObserveNetworkError(
    editIdeaUiState: EditIdeaUiState,
    snackbarHostState: SnackbarHostState,
    onActionPerformedClick: () -> Unit,
) {
    val retryLabel = stringResource(R.string.retry)
    val serverErrorMessage = stringResource(R.string.error_connecting_to_server)
    val currentOnActionPerformedClick by rememberUpdatedState(onActionPerformedClick)
    LaunchedEffect(editIdeaUiState.isNetworkError) {
        if (editIdeaUiState.isNetworkError) {
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
private fun ObserveIsPublished(
    editIdeaUiState: EditIdeaUiState,
    navigateToHomeScreen: () -> Unit
) {
    val currentNavigateToHomeScreen by rememberUpdatedState(navigateToHomeScreen)
    LaunchedEffect(editIdeaUiState.isPublished) {
        if (editIdeaUiState.isPublished) {
            currentNavigateToHomeScreen()
        }
    }
}