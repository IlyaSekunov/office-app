package ru.ilyasekunov.officeapp.navigation.home.editidea

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.navigation.Screen
import ru.ilyasekunov.officeapp.preferences.ImagePickerDefaults
import ru.ilyasekunov.officeapp.ui.home.editidea.EditIdeaScreen
import ru.ilyasekunov.officeapp.ui.home.editidea.EditIdeaViewModel

fun NavGraphBuilder.editIdeaScreen(
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(
        route = Screen.EditIdea.route,
        arguments = Screen.EditIdea.arguments
    ) { backStackEntry ->
        val postId = backStackEntry.arguments?.getLong("postId")!!
        val editIdeaViewModel = hiltViewModel<EditIdeaViewModel>()
        LaunchedEffect(Unit) {
            editIdeaViewModel.loadPostById(postId)
        }

        // Initialize image picker
        val multipleImagePicker =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickMultipleVisualMedia(
                    maxItems = ImagePickerDefaults.MAX_ATTACH_IMAGES
                )
            ) {
                it.forEach { imageUri ->
                    editIdeaViewModel.attachImage(imageUri)
                }
            }

        EditIdeaScreen(
            editIdeaUiState = editIdeaViewModel.editIdeaUiState,
            onTitleValueChange = editIdeaViewModel::updateTitle,
            onIdeaBodyValueChange = editIdeaViewModel::updateContent,
            onRemoveImageClick = editIdeaViewModel::removeImage,
            onPublishClick = editIdeaViewModel::editPost,
            onAttachImagesButtonClick = {
                multipleImagePicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onRetryClick = editIdeaViewModel::editPost,
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToProfileScreen = navigateToProfileScreen,
            navigateBack = navigateBack
        )
    }
}

fun NavController.navigateToEditIdeaScreen(
    postId: Long, navOptions: NavOptions? = null
) {
    val destination = Screen.EditIdea.route.replace("{postId}", postId.toString())
    navigate(destination, navOptions)
}