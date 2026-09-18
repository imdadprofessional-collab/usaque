package com.cdlpermitprep.usa.presentation.premium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cdlpermitprep.usa.domain.repository.BillingProduct
import com.cdlpermitprep.usa.domain.repository.BillingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PremiumUiState(
    val loading: Boolean = true,
    val products: List<BillingProduct> = emptyList(),
    val isPremium: Boolean = false,
    val ownedPackIds: Set<String> = emptySet(),
    val message: String? = null,
) {
    /** True once loading has finished but Play returned no real products to purchase. */
    val storeUnavailable: Boolean get() = !loading && !isPremium && products.isEmpty()
}

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val billingRepository: BillingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PremiumUiState())
    val uiState: StateFlow<PremiumUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            billingRepository.premiumStatus().collect { premium ->
                _uiState.value = _uiState.value.copy(isPremium = premium)
            }
        }
        viewModelScope.launch {
            billingRepository.ownedPackIds().collect { owned ->
                _uiState.value = _uiState.value.copy(ownedPackIds = owned)
            }
        }
        loadProducts()
    }

    /**
     * Retries querying Play Billing for products. Exposed so the UI can offer a real "Try
     * again" action instead of ever showing a purchase button for a product that doesn't
     * actually exist yet.
     */
    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, message = null)
            val products = runCatching { billingRepository.queryProducts() }.getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(loading = false, products = products)
        }
    }

    fun purchase(productId: String) {
        viewModelScope.launch {
            val result = billingRepository.launchPurchase(productId)
            result.onFailure { _uiState.value = _uiState.value.copy(message = it.message) }
        }
    }

    fun restorePurchases() {
        viewModelScope.launch {
            val result = billingRepository.restorePurchases()
            _uiState.value = _uiState.value.copy(
                message = if (result.isSuccess) "Purchases restored" else "Restore failed: ${result.exceptionOrNull()?.message}",
            )
        }
    }
}
