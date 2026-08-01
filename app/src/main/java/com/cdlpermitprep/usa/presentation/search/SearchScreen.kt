package com.cdlpermitprep.usa.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.cdlpermitprep.usa.presentation.components.CdlCard
import com.cdlpermitprep.usa.presentation.components.CdlTopBar
import com.cdlpermitprep.usa.presentation.theme.CdlColors

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val filter by viewModel.filter.collectAsState()
    val results = viewModel.results.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        CdlTopBar(title = "Search", showBack = true, onBack = onBack)

        OutlinedTextField(
            value = filter.keyword,
            onValueChange = viewModel::updateKeyword,
            placeholder = { Text("Search questions, tags...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(results.itemCount) { index ->
                val question = results[index] ?: return@items
                CdlCard(modifier = Modifier.fillMaxWidth()) {
                    Text("${question.category} • ${question.difficulty}", style = MaterialTheme.typography.labelMedium, color = CdlColors.TextSecondaryLight)
                    Spacer(Modifier.height(6.dp))
                    Text(question.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
