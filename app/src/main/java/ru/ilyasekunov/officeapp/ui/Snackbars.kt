package ru.ilyasekunov.officeapp.ui

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

suspend fun networkErrorSnackbar(
    snackbarHostState: SnackbarHostState,
    duration: SnackbarDuration,
    message: String,
    retryLabel: String,
    onRetryClick: () -> Unit
) = snackbarHostState.showSnackbar(
    message = message,
    actionLabel = retryLabel,
    duration = duration
).also {
    if (it == SnackbarResult.ActionPerformed) {
        onRetryClick()
    }
}

fun attachedImagesCountExceededSnackbar(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    duration: SnackbarDuration,
    message: String
) = coroutineScope.launch {
    snackbarHostState.showSnackbar(
        message = message,
        duration = duration
    )
}