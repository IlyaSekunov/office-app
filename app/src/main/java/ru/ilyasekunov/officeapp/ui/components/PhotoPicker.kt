package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.ui.LoadingScreen
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@Composable
fun PhotoPicker(
    selectedPhoto: Any?,
    onPhotoPickerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isImageLoading by remember { mutableStateOf(false) }
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
            if (selectedPhoto != null) {
                AsyncImage(
                    model = selectedPhoto,
                    contentDescription = "selected_photo",
                    contentScale = ContentScale.Crop,
                    onState = {
                        isImageLoading = when (it) {
                            is AsyncImagePainter.State.Error -> false
                            is AsyncImagePainter.State.Loading -> true
                            is AsyncImagePainter.State.Success -> false
                            is AsyncImagePainter.State.Empty -> false
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            if (isImageLoading) {
                LoadingScreen(
                    circularProgressingColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    circularProgressingSize = 30.dp,
                    circularProgressingWidth = 3.dp,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
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