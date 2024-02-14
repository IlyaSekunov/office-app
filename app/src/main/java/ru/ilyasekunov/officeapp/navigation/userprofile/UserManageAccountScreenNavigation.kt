package ru.ilyasekunov.officeapp.navigation.userprofile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.officeList
import ru.ilyasekunov.officeapp.navigation.Screen
import ru.ilyasekunov.officeapp.ui.animations.enterSlideLeft
import ru.ilyasekunov.officeapp.ui.animations.exitSlideRight
import ru.ilyasekunov.officeapp.ui.userprofile.UserInfoUiState
import ru.ilyasekunov.officeapp.ui.userprofile.UserManageAccountScreen
import ru.ilyasekunov.officeapp.ui.userprofile.UserViewModel
import ru.ilyasekunov.officeapp.ui.userprofile.differFrom
import ru.ilyasekunov.officeapp.ui.userprofile.toUserInfoUiState
import ru.ilyasekunov.officeapp.util.toBitmap
import ru.ilyasekunov.officeapp.util.toByteArray

fun NavGraphBuilder.userManageAccountScreen(
    viewModelStoreOwnerProvider: () -> ViewModelStoreOwner,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    composable(
        route = Screen.UserManageAccount.route,
        enterTransition = { enterSlideLeft() },
        exitTransition = { exitSlideRight() }
    ) { backStackEntry ->
        val viewModelStoreOwner = remember(backStackEntry) { viewModelStoreOwnerProvider() }
        val userInfoViewModel = hiltViewModel<UserViewModel>(viewModelStoreOwner)

        val user = userInfoViewModel.user!!
        var userInfoUiState by rememberSaveable(stateSaver = UserInfoUiState.Saver) {
            mutableStateOf(user.toUserInfoUiState())
        }

        // Initialize image picker
        val contentResolver = LocalContext.current.contentResolver
        val coroutineScope = rememberCoroutineScope()
        val singleImagePicker =
            rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
                coroutineScope.launch(Dispatchers.IO) {
                    val bitmap = it.toBitmap(contentResolver)
                    val photo = bitmap.toByteArray()
                    userInfoUiState = userInfoUiState.copy(photo = photo)
                }
            }

        UserManageAccountScreen(
            userInfoUiState = userInfoUiState,
            officeList = officeList,
            onPhotoPickerClick = {
                singleImagePicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onNameValueChange = {
                userInfoUiState = userInfoUiState.copy(name = it)
            },
            onSurnameValueChange = {
                userInfoUiState = userInfoUiState.copy(surname = it)
            },
            onJobValueChange = {
                userInfoUiState = userInfoUiState.copy(job = it)
            },
            onOfficeChange = {
                userInfoUiState = userInfoUiState.copy(office = it)
            },
            isSaveButtonEnabled = { user differFrom userInfoUiState },
            onSaveButtonClick = {
                val updatedUser = user.copy(
                    name = userInfoUiState.name,
                    surname = userInfoUiState.surname,
                    job = userInfoUiState.job,
                    office = userInfoUiState.office,
                    photo = userInfoUiState.photo
                )
                userInfoViewModel.updateUser(updatedUser)
                navigateToProfileScreen()
            },
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToProfileScreen = navigateToProfileScreen,
            navigateBack = navigateBack
        )
    }
}

fun NavController.navigateToUserManageAccountScreen(navOptions: NavOptions? = null) =
    navigate(Screen.UserManageAccount.route, navOptions)