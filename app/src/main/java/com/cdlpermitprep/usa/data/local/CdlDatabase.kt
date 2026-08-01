package com.cdlpermitprep.usa.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cdlpermitprep.usa.data.local.dao.AnswerRecordDao
import com.cdlpermitprep.usa.data.local.dao.BookmarkDao
import com.cdlpermitprep.usa.data.local.dao.ExamAttemptDao
import com.cdlpermitprep.usa.data.local.dao.QuestionDao
import com.cdlpermitprep.usa.data.local.entity.AnswerRecordEntity
import com.cdlpermitprep.usa.data.local.entity.BookmarkEntity
import com.cdlpermitprep.usa.data.local.entity.ExamAttemptEntity
import com.cdlpermitprep.usa.data.local.entity.QuestionEntity

@Database(
    entities = [
        QuestionEntity::class,
        BookmarkEntity::class,
        AnswerRecordEntity::class,
        ExamAttemptEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class CdlDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun answerRecordDao(): AnswerRecordDao
    abstract fun examAttemptDao(): ExamAttemptDao

    companion object {
        const val DATABASE_NAME = "cdl_permit_prep.db"
    }
}
