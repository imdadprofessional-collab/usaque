package com.cdlpermitprep.usa.presentation.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cdlpermitprep.usa.domain.model.CategoryAccuracyStat
import com.cdlpermitprep.usa.domain.model.PassPrediction
import com.cdlpermitprep.usa.presentation.components.CdlCard
import com.cdlpermitprep.usa.presentation.components.CdlTopBar
import com.cdlpermitprep.usa.presentation.theme.CdlColors

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val summary by viewModel.summary.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        CdlTopBar(title = "Analytics", showBack = true, onBack = onBack)
        val data = summary ?: return@Column

        LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CdlCard(modifier = Modifier.weight(1f), background = CdlColors.Purple) {
                        Text("${data.readinessScore}%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                        Text("Readiness Score", style = MaterialTheme.typography.labelMedium)
                    }
                    CdlCard(modifier = Modifier.weight(1f), background = CdlColors.Blue) {
                        Text("${data.accuracyPercent}%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                        Text("Overall Accuracy", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CdlCard(modifier = Modifier.weight(1f)) {
                        Text("${data.totalAnswered}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                        Text("Questions Solved", style = MaterialTheme.typography.labelMedium)
                    }
                    CdlCard(modifier = Modifier.weight(1f)) {
                        Text("${data.averageTimeSeconds}s", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                        Text("Avg. Time / Q", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            item {
                CdlCard(modifier = Modifier.fillMaxWidth(), background = predictionColor(data.passPrediction)) {
                    Text("Pass Prediction", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(predictionLabel(data.passPrediction), style = MaterialTheme.typography.bodyLarge)
                }
            }
            item {
                Text("Accuracy by Category", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            items(data.accuracyByCategory) { stat -> CategoryAccuracyRow(stat) }
        }
    }
}

@Composable
private fun CategoryAccuracyRow(stat: CategoryAccuracyStat) {
    CdlCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stat.category, fontWeight = FontWeight.Bold)
            Text("${stat.accuracyPercent}%", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.background),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(stat.accuracyPercent / 100f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (stat.accuracyPercent >= 80) CdlColors.Green else CdlColors.Orange),
            )
        }
    }
}

private fun predictionColor(prediction: PassPrediction) = when (prediction) {
    PassPrediction.LIKELY_PASS -> CdlColors.Green
    PassPrediction.NEEDS_MORE_PRACTICE -> CdlColors.Yellow
    PassPrediction.AT_RISK -> CdlColors.Danger
    PassPrediction.NOT_ENOUGH_DATA -> CdlColors.Blue
}

private fun predictionLabel(prediction: PassPrediction) = when (prediction) {
    PassPrediction.LIKELY_PASS -> "You're on track to pass. Keep it up!"
    PassPrediction.NEEDS_MORE_PRACTICE -> "Getting there — a bit more practice will help."
    PassPrediction.AT_RISK -> "Focus on your weak categories before test day."
    PassPrediction.NOT_ENOUGH_DATA -> "Answer more questions to unlock your prediction."
}
