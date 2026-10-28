package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.CenterContactConfig
import com.example.data.model.ServiceCategoryItem
import com.example.data.model.ServiceItem
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdityaRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun customerCannotCreateServiceOrCategoryOrSettings_failsWithPermissionDenied() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = AdityaRepository(firestore, auth)

        val svcId = "svc_unauth_${UUID.randomUUID().toString().take(8)}"
        val svcResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveServiceAsAdmin(
                ServiceItem(
                    id = svcId,
                    title = "Unauthorized Service",
                    titleHi = "अवैध सेवा",
                    category = "resume",
                    description = "Should fail because Alice is not an admin.",
                    descriptionHi = "यह असफल होना चाहिए।",
                    price = 50L
                )
            )
        }
        assertTrue("Expected failure for non-admin creating service", svcResult.isFailure)
        assertEquals(
            FirebaseFirestoreException.Code.PERMISSION_DENIED,
            svcResult.exceptionOrNull()?.unwrapFirestoreException()?.code
        )

        val catResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveCategoryAsAdmin(
                ServiceCategoryItem(
                    id = "unauth_cat",
                    key = "unauth_cat",
                    labelEn = "Unauthorized Category",
                    labelHi = "अवैध श्रेणी"
                )
            )
        }
        assertTrue("Expected failure for non-admin creating category", catResult.isFailure)
        assertEquals(
            FirebaseFirestoreException.Code.PERMISSION_DENIED,
            catResult.exceptionOrNull()?.unwrapFirestoreException()?.code
        )

        val settingsResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveBusinessSettingsAsAdmin(
                CenterContactConfig(centerName = "Hacked Center")
            )
        }
        assertTrue("Expected failure for non-admin updating business settings", settingsResult.isFailure)
        assertEquals(
            FirebaseFirestoreException.Code.PERMISSION_DENIED,
            settingsResult.exceptionOrNull()?.unwrapFirestoreException()?.code
        )
    }

    @Test
    fun adminCanManageExtendedServicesCategoriesAndBusinessSettings() = runBlocking {
        signInTestUser(ADMIN_EMAIL)
        val repository = AdityaRepository(firestore, auth)

        // 1. Admin creates a new category
        val catResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveCategoryAsAdmin(
                ServiceCategoryItem(
                    id = "banking_kyc",
                    key = "banking_kyc",
                    labelEn = "Banking & e-KYC",
                    labelHi = "बैंकिंग और ई-केवाईसी",
                    iconKey = "account_balance",
                    sortOrder = 5L,
                    active = true
                )
            )
        }
        assertTrue("Admin category creation should succeed: ${catResult.exceptionOrNull()}", catResult.isSuccess)

        val observedCategories = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeCategories().first { list -> list.any { it.key == "banking_kyc" } }
        }
        assertTrue(observedCategories.any { it.key == "banking_kyc" })

        // 2. Admin creates an extended service with custom category, featured priority, and document checklist
        val svcId = "svc_admin_${UUID.randomUUID().toString().take(8)}"
        val saveSvcResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveServiceAsAdmin(
                ServiceItem(
                    id = svcId,
                    title = "PM Kisan e-KYC & NPCI Link",
                    titleHi = "पीएम किसान ई-केवाईसी",
                    category = "banking_kyc",
                    shortDescription = "Instant biometric e-KYC and Aadhaar bank seeding check.",
                    shortDescriptionHi = "त्वरित बायोमेट्रिक ई-केवाईसी।",
                    description = "Complete PM Kisan Samman Nidhi e-KYC and land seeding verification.",
                    descriptionHi = "पीएम किसान सम्मान निधि ई-केवाईसी और भूमि सत्यापन।",
                    price = 60L,
                    turnaroundTime = "1 Hour",
                    requirements = listOf(
                        "Aadhaar Card [Required] - Clear front & back scan",
                        "Mobile Number [Required] - Aadhaar-linked active mobile",
                        "Bank Passbook [Optional] - First page with IFSC"
                    ),
                    imageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/",
                    iconKey = "fingerprint",
                    active = true,
                    isFeatured = true,
                    featuredOrder = 1L,
                    sortOrder = 1L,
                    isArchived = false
                )
            )
        }
        assertTrue("Admin extended service save should succeed: ${saveSvcResult.exceptionOrNull()}", saveSvcResult.isSuccess)

        // 3. Soft-delete (archive) and restore service
        val createdSvc = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeServices().first { list -> list.any { it.id == svcId } }
        }.first { it.id == svcId }
        assertTrue(createdSvc.isFeatured)
        assertFalse(createdSvc.isArchived)

        val archiveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.archiveOrRestoreServiceAsAdmin(createdSvc, archive = true)
        }
        assertTrue("Admin archive should succeed: ${archiveResult.exceptionOrNull()}", archiveResult.isSuccess)

        val archivedSvc = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeServices().first { list -> list.any { it.id == svcId && it.isArchived } }
        }.first { it.id == svcId }
        assertTrue(archivedSvc.isArchived)
        assertFalse(archivedSvc.active)

        val restoreResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.archiveOrRestoreServiceAsAdmin(archivedSvc, archive = false)
        }
        assertTrue("Admin restore should succeed: ${restoreResult.exceptionOrNull()}", restoreResult.isSuccess)

        // 4. Admin updates shared Business Profile & Contact Settings
        val settingsSaveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveBusinessSettingsAsAdmin(
                CenterContactConfig(
                    centerName = "Aditya Online Center Kateya",
                    ownerName = "Aditya Kumar",
                    whatsappNumber = "9123456789",
                    phoneNumber = "9123456789",
                    email = ADMIN_EMAIL,
                    location = "Main Road, Kateya, Gopalganj, Bihar",
                    workingHours = "Mon - Sat: 8:00 AM - 8:30 PM",
                    contactInstructions = "Send documents on WhatsApp for 1-hour express processing."
                )
            )
        }
        assertTrue("Admin business settings save should succeed: ${settingsSaveResult.exceptionOrNull()}", settingsSaveResult.isSuccess)

        val observedSettings = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeBusinessSettings().first { it?.whatsappNumber == "9123456789" }
        }
        assertNotNull(observedSettings)
        assertEquals("Aditya Online Center Kateya", observedSettings?.centerName)
    }

    @Test
    fun saveAndObserveUserProfile_ownerSucceeds_crossUserFails() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repository = AdityaRepository(firestore, auth)

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveUserProfile(
                userId = aliceUid,
                name = "Aarti Kumari",
                email = ALICE_EMAIL,
                phone = "9876543210",
                address = "Kateya Bazar, Gopalganj",
                preferredLanguage = "hi"
            )
        }
        assertTrue("Owner profile save should succeed: ${saveResult.exceptionOrNull()}", saveResult.isSuccess)

        val emittedProfile = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeUserProfile(aliceUid).first { it != null }
        }
        assertNotNull(emittedProfile)
        assertEquals("Aarti Kumari", emittedProfile?.name)

        // Sign in as Bob and verify Bob cannot read Alice's profile
        signInTestUser(BOB_EMAIL)
        val bobRepo = AdityaRepository(firestore, auth)
        var caughtEx: Throwable? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                bobRepo.observeUserProfile(aliceUid).first()
            }
        } catch (e: Throwable) {
            caughtEx = e
        }
        assertNotNull("Expected exception when Bob observes Alice's profile", caughtEx)
        val fsEx = caughtEx?.unwrapFirestoreException()
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsEx?.code)
    }

    @Test
    fun observeCustomerOrders_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = AdityaRepository(firestore, auth)

        var caughtEx: Throwable? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeCustomerOrders("some_uid").first()
            }
        } catch (e: Throwable) {
            caughtEx = e
        }
        assertNotNull("Expected exception for unauthenticated order listener", caughtEx)
        val fsEx = caughtEx?.unwrapFirestoreException()
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsEx?.code)
    }

    private companion object {
        const val ADMIN_EMAIL = "adityakty77@gmail.com"
        const val ALICE_EMAIL = "alice_customer@example.com"
        const val BOB_EMAIL = "bob_customer@example.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
