package ru.ilyasekunov.officeapp.ui.components

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@Composable
fun PhotoPicker(
    selectedPhoto: String?,
    onPhotoPickerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.large
                )
                .clickable { onPhotoPickerClick() },
            contentAlignment = Alignment.Center
        ) {
            if (selectedPhoto != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedPhoto),
                    contentDescription = "selected_photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.outline_photo_camera_24),
                    contentDescription = "photo_icon",
                    tint = Color.White,
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