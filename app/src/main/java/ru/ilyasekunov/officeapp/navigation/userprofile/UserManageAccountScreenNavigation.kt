package ru.ilyasekunov.officeapp.navigation.userprofile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import ru.ilyasekunov.officeapp.ui.userprofile.UserManageAccountScreen
import ru.ilyasekunov.officeapp.ui.userprofile.UserViewModel
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
    composable(route = Screen.UserManageAccount.route) { backStackEntry ->
        val viewModelStoreOwner = remember(backStackEntry) { viewModelStoreOwnerProvider() }
        val userInfoViewModel = hiltViewModel<UserViewModel>(viewModelStoreOwner)
        val contentResolver = LocalContext.current.contentResolver
        val coroutineScope = rememberCoroutineScope()
        val galleryLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
                coroutineScope.launch(Dispatchers.IO) {
                    val bitmap = it.toBitmap(contentResolver)
                    val photo = bitmap.toByteArray()
                    userInfoViewModel.updatePhoto(photo)
                }
            }
        UserManageAccountScreen(
            userInfoUiState = userInfoViewModel.userInfoUiState,
            officeList = officeList,
            onPhotoPickerClick = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onNameValueChange = userInfoViewModel::updateName,
            onSurnameValueChange = userInfoViewModel::updateSurname,
            onJobValueChange = userInfoViewModel::updateJob,
            onOfficeChange = userInfoViewModel::updateOffice,
            isSaveButtonEnabled = userInfoViewModel.isUserNewInfoUnsaved,
            onSaveButtonClick = {
                userInfoViewModel.save()
                navigateToProfileScreen()
            },
            navigateToHomeScreen = {
                userInfoViewModel.restoreUserInfoUiChanges()
                navigateToHomeScreen()
            },
            navigateToFavouriteScreen = {
                userInfoViewModel.restoreUserInfoUiChanges()
                navigateToFavouriteScreen()
            },
            navigateToMyOfficeScreen = {
                userInfoViewModel.restoreUserInfoUiChanges()
                navigateToMyOfficeScreen()
            },
            navigateToProfileScreen = {
                userInfoViewModel.restoreUserInfoUiChanges()
                navigateToProfileScreen()
            },
            navigateBack = {
                userInfoViewModel.restoreUserInfoUiChanges()
                navigateBack()
            }
        )
    }
}

fun NavController.navigateToUserManageAccountScreen(navOptions: NavOptions? = null) =
    navigate(Screen.UserManageAccount.route, navOptions)