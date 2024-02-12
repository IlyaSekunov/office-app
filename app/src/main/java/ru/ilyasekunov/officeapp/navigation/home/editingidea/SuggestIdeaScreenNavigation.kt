package ru.ilyasekunov.officeapp.navigation.home.editingidea

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.navigation.Screen
import ru.ilyasekunov.officeapp.ui.animations.enterSlideUp
import ru.ilyasekunov.officeapp.ui.animations.exitSlideDown
import ru.ilyasekunov.officeapp.ui.home.editingtidea.EditingIdeaViewModel
import ru.ilyasekunov.officeapp.ui.home.editingtidea.SuggestIdeaScreen
import ru.ilyasekunov.officeapp.util.toBitmap
import ru.ilyasekunov.officeapp.util.toByteArray

fun NavGraphBuilder.suggestIdeaScreen(
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(
        route = Screen.SuggestIdea.route,
        enterTransition = { enterSlideUp() },
        exitTransition = { exitSlideDown() }
    ) {
        val editingIdeaViewModel = hiltViewModel<EditingIdeaViewModel>()
        val coroutineScope = rememberCoroutineScope()
        val contentResolver = LocalContext.current.contentResolver
        val multipleImagePicker =
            rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) {
                coroutineScope.launch(Dispatchers.IO) {
                    it.forEach { imageUri ->
                        val bitmap = imageUri.toBitmap(contentResolver)
                        val image = bitmap.toByteArray()
                        if (image != null) {
                            editingIdeaViewModel.attachImage(image)
                        }
                    }
                }
            }
        SuggestIdeaScreen(
            editingIdeaUiState = editingIdeaViewModel.editingIdeaUiState,
            onTitleValueChange = editingIdeaViewModel::updateTitle,
            onIdeaBodyValueChange = editingIdeaViewModel::updateBody,
            onRemoveImageClick = editingIdeaViewModel::removeImage,
            onPublishClick = {
                editingIdeaViewModel.publishPost()
                navigateToHomeScreen()
            },
            onAttachImagesButtonClick = {
                multipleImagePicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToProfileScreen = navigateToProfileScreen,
            navigateBack = navigateBack
        )
    }
}

fun NavController.navigateToSuggestIdeaScreen(navOptions: NavOptions? = null) =
    navigate(Screen.SuggestIdea.route, navOptions)