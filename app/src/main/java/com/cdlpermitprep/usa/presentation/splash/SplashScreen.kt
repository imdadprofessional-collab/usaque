package com.cdlpermitprep.usa.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cdlpermitprep.usa.presentation.theme.CdlColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateNext: (startDestination: String) -> Unit,
) {
    LaunchedEffect(Unit) {
        delay(900)
        onNavigateNext(viewModel.resolveStartDestination())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(CdlColors.Yellow),
            contentAlignment = Alignment.Center,
        ) {
            Text("CDL", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        }
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("CDL Permit Prep USA", style = MaterialTheme.typography.titleMedium)
            Text("Study now. Pass anytime.", style = MaterialTheme.typography.bodyMedium, color = CdlColors.TextSecondaryLight)
        }
    }
}
