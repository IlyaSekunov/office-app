package ru.ilyasekunov.officeapp.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.ui.imagepickers.rememberSingleImagePicker
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@Composable
fun PhotoPicker(
    selectedPhoto: Any?,
    onPhotoPickerClick: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(selectedPhoto)
            .size(coil.size.Size.ORIGINAL)
            .build()
    )
    val singleImagePicker = rememberSingleImagePicker { onPhotoPickerClick(it) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.photo_picker_title),
            style = MaterialTheme.typography.titleSmall,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(17.dp))
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
                .clickable(onClick = singleImagePicker::launch),
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
                        painter = painterResource(R.drawable.outline_photo_camera_24),
                        contentDescription = "photo_icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
                else -> {
                    Icon(
                        painter = painterResource(R.drawable.outline_photo_camera_24),
                        contentDescription = "photo_icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

/*@Composable
fun PhotoPicker(
    selectedPhoto: Any?,
    onPhotoPickerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(selectedPhoto)
            .size(coil.size.Size.ORIGINAL)
            .build()
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.photo_picker_title),
            style = MaterialTheme.typography.titleSmall,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(17.dp))
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
                .clickable { onPhotoPickerClick() },
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
                        painter = painterResource(R.drawable.outline_photo_camera_24),
                        contentDescription = "photo_icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
                else -> {
                    Icon(
                        painter = painterResource(R.drawable.outline_photo_camera_24),
                        contentDescription = "photo_icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}*/

@Preview
@Composable
fun PhotoPickerPreview() {
    OfficeAppTheme {
        Surface {
            PhotoPicker(
                selectedPhoto = null,
                onPhotoPickerClick = {},
                modifier = Modifier.size(150.dp)
            )
        }
    }
}