package ru.ilyasekunov.officeapp.ui.editidea

import android.net.Uri
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.ui.AnimatedLoadingScreen
import ru.ilyasekunov.officeapp.ui.ErrorScreen
import ru.ilyasekunov.officeapp.ui.LocalCoroutineScope
import ru.ilyasekunov.officeapp.ui.LocalCurrentNavigationBarScreen
import ru.ilyasekunov.officeapp.ui.LocalSnackbarHostState
import ru.ilyasekunov.officeapp.ui.components.AttachedImage
import ru.ilyasekunov.officeapp.ui.components.BottomNavigationBar
import ru.ilyasekunov.officeapp.ui.snackbarWithAction
import ru.ilyasekunov.officeapp.ui.suggestidea.EditIdeaSection
import ru.ilyasekunov.officeapp.ui.suggestidea.SuggestIdeaTopBar

@Composable
fun EditIdeaScreen(
    editIdeaUiState: EditIdeaUiState,
    onTitleValueChange: (String) -> Unit,
    onIdeaBodyValueChange: (String) -> Unit,
    onRemoveImageClick: (AttachedImage) -> Unit,
    onPublishClick: () -> Unit,
    onAttachImagesButtonClick: (List<Uri>) -> Unit,
    onRetryClick: () -> Unit,
    onRetryLoadPost: () -> Unit,
    onErrorWhilePublishingShown: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    when {
        editIdeaUiState.isLoading -> AnimatedLoadingScreen()
        editIdeaUiState.isErrorWhileLoading -> {
            ErrorScreen(
                message = stringResource(R.string.error_connecting_to_server),
                onRetryButtonClick = onRetryLoadPost
            )
        }

        else -> EditIdeaScreenContent(
            editIdeaUiState = editIdeaUiState,
            onTitleValueChange = onTitleValueChange,
            onIdeaBodyValueChange = onIdeaBodyValueChange,
            onRemoveImageClick = onRemoveImageClick,
            onPublishClick = onPublishClick,
            onAttachImagesButtonClick = onAttachImagesButtonClick,
            onRetryClick = onRetryClick,
            onErrorWhilePublishingShown = onErrorWhilePublishingShown,
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToProfileScreen = navigateToProfileScreen,
            navigateBack = navigateBack,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun EditIdeaScreenContent(
    editIdeaUiState: EditIdeaUiState,
    onTitleValueChange: (String) -> Unit,
    onIdeaBodyValueChange: (String) -> Unit,
    onRemoveImageClick: (AttachedImage) -> Unit,
    onPublishClick: () -> Unit,
    onAttachImagesButtonClick: (List<Uri>) -> Unit,
    onRetryClick: () -> Unit,
    onErrorWhilePublishingShown: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = LocalSnackbarHostState.current
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SuggestIdeaTopBar(
                onCloseClick = navigateBack,
                onPublishClick = onPublishClick,
                modifier = Modifier.padding(start = 10.dp, end = 15.dp)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = LocalCurrentNavigationBarScreen.current,
                navigateToHomeScreen = navigateToHomeScreen,
                navigateToFavouriteScreen = navigateToFavouriteScreen,
                navigateToMyOfficeScreen = navigateToMyOfficeScreen,
                navigateToProfileScreen = navigateToProfileScreen
            )
        },
        modifier = modifier
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
    ObserveStateChanges(
        editIdeaUiState = editIdeaUiState,
        snackbarHostState = snackbarHostState,
        coroutineScope = LocalCoroutineScope.current,
        onRetryClick = onRetryClick,
        onErrorWhilePublishingShown = onErrorWhilePublishingShown,
        navigateBack = navigateBack
    )
}

@Composable
private fun ObserveStateChanges(
    editIdeaUiState: EditIdeaUiState,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onRetryClick: () -> Unit,
    onErrorWhilePublishingShown: () -> Unit,
    navigateBack: () -> Unit
) {
    ObserveIsErrorWhilePublishing(
        editIdeaUiState = editIdeaUiState,
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope,
        onActionPerformedClick = onRetryClick,
        onErrorShown = onErrorWhilePublishingShown
    )
    ObserveIsPublished(
        editIdeaUiState = editIdeaUiState,
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope,
        navigateBack = navigateBack
    )
}

@Composable
private fun ObserveIsErrorWhilePublishing(
    editIdeaUiState: EditIdeaUiState,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onActionPerformedClick: () -> Unit,
    onErrorShown: () -> Unit
) {
    val retryLabel = stringResource(R.string.retry)
    val serverErrorMessage = stringResource(R.string.error_connecting_to_server)
    val currentOnActionPerformedClick by rememberUpdatedState(onActionPerformedClick)
    val currentOnErrorShown by rememberUpdatedState(onErrorShown)
    LaunchedEffect(Unit) {
        snapshotFlow { editIdeaUiState }
            .filter { it.isErrorWhilePublishing }
            .collectLatest {
                coroutineScope.launch {
                    snackbarHostState.snackbarWithAction(
                        message = serverErrorMessage,
                        actionLabel = retryLabel,
                        onActionClick = currentOnActionPerformedClick,
                        duration = SnackbarDuration.Short
                    )
                    currentOnErrorShown()
                }
            }
    }
}

@Composable
private fun ObserveIsPublished(
    editIdeaUiState: EditIdeaUiState,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    navigateBack: () -> Unit
) {
    val currentNavigateBack by rememberUpdatedState(navigateBack)
    val message = stringResource(R.string.idea_edited_successfully)
    LaunchedEffect(editIdeaUiState) {
        if (editIdeaUiState.isPublished) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
            currentNavigateBack()
        }
    }
}