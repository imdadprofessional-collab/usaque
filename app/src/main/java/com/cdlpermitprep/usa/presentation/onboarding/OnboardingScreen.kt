package com.cdlpermitprep.usa.presentation.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cdlpermitprep.usa.presentation.components.CdlChip
import com.cdlpermitprep.usa.presentation.components.CdlPrimaryButton
import com.cdlpermitprep.usa.presentation.theme.CdlColors
import kotlinx.coroutines.launch

private data class OnboardingPage(val emoji: String, val title: String, val body: String, val color: androidx.compose.ui.graphics.Color)

private val pages = listOf(
    OnboardingPage("🚛", "Every state. Every topic.", "General knowledge, air brakes, combination vehicles, hazmat, and more — all offline.", CdlColors.Yellow),
    OnboardingPage("🎯", "Practice your way", "Topic-wise drills, timed practice, weak-topic review, and full mock exams.", CdlColors.Pink),
    OnboardingPage("📈", "Track your readiness", "See accuracy, streaks, and a pass prediction so you know exactly when you're ready.", CdlColors.Purple),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onFinished: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 16.dp)) {
            pages.forEachIndexed { index, _ ->
                CdlChip(text = "${index + 1}", color = if (index == pagerState.currentPage) CdlColors.Yellow else MaterialTheme.colorScheme.surface)
            }
        }
        Spacer(Modifier.height(24.dp))
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            val p = pages[page]
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(p.emoji, style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.height(24.dp))
                Text(p.title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Text(p.body, style = MaterialTheme.typography.bodyLarge, textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = CdlColors.TextSecondaryLight)
            }
        }
        CdlPrimaryButton(
            text = if (pagerState.currentPage == pages.lastIndex) "Get Started" else "Next",
            onClick = {
                if (pagerState.currentPage == pages.lastIndex) {
                    viewModel.completeOnboarding()
                    onFinished()
                } else {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            },
        )
    }
}
