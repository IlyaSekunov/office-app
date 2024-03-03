package ru.ilyasekunov.officeapp.navigation.home.editidea

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.ilyasekunov.officeapp.navigation.Screen
import ru.ilyasekunov.officeapp.ui.home.editidea.EditIdeaScreen
import ru.ilyasekunov.officeapp.ui.home.editidea.EditIdeaViewModel
import ru.ilyasekunov.officeapp.ui.util.rememberMultipleImagePicker

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

        val multipleImagePicker = rememberMultipleImagePicker(onUrisPicked = editIdeaViewModel::attachImages)
        EditIdeaScreen(
            editIdeaUiState = editIdeaViewModel.editIdeaUiState,
            onTitleValueChange = editIdeaViewModel::updateTitle,
            onIdeaBodyValueChange = editIdeaViewModel::updateContent,
            onRemoveImageClick = editIdeaViewModel::removeImage,
            onPublishClick = editIdeaViewModel::editPost,
            onAttachImagesButtonClick = multipleImagePicker::launch,
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