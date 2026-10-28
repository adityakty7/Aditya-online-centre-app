package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.BOOTSTRAPPED_ADMIN_EMAIL
import com.example.data.model.BUSINESS_SETTINGS_DOC_ID
import com.example.data.model.CenterContactConfig
import com.example.data.model.DefaultCatalogueSeed
import com.example.data.model.DefaultCategorySeed
import com.example.data.model.ServiceCategoryItem
import com.example.data.model.ServiceItem
import com.example.data.model.ServiceOrder
import com.example.data.model.TENANT_ID_ADITYA
import com.example.data.model.UserProfile
import com.example.data.remote.OperationType
import com.example.data.remote.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class AdityaRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    private fun DocumentSnapshot.toServiceItem(): ServiceItem? {
        val base = toObject(
            ServiceItem::class.java,
            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
        ) ?: return null
        return base.copy(
            id = id,
            isFeatured = getBoolean("isFeatured") ?: getBoolean("featured") ?: base.isFeatured,
            isArchived = getBoolean("isArchived") ?: getBoolean("archived") ?: base.isArchived,
            featuredOrder = getLong("featuredOrder") ?: base.featuredOrder,
            sortOrder = getLong("sortOrder") ?: base.sortOrder
        )
    }

    // 1. REALTIME SERVICE CATALOGUE (Shared across Website & Android)
    fun observeServices(): Flow<List<ServiceItem>> {
        val path = "services"
        return db.collection(path)
            .whereEqualTo("tenantId", TENANT_ID_ADITYA)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toServiceItem()
                }.sortedWith(
                    compareBy<ServiceItem> { it.isArchived }
                        .thenByDescending { it.active }
                        .thenBy { it.sortOrder }
                        .thenBy { it.title }
                )
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, path)
                }
                throw error
            }
    }

    suspend fun getServiceById(serviceId: String): Result<ServiceItem> {
        val docRef = db.collection("services").document(serviceId)
        return try {
            val snapshot = docRef.get().await()
            val item = snapshot.toServiceItem()
                ?: throw NoSuchElementException("Service $serviceId not found")
            Result.success(item)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    // Admin-only: Create or update a service in the shared catalogue
    suspend fun saveServiceAsAdmin(service: ServiceItem): Result<String> {
        requireUserId()
        val cleanTitle = service.title.trim()
        if (cleanTitle.length < 2) {
            return Result.failure(IllegalArgumentException("Service title must be at least 2 characters."))
        }
        if (service.price < 0L || service.price > 100000L) {
            return Result.failure(IllegalArgumentException("Service price must be between ₹0 and ₹1,00,000."))
        }

        val docId = service.id.ifBlank {
            "svc_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        }
        val docRef = db.collection("services").document(docId)
        return try {
            val existingSnap = docRef.get().await()
            val cleanDesc = service.description.trim().ifBlank {
                service.shortDescription.trim().ifBlank { "$cleanTitle digital service at Aditya Online Center." }
            }
            val cleanDescHi = service.descriptionHi.trim().ifBlank {
                service.shortDescriptionHi.trim().ifBlank { cleanDesc }
            }
            val cleanShort = service.shortDescription.trim().take(300)
            val cleanShortHi = service.shortDescriptionHi.trim().ifBlank { cleanShort }.take(400)
            val cleanCategory = service.category.trim().lowercase()
                .replace(Regex("[^a-z0-9_\\-]"), "_")
                .ifBlank { "resume" }
                .take(64)

            val payload = mutableMapOf<String, Any>(
                "tenantId" to TENANT_ID_ADITYA,
                "title" to cleanTitle.take(140),
                "titleHi" to service.titleHi.trim().ifBlank { cleanTitle }.take(220),
                "category" to cleanCategory,
                "shortDescription" to cleanShort,
                "shortDescriptionHi" to cleanShortHi,
                "description" to cleanDesc.take(3000),
                "descriptionHi" to cleanDescHi.take(3500),
                "price" to service.price,
                "turnaroundTime" to service.turnaroundTime.trim().ifBlank { "2 - 4 Hours" }.take(100),
                "iconKey" to service.iconKey.trim().ifBlank { "description" }.take(60),
                "requirements" to service.requirements.map { it.trim().take(300) }.filter { it.isNotEmpty() }.distinct().take(25),
                "imageUrl" to service.imageUrl.trim().take(250000),
                "active" to (service.active && !service.isArchived),
                "isFeatured" to service.isFeatured,
                "featuredOrder" to service.featuredOrder.coerceIn(0L, 100000L),
                "sortOrder" to service.sortOrder.coerceIn(0L, 100000L),
                "isArchived" to service.isArchived,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (!existingSnap.exists()) {
                payload["createdAt"] = FieldValue.serverTimestamp()
                docRef.set(payload).await()
            } else {
                docRef.update(payload).await()
            }
            Result.success(docId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    // Admin-only: Soft-delete (archive) or restore a service while preserving all customer orders
    suspend fun archiveOrRestoreServiceAsAdmin(
        service: ServiceItem,
        archive: Boolean
    ): Result<String> {
        val updated = service.copy(
            isArchived = archive,
            active = if (archive) false else true
        )
        return saveServiceAsAdmin(updated)
    }

    // Admin-only: Permanently delete a service document (orders retain their snapshot fields)
    suspend fun permanentlyDeleteServiceAsAdmin(serviceId: String): Result<Unit> {
        requireUserId()
        val docRef = db.collection("services").document(serviceId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    // Admin-only: Persist reordered services, featured flags, and priority positions atomically
    suspend fun updateServiceOrderingAsAdmin(orderedServices: List<ServiceItem>): Result<Int> {
        requireUserId()
        return try {
            var updatedCount = 0
            for ((index, svc) in orderedServices.withIndex()) {
                val newSortOrder = (index + 1).toLong()
                val newFeaturedOrder = if (svc.isFeatured) (index + 1).toLong() else 999L
                val res = saveServiceAsAdmin(
                    svc.copy(
                        sortOrder = newSortOrder,
                        featuredOrder = newFeaturedOrder
                    )
                )
                if (res.isSuccess) updatedCount++
            }
            Result.success(updatedCount)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, "services")
            Result.failure(e)
        }
    }

    suspend fun seedDefaultServicesAsAdmin(): Result<Int> {
        requireUserId()
        var seededCount = 0
        return try {
            for (cat in DefaultCategorySeed.categories) {
                saveCategoryAsAdmin(cat)
            }
            for (seed in DefaultCatalogueSeed.services) {
                val res = saveServiceAsAdmin(seed)
                if (res.isSuccess) seededCount++
            }
            Result.success(seededCount)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "services")
            Result.failure(e)
        }
    }

    // 1B. SHARED CATEGORY MANAGEMENT (/categories/{categoryId})
    fun observeCategories(): Flow<List<ServiceCategoryItem>> {
        val path = "categories"
        return db.collection(path)
            .whereEqualTo("tenantId", TENANT_ID_ADITYA)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(
                        ServiceCategoryItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }.sortedBy { it.sortOrder }
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, path)
                }
                throw error
            }
    }

    suspend fun saveCategoryAsAdmin(category: ServiceCategoryItem): Result<String> {
        requireUserId()
        val cleanKey = category.key.trim().lowercase()
            .replace(Regex("[^a-z0-9_\\-]"), "_")
            .trim('_')
            .take(64)
        if (cleanKey.length < 2) {
            return Result.failure(IllegalArgumentException("Category key must be at least 2 characters."))
        }
        val cleanLabelEn = category.labelEn.trim()
        if (cleanLabelEn.length < 2) {
            return Result.failure(IllegalArgumentException("Category name must be at least 2 characters."))
        }
        val docRef = db.collection("categories").document(cleanKey)
        return try {
            val existing = docRef.get().await()
            val payload = mutableMapOf<String, Any>(
                "tenantId" to TENANT_ID_ADITYA,
                "key" to cleanKey,
                "labelEn" to cleanLabelEn.take(80),
                "labelHi" to category.labelHi.trim().ifBlank { cleanLabelEn }.take(120),
                "iconKey" to category.iconKey.trim().ifBlank { "category" }.take(60),
                "sortOrder" to category.sortOrder.coerceIn(0L, 10000L),
                "active" to category.active,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (!existing.exists()) {
                payload["createdAt"] = FieldValue.serverTimestamp()
                docRef.set(payload).await()
            } else {
                docRef.update(payload).await()
            }
            Result.success(cleanKey)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteCategoryAsAdmin(categoryKey: String): Result<Unit> {
        requireUserId()
        val docRef = db.collection("categories").document(categoryKey)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    // 1C. SHARED BUSINESS PROFILE & SOCIAL MEDIA SETTINGS (/settings/business_profile)
    fun observeBusinessSettings(): Flow<CenterContactConfig?> {
        val docRef = db.collection("settings").document(BUSINESS_SETTINGS_DOC_ID)
        return docRef.snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(
                        CenterContactConfig::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                } else {
                    null
                }
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.GET, docRef.path)
                }
                throw error
            }
    }

    suspend fun saveBusinessSettingsAsAdmin(config: CenterContactConfig): Result<Unit> {
        requireUserId()
        val cleanWhatsApp = config.whatsappNumber.filter { it.isDigit() }.takeLast(10)
        val cleanPhone = config.phoneNumber.filter { it.isDigit() }.takeLast(10)
        if (!cleanWhatsApp.matches(Regex("^[0-9]{10}$"))) {
            return Result.failure(IllegalArgumentException("WhatsApp number must be a valid 10-digit Indian mobile number."))
        }
        if (!cleanPhone.matches(Regex("^[0-9]{10}$"))) {
            return Result.failure(IllegalArgumentException("Business phone number must be a valid 10-digit mobile number."))
        }
        if (!config.email.trim().contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid support email address."))
        }

        val docRef = db.collection("settings").document(BUSINESS_SETTINGS_DOC_ID)
        return try {
            val existing = docRef.get().await()
            val payload = mutableMapOf<String, Any>(
                "tenantId" to TENANT_ID_ADITYA,
                "centerName" to config.centerName.trim().ifBlank { "Aditya Online Center" }.take(120),
                "ownerName" to config.ownerName.trim().ifBlank { "Aditya Kumar" }.take(100),
                "whatsappNumber" to cleanWhatsApp,
                "phoneNumber" to cleanPhone,
                "email" to config.email.trim().take(150),
                "location" to config.location.trim().ifBlank { "Main Market, Kateya, Gopalganj, Bihar" }.take(300),
                "workingHours" to config.workingHours.trim().take(200),
                "contactInstructions" to config.contactInstructions.trim().take(500),
                "instagramUrl" to config.instagramUrl.trim().take(300),
                "facebookUrl" to config.facebookUrl.trim().take(300),
                "youtubeUrl" to config.youtubeUrl.trim().take(300),
                "telegramUrl" to config.telegramUrl.trim().take(300),
                "githubUrl" to config.githubUrl.trim().take(300),
                "websiteUrl" to config.websiteUrl.trim().take(300),
                "socialLinksSpec" to config.socialLinksSpec.map { it.trim().take(500) }.take(15),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (!existing.exists()) {
                payload["createdAt"] = FieldValue.serverTimestamp()
                docRef.set(payload).await()
            } else {
                docRef.update(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    // 2. SHARED AUTHORITATIVE ORDER SYSTEM
    suspend fun submitOrder(
        serviceId: String,
        customerName: String,
        customerPhone: String,
        customerEmail: String,
        applicantDetailsSummary: String,
        documentLinks: List<String>,
        upiReference: String
    ): Result<ServiceOrder> {
        val uid = requireUserId()
        val cleanName = customerName.trim()
        val cleanPhone = customerPhone.trim()
        val cleanSummary = applicantDetailsSummary.trim()
        val cleanUpi = upiReference.trim()

        if (cleanName.length < 2) {
            return Result.failure(IllegalArgumentException("Please enter applicant full name (minimum 2 characters)."))
        }
        if (!cleanPhone.matches(Regex("^[0-9]{10}$"))) {
            return Result.failure(IllegalArgumentException("Please enter a valid 10-digit mobile number."))
        }
        if (cleanSummary.length < 2) {
            return Result.failure(IllegalArgumentException("Please provide the required application details."))
        }

        val serviceRef = db.collection("services").document(serviceId)
        val orderDocId = "ord_${UUID.randomUUID().toString().replace("-", "").take(12)}"
        val orderRef = db.collection("orders").document(orderDocId)

        return try {
            // Fetch trusted service document to verify availability and authoritative price
            val serviceSnap = serviceRef.get().await()
            if (!serviceSnap.exists()) {
                return Result.failure(IllegalStateException("Selected service does not exist in the shared catalogue."))
            }
            val isServiceActive = serviceSnap.getBoolean("active") ?: false
            val isServiceArchived = serviceSnap.getBoolean("isArchived") ?: false
            if (!isServiceActive || isServiceArchived) {
                return Result.failure(IllegalStateException("This service is currently inactive and cannot be newly ordered."))
            }
            val trustedPrice = serviceSnap.getLong("price") ?: 0L
            val trustedTitle = serviceSnap.getString("title") ?: "Digital Service"

            val datePart = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
            val randomSuffix = Random.nextInt(1000, 9999)
            val orderNumber = "AOC-$datePart-$randomSuffix"

            // Never mark as verified based on a client-supplied UPI UTR
            val initialPaymentStatus = if (cleanUpi.isNotEmpty()) "pending_verification" else "unpaid"
            val boundedDocs = documentLinks.map { it.trim() }.filter { it.isNotEmpty() }.take(10)

            val payload = mapOf(
                "orderNumber" to orderNumber,
                "userId" to uid,
                "customerUid" to uid,
                "customerName" to cleanName,
                "customerPhone" to cleanPhone,
                "customerEmail" to customerEmail.trim(),
                "sourceChannel" to "android",
                "serviceId" to serviceId,
                "serviceTitleSnapshot" to trustedTitle,
                "priceSnapshot" to trustedPrice,
                "applicantDetailsSummary" to cleanSummary,
                "documentLinks" to boundedDocs,
                "upiReference" to cleanUpi,
                "paymentStatus" to initialPaymentStatus,
                "status" to "pending",
                "adminNotes" to "",
                "deliveryFileUrl" to "",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            orderRef.set(payload).await()

            val createdOrder = ServiceOrder(
                id = orderDocId,
                orderNumber = orderNumber,
                userId = uid,
                customerUid = uid,
                customerName = cleanName,
                customerPhone = cleanPhone,
                customerEmail = customerEmail.trim(),
                sourceChannel = "android",
                serviceId = serviceId,
                serviceTitleSnapshot = trustedTitle,
                priceSnapshot = trustedPrice,
                applicantDetailsSummary = cleanSummary,
                documentLinks = boundedDocs,
                upiReference = cleanUpi,
                paymentStatus = initialPaymentStatus,
                status = "pending",
                adminNotes = "",
                deliveryFileUrl = "",
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
            Result.success(createdOrder)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, orderRef.path)
            Result.failure(e)
        }
    }

    fun observeCustomerOrders(userId: String): Flow<List<ServiceOrder>> {
        val path = "orders"
        return db.collection(path)
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(
                        ServiceOrder::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }.sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, path)
                }
                throw error
            }
    }

    fun observeAllOrdersForAdmin(): Flow<List<ServiceOrder>> {
        val path = "orders"
        return db.collection(path)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(
                        ServiceOrder::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }.sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, path)
                }
                throw error
            }
    }

    suspend fun getCustomerOrders(userId: String): Result<List<ServiceOrder>> {
        val path = "orders"
        return try {
            val snapshot = db.collection(path)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val list = snapshot.documents.mapNotNull { doc ->
                doc.toObject(
                    ServiceOrder::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }.sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            Result.success(list)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            Result.failure(e)
        }
    }

    suspend fun getOrderById(orderId: String): Result<ServiceOrder> {
        val docRef = db.collection("orders").document(orderId)
        return try {
            val snapshot = docRef.get().await()
            val order = snapshot.toObject(
                ServiceOrder::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            ) ?: throw NoSuchElementException("Order $orderId not found")
            Result.success(order)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun submitCustomerUpiReference(orderId: String, upiReference: String): Result<Unit> {
        requireUserId()
        val cleanUpi = upiReference.trim()
        if (cleanUpi.length < 6 || cleanUpi.length > 50) {
            return Result.failure(IllegalArgumentException("UPI reference / UTR must be between 6 and 50 characters."))
        }
        val docRef = db.collection("orders").document(orderId)
        return try {
            docRef.update(
                mapOf(
                    "upiReference" to cleanUpi,
                    "paymentStatus" to "pending_verification",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun cancelPendingOrder(orderId: String): Result<Unit> {
        requireUserId()
        val docRef = db.collection("orders").document(orderId)
        return try {
            docRef.update(
                mapOf(
                    "status" to "cancelled",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    // Admin-only: Update order status, verify UPI payment, add admin notes, or attach final PDF delivery URL
    suspend fun adminUpdateOrder(
        orderId: String,
        status: String,
        paymentStatus: String,
        adminNotes: String,
        deliveryFileUrl: String
    ): Result<Unit> {
        requireUserId()
        val docRef = db.collection("orders").document(orderId)
        return try {
            docRef.update(
                mapOf(
                    "status" to status,
                    "paymentStatus" to paymentStatus,
                    "adminNotes" to adminNotes.trim(),
                    "deliveryFileUrl" to deliveryFileUrl.trim(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    // 3. CUSTOMER PROFILE & ADMIN CHECK
    fun observeUserProfile(userId: String): Flow<UserProfile?> {
        val docRef = db.collection("users").document(userId)
        return docRef.snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(
                        UserProfile::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                } else {
                    null
                }
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.GET, docRef.path)
                }
                throw error
            }
    }

    suspend fun saveUserProfile(
        userId: String,
        name: String,
        email: String,
        phone: String,
        address: String,
        preferredLanguage: String
    ): Result<Unit> {
        val uid = requireUserId()
        require(uid == userId) { "Cannot modify another user's profile." }
        val docRef = db.collection("users").document(uid)
        return try {
            val existing = docRef.get().await()
            val payload = mutableMapOf<String, Any>(
                "userId" to uid,
                "name" to name.trim().ifBlank { "Customer" }.take(100),
                "email" to email.trim().take(150),
                "phone" to phone.trim().take(15),
                "address" to address.trim().take(300),
                "preferredLanguage" to if (preferredLanguage == "hi") "hi" else "en",
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (!existing.exists()) {
                payload["createdAt"] = FieldValue.serverTimestamp()
                docRef.set(payload).await()
            } else {
                docRef.update(
                    mapOf(
                        "name" to payload["name"]!!,
                        "email" to payload["email"]!!,
                        "phone" to payload["phone"]!!,
                        "address" to payload["address"]!!,
                        "preferredLanguage" to payload["preferredLanguage"]!!,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun checkIsAdmin(userId: String, email: String?): Boolean {
        if (email != null && email.equals(BOOTSTRAPPED_ADMIN_EMAIL, ignoreCase = true)) {
            return true
        }
        return try {
            val adminDoc = db.collection("admins").document(userId).get().await()
            adminDoc.exists()
        } catch (e: Exception) {
            false
        }
    }
}
