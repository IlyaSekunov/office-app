package ru.ilyasekunov.ui.imagepickers

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

object ImagePickerDefaults {
    const val MAX_ATTACH_IMAGES = 10
    const val COMMENTS_MAX_ATTACH_IMAGES = 1
}

@Composable
fun rememberSingleImagePickerRequest(onResult: (Uri?) -> Unit): () -> Unit {
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = onResult
    )
    return remember(activityResultLauncher, onResult) {
        {
            activityResultLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
}

@Composable
fun rememberMultipleImagePickerRequest(
    maxItems: Int,
    onResult: (List<Uri>) -> Unit
): () -> Unit {
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxItems),
        onResult = onResult
    )
    return remember(activityResultLauncher, onResult) {
        {
            activityResultLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
}