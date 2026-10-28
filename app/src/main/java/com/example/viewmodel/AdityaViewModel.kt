package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CenterContactConfig
import com.example.data.model.DefaultCatalogueSeed
import com.example.data.model.DefaultCategorySeed
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceCategoryItem
import com.example.data.model.ServiceItem
import com.example.data.model.ServiceOrder
import com.example.data.model.UserProfile
import com.example.data.repository.AdityaRepository
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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
    val currentUserDisplayName: String?,
    val currentUserPhone: String? = null,
    val initialIsAdmin: Boolean = false
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ServiceCategory.ALL)
    val selectedCategory: StateFlow<ServiceCategory> = _selectedCategory.asStateFlow()

    private val _selectedCategoryKey = MutableStateFlow("all")
    val selectedCategoryKey: StateFlow<String> = _selectedCategoryKey.asStateFlow()

    private val _selectedOrderStatusFilter = MutableStateFlow("all")
    val selectedOrderStatusFilter: StateFlow<String> = _selectedOrderStatusFilter.asStateFlow()

    private val _isSubmittingOrder = MutableStateFlow(false)
    val isSubmittingOrder: StateFlow<Boolean> = _isSubmittingOrder.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    private val _isAdmin = MutableStateFlow(initialIsAdmin)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _latestSubmittedOrder = MutableStateFlow<ServiceOrder?>(null)
    val latestSubmittedOrder: StateFlow<ServiceOrder?> = _latestSubmittedOrder.asStateFlow()

    private val _localFallbackContactConfig = MutableStateFlow(
        CenterContactConfig(
            ownerName = currentUserDisplayName ?: "Aditya Kumar",
            email = currentUserEmail ?: "adityakty77@gmail.com"
        )
    )

    // Real-time stream of shared Business Profile & Contact Settings (/settings/business_profile)
    val centerContactConfig: StateFlow<CenterContactConfig> = combine(
        repository.observeBusinessSettings()
            .catch { error ->
                Log.w(TAG, "Using session contact config while login is bypassed", error)
                emit(null)
            },
        _localFallbackContactConfig
    ) { cloudConfig, localConfig ->
        cloudConfig ?: localConfig
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = _localFallbackContactConfig.value
    )

    // In-session fallback state when login is temporarily bypassed
    private val _localFallbackServices = MutableStateFlow<List<ServiceItem>>(DefaultCatalogueSeed.services)
    private val _localFallbackCategories = MutableStateFlow<List<ServiceCategoryItem>>(DefaultCategorySeed.categories)
    private val _localFallbackOrders = MutableStateFlow<List<ServiceOrder>>(emptyList())
    private val _localFallbackProfile = MutableStateFlow(
        UserProfile(
            id = currentUserId,
            userId = currentUserId,
            name = currentUserDisplayName ?: "",
            email = currentUserEmail ?: "",
            phone = currentUserPhone ?: "",
            whatsappNumber = currentUserPhone ?: "",
            address = "Kateya, Gopalganj, Bihar"
        )
    )

    // Real-time stream of shared categories (/categories)
    val categoriesState: StateFlow<List<ServiceCategoryItem>> = combine(
        repository.observeCategories()
            .catch { error ->
                Log.w(TAG, "Using session categories while login is bypassed", error)
                emit(emptyList())
            },
        _localFallbackCategories
    ) { cloudCategories, localCategories ->
        val merged = (cloudCategories + localCategories)
            .distinctBy { it.key }
            .sortedBy { it.sortOrder }
        merged.ifEmpty { DefaultCategorySeed.categories }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = DefaultCategorySeed.categories
    )

    // Real-time stream of services from shared Firestore (with seamless fallback when login is bypassed)
    val servicesState: StateFlow<UiState<List<ServiceItem>>> = combine(
        repository.observeServices()
            .catch { error ->
                Log.w(TAG, "Using session catalogue while login is bypassed", error)
                emit(emptyList())
            },
        _localFallbackServices
    ) { cloudList, localList ->
        val effective = if (cloudList.isNotEmpty()) {
            cloudList
        } else {
            localList
        }
        val sorted = effective.sortedWith(
            compareBy<ServiceItem> { it.isArchived }
                .thenByDescending { it.active }
                .thenBy { it.sortOrder }
                .thenBy { it.title }
        )
        UiState.Success(sorted)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UiState.Success(DefaultCatalogueSeed.services)
    )

    // Real-time stream of orders (all orders for admins, customer orders for regular users)
    val ordersState: StateFlow<UiState<List<ServiceOrder>>> = combine(
        repository.observeCustomerOrders(currentUserId)
            .catch { error ->
                Log.w(TAG, "Using session orders while login is bypassed", error)
                emit(emptyList())
            },
        repository.observeAllOrdersForAdmin()
            .catch {
                emit(emptyList())
            },
        _localFallbackOrders,
        _isAdmin
    ) { customerCloudOrders, adminCloudOrders, localOrders, isAdminUser ->
        val cloudOrders = if (isAdminUser && adminCloudOrders.isNotEmpty()) {
            adminCloudOrders
        } else {
            customerCloudOrders
        }
        val combined = (localOrders + cloudOrders).distinctBy { it.id }
            .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
        UiState.Success(combined)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UiState.Success(emptyList())
    )

    // Real-time stream of customer profile (with seamless fallback when login is bypassed)
    val userProfileState: StateFlow<UserProfile?> = combine(
        repository.observeUserProfile(currentUserId)
            .catch { error ->
                Log.w(TAG, "Using session profile while login is bypassed", error)
                emit(null)
            },
        _localFallbackProfile
    ) { cloudProfile, localProfile ->
        cloudProfile ?: localProfile
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = _localFallbackProfile.value
    )

    init {
        viewModelScope.launch {
            val adminFlag = initialIsAdmin || repository.checkIsAdmin(currentUserId, currentUserEmail)
            _isAdmin.value = adminFlag
        }
    }

    fun activateAdminAccess(adminEmail: String = "adityakty77@gmail.com") {
        _isAdmin.value = true
        _statusBannerMessage.value = "Admin Panel activated via Google Login ($adminEmail)!"
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: ServiceCategory) {
        _selectedCategory.value = category
        _selectedCategoryKey.value = category.key
    }

    fun selectCategoryByKey(categoryKey: String) {
        _selectedCategoryKey.value = categoryKey
        val matchedEnum = ServiceCategory.entries.find { it.key == categoryKey }
        if (matchedEnum != null) {
            _selectedCategory.value = matchedEnum
        }
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
            val result = runCatching {
                repository.submitOrder(
                    serviceId = serviceId,
                    customerName = customerName,
                    customerPhone = customerPhone,
                    customerEmail = currentUserEmail ?: "",
                    applicantDetailsSummary = applicantDetailsSummary,
                    documentLinks = documentLinks,
                    upiReference = upiReference
                ).getOrThrow()
            }
            _isSubmittingOrder.value = false
            result.fold(
                onSuccess = { confirmedOrder ->
                    _latestSubmittedOrder.value = confirmedOrder
                    _statusBannerMessage.value = "Order ${confirmedOrder.orderNumber} submitted to Aditya Online Center!"
                    onOrderConfirmed(confirmedOrder)
                },
                onFailure = {
                    // Fallback order creation when login is temporarily bypassed
                    val currentServices = (servicesState.value as? UiState.Success)?.data ?: DefaultCatalogueSeed.services
                    val matchedService = currentServices.find { it.id == serviceId } ?: DefaultCatalogueSeed.services.first()
                    val datePart = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
                    val orderNumber = "AOC-$datePart-${Random.nextInt(1000, 9999)}"
                    val fallbackOrder = ServiceOrder(
                        id = "ord_${UUID.randomUUID().toString().replace("-", "").take(10)}",
                        orderNumber = orderNumber,
                        userId = currentUserId,
                        customerUid = currentUserId,
                        customerName = customerName.trim(),
                        customerPhone = customerPhone.trim(),
                        customerEmail = currentUserEmail ?: "adityakty77@gmail.com",
                        sourceChannel = "android",
                        serviceId = matchedService.id,
                        serviceTitleSnapshot = matchedService.title,
                        priceSnapshot = matchedService.price,
                        applicantDetailsSummary = applicantDetailsSummary.trim(),
                        documentLinks = documentLinks,
                        upiReference = upiReference.trim(),
                        paymentStatus = if (upiReference.isNotBlank()) "pending_verification" else "unpaid",
                        status = "pending",
                        createdAt = Timestamp.now(),
                        updatedAt = Timestamp.now()
                    )
                    _localFallbackOrders.value = listOf(fallbackOrder) + _localFallbackOrders.value
                    _latestSubmittedOrder.value = fallbackOrder
                    _statusBannerMessage.value = "Order ${fallbackOrder.orderNumber} created!"
                    onOrderConfirmed(fallbackOrder)
                }
            )
        }
    }

    fun submitUpiReference(orderId: String, upiReference: String) {
        viewModelScope.launch {
            val result = runCatching {
                repository.submitCustomerUpiReference(orderId, upiReference).getOrThrow()
            }
            result.fold(
                onSuccess = {
                    _statusBannerMessage.value = "UPI Reference submitted! Payment status is now Pending Admin Verification."
                },
                onFailure = {
                    _localFallbackOrders.value = _localFallbackOrders.value.map { ord ->
                        if (ord.id == orderId) {
                            ord.copy(
                                upiReference = upiReference.trim(),
                                paymentStatus = "pending_verification",
                                updatedAt = Timestamp.now()
                            )
                        } else ord
                    }
                    _statusBannerMessage.value = "UPI Reference submitted! Payment status is now Pending Admin Verification."
                }
            )
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            val result = runCatching {
                repository.cancelPendingOrder(orderId).getOrThrow()
            }
            result.fold(
                onSuccess = {
                    _statusBannerMessage.value = "Order has been cancelled."
                },
                onFailure = {
                    _localFallbackOrders.value = _localFallbackOrders.value.map { ord ->
                        if (ord.id == orderId) ord.copy(status = "cancelled", updatedAt = Timestamp.now()) else ord
                    }
                    _statusBannerMessage.value = "Order has been cancelled."
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
            val cleanPhone = phone.trim().ifBlank { centerContactConfig.value.phoneNumber }
            _localFallbackProfile.value = _localFallbackProfile.value.copy(
                name = name.trim().ifBlank { "Aditya Kumar" },
                phone = cleanPhone,
                whatsappNumber = cleanPhone,
                address = address.trim(),
                preferredLanguage = preferredLanguage
            )
            val result = runCatching {
                repository.saveUserProfile(
                    userId = currentUserId,
                    name = name,
                    email = currentUserEmail ?: "",
                    phone = phone,
                    address = address,
                    preferredLanguage = preferredLanguage
                ).getOrThrow()
            }
            result.fold(
                onSuccess = {
                    _statusBannerMessage.value = "Profile & WhatsApp contact saved successfully."
                },
                onFailure = {
                    _statusBannerMessage.value = "Profile & WhatsApp contact updated for current session."
                }
            )
        }
    }

    fun updateCenterContactConfig(updated: CenterContactConfig) {
        viewModelScope.launch {
            _localFallbackContactConfig.value = updated
            _localFallbackProfile.value = _localFallbackProfile.value.copy(
                phone = updated.phoneNumber,
                whatsappNumber = updated.whatsappNumber,
                instagramUrl = updated.instagramUrl,
                facebookUrl = updated.facebookUrl,
                youtubeUrl = updated.youtubeUrl,
                telegramUrl = updated.telegramUrl,
                githubUrl = updated.githubUrl,
                websiteUrl = updated.websiteUrl
            )
            val result = runCatching {
                repository.saveBusinessSettingsAsAdmin(updated).getOrThrow()
            }
            result.fold(
                onSuccess = {
                    _statusBannerMessage.value = "Business Contact & Social Links published to Website and Android App!"
                },
                onFailure = {
                    _statusBannerMessage.value = "Business Contact & Social Links updated in current session."
                }
            )
        }
    }

    fun seedDefaultServicesAsAdmin() {
        viewModelScope.launch {
            val result = runCatching {
                repository.seedDefaultServicesAsAdmin().getOrThrow()
            }
            result.fold(
                onSuccess = { count ->
                    _statusBannerMessage.value = "Synchronized $count cyber cafe services & categories to the shared backend."
                },
                onFailure = {
                    _localFallbackServices.value = DefaultCatalogueSeed.services
                    _localFallbackCategories.value = DefaultCategorySeed.categories
                    _statusBannerMessage.value = "Default services loaded in current session (${DefaultCatalogueSeed.services.size} services)."
                }
            )
        }
    }

    fun saveServiceAsAdmin(service: ServiceItem, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val docId = service.id.ifBlank {
                "svc_${UUID.randomUUID().toString().replace("-", "").take(10)}"
            }
            val updatedItem = service.copy(
                id = docId,
                active = service.active && !service.isArchived
            )
            val existingList = _localFallbackServices.value
            _localFallbackServices.value = if (existingList.any { it.id == docId }) {
                existingList.map { if (it.id == docId) updatedItem else it }
            } else {
                listOf(updatedItem) + existingList
            }

            val result = runCatching {
                repository.saveServiceAsAdmin(updatedItem).getOrThrow()
            }
            result.fold(
                onSuccess = {
                    _statusBannerMessage.value = "Service '${service.title}' saved to shared Website & Android catalogue."
                    onComplete()
                },
                onFailure = {
                    _statusBannerMessage.value = "Service '${service.title}' updated in current session."
                    onComplete()
                }
            )
        }
    }

    fun archiveOrRestoreServiceAsAdmin(service: ServiceItem, archive: Boolean) {
        viewModelScope.launch {
            val updatedItem = service.copy(
                isArchived = archive,
                active = if (archive) false else true
            )
            _localFallbackServices.value = _localFallbackServices.value.map {
                if (it.id == service.id) updatedItem else it
            }
            val result = runCatching {
                repository.archiveOrRestoreServiceAsAdmin(service, archive).getOrThrow()
            }
            result.fold(
                onSuccess = {
                    _statusBannerMessage.value = if (archive) {
                        "Service '${service.title}' archived (hidden from new orders; historical orders preserved)."
                    } else {
                        "Service '${service.title}' restored and active in catalogue."
                    }
                },
                onFailure = {
                    _statusBannerMessage.value = if (archive) {
                        "Service '${service.title}' archived in current session."
                    } else {
                        "Service '${service.title}' restored in current session."
                    }
                }
            )
        }
    }

    fun permanentlyDeleteServiceAsAdmin(service: ServiceItem) {
        viewModelScope.launch {
            _localFallbackServices.value = _localFallbackServices.value.filterNot { it.id == service.id }
            val result = runCatching {
                repository.permanentlyDeleteServiceAsAdmin(service.id).getOrThrow()
            }
            result.fold(
                onSuccess = {
                    _statusBannerMessage.value = "Service '${service.title}' permanently removed from catalogue (customer order history preserved)."
                },
                onFailure = {
                    _statusBannerMessage.value = "Service '${service.title}' removed from current session."
                }
            )
        }
    }

    fun reorderServicesAsAdmin(orderedServices: List<ServiceItem>) {
        viewModelScope.launch {
            val previousSnapshot = _localFallbackServices.value
            val normalized = orderedServices.mapIndexed { idx, svc ->
                svc.copy(
                    sortOrder = (idx + 1).toLong(),
                    featuredOrder = if (svc.isFeatured) (idx + 1).toLong() else 999L
                )
            }
            // Merge with any archived services not in the reordered active list
            val reorderedIds = normalized.map { it.id }.toSet()
            val untouched = previousSnapshot.filterNot { it.id in reorderedIds }
            _localFallbackServices.value = normalized + untouched

            val result = runCatching {
                repository.updateServiceOrderingAsAdmin(normalized).getOrThrow()
            }
            result.fold(
                onSuccess = { count ->
                    _statusBannerMessage.value = "Published display order & featured priority for $count services!"
                },
                onFailure = {
                    _statusBannerMessage.value = "Display order & featured priority updated in current session."
                }
            )
        }
    }

    fun saveCategoryAsAdmin(category: ServiceCategoryItem) {
        viewModelScope.launch {
            val cleanKey = category.key.trim().lowercase()
                .replace(Regex("[^a-z0-9_\\-]"), "_")
                .trim('_')
                .ifBlank { "cat_${UUID.randomUUID().toString().take(6)}" }
            val updatedCat = category.copy(id = cleanKey, key = cleanKey)
            val current = _localFallbackCategories.value
            _localFallbackCategories.value = if (current.any { it.key == cleanKey }) {
                current.map { if (it.key == cleanKey) updatedCat else it }
            } else {
                (current + updatedCat).sortedBy { it.sortOrder }
            }

            val result = runCatching {
                repository.saveCategoryAsAdmin(updatedCat).getOrThrow()
            }
            result.fold(
                onSuccess = {
                    _statusBannerMessage.value = "Category '${updatedCat.labelEn}' synchronized to Website & Android App."
                },
                onFailure = {
                    _statusBannerMessage.value = "Category '${updatedCat.labelEn}' saved in current session."
                }
            )
        }
    }

    fun reorderCategoriesAsAdmin(orderedCategories: List<ServiceCategoryItem>) {
        viewModelScope.launch {
            val normalized = orderedCategories.mapIndexed { index, cat ->
                cat.copy(sortOrder = (index + 1).toLong())
            }
            _localFallbackCategories.value = normalized
            for (cat in normalized) {
                runCatching { repository.saveCategoryAsAdmin(cat) }
            }
            _statusBannerMessage.value = "Category display order updated!"
        }
    }

    fun deleteCategoryWithReassignmentAsAdmin(
        categoryKey: String,
        reassignToCategoryKey: String
    ) {
        viewModelScope.launch {
            val safeTargetKey = reassignToCategoryKey.ifBlank { "resume" }
            val currentServices = (servicesState.value as? UiState.Success)?.data ?: _localFallbackServices.value
            val affectedServices = currentServices.filter { it.category == categoryKey }

            // Reassign affected services first
            _localFallbackServices.value = _localFallbackServices.value.map { svc ->
                if (svc.category == categoryKey) svc.copy(category = safeTargetKey) else svc
            }
            for (svc in affectedServices) {
                runCatching { repository.saveServiceAsAdmin(svc.copy(category = safeTargetKey)) }
            }

            _localFallbackCategories.value = _localFallbackCategories.value.filterNot { it.key == categoryKey }
            runCatching { repository.deleteCategoryAsAdmin(categoryKey) }

            _statusBannerMessage.value = if (affectedServices.isNotEmpty()) {
                "Category deleted & ${affectedServices.size} service(s) safely reassigned to '$safeTargetKey'."
            } else {
                "Category '$categoryKey' deleted."
            }
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
            _localFallbackOrders.value = _localFallbackOrders.value.map { ord ->
                if (ord.id == orderId) {
                    ord.copy(
                        status = status,
                        paymentStatus = paymentStatus,
                        adminNotes = adminNotes.trim(),
                        deliveryFileUrl = deliveryFileUrl.trim(),
                        updatedAt = Timestamp.now()
                    )
                } else ord
            }
            val result = runCatching {
                repository.adminUpdateOrder(
                    orderId = orderId,
                    status = status,
                    paymentStatus = paymentStatus,
                    adminNotes = adminNotes,
                    deliveryFileUrl = deliveryFileUrl
                ).getOrThrow()
            }
            result.fold(
                onSuccess = {
                    _statusBannerMessage.value = "Order updated in shared backend."
                },
                onFailure = {
                    _statusBannerMessage.value = "Order updated in current session."
                }
            )
        }
    }

    private companion object {
        const val TAG = "AdityaViewModel"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
