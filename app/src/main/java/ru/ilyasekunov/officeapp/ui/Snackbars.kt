package ru.ilyasekunov.officeapp.ui

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun networkErrorSnackbar(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    duration: SnackbarDuration,
    message: String,
    retryLabel: String,
    onRetryClick: () -> Unit
) = coroutineScope.launch {
    snackbarHostState.showSnackbar(
        message = message,
        actionLabel = retryLabel,
        duration = duration
    ).also {
        if (it == SnackbarResult.ActionPerformed) {
            onRetryClick()
        }
    }
}

fun deletePostSnackbar(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    message: String,
    undoLabel: String,
    onSnackbarTimeOut: () -> Unit
) = coroutineScope.launch {
    snackbarHostState.showSnackbar(
        message = message,
        actionLabel = undoLabel,
        duration = SnackbarDuration.Short
    ).also {
        if (it != SnackbarResult.ActionPerformed) {
            onSnackbarTimeOut()
        }
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

fun ideaEditedSuccessfullySnackbar(
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

fun changesSavedSuccessfullySnackbar(
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

fun loginErrorSnackbar(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    message: String,
    duration: SnackbarDuration = SnackbarDuration.Short,
    withDismissAction: Boolean = true
) = coroutineScope.launch {
    snackbarHostState.showSnackbar(
        message = message,
        withDismissAction = withDismissAction,
        duration = duration
    )
}

fun suggestIdeaToMyOfficeResultSnackbar(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    message: String,
    duration: SnackbarDuration = SnackbarDuration.Short
) = coroutineScope.launch {
    snackbarHostState.showSnackbar(
        message = message,
        duration = duration
    )
}