package ru.ilyasekunov.profile

import androidx.compose.runtime.Immutable
import ru.ilyasekunov.model.User

@Immutable
data class CurrentUserUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isErrorWhileLoading: Boolean = false,
    val isUnauthorized: Boolean = false
)