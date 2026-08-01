package com.cdlpermitprep.usa.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.cdlpermitprep.usa.domain.model.Question
import com.cdlpermitprep.usa.domain.model.isAccessibleWith
import com.cdlpermitprep.usa.domain.repository.BillingRepository
import com.cdlpermitprep.usa.domain.repository.QuestionFilter
import com.cdlpermitprep.usa.domain.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

data class SearchFilterState(
    val keyword: String = "",
    val state: String? = null,
    val category: String? = null,
    val difficulty: String? = null,
)

/** Same entitlement rule as [com.cdlpermitprep.usa.domain.usecase.GetRandomQuestionsUseCase]: search must never surface premium-pack content a free user hasn't unlocked. */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val billingRepository: BillingRepository,
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
        .combine(
            combine(billingRepository.premiumStatus(), billingRepository.ownedPackIds()) { isPremium, owned -> isPremium to owned },
        ) { pagingData, (isPremium, ownedPacks) ->
            pagingData.filter { question -> question.isAccessibleWith(isPremium, ownedPacks) }
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
