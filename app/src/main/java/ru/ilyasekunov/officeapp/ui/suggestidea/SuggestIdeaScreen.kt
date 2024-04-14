package ru.ilyasekunov.officeapp.ui.suggestidea

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ilyasekunov.officeapp.LocalCurrentNavigationBarScreen
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.navigation.BottomNavigationScreen
import ru.ilyasekunov.officeapp.ui.LoadingScreen
import ru.ilyasekunov.officeapp.ui.components.AttachedImage
import ru.ilyasekunov.officeapp.ui.components.AttachedImages
import ru.ilyasekunov.officeapp.ui.components.BottomNavigationBar
import ru.ilyasekunov.officeapp.ui.networkErrorSnackbar
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@Composable
fun SuggestIdeaScreen(
    suggestIdeaUiState: SuggestIdeaUiState,
    onTitleValueChange: (String) -> Unit,
    onIdeaBodyValueChange: (String) -> Unit,
    onRemoveImageClick: (AttachedImage) -> Unit,
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
    if (suggestIdeaUiState.isLoading) {
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
                    selectedScreen = LocalCurrentNavigationBarScreen.current,
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
                    text = stringResource(R.string.suggest_an_idea),
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
                    title = suggestIdeaUiState.title,
                    content = suggestIdeaUiState.content,
                    attachedImages = suggestIdeaUiState.attachedImages,
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
        suggestIdeaUiState = suggestIdeaUiState,
        snackbarHostState = snackbarHostState,
        onActionPerformedClick = onRetryClick
    )

    ObserveIsPublished(
        suggestIdeaUiState = suggestIdeaUiState,
        navigateToHomeScreen = navigateToHomeScreen
    )
}

@Composable
private fun ObserveNetworkError(
    suggestIdeaUiState: SuggestIdeaUiState,
    snackbarHostState: SnackbarHostState,
    onActionPerformedClick: () -> Unit
) {
    val retryLabel = stringResource(R.string.retry)
    val serverErrorMessage = stringResource(R.string.error_connecting_to_server)
    val currentOnActionPerformedClick by rememberUpdatedState(onActionPerformedClick)
    LaunchedEffect(suggestIdeaUiState) {
        if (suggestIdeaUiState.isNetworkError) {
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
    suggestIdeaUiState: SuggestIdeaUiState,
    navigateToHomeScreen: () -> Unit
) {
    val currentNavigateToHomeScreen by rememberUpdatedState(navigateToHomeScreen)
    LaunchedEffect(suggestIdeaUiState) {
        if (suggestIdeaUiState.isPublished) {
            currentNavigateToHomeScreen()
        }
    }
}

@Composable
fun EditIdeaSection(
    title: String,
    content: String,
    attachedImages: List<AttachedImage>,
    onTitleValueChange: (String) -> Unit,
    onIdeaBodyValueChange: (String) -> Unit,
    onAttachImagesButtonClick: () -> Unit,
    onRemoveImageClick: (image: AttachedImage) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TextFieldsWithImagesSection(
            title = title,
            content = content,
            attachedImages = attachedImages,
            onTitleValueChange = onTitleValueChange,
            onIdeaBodyValueChange = onIdeaBodyValueChange,
            onRemoveImageClick = onRemoveImageClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            AttachImagesButton(
                size = 50.dp,
                onClick = onAttachImagesButtonClick,
                modifier = Modifier.padding(start = 20.dp, bottom = 10.dp, top = 10.dp)
            )
        }
    }
}

@Composable
fun TextFieldsWithImagesSection(
    title: String,
    content: String,
    attachedImages: List<AttachedImage>,
    onTitleValueChange: (String) -> Unit,
    onIdeaBodyValueChange: (String) -> Unit,
    onRemoveImageClick: (AttachedImage) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val textFieldColors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary
        )
        TextField(
            value = title,
            onValueChange = onTitleValueChange,
            textStyle = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
            placeholder = {
                Text(
                    text = stringResource(R.string.title),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    fontSize = 20.sp
                )
            },
            colors = textFieldColors,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        TextField(
            value = content,
            onValueChange = onIdeaBodyValueChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
            placeholder = {
                Text(
                    text = stringResource(R.string.description),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    fontSize = 16.sp
                )
            },
            colors = textFieldColors,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        )
        if (attachedImages.isNotEmpty()) {
            AttachedImages(
                images = attachedImages,
                imageSize = DpSize(width = 190.dp, height = 170.dp),
                onRemoveClick = onRemoveImageClick,
                contentPadding = PaddingValues(horizontal = 18.dp),
                closeIconSize = 26.dp
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestIdeaTopBar(
    onCloseClick: () -> Unit,
    onPublishClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            Icon(
                painter = painterResource(R.drawable.baseline_close_24),
                contentDescription = "close_icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(30.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onCloseClick
                    )
            )
        },
        actions = {
            PublishButton(
                onClick = onPublishClick,
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 7.dp)
            )
        },
        modifier = modifier
    )
}

@Composable
fun CloseIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clickable { onClick() }
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_close_24),
            contentDescription = "close_icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize()
        )
    }
}

@Composable
fun PublishButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.large
            )
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            .clickable { onClick() }
            .padding(contentPadding)
    ) {
        Text(
            text = stringResource(R.string.publish),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun AttachImagesButton(
    size: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    ) {
        val iconSize = (size.value * 0.6).dp
        Icon(
            painter = painterResource(R.drawable.outline_image_24),
            contentDescription = "image_icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(6.dp)
                .size(iconSize)
        )
    }
}

@Preview
@Composable
fun SuggestIdeaScreenPreview() {
    OfficeAppTheme {
        SuggestIdeaScreen(
            suggestIdeaUiState = SuggestIdeaUiState(),
            onTitleValueChange = {},
            onIdeaBodyValueChange = {},
            onRemoveImageClick = {},
            onPublishClick = {},
            onAttachImagesButtonClick = {},
            onRetryClick = {},
            navigateToHomeScreen = {},
            navigateToProfileScreen = {},
            navigateToMyOfficeScreen = {},
            navigateToFavouriteScreen = {},
            navigateBack = {}
        )
    }
}

@Preview
@Composable
fun SuggestIdeaTopBarPreview() {
    OfficeAppTheme {
        Surface {
            SuggestIdeaTopBar(
                onCloseClick = {},
                onPublishClick = {}
            )
        }
    }
}

@Preview
@Composable
fun PublishButtonPreview() {
    OfficeAppTheme {
        Surface {
            PublishButton(
                onClick = {}
            )
        }
    }
}