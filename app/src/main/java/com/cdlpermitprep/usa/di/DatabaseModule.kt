package com.cdlpermitprep.usa.di

import android.content.Context
import androidx.room.Room
import com.cdlpermitprep.usa.data.local.CdlDatabase
import com.cdlpermitprep.usa.data.local.dao.AnswerRecordDao
import com.cdlpermitprep.usa.data.local.dao.BookmarkDao
import com.cdlpermitprep.usa.data.local.dao.ExamAttemptDao
import com.cdlpermitprep.usa.data.local.dao.QuestionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CdlDatabase =
        Room.databaseBuilder(context, CdlDatabase::class.java, CdlDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideQuestionDao(db: CdlDatabase): QuestionDao = db.questionDao()

    @Provides
    fun provideBookmarkDao(db: CdlDatabase): BookmarkDao = db.bookmarkDao()

    @Provides
    fun provideAnswerRecordDao(db: CdlDatabase): AnswerRecordDao = db.answerRecordDao()

    @Provides
    fun provideExamAttemptDao(db: CdlDatabase): ExamAttemptDao = db.examAttemptDao()
}
