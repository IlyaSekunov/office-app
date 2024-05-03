package ru.ilyasekunov.officeapp.ui

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult

suspend fun SnackbarHostState.snackbarWithAction(
    duration: SnackbarDuration,
    message: String,
    actionLabel: String,
    onActionClick: (() -> Unit)? = null,
    onTimeout: (() -> Unit)? = null
) = showSnackbar(
    message = message,
    actionLabel = actionLabel,
    duration = duration
).also {
    if (it == SnackbarResult.ActionPerformed) {
        onActionClick?.invoke()
    } else {
        onTimeout?.invoke()
    }
}