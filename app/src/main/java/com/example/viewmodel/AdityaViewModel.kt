package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.ServiceOrder
import com.example.data.model.UserProfile
import com.example.data.repository.AdityaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class AdityaViewModel(
    private val repository: AdityaRepository,
    val currentUserId: String,
    val currentUserEmail: String?,
    val currentUserDisplayName: String?
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ServiceCategory.ALL)
    val selectedCategory: StateFlow<ServiceCategory> = _selectedCategory.asStateFlow()

    private val _selectedOrderStatusFilter = MutableStateFlow("all")
    val selectedOrderStatusFilter: StateFlow<String> = _selectedOrderStatusFilter.asStateFlow()

    private val _isSubmittingOrder = MutableStateFlow(false)
    val isSubmittingOrder: StateFlow<Boolean> = _isSubmittingOrder.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _latestSubmittedOrder = MutableStateFlow<ServiceOrder?>(null)
    val latestSubmittedOrder: StateFlow<ServiceOrder?> = _latestSubmittedOrder.asStateFlow()

    // Real-time stream of services from shared Firestore
    val servicesState: StateFlow<UiState<List<ServiceItem>>> = repository.observeServices()
        .map<List<ServiceItem>, UiState<List<ServiceItem>>> { UiState.Success(it) }
        .catch { error ->
            Log.w(TAG, "Error observing services catalogue", error)
            emit(UiState.Error(error.message ?: "Failed to load services from backend."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    // Real-time stream of signed-in customer's orders
    val ordersState: StateFlow<UiState<List<ServiceOrder>>> = repository.observeCustomerOrders(currentUserId)
        .map<List<ServiceOrder>, UiState<List<ServiceOrder>>> { UiState.Success(it) }
        .catch { error ->
            Log.w(TAG, "Error observing customer orders", error)
            emit(UiState.Error(error.message ?: "Failed to load your orders."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    // Real-time stream of signed-in customer's profile
    val userProfileState: StateFlow<UserProfile?> = repository.observeUserProfile(currentUserId)
        .catch { error ->
            Log.w(TAG, "Error observing user profile", error)
            emit(null)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            val adminFlag = repository.checkIsAdmin(currentUserId, currentUserEmail)
            _isAdmin.value = adminFlag
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: ServiceCategory) {
        _selectedCategory.value = category
    }

    fun selectOrderStatusFilter(statusKey: String) {
        _selectedOrderStatusFilter.value = statusKey
    }

    fun clearBannerMessage() {
        _statusBannerMessage.value = null
    }

    fun submitOrder(
        serviceId: String,
        customerName: String,
        customerPhone: String,
        applicantDetailsSummary: String,
        documentLinks: List<String>,
        upiReference: String,
        onOrderConfirmed: (ServiceOrder) -> Unit
    ) {
        if (_isSubmittingOrder.value) return
        _isSubmittingOrder.value = true
        _statusBannerMessage.value = null

        viewModelScope.launch {
            val result = repository.submitOrder(
                serviceId = serviceId,
                customerName = customerName,
                customerPhone = customerPhone,
                customerEmail = currentUserEmail ?: "",
                applicantDetailsSummary = applicantDetailsSummary,
                documentLinks = documentLinks,
                upiReference = upiReference
            )
            _isSubmittingOrder.value = false
            result.fold(
                onSuccess = { confirmedOrder ->
                    _latestSubmittedOrder.value = confirmedOrder
                    _statusBannerMessage.value = "Order ${confirmedOrder.orderNumber} submitted to Aditya Online Center!"
                    onOrderConfirmed(confirmedOrder)
                },
                onFailure = { error ->
                    _statusBannerMessage.value = error.localizedMessage ?: "Failed to submit order. Please check your details."
                }
            )
        }
    }

    fun submitUpiReference(orderId: String, upiReference: String) {
        viewModelScope.launch {
            val result = repository.submitCustomerUpiReference(orderId, upiReference)
            result.fold(
                onSuccess = {
                    _statusBannerMessage.value = "UPI Reference submitted! Payment status is now Pending Admin Verification."
                },
                onFailure = { err ->
                    _statusBannerMessage.value = err.localizedMessage ?: "Could not update UPI Reference."
                }
            )
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            val result = repository.cancelPendingOrder(orderId)
            result.fold(
                onSuccess = {
                    _statusBannerMessage.value = "Order has been cancelled."
                },
                onFailure = { err ->
                    _statusBannerMessage.value = err.localizedMessage ?: "Failed to cancel order."
                }
            )
        }
    }

    fun saveUserProfile(
        name: String,
        phone: String,
        address: String,
        preferredLanguage: String
    ) {
        viewModelScope.launch {
            val result = repository.saveUserProfile(
                userId = currentUserId,
                name = name,
                email = currentUserEmail ?: "",
                phone = phone,
                address = address,
                preferredLanguage = preferredLanguage
            )
            result.fold(
                onSuccess = {
                    _statusBannerMessage.value = "Profile saved to cloud successfully."
                },
                onFailure = { err ->
                    _statusBannerMessage.value = err.localizedMessage ?: "Could not save profile."
                }
            )
        }
    }

    // Admin actions (authorized via Firestore Security Rules)
    fun seedDefaultServicesAsAdmin() {
        viewModelScope.launch {
            val result = repository.seedDefaultServicesAsAdmin()
            result.fold(
                onSuccess = { count ->
                    _statusBannerMessage.value = "Synchronized $count cyber cafe services to the shared backend."
                },
                onFailure = { err ->
                    _statusBannerMessage.value = "Admin authorization required to seed catalogue: ${err.localizedMessage}"
                }
            )
        }
    }

    fun saveServiceAsAdmin(service: ServiceItem, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val result = repository.saveServiceAsAdmin(service)
            result.fold(
                onSuccess = {
                    _statusBannerMessage.value = "Service '${service.title}' updated in shared catalogue."
                    onComplete()
                },
                onFailure = { err ->
                    _statusBannerMessage.value = "Admin permission required: ${err.localizedMessage}"
                }
            )
        }
    }

    fun adminUpdateOrder(
        orderId: String,
        status: String,
        paymentStatus: String,
        adminNotes: String,
        deliveryFileUrl: String
    ) {
        viewModelScope.launch {
            val result = repository.adminUpdateOrder(
                orderId = orderId,
                status = status,
                paymentStatus = paymentStatus,
                adminNotes = adminNotes,
                deliveryFileUrl = deliveryFileUrl
            )
            result.fold(
                onSuccess = {
                    _statusBannerMessage.value = "Order updated in shared backend."
                },
                onFailure = { err ->
                    _statusBannerMessage.value = "Admin permission required: ${err.localizedMessage}"
                }
            )
        }
    }

    private companion object {
        const val TAG = "AdityaViewModel"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
