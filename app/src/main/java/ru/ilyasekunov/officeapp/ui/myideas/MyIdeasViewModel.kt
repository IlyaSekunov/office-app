package ru.ilyasekunov.officeapp.ui.myideas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.data.repository.posts.PostsPagingRepository
import ru.ilyasekunov.officeapp.ui.IdeasUiState
import javax.inject.Inject

@HiltViewModel
class MyIdeasViewModel @Inject constructor(
    private val postsPagingRepository: PostsPagingRepository
) : ViewModel() {
    val myIdeasUiState = IdeasUiState()

    init {
        loadMyIdeas()
    }

    private fun loadMyIdeas() {
        viewModelScope.launch {
            postsPagingRepository.myIdeas()
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collectLatest { myIdeasUiState.updateIdeas(it) }
        }
    }
}