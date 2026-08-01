package com.cdlpermitprep.usa.data.local.seed

import android.content.Context
import com.cdlpermitprep.usa.data.local.entity.QuestionEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Loads the bundled question bank from `assets/questions_seed.json` — the same import path a
 * future CSV/JSON bulk-loading pipeline would use to scale into the tens of thousands of rows.
 * Falls back to [SeedQuestionProvider]'s small hardcoded set if the asset is missing or malformed,
 * so the app never ships with zero offline content.
 */
@Singleton
class AssetQuestionLoader @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun load(): List<QuestionEntity> = runCatching {
        val json = context.assets.open(ASSET_NAME).bufferedReader().use { it.readText() }
        parse(json)
    }.getOrElse {
        Timber.w(it, "Falling back to built-in seed questions; failed to load $ASSET_NAME")
        SeedQuestionProvider.all()
    }

    private fun parse(json: String): List<QuestionEntity> {
        val array = JSONArray(json)
        return buildList {
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                add(
                    QuestionEntity(
                        state = obj.optString("state", "General"),
                        category = obj.getString("category"),
                        subCategory = obj.getString("subCategory"),
                        difficulty = obj.optString("difficulty", "Medium"),
                        question = obj.getString("question"),
                        optionA = obj.getString("optionA"),
                        optionB = obj.getString("optionB"),
                        optionC = obj.getString("optionC"),
                        optionD = obj.getString("optionD"),
                        correctAnswer = obj.getString("correctAnswer"),
                        explanation = obj.getString("explanation"),
                        reference = obj.optString("reference").takeIf { it.isNotBlank() },
                        tags = obj.optString("tags", ""),
                        isPremium = obj.optBoolean("isPremium", false),
                        packId = obj.optString("packId").takeIf { it.isNotBlank() },
                    ),
                )
            }
        }
    }

    companion object {
        private const val ASSET_NAME = "questions_seed.json"
    }
}
