package com.cdlpermitprep.usa.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cdlpermitprep.usa.presentation.components.CdlCard
import com.cdlpermitprep.usa.presentation.components.CdlOutlineButton
import com.cdlpermitprep.usa.presentation.components.CdlTopBar
import com.cdlpermitprep.usa.presentation.theme.CdlColors

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onOpenPremium: () -> Unit,
) {
    val profile by viewModel.profile.collectAsState()
    val history by viewModel.examHistory.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        CdlTopBar(title = "Profile", showBack = true, onBack = onBack)
        val user = profile ?: return@Column

        CdlCard(modifier = Modifier.fillMaxWidth(), background = CdlColors.Purple) {
            Text(user.displayName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Text(if (user.isPremium) "Premium Member" else "Free Member", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text("Level ${user.level} • ${user.xp} XP • ${user.streakDays} day streak", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(14.dp))
        if (!user.isPremium) {
            CdlOutlineButton(text = "Upgrade to Premium", onClick = onOpenPremium)
            Spacer(Modifier.height(14.dp))
        }

        Text("Exam History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(history) { attempt ->
                CdlCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(attempt.state, fontWeight = FontWeight.Bold)
                        Text("${attempt.scorePercent}%", fontWeight = FontWeight.Bold, color = if (attempt.passed) CdlColors.Success else CdlColors.Danger)
                    }
                    Text("${attempt.correctCount}/${attempt.totalQuestions} correct", style = MaterialTheme.typography.bodyMedium, color = CdlColors.TextSecondaryLight)
                }
            }
        }
    }
}
