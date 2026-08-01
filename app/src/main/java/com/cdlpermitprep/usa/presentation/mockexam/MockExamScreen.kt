package com.cdlpermitprep.usa.presentation.mockexam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cdlpermitprep.usa.presentation.components.CdlIconButton
import com.cdlpermitprep.usa.presentation.components.CdlOutlineButton
import com.cdlpermitprep.usa.presentation.components.CdlPrimaryButton
import com.cdlpermitprep.usa.presentation.theme.CdlColors
import kotlinx.coroutines.delay

@Composable
fun MockExamScreen(
    viewModel: MockExamViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onExamSubmitted: (attemptId: Long) -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.submittedAttemptId) {
        state.submittedAttemptId?.let(onExamSubmitted)
    }

    LaunchedEffect(state.isPaused, state.submittedAttemptId) {
        while (!state.isPaused && state.submittedAttemptId == null) {
            delay(1000)
            viewModel.onTimerTick()
        }
    }

    if (state.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            val minutes = state.secondsRemaining / 60
            val seconds = state.secondsRemaining % 60
            Text(
                "%02d:%02d".format(minutes, seconds),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = if (state.secondsRemaining < 120) CdlColors.Danger else MaterialTheme.colorScheme.onBackground,
            )
            CdlIconButton(
                icon = if (state.isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                onClick = viewModel::togglePause,
            )
        }

        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.questions.size) { index ->
                val question = state.questions[index]
                val answered = state.answers.containsKey(question.id)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when {
                                index == state.currentIndex -> CdlColors.Ink
                                answered -> CdlColors.Green
                                else -> MaterialTheme.colorScheme.surface
                            },
                        )
                        .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(10.dp))
                        .clickable { viewModel.goToQuestion(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "${index + 1}",
                        color = if (index == state.currentIndex) CdlColors.Cream else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        if (state.isPaused) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Exam Paused", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            }
            return@Column
        }

        val question = state.currentQuestion ?: return@Column
        Spacer(Modifier.height(20.dp))
        Text(question.question, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))

        question.options.forEach { (option, text) ->
            val selected = state.answers[question.id] == option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (selected) CdlColors.Yellow else MaterialTheme.colorScheme.surface)
                    .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(16.dp))
                    .clickable { viewModel.selectAnswer(option) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(option.name, fontWeight = FontWeight.Black, modifier = Modifier.padding(end = 12.dp))
                Text(text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            }
        }

        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 20.dp)) {
            CdlOutlineButton(text = "Previous", onClick = viewModel::previousQuestion, modifier = Modifier.weight(1f))
            if (state.currentIndex + 1 == state.questions.size) {
                CdlPrimaryButton(text = "Submit Exam", onClick = viewModel::submitExam, modifier = Modifier.weight(1f))
            } else {
                CdlPrimaryButton(text = "Next", onClick = viewModel::nextQuestion, modifier = Modifier.weight(1f))
            }
        }
    }
}
