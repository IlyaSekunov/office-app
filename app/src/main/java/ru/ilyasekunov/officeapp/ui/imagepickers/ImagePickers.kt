package ru.ilyasekunov.officeapp.ui.imagepickers

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

object ImagePickerDefaults {
    const val MAX_ATTACH_IMAGES = 10
}

abstract class ImagePicker<T>(
    private val activityResultLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, T>
) {
    fun launch() {
        activityResultLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }
}

class SingleImagePicker(
    activityResultLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
) : ImagePicker<Uri?>(activityResultLauncher)

class MultipleImagePicker(
    activityResultLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<Uri>>
) : ImagePicker<List<Uri>>(activityResultLauncher)

@Composable
fun rememberSingleImagePicker(onUriPicked: (Uri?) -> Unit): SingleImagePicker {
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { onUriPicked(it) }
    return SingleImagePicker(activityResultLauncher)
}

@Composable
fun rememberMultipleImagePicker(onUrisPicked: (List<Uri>) -> Unit): MultipleImagePicker {
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = ImagePickerDefaults.MAX_ATTACH_IMAGES
        )
    ) { onUrisPicked(it) }
    return MultipleImagePicker(activityResultLauncher)
}
