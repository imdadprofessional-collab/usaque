package com.cdlpermitprep.usa.di

import com.cdlpermitprep.usa.data.ai.LocalAnswerExplanationProvider
import com.cdlpermitprep.usa.data.ai.LocalPersonalTutor
import com.cdlpermitprep.usa.data.ai.LocalQuestionRecommendationEngine
import com.cdlpermitprep.usa.data.ai.LocalStudyPlanner
import com.cdlpermitprep.usa.domain.ai.AnswerExplanationProvider
import com.cdlpermitprep.usa.domain.ai.PersonalTutor
import com.cdlpermitprep.usa.domain.ai.QuestionRecommendationEngine
import com.cdlpermitprep.usa.domain.ai.StudyPlanner
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Swap these bindings for LLM-backed implementations later without touching any UI code. */
@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    abstract fun bindAnswerExplanationProvider(impl: LocalAnswerExplanationProvider): AnswerExplanationProvider

    @Binds
    abstract fun bindStudyPlanner(impl: LocalStudyPlanner): StudyPlanner

    @Binds
    abstract fun bindPersonalTutor(impl: LocalPersonalTutor): PersonalTutor

    @Binds
    abstract fun bindQuestionRecommendationEngine(impl: LocalQuestionRecommendationEngine): QuestionRecommendationEngine
}
