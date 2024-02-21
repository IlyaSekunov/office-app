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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import ru.ilyasekunov.officeapp.ui.home.suggestidea.EditingIdeaSection
import ru.ilyasekunov.officeapp.ui.home.suggestidea.SuggestIdeaTopBar

@Composable
fun EditIdeaScreen(
    editingIdeaUiState: EditIdeaUiState,
    onTitleValueChange: (String) -> Unit,
    onIdeaBodyValueChange: (String) -> Unit,
    onRemoveImageClick: (image: AttachedImage) -> Unit,
    onPublishClick: () -> Unit,
    onAttachImagesButtonClick: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    if (editingIdeaUiState.isLoading) {
        LoadingScreen(
            circularProgressingColor = MaterialTheme.colorScheme.primary,
            circularProgressingWidth = 3.dp,
            circularProgressingSize = 40.dp,
            modifier = Modifier.fillMaxSize()
        )
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
                EditingIdeaSection(
                    title = editingIdeaUiState.title,
                    content = editingIdeaUiState.content,
                    attachedImages = editingIdeaUiState.attachedImages,
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

    // Observe isPublished state to navigate to home screen
    val currentNavigateToHomeScreen by rememberUpdatedState(navigateToHomeScreen)
    LaunchedEffect(editingIdeaUiState) {
        if (editingIdeaUiState.isPublished) {
            currentNavigateToHomeScreen()
        }
    }
}