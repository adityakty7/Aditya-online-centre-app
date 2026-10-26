package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.ServiceItem
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdityaRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun customerCannotCreateService_failsWithPermissionDenied() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = AdityaRepository(firestore, auth)

        val svcId = "svc_unauth_${UUID.randomUUID().toString().take(8)}"
        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
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
        assertTrue("Expected failure for non-admin creating service", result.isFailure)
        val fsEx = result.exceptionOrNull()?.unwrapFirestoreException()
        assertNotNull("Expected FirebaseFirestoreException", fsEx)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsEx?.code)
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
        const val ALICE_EMAIL = "alice_customer@example.com"
        const val BOB_EMAIL = "bob_customer@example.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
