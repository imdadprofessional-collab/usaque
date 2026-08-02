package com.cdlpermitprep.usa.presentation.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cdlpermitprep.usa.domain.model.PracticeMode
import com.cdlpermitprep.usa.presentation.components.CdlCard
import com.cdlpermitprep.usa.presentation.components.CdlTopBar
import com.cdlpermitprep.usa.presentation.theme.CdlColors

private data class PracticeOption(val title: String, val subtitle: String, val mode: PracticeMode)

private val options = listOf(
    PracticeOption("Topic Wise", "Focused practice on this topic", PracticeMode.TOPIC_WISE),
    PracticeOption("Timed Practice", "Race the clock, question by question", PracticeMode.TIMED),
    PracticeOption("Unlimited Practice", "No limits, study at your pace", PracticeMode.UNLIMITED),
    PracticeOption("Daily Challenge", "Today's 10-question challenge", PracticeMode.DAILY_CHALLENGE),
)

@Composable
fun CategoryScreen(
    categoryName: String,
    onBack: () -> Unit,
    onStartPractice: (PracticeMode, String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        CdlTopBar(title = categoryName, showBack = true, onBack = onBack)
        Spacer(Modifier.height(12.dp))
        Text("Choose how you want to practice", style = MaterialTheme.typography.bodyLarge, color = CdlColors.TextSecondaryLight)
        Spacer(Modifier.height(20.dp))
        options.forEach { option ->
            CdlCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .clickable { onStartPractice(option.mode, categoryName) },
            ) {
                Text(option.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(option.subtitle, style = MaterialTheme.typography.bodyMedium, color = CdlColors.TextSecondaryLight)
            }
        }
    }
}
