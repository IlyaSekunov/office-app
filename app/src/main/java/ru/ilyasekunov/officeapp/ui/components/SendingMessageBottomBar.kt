package ru.ilyasekunov.officeapp.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.ui.LocalCoroutineScope
import ru.ilyasekunov.officeapp.ui.LocalSnackbarHostState
import ru.ilyasekunov.officeapp.ui.imagepickers.ImagePickerDefaults
import ru.ilyasekunov.officeapp.ui.imagepickers.rememberSingleImagePickerRequest
import ru.ilyasekunov.officeapp.ui.modifiers.BorderSide
import ru.ilyasekunov.officeapp.ui.modifiers.border
import ru.ilyasekunov.officeapp.ui.suggestidea.rememberOnAttachImageButtonClick
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

data class SendingMessageUiState(
    val message: String = "",
    val attachedImages: List<AttachedImage> = emptyList(),
    val isLoading: Boolean = false,
    val isErrorWhileSending: Boolean = false
)

@Composable
fun SendingMessageBottomBar(
    sendingMessageUiState: SendingMessageUiState,
    onMessageValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onImageRemoveClick: (attachedImage: AttachedImage) -> Unit,
    onImageAttach: (Uri) -> Unit,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    val singleImagePickerRequest = rememberSingleImagePickerRequest { uri ->
        uri?.let { onImageAttach(it) }
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
    ) {
        if (sendingMessageUiState.attachedImages.isNotEmpty()) {
            SendingMessageAttachedImagesSection(
                attachedImages = sendingMessageUiState.attachedImages,
                onImageRemoveClick = onImageRemoveClick,
                imagesArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier.fillMaxWidth()
            )
        }
        CommentSection(
            message = sendingMessageUiState.message,
            attachedImagesCount = sendingMessageUiState.attachedImages.size,
            onMessageValueChange = onMessageValueChange,
            onSendClick = onSendClick,
            onAttachImageClick = singleImagePickerRequest,
            containerColor = containerColor,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CommentSection(
    message: String,
    attachedImagesCount: Int,
    onMessageValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onAttachImageClick: () -> Unit,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    val onAttachImagesClick = rememberOnAttachImageButtonClick(
        attachedImagesCount = attachedImagesCount,
        maxAttachedImagesCount = ImagePickerDefaults.COMMENTS_MAX_ATTACH_IMAGES,
        onAttachImageClick = onAttachImageClick,
        coroutineScope = LocalCoroutineScope.current,
        snackbarHostState = LocalSnackbarHostState.current
    )
    TextField(
        value = message,
        onValueChange = onMessageValueChange,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 14.sp
        ),
        leadingIcon = {
            IconButton(onClick = onAttachImagesClick) {
                Icon(
                    painter = painterResource(R.drawable.outline_attach_file_24),
                    contentDescription = "attach_icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        trailingIcon = {
            IconButton(onClick = onSendClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "attach_image",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        placeholder = {
            Text(
                text = stringResource(R.string.message),
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = containerColor,
            disabledContainerColor = containerColor,
            errorContainerColor = containerColor,
            unfocusedContainerColor = containerColor
        ),
        shape = RectangleShape,
        modifier = modifier
            .border(
                borderSide = BorderSide.TOP,
                strokeWidth = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
    )
}

@Composable
private fun SendingMessageAttachedImagesSection(
    attachedImages: List<AttachedImage>,
    onImageRemoveClick: (attachedImage: AttachedImage) -> Unit,
    imagesArrangement: Arrangement.Horizontal,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = imagesArrangement,
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .border(
                borderSide = BorderSide.TOP,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                strokeWidth = 2.dp
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        attachedImages.forEach {
            AttachedImage(
                image = it,
                onRemoveClick = { onImageRemoveClick(it) },
                closeIconSize = 22.dp,
                attachedImageSize = DpSize(width = 120.dp, height = 105.dp)
            )
        }
    }
}

@Preview
@Composable
private fun SendingMessageBottomBarPreview() {
    OfficeAppTheme {
        Surface {
            SendingMessageBottomBar(
                sendingMessageUiState = SendingMessageUiState(),
                onMessageValueChange = {},
                onSendClick = {},
                onImageRemoveClick = {},
                containerColor = MaterialTheme.colorScheme.background,
                onImageAttach = {}
            )
        }
    }
}