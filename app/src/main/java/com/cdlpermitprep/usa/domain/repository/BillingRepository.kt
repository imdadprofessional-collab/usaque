package com.cdlpermitprep.usa.domain.repository

import com.cdlpermitprep.usa.domain.model.PremiumTier
import com.cdlpermitprep.usa.domain.model.PurchaseHistoryItem
import kotlinx.coroutines.flow.Flow

data class BillingProduct(
    val productId: String,
    val title: String,
    val price: String,
    val tier: PremiumTier,
)

interface BillingRepository {
    /** True for any active subscription or lifetime purchase — unlocks every category and pack. */
    fun premiumStatus(): Flow<Boolean>
    suspend fun queryProducts(): List<BillingProduct>
    suspend fun launchPurchase(productId: String): Result<Unit>
    suspend fun restorePurchases(): Result<Unit>
    suspend fun purchaseHistory(): List<PurchaseHistoryItem>

    /** Product IDs (from [com.cdlpermitprep.usa.data.billing.BillingProducts.PREMIUM_PACKS]) the user owns individually. */
    fun ownedPackIds(): Flow<Set<String>>
}
