package com.cdlpermitprep.usa.presentation.premium

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
import com.cdlpermitprep.usa.data.billing.BillingProducts
import com.cdlpermitprep.usa.domain.repository.BillingProduct
import com.cdlpermitprep.usa.presentation.components.CdlCard
import com.cdlpermitprep.usa.presentation.components.CdlOutlineButton
import com.cdlpermitprep.usa.presentation.components.CdlPrimaryButton
import com.cdlpermitprep.usa.presentation.components.CdlTopBar
import com.cdlpermitprep.usa.presentation.theme.CdlColors

private val featureList = listOf(
    "All premium categories: Hazmat, Tanker, Doubles/Triples, Passenger",
    "Unlimited mock exams with full review",
    "Advanced analytics & pass prediction",
    "Ad-free, offline-first experience",
)

@Composable
fun PremiumScreen(
    viewModel: PremiumViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        CdlTopBar(title = "Go Premium", showBack = true, onBack = onBack)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                CdlCard(modifier = Modifier.fillMaxWidth(), background = CdlColors.Ink) {
                    Text("Unlock everything", color = CdlColors.Cream, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(10.dp))
                    featureList.forEach {
                        Text("• $it", color = CdlColors.TextSecondaryDark, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            if (state.isPremium) {
                item {
                    CdlCard(modifier = Modifier.fillMaxWidth(), background = CdlColors.Green) {
                        Text("You're Premium 🎉", fontWeight = FontWeight.Black)
                    }
                }
            } else {
                items(state.products.ifEmpty { placeholderProducts() }) { product ->
                    ProductCard(product = product, onClick = { viewModel.purchase(product.productId) })
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                CdlOutlineButton(text = "Restore Purchases", onClick = viewModel::restorePurchases)
                state.message?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = CdlColors.TextSecondaryLight)
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ProductCard(product: BillingProduct, onClick: () -> Unit) {
    CdlCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Column {
                Text(product.title.ifBlank { product.productId }, fontWeight = FontWeight.Bold)
                Text(product.price.ifBlank { "—" }, style = MaterialTheme.typography.bodyMedium, color = CdlColors.TextSecondaryLight)
            }
            CdlPrimaryButton(text = "Buy", onClick = onClick, modifier = Modifier.width(100.dp))
        }
    }
}

// Shown while Play Billing product details are loading (e.g. no network) so the screen isn't empty.
private fun placeholderProducts() = listOf(
    BillingProduct(BillingProducts.MONTHLY_SUB, "Monthly", "", com.cdlpermitprep.usa.domain.model.PremiumTier.MONTHLY),
    BillingProduct(BillingProducts.YEARLY_SUB, "Yearly (Best Value)", "", com.cdlpermitprep.usa.domain.model.PremiumTier.YEARLY),
    BillingProduct(BillingProducts.LIFETIME, "Lifetime", "", com.cdlpermitprep.usa.domain.model.PremiumTier.LIFETIME),
)
