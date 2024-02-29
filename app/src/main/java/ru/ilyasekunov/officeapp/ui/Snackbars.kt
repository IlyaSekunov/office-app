package ru.ilyasekunov.officeapp.ui

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult

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