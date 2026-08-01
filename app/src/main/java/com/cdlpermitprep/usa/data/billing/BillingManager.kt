package com.cdlpermitprep.usa.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.cdlpermitprep.usa.data.preferences.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

object BillingProducts {
    const val MONTHLY_SUB = "cdl_premium_monthly"
    const val YEARLY_SUB = "cdl_premium_yearly"
    const val LIFETIME = "cdl_premium_lifetime"
    val PREMIUM_PACKS = listOf("pack_hazmat", "pack_doubles_triples", "pack_tanker", "pack_passenger")

    val SUBSCRIPTIONS = listOf(MONTHLY_SUB, YEARLY_SUB)
    val ONE_TIME = listOf(LIFETIME) + PREMIUM_PACKS
}

/**
 * Thin wrapper around Play Billing Library. Verification of purchases should happen
 * server-side (Cloud Function validating the purchase token against the Play Developer API)
 * before granting entitlements for anything beyond a soft client-side unlock.
 */
@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext context: Context,
    private val userPreferences: UserPreferences,
) : PurchasesUpdatedListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _ownedPackIds = MutableStateFlow<Set<String>>(emptySet())
    val ownedPackIds: StateFlow<Set<String>> = _ownedPackIds.asStateFlow()

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    fun startConnection(onReady: () -> Unit = {}) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    onReady()
                    scope.launch { refreshEntitlements() }
                } else {
                    Timber.w("Billing setup failed: ${result.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Timber.w("Billing service disconnected")
            }
        })
    }

    suspend fun queryProductDetails(productIds: List<String>, isSubscription: Boolean): List<ProductDetails> {
        val products = productIds.map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(if (isSubscription) BillingClient.ProductType.SUBS else BillingClient.ProductType.INAPP)
                .build()
        }
        val params = QueryProductDetailsParams.newBuilder().setProductList(products).build()
        val result = billingClient.queryProductDetails(params)
        return result.productDetailsList.orEmpty()
    }

    fun launchPurchaseFlow(activity: Activity, productDetails: ProductDetails, offerToken: String? = null) {
        val productParamsBuilder = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
        offerToken?.let { productParamsBuilder.setOfferToken(it) }

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParamsBuilder.build()))
            .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

    suspend fun refreshEntitlements() {
        val subsResult = billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),
        )
        val inAppResult = billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
        )
        val allPurchases = subsResult.purchasesList + inAppResult.purchasesList
        val purchasedProductIds = allPurchases
            .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
            .flatMap { it.products }
            .toSet()

        // A subscription or the lifetime purchase unlocks everything; owning an individual
        // pack (e.g. pack_hazmat) does NOT grant blanket premium — only that pack's content.
        val fullPremiumProductIds = BillingProducts.SUBSCRIPTIONS + BillingProducts.LIFETIME
        val hasActivePremium = purchasedProductIds.any { it in fullPremiumProductIds }
        userPreferences.setPremium(hasActivePremium)
        _ownedPackIds.value = purchasedProductIds.filter { it in BillingProducts.PREMIUM_PACKS }.toSet()

        allPurchases.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED && !it.isAcknowledged }
            .forEach { acknowledgePurchase(it) }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClient.acknowledgePurchase(params) { result ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                Timber.w("Acknowledge failed: ${result.debugMessage}")
            }
        }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            scope.launch { refreshEntitlements() }
        }
    }
}
