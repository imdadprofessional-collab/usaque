package com.cdlpermitprep.usa.data.repository

import com.cdlpermitprep.usa.data.billing.BillingManager
import com.cdlpermitprep.usa.data.billing.BillingProducts
import com.cdlpermitprep.usa.data.billing.CurrentActivityHolder
import com.cdlpermitprep.usa.data.preferences.UserPreferences
import com.cdlpermitprep.usa.domain.model.PremiumTier
import com.cdlpermitprep.usa.domain.model.PurchaseHistoryItem
import com.cdlpermitprep.usa.domain.repository.BillingProduct
import com.cdlpermitprep.usa.domain.repository.BillingRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepositoryImpl @Inject constructor(
    private val billingManager: BillingManager,
    private val activityHolder: CurrentActivityHolder,
    private val userPreferences: UserPreferences,
) : BillingRepository {

    override fun premiumStatus(): Flow<Boolean> = userPreferences.isPremium

    override suspend fun queryProducts(): List<BillingProduct> {
        val subs = billingManager.queryProductDetails(BillingProducts.SUBSCRIPTIONS, isSubscription = true)
        val oneTime = billingManager.queryProductDetails(BillingProducts.ONE_TIME, isSubscription = false)
        return (subs + oneTime).map {
            BillingProduct(
                productId = it.productId,
                title = it.title,
                price = it.oneTimePurchaseOfferDetails?.formattedPrice
                    ?: it.subscriptionOfferDetails?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice
                    ?: "",
                tier = when (it.productId) {
                    BillingProducts.MONTHLY_SUB -> PremiumTier.MONTHLY
                    BillingProducts.YEARLY_SUB -> PremiumTier.YEARLY
                    BillingProducts.LIFETIME -> PremiumTier.LIFETIME
                    else -> PremiumTier.NONE
                },
            )
        }
    }

    override suspend fun launchPurchase(productId: String): Result<Unit> = runCatching {
        val activity = activityHolder.get() ?: error("No active Activity to launch purchase flow from")
        val isSub = productId in BillingProducts.SUBSCRIPTIONS
        val details = billingManager.queryProductDetails(listOf(productId), isSubscription = isSub).firstOrNull()
            ?: error("Product not found: $productId")
        val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken
        billingManager.launchPurchaseFlow(activity, details, offerToken)
    }

    override suspend fun restorePurchases(): Result<Unit> = runCatching {
        billingManager.refreshEntitlements()
    }.onFailure { Timber.w(it, "Restore purchases failed") }

    override suspend fun purchaseHistory(): List<PurchaseHistoryItem> = emptyList() // populated from Firestore purchase history in a full build
}
