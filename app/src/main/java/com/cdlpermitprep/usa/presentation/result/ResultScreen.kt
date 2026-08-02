package com.cdlpermitprep.usa.presentation.result

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cdlpermitprep.usa.presentation.components.CdlCard
import com.cdlpermitprep.usa.presentation.components.CdlPrimaryButton
import com.cdlpermitprep.usa.presentation.theme.CdlColors

@Composable
fun ResultScreen(
    viewModel: ResultViewModel = hiltViewModel(),
    onDone: () -> Unit,
) {
    val attempt by viewModel.attempt.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        val current = attempt
        if (current == null) {
            CircularProgressIndicator()
            return@Box
        }
        // Scrolling the inner column rather than the Box keeps the result card centred when it
        // fits, while still letting it scroll when it does not (small screen, large font).
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(if (current.passed) "🎉" else "📘", style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(12.dp))
            Text(
                if (current.passed) "You passed!" else "Keep practicing",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
            )
            Spacer(Modifier.height(24.dp))
            CdlCard(modifier = Modifier.fillMaxWidth(), background = if (current.passed) CdlColors.Green else CdlColors.Yellow) {
                Text("${current.scorePercent}%", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Black)
                Text("${current.correctCount} / ${current.totalQuestions} correct", style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Passing score is generally 80% on the CDL general knowledge exam.",
                style = MaterialTheme.typography.bodyMedium,
                color = CdlColors.TextSecondaryLight,
            )
            Spacer(Modifier.height(32.dp))
            CdlPrimaryButton(text = "Done", onClick = onDone)
        }
    }
}
