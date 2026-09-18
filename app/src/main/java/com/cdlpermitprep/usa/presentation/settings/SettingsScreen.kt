package com.cdlpermitprep.usa.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cdlpermitprep.usa.presentation.components.CdlCard
import com.cdlpermitprep.usa.presentation.components.CdlTopBar
import com.cdlpermitprep.usa.presentation.theme.CdlColors

private val US_STATES = listOf("General", "California", "Texas", "Florida", "New York", "Pennsylvania", "Ohio", "Illinois", "Georgia", "North Carolina")

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    var stateMenuExpanded by remember { mutableStateOf(false) }

    // Settings is a tall stack of cards; it already exceeds a short screen and will exceed any
    // screen at a large font scale, so the page scrolls.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        CdlTopBar(title = "Settings", showBack = true, onBack = onBack)

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            CdlCard(modifier = Modifier.fillMaxWidth()) {
                Text("Dark Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("system" to "System", "on" to "On", "off" to "Off").forEach { (value, label) ->
                        FilterChip(
                            selected = state.darkMode == value,
                            onClick = { viewModel.setDarkMode(value) },
                            label = { Text(label) },
                        )
                    }
                }
            }

            CdlCard(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Daily Reminders", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Switch(checked = state.notificationsEnabled, onCheckedChange = viewModel::setNotificationsEnabled)
                }
            }

            CdlCard(modifier = Modifier.fillMaxWidth()) {
                Text("Daily Goal: ${state.dailyGoal} questions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Slider(
                    value = state.dailyGoal.toFloat(),
                    onValueChange = { viewModel.setDailyGoal(it.toInt()) },
                    valueRange = 5f..100f,
                    steps = 18,
                )
            }

            CdlCard(modifier = Modifier.fillMaxWidth()) {
                Text("Exam State", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Box {
                    androidx.compose.material3.OutlinedButton(onClick = { stateMenuExpanded = true }) {
                        Text(state.selectedState)
                    }
                    DropdownMenu(expanded = stateMenuExpanded, onDismissRequest = { stateMenuExpanded = false }) {
                        US_STATES.forEach { stateName ->
                            DropdownMenuItem(
                                text = { Text(stateName) },
                                onClick = { viewModel.setSelectedState(stateName); stateMenuExpanded = false },
                            )
                        }
                    }
                }
            }

            // Required by Play's Misleading Claims policy: any app referencing government
            // information (this one cites FMCSA/state DMV material) must show a clear,
            // easy-to-find non-affiliation disclaimer inside the app itself, not just in the
            // store listing.
            CdlCard(modifier = Modifier.fillMaxWidth()) {
                Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(
                    "CDL Permit Prep is an independent study aid. It is not affiliated with, " +
                        "endorsed by, or a substitute for the FMCSA, any state DMV, or any " +
                        "government agency, and it does not issue any official license or " +
                        "credential.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CdlColors.TextSecondaryLight,
                )
            }
        }
    }
}
