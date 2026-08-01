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
    val message: String? = null,
)

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
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
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
