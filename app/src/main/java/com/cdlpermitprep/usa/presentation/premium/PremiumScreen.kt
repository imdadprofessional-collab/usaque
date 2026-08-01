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
import com.cdlpermitprep.usa.domain.model.PremiumTier
import com.cdlpermitprep.usa.domain.repository.BillingProduct
import com.cdlpermitprep.usa.presentation.components.CdlCard
import com.cdlpermitprep.usa.presentation.components.CdlOutlineButton
import com.cdlpermitprep.usa.presentation.components.CdlPrimaryButton
import com.cdlpermitprep.usa.presentation.components.CdlTopBar
import com.cdlpermitprep.usa.presentation.theme.CdlColors

private val featureList = listOf(
    "All premium categories: Hazmat, Tanker, Doubles/Triples, Passenger & School Bus",
    "Unlimited mock exams with full review",
    "Advanced analytics & pass prediction",
    "Ad-free, offline-first experience",
)

private val packLabels = mapOf(
    BillingProducts.PREMIUM_PACKS[0] to ("Hazardous Materials Pack" to "Hazmat placarding, shipping papers & more"),
    BillingProducts.PREMIUM_PACKS[1] to ("Doubles/Triples Pack" to "Coupling order, crack-the-whip & more"),
    BillingProducts.PREMIUM_PACKS[2] to ("Tank Vehicles Pack" to "Surge, baffles & tanker-specific hazards"),
    BillingProducts.PREMIUM_PACKS[3] to ("Passenger & School Bus Pack" to "Loading, student safety & bus-specific rules"),
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
                        Text("Every category is unlocked.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                item {
                    Text("Subscriptions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                val allProducts = state.products.ifEmpty { placeholderProducts() }
                items(allProducts.filter { it.tier != PremiumTier.NONE }) { product ->
                    ProductCard(product = product, owned = false, onClick = { viewModel.purchase(product.productId) })
                }

                item {
                    Spacer(Modifier.height(4.dp))
                    Text("Or unlock just one topic", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("One-time purchase, no subscription required.", style = MaterialTheme.typography.bodyMedium, color = CdlColors.TextSecondaryLight)
                }
                val packProducts = allProducts.filter { it.tier == PremiumTier.NONE }
                items(packProducts) { product ->
                    val owned = product.productId in state.ownedPackIds
                    val label = packLabels[product.productId]
                    ProductCard(
                        product = product.copy(title = product.title.ifBlank { label?.first ?: product.productId }),
                        subtitle = label?.second,
                        owned = owned,
                        onClick = { viewModel.purchase(product.productId) },
                    )
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
private fun ProductCard(
    product: BillingProduct,
    owned: Boolean,
    onClick: () -> Unit,
    subtitle: String? = null,
) {
    CdlCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.title.ifBlank { product.productId }, fontWeight = FontWeight.Bold)
                Text(
                    subtitle ?: product.price.ifBlank { "—" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = CdlColors.TextSecondaryLight,
                )
            }
            if (owned) {
                Text("Owned", fontWeight = FontWeight.Bold, color = CdlColors.Success)
            } else {
                CdlPrimaryButton(text = "Buy", onClick = onClick, modifier = Modifier.width(100.dp))
            }
        }
    }
}

// Shown while Play Billing product details are loading (e.g. no network) so the screen isn't empty.
private fun placeholderProducts() = listOf(
    BillingProduct(BillingProducts.MONTHLY_SUB, "Monthly", "", PremiumTier.MONTHLY),
    BillingProduct(BillingProducts.YEARLY_SUB, "Yearly (Best Value)", "", PremiumTier.YEARLY),
    BillingProduct(BillingProducts.LIFETIME, "Lifetime", "", PremiumTier.LIFETIME),
) + BillingProducts.PREMIUM_PACKS.map { BillingProduct(it, "", "", PremiumTier.NONE) }
