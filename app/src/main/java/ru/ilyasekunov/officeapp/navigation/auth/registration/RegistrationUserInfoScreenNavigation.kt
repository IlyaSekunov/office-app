package ru.ilyasekunov.officeapp.navigation.auth.registration

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
import ru.ilyasekunov.officeapp.ui.auth.registration.RegistrationUserInfoScreen
import ru.ilyasekunov.officeapp.ui.auth.registration.RegistrationViewModel
import ru.ilyasekunov.officeapp.util.toBitmap
import ru.ilyasekunov.officeapp.util.toByteArray

fun NavGraphBuilder.registrationUserInfoScreen(
    viewModelStoreOwnerProvider: () -> ViewModelStoreOwner,
    navigateBack: () -> Unit,
    navigateToMainGraph: () -> Unit
) {
    composable(Screen.RegistrationUserInfo.route) { backStackEntry ->
        val viewModelStoreOwner = remember(backStackEntry) { viewModelStoreOwnerProvider() }
        val registrationViewModel = hiltViewModel<RegistrationViewModel>(viewModelStoreOwner)
        val contentResolver = LocalContext.current.contentResolver
        val coroutineScope = rememberCoroutineScope()
        val galleryLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
                coroutineScope.launch(Dispatchers.IO) {
                    val bitmap = it.toBitmap(contentResolver)
                    val photo = bitmap.toByteArray()
                    registrationViewModel.updatePhoto(photo)
                }
            }
        RegistrationUserInfoScreen(
            userInfoUiState = registrationViewModel.registrationUiState.userInfoUiState,
            officeList = officeList,
            navigateBack = navigateBack,
            onPhotoPickerClick = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onNameValueChange = registrationViewModel::updateName,
            onSurnameValueChange = registrationViewModel::updateSurname,
            onJobValueChange = registrationViewModel::updateJob,
            onOfficeChange = registrationViewModel::updateOffice,
            onSaveButtonClick = {
                registrationViewModel.register()
                navigateToMainGraph()
            }
        )
    }
}

fun NavController.navigateToRegistrationUserInfoScreen(navOptions: NavOptions? = null) =
    navigate(Screen.RegistrationUserInfo.route, navOptions)