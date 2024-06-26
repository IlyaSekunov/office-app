package ru.ilyasekunov.ui.components

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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.core.ui.R
import ru.ilyasekunov.storage.rememberStorageAccessPermissionRequest
import ru.ilyasekunov.ui.LocalCoroutineScope
import ru.ilyasekunov.ui.LocalSnackbarHostState
import ru.ilyasekunov.ui.imagepickers.ImagePickerDefaults
import ru.ilyasekunov.ui.imagepickers.rememberSingleImagePickerRequest
import ru.ilyasekunov.ui.modifiers.BorderSide
import ru.ilyasekunov.ui.modifiers.border
import ru.ilyasekunov.ui.theme.OfficeAppTheme

@Immutable
data class SendMessageUiState(
    val message: String = "",
    val attachedImages: List<AttachedImage> = emptyList(),
    val isLoading: Boolean = false,
    val isPublished: Boolean = false,
    val isError: Boolean = false
)

@Composable
fun SendMessageBottomBar(
    sendMessageUiState: SendMessageUiState,
    onMessageValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onImageRemoveClick: (attachedImage: AttachedImage) -> Unit,
    onImageAttach: (Uri) -> Unit,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
    ) {
        if (sendMessageUiState.attachedImages.isNotEmpty()) {
            SendingMessageAttachedImagesSection(
                attachedImages = sendMessageUiState.attachedImages,
                onImageRemoveClick = onImageRemoveClick,
                imagesArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
        CommentSection(
            message = sendMessageUiState.message,
            attachedImagesCount = sendMessageUiState.attachedImages.size,
            onMessageValueChange = onMessageValueChange,
            onSendClick = onSendClick,
            onAttachImageClick = onImageAttach,
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
    onAttachImageClick: (Uri) -> Unit,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    val onAttachImagesClick = rememberOnAttachImageToCommentClick(
        attachedImagesCount = attachedImagesCount,
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
                    painter = painterResource(R.drawable.core_ui_outline_attach_file_24),
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
                text = stringResource(R.string.core_ui_message),
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
private fun rememberOnAttachImageToCommentClick(
    attachedImagesCount: Int,
    onAttachImageClick: (Uri) -> Unit,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState
): () -> Unit {
    val attachedImagesCountExceededMessage = stringResource(R.string.core_ui_attached_images_count_exceeded)
    val multipleImagePickerRequest = rememberSingleImagePickerRequest { uri ->
        uri?.let { onAttachImageClick(it) }
    }
    val storageAccessPermissionRequest = rememberStorageAccessPermissionRequest { granted ->
        if (granted) {
            multipleImagePickerRequest()
        }
    }
    val context = LocalContext.current

    return remember(
        attachedImagesCount,
        multipleImagePickerRequest,
        coroutineScope,
        snackbarHostState
    ) {
        if (attachedImagesCount >= ImagePickerDefaults.COMMENTS_MAX_ATTACH_IMAGES) {
            {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = attachedImagesCountExceededMessage,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        } else {
            {
                onImagePickerClick(
                    context = context,
                    openImagePickerRequest = multipleImagePickerRequest,
                    storageAccessPermissionRequest = storageAccessPermissionRequest
                )
            }
        }
    }
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
private fun SendMessageBottomBarPreview() {
    OfficeAppTheme {
        Surface {
            SendMessageBottomBar(
                sendMessageUiState = SendMessageUiState(),
                onMessageValueChange = {},
                onSendClick = {},
                onImageRemoveClick = {},
                containerColor = MaterialTheme.colorScheme.background,
                onImageAttach = {}
            )
        }
    }
}