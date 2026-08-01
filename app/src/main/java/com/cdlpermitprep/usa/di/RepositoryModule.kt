package com.cdlpermitprep.usa.di

import com.cdlpermitprep.usa.data.repository.BillingRepositoryImpl
import com.cdlpermitprep.usa.data.repository.BookmarkRepositoryImpl
import com.cdlpermitprep.usa.data.repository.ExamRepositoryImpl
import com.cdlpermitprep.usa.data.repository.QuestionRepositoryImpl
import com.cdlpermitprep.usa.data.repository.UserRepositoryImpl
import com.cdlpermitprep.usa.domain.repository.BillingRepository
import com.cdlpermitprep.usa.domain.repository.BookmarkRepository
import com.cdlpermitprep.usa.domain.repository.ExamRepository
import com.cdlpermitprep.usa.domain.repository.QuestionRepository
import com.cdlpermitprep.usa.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindQuestionRepository(impl: QuestionRepositoryImpl): QuestionRepository

    @Binds
    abstract fun bindBookmarkRepository(impl: BookmarkRepositoryImpl): BookmarkRepository

    @Binds
    abstract fun bindExamRepository(impl: ExamRepositoryImpl): ExamRepository

    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindBillingRepository(impl: BillingRepositoryImpl): BillingRepository
}
