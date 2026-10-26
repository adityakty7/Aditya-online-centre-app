package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.BOOTSTRAPPED_ADMIN_EMAIL
import com.example.data.model.DefaultCatalogueSeed
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
import com.google.firebase.firestore.SetOptions
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

    // 1. REALTIME SERVICE CATALOGUE (Shared across Website & Android)
    fun observeServices(): Flow<List<ServiceItem>> {
        val path = "services"
        return db.collection(path)
            .whereEqualTo("tenantId", TENANT_ID_ADITYA)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(
                        ServiceItem::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }.sortedWith(compareByDescending<ServiceItem> { it.active }.thenBy { it.title })
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
            val item = snapshot.toObject(
                ServiceItem::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            ) ?: throw NoSuchElementException("Service $serviceId not found")
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
        val docId = service.id.ifBlank {
            "svc_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        }
        val docRef = db.collection("services").document(docId)
        return try {
            val existingSnap = docRef.get().await()
            val payload = mutableMapOf<String, Any>(
                "tenantId" to TENANT_ID_ADITYA,
                "title" to service.title.trim(),
                "titleHi" to service.titleHi.trim().ifBlank { service.title.trim() },
                "category" to service.category,
                "description" to service.description.trim(),
                "descriptionHi" to service.descriptionHi.trim().ifBlank { service.description.trim() },
                "price" to service.price,
                "turnaroundTime" to service.turnaroundTime.trim().ifBlank { "2 - 4 Hours" },
                "requirements" to service.requirements.take(10),
                "imageUrl" to service.imageUrl.trim(),
                "active" to service.active,
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

    suspend fun seedDefaultServicesAsAdmin(): Result<Int> {
        requireUserId()
        var seededCount = 0
        return try {
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
            if (!isServiceActive) {
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
