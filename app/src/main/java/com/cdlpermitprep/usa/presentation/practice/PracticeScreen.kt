package com.cdlpermitprep.usa.presentation.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cdlpermitprep.usa.domain.model.AnswerOption
import com.cdlpermitprep.usa.presentation.components.CdlIconButton
import com.cdlpermitprep.usa.presentation.components.CdlPrimaryButton
import com.cdlpermitprep.usa.presentation.components.CdlTopBar
import com.cdlpermitprep.usa.presentation.theme.CdlColors
import kotlinx.coroutines.delay

@Composable
fun PracticeScreen(
    viewModel: PracticeViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onFinished: (correctCount: Int, total: Int) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.finished) {
        if (state.finished) onFinished(state.correctCount, state.questions.size)
    }

    LaunchedEffect(state.secondsRemaining, state.isAnswerRevealed) {
        if (state.secondsRemaining != null && !state.isAnswerRevealed) {
            delay(1000)
            viewModel.onTimerTick()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        CdlTopBar(
            title = "Question ${state.currentIndex + 1}/${state.questions.size.coerceAtLeast(1)}",
            showBack = true,
            onBack = onBack,
        ) {
            CdlIconButton(
                icon = if (state.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                onClick = viewModel::toggleBookmark,
            )
        }

        LinearProgressIndicator(
            progress = { state.progress },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50)),
            color = CdlColors.Pink,
        )

        state.secondsRemaining?.let {
            Spacer(Modifier.height(8.dp))
            Text("⏱ ${it}s", fontWeight = FontWeight.Bold, color = if (it <= 10) CdlColors.Danger else MaterialTheme.colorScheme.onBackground)
        }

        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { CircularProgressIndicator() }
            return@Column
        }

        val question = state.currentQuestion ?: return@Column

        Spacer(Modifier.height(20.dp))
        Text(question.question, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))

        question.options.forEach { (option, text) ->
            AnswerRow(
                option = option,
                text = text,
                selected = state.selectedAnswer,
                correctAnswer = question.correctAnswer,
                revealed = state.isAnswerRevealed,
                onClick = { viewModel.selectAnswer(option) },
            )
            Spacer(Modifier.height(10.dp))
        }

        if (state.isAnswerRevealed) {
            Spacer(Modifier.height(8.dp))
            Text(
                state.liveExplanation ?: question.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = CdlColors.TextSecondaryLight,
            )
        }

        Spacer(Modifier.weight(1f))
        if (state.isAnswerRevealed) {
            CdlPrimaryButton(
                text = if (state.currentIndex + 1 == state.questions.size) "Finish" else "Next Question",
                onClick = viewModel::nextQuestion,
                modifier = Modifier.padding(bottom = 20.dp),
            )
        } else {
            Spacer(Modifier.height(76.dp))
        }
    }
}

@Composable
private fun AnswerRow(
    option: AnswerOption,
    text: String,
    selected: AnswerOption?,
    correctAnswer: AnswerOption,
    revealed: Boolean,
    onClick: () -> Unit,
) {
    val background = when {
        revealed && option == correctAnswer -> CdlColors.Success.copy(alpha = 0.25f)
        revealed && option == selected && option != correctAnswer -> CdlColors.Danger.copy(alpha = 0.25f)
        else -> MaterialTheme.colorScheme.surface
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(16.dp))
            .clickable(enabled = !revealed, onClick = onClick)
            .padding(16.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CdlColors.Yellow)
                .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(8.dp)),
            contentAlignment = androidx.compose.ui.Alignment.Center,
        ) {
            Text(option.name, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
    }
}
