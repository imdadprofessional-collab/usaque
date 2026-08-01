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
    fun premiumStatus(): Flow<Boolean>
    suspend fun queryProducts(): List<BillingProduct>
    suspend fun launchPurchase(productId: String): Result<Unit>
    suspend fun restorePurchases(): Result<Unit>
    suspend fun purchaseHistory(): List<PurchaseHistoryItem>
}
