package com.cdlpermitprep.usa.data.local

import com.cdlpermitprep.usa.data.local.seed.AssetQuestionLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val database: CdlDatabase,
    private val assetQuestionLoader: AssetQuestionLoader,
) {
    fun seedIfEmpty(scope: CoroutineScope) {
        scope.launch {
            val existing = database.questionDao().count()
            if (existing == 0) {
                val questions = assetQuestionLoader.load()
                Timber.i("Seeding question bank with ${questions.size} starter offline questions")
                database.questionDao().insertAll(questions)
            }
        }
    }
}
