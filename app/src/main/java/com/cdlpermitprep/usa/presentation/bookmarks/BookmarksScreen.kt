package com.cdlpermitprep.usa.presentation.bookmarks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.cdlpermitprep.usa.presentation.components.CdlCard
import com.cdlpermitprep.usa.presentation.components.CdlTopBar
import com.cdlpermitprep.usa.presentation.theme.CdlColors

@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val questions = viewModel.bookmarkedQuestions.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        CdlTopBar(title = "Bookmarks", showBack = true, onBack = onBack)
        if (questions.itemCount == 0) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No bookmarks yet. Tap the bookmark icon while practicing to save a question here.", color = CdlColors.TextSecondaryLight)
            }
            return@Column
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(questions.itemCount) { index ->
                val question = questions[index] ?: return@items
                CdlCard(modifier = Modifier.fillMaxWidth()) {
                    Text(question.category, style = MaterialTheme.typography.labelMedium, color = CdlColors.TextSecondaryLight)
                    Spacer(Modifier.height(6.dp))
                    Text(question.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
