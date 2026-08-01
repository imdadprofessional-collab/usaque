package com.cdlpermitprep.usa.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cdlpermitprep.usa.domain.model.Question
import com.cdlpermitprep.usa.domain.repository.QuestionFilter
import com.cdlpermitprep.usa.domain.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SearchFilterState(
    val keyword: String = "",
    val state: String? = null,
    val category: String? = null,
    val difficulty: String? = null,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
) : ViewModel() {

    private val _filter = MutableStateFlow(SearchFilterState())
    val filter: StateFlow<SearchFilterState> = _filter.asStateFlow()

    val results: kotlinx.coroutines.flow.Flow<PagingData<Question>> = _filter
        .flatMapLatest { f ->
            questionRepository.pagedQuestions(
                QuestionFilter(
                    state = f.state,
                    category = f.category,
                    difficulty = f.difficulty,
                    keyword = f.keyword.ifBlank { null },
                ),
            )
        }
        .cachedIn(viewModelScope)

    fun updateKeyword(keyword: String) {
        _filter.value = _filter.value.copy(keyword = keyword)
    }

    fun updateCategory(category: String?) {
        _filter.value = _filter.value.copy(category = category)
    }

    fun updateDifficulty(difficulty: String?) {
        _filter.value = _filter.value.copy(difficulty = difficulty)
    }
}
