package com.cdlpermitprep.usa.presentation.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.cdlpermitprep.usa.domain.model.Question
import com.cdlpermitprep.usa.domain.model.isAccessibleWith
import com.cdlpermitprep.usa.domain.repository.BillingRepository
import com.cdlpermitprep.usa.domain.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Same entitlement rule as [com.cdlpermitprep.usa.domain.usecase.GetRandomQuestionsUseCase].
 * Bookmarks are the one place a user can keep a reference to premium content after losing
 * access to it: bookmark Hazmat questions while subscribed, let the subscription lapse, and
 * without this filter the full question, answer and explanation stay readable here.
 */
@HiltViewModel
class BookmarksViewModel @Inject constructor(
    questionRepository: QuestionRepository,
    billingRepository: BillingRepository,
) : ViewModel() {
    val bookmarkedQuestions: Flow<PagingData<Question>> =
        questionRepository.bookmarkedQuestions()
            .combine(
                combine(billingRepository.premiumStatus(), billingRepository.ownedPackIds()) { isPremium, owned -> isPremium to owned },
            ) { pagingData, (isPremium, ownedPacks) ->
                pagingData.filter { question -> question.isAccessibleWith(isPremium, ownedPacks) }
            }
            .cachedIn(viewModelScope)
}
