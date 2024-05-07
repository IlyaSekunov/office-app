package ru.ilyasekunov.officeapp.ui

import androidx.paging.PagingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.ilyasekunov.officeapp.data.model.IdeaPost

class IdeasUiState(ideas: PagingData<IdeaPost> = PagingData.empty()) {
    private val _ideas = MutableStateFlow(ideas)
    val ideas get() = _ideas.asStateFlow()

    fun updateIdeas(ideas: PagingData<IdeaPost>) {
        _ideas.value = ideas
    }
}