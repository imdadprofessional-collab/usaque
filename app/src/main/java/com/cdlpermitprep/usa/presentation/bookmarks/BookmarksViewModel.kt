package com.cdlpermitprep.usa.presentation.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cdlpermitprep.usa.domain.model.Question
import com.cdlpermitprep.usa.domain.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    questionRepository: QuestionRepository,
) : ViewModel() {
    val bookmarkedQuestions: Flow<PagingData<Question>> =
        questionRepository.bookmarkedQuestions().cachedIn(viewModelScope)
}
