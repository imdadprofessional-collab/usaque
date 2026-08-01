package com.cdlpermitprep.usa.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cdlpermitprep.usa.domain.model.Category
import com.cdlpermitprep.usa.presentation.components.CdlCard
import com.cdlpermitprep.usa.presentation.components.CdlIconButton
import com.cdlpermitprep.usa.presentation.components.CollectionCard
import com.cdlpermitprep.usa.presentation.theme.CdlColors

private val categoryColors = listOf(CdlColors.Green, CdlColors.Yellow, CdlColors.Purple, CdlColors.Pink, CdlColors.Blue, CdlColors.Orange)
private val categoryIcons = listOf(Icons.Filled.Bolt, Icons.Filled.MenuBook, Icons.Filled.LocalShipping, Icons.Filled.Warning, Icons.Filled.DirectionsBus, Icons.Filled.Flight)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onOpenCategory: (String) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenMockExam: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenPremium: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            CdlIconButton(icon = Icons.Filled.Settings, onClick = onOpenSettings)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CdlIconButton(icon = Icons.Filled.Person, onClick = onOpenProfile)
                CdlIconButton(icon = Icons.Filled.BookmarkBorder, onClick = onOpenBookmarks)
                CdlIconButton(icon = Icons.Filled.Search, onClick = onOpenSearch)
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Study now.", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Black)
        Text("Pass anytime.", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Black)

        Spacer(Modifier.height(20.dp))
        StreakAndReadinessRow(
            streakDays = state.gamification.streakDays,
            readiness = state.analytics?.readinessScore ?: 0,
            level = state.gamification.level,
            xp = state.gamification.xp,
        )

        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            QuickActionPill(icon = Icons.Filled.Quiz, label = "Mock Exam", color = CdlColors.Pink, onClick = onOpenMockExam, modifier = Modifier.weight(1f))
            QuickActionPill(icon = Icons.Filled.BarChart, label = "Analytics", color = CdlColors.Purple, onClick = onOpenAnalytics, modifier = Modifier.weight(1f))
        }
        if (!state.isPremium) {
            Spacer(Modifier.height(12.dp))
            PremiumBanner(onClick = onOpenPremium)
        }

        Spacer(Modifier.height(24.dp))
        Text("Practice Categories", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(state.categories, key = { it.name }) { category ->
                val index = state.categories.indexOf(category)
                val unlocked = state.isCategoryUnlocked(category)
                CollectionCard(
                    title = category.name,
                    subtitle = if (category.isPremium) "Premium" else "Free",
                    icon = categoryIcons[index % categoryIcons.size],
                    iconColor = categoryColors[index % categoryColors.size],
                    badge = if (!unlocked) "LOCKED" else null,
                    onClick = { if (!unlocked) onOpenPremium() else onOpenCategory(category.name) },
                )
            }
        }
    }
}

@Composable
private fun StreakAndReadinessRow(streakDays: Int, readiness: Int, level: Int, xp: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        CdlCard(modifier = Modifier.weight(1f), background = CdlColors.Orange) {
            Text("🔥 $streakDays", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Text("Day Streak", style = MaterialTheme.typography.labelMedium)
        }
        CdlCard(modifier = Modifier.weight(1f), background = CdlColors.Green) {
            Text("$readiness%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Text("Readiness", style = MaterialTheme.typography.labelMedium)
        }
        CdlCard(modifier = Modifier.weight(1f), background = CdlColors.Blue) {
            Text("Lv $level", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Text("$xp XP", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun QuickActionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(color)
            .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null)
        Text(label, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PremiumBanner(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CdlColors.Ink)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text("Go Premium", color = CdlColors.Cream, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
            Text("Unlock Hazmat, Tanker, Doubles & more", color = CdlColors.TextSecondaryDark, style = MaterialTheme.typography.bodyMedium)
        }
        Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = CdlColors.Yellow)
    }
}
