package ru.ilyasekunov.officeapp.navigation.home.editidea

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
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
import ru.ilyasekunov.officeapp.ui.home.editidea.EditIdeaScreen
import ru.ilyasekunov.officeapp.ui.home.editidea.EditIdeaViewModel
import ru.ilyasekunov.officeapp.util.toBitmap
import ru.ilyasekunov.officeapp.util.toByteArray

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
        val coroutineScope = rememberCoroutineScope()
        val contentResolver = LocalContext.current.contentResolver
        val multipleImagePicker =
            rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) {
                coroutineScope.launch(Dispatchers.IO) {
                    it.forEach { imageUri ->
                        val bitmap = imageUri.toBitmap(contentResolver)
                        val image = bitmap.toByteArray()
                        if (image != null) {
                            editIdeaViewModel.attachImage(image)
                        }
                    }
                }
            }

        EditIdeaScreen(
            editingIdeaUiState = editIdeaViewModel.editIdeaUiState,
            isLoading = editIdeaViewModel.isLoading,
            onTitleValueChange = editIdeaViewModel::updateTitle,
            onIdeaBodyValueChange = editIdeaViewModel::updateContent,
            onRemoveImageClick = editIdeaViewModel::removeImage,
            onPublishClick = {
                editIdeaViewModel.editPost()
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

fun NavController.navigateToEditIdeaScreen(
    postId: Long, navOptions: NavOptions? = null
) {
    val destination = Screen.EditIdea.route.replace("{postId}", postId.toString())
    navigate(destination, navOptions)
}