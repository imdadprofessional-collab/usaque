package com.cdlpermitprep.usa.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cdlpermitprep.usa.domain.model.UserProfile
import com.cdlpermitprep.usa.domain.repository.ExamRepository
import com.cdlpermitprep.usa.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    examRepository: ExamRepository,
) : ViewModel() {

    val profile: StateFlow<UserProfile?> = userRepository.currentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val examHistory = examRepository.examHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun signOut() {
        viewModelScope.launch { userRepository.signOut() }
    }
}
