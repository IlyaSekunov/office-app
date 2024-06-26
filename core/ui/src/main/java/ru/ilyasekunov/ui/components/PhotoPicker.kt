package ru.ilyasekunov.ui.components

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import ru.ilyasekunov.officeapp.core.ui.R
import ru.ilyasekunov.storage.rememberStorageAccessPermissionRequest
import ru.ilyasekunov.storage.storageAccessPermissionsGranted
import ru.ilyasekunov.ui.imagepickers.rememberSingleImagePickerRequest
import ru.ilyasekunov.ui.theme.OfficeAppTheme

@Composable
fun PhotoPicker(
    selectedPhoto: Any?,
    onSelectedPhoto: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(selectedPhoto)
            .size(coil.size.Size.ORIGINAL)
            .build()
    )
    val onPhotoPickerClick = rememberOnPhotoPickerClick(onSelectedPhoto)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.core_ui_photo_picker_title),
            style = MaterialTheme.typography.titleSmall,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 17.dp)
        )
        Box(
            modifier = modifier
                .clip(MaterialTheme.shapes.large)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.large
                )
                .clickable(onClick = onPhotoPickerClick),
            contentAlignment = Alignment.Center
        ) {
            when (imagePainter.state) {
                is AsyncImagePainter.State.Loading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp,
                        modifier = modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                            .size(30.dp)
                    )
                }

                is AsyncImagePainter.State.Success -> {
                    Image(
                        painter = imagePainter,
                        contentDescription = "selected_photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Icon(
                        painter = painterResource(R.drawable.core_ui_outline_photo_camera_24),
                        contentDescription = "photo_icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }

                else -> {
                    Icon(
                        painter = painterResource(R.drawable.core_ui_outline_photo_camera_24),
                        contentDescription = "photo_icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun rememberOnPhotoPickerClick(onSelectedPhoto: (Uri?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val singleImagePickerRequest = rememberSingleImagePickerRequest(onResult = onSelectedPhoto)
    val storageAccessPermissionRequest = rememberStorageAccessPermissionRequest { granted ->
        if (granted) {
            singleImagePickerRequest()
        }
    }
    return remember(singleImagePickerRequest, storageAccessPermissionRequest) {
        {
            onImagePickerClick(
                context = context,
                openImagePickerRequest = singleImagePickerRequest,
                storageAccessPermissionRequest = storageAccessPermissionRequest
            )
        }
    }
}

fun onImagePickerClick(
    context: Context,
    openImagePickerRequest: () -> Unit,
    storageAccessPermissionRequest: () -> Unit
) {
    if (storageAccessPermissionsGranted(context)) {
        openImagePickerRequest()
    } else {
        storageAccessPermissionRequest()
    }
}

@Preview
@Composable
fun PhotoPickerPreview() {
    OfficeAppTheme {
        Surface {
            PhotoPicker(
                selectedPhoto = null,
                onSelectedPhoto = {},
                modifier = Modifier.size(150.dp)
            )
        }
    }
}