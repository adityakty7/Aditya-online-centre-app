package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CenterContactConfig
import com.example.data.model.DefaultCatalogueSeed
import com.example.data.model.DefaultCategorySeed
import com.example.data.model.DocumentLibraryCatalog
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceCategoryItem
import com.example.data.model.ServiceItem
import com.example.data.model.ServiceOrder
import com.example.data.model.UserProfile
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminDashboardTab
import com.example.ui.screens.HomeCatalogueScreen
import com.example.ui.screens.MyOrdersScreen
import com.example.ui.screens.ProfileAndHelpScreen
import com.example.ui.theme.AdityaOnlineTheme
import com.example.viewmodel.UiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w360dp-h800dp-mdpi")
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verifyAppIdentityAndCatalogueOnNarrow320dpScreen() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("Aditya Online", context.getString(R.string.app_name))

        var selectedService: ServiceItem? = null
        composeTestRule.setContent {
            AdityaOnlineTheme(darkTheme = true) {
                HomeCatalogueScreen(
                    servicesState = UiState.Success(DefaultCatalogueSeed.services),
                    searchQuery = "",
                    selectedCategory = ServiceCategory.ALL,
                    isHindi = false,
                    isAdmin = false,
                    onSearchQueryChange = {},
                    onSelectCategory = {},
                    onSelectService = { selectedService = it },
                    onSeedCatalogueAsAdmin = {},
                    onSaveServiceAsAdmin = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("home_catalogue_list").assertIsDisplayed()
        composeTestRule.onNodeWithTag("search_services_input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("featured_services_row").assertIsDisplayed()

        val firstService = DefaultCatalogueSeed.services.first()
        composeTestRule.onNodeWithTag("featured_card_${firstService.id}").performClick()
        assertNotNull(selectedService)
        assertEquals(firstService.id, selectedService?.id)
    }

    @Test
    fun verifyAdminToolsAndHindiLanguageSwitch() {
        var openedAdminDashboard = false
        var seededCatalogue = false
        composeTestRule.setContent {
            AdityaOnlineTheme(darkTheme = true) {
                HomeCatalogueScreen(
                    servicesState = UiState.Success(DefaultCatalogueSeed.services),
                    searchQuery = "",
                    selectedCategory = ServiceCategory.ALL,
                    isHindi = true,
                    isAdmin = true,
                    onSearchQueryChange = {},
                    onSelectCategory = {},
                    onSelectService = {},
                    onSeedCatalogueAsAdmin = { seededCatalogue = true },
                    onSaveServiceAsAdmin = {},
                    onOpenAdminDashboard = { openedAdminDashboard = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("admin_tools_toggle_bar").assertIsDisplayed()
        composeTestRule.onNodeWithTag("open_admin_dashboard_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("open_admin_dashboard_button").performClick()
        assertTrue(openedAdminDashboard)

        composeTestRule.onNodeWithTag("admin_sync_catalogue_button").performClick()
        assertTrue(seededCatalogue)
    }

    @Test
    fun verifyAdvancedAdminDashboard_addServiceDocumentLibraryReorderAndContactManager() {
        // Verify 25 standard document types in DocumentLibraryCatalog
        assertEquals(25, DocumentLibraryCatalog.defaultDocuments.size)

        var publishedService: ServiceItem? = null
        var archivedPair: Pair<ServiceItem, Boolean>? = null
        var reorderedList: List<ServiceItem>? = null
        var savedCategory: ServiceCategoryItem? = null
        var savedContact: CenterContactConfig? = null

        composeTestRule.setContent {
            AdityaOnlineTheme(darkTheme = true) {
                AdminDashboardScreen(
                    services = DefaultCatalogueSeed.services,
                    categories = DefaultCategorySeed.categories,
                    orders = emptyList(),
                    contactConfig = CenterContactConfig(),
                    isHindi = false,
                    isAdmin = true,
                    initialTab = AdminDashboardTab.OVERVIEW,
                    onBackToHome = {},
                    onSaveService = { publishedService = it },
                    onArchiveOrRestoreService = { svc, archive -> archivedPair = svc to archive },
                    onPermanentlyDeleteService = {},
                    onPublishServiceOrder = { reorderedList = it },
                    onSaveCategory = { savedCategory = it },
                    onReorderCategories = {},
                    onDeleteCategoryWithReassignment = { _, _ -> },
                    onSaveContactConfig = { savedContact = it },
                    onSeedDefaultCatalogue = {},
                    onOpenOrderDetail = {},
                    onAdminQuickUpdateOrder = { _, _, _, _, _ -> }
                )
            }
        }

        // 1. Overview Tab is displayed
        composeTestRule.onNodeWithTag("admin_dashboard_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("overview_add_service_btn").performClick()

        // 2. Advanced Add Service Tab with 25-item Document Library
        composeTestRule.onNodeWithTag("adv_service_title_input").performTextInput("Ayushman Golden Card Express")
        composeTestRule.onNodeWithTag("adv_service_full_desc_input").performTextInput("PMJAY Ayushman Bharat Health Card e-KYC and PVC print.")
        composeTestRule.onNodeWithTag("adv_service_price_input").performTextReplacement("70")

        // Scroll to Document Library and add a custom document
        composeTestRule.onNodeWithTag("admin_advanced_add_edit_list")
            .performScrollToNode(hasTestTag("admin_add_custom_doc_input"))
        composeTestRule.onNodeWithTag("admin_add_custom_doc_input").performTextInput("Family Samagra ID")
        composeTestRule.onNodeWithTag("admin_add_custom_doc_btn").performClick()

        // Scroll to Publish button and submit
        composeTestRule.onNodeWithTag("admin_advanced_add_edit_list")
            .performScrollToNode(hasTestTag("adv_publish_service_btn"))
        composeTestRule.onNodeWithTag("adv_publish_service_btn").performClick()

        assertNotNull(publishedService)
        assertEquals("Ayushman Golden Card Express", publishedService?.title)
        assertEquals(70L, publishedService?.price)
        assertTrue(publishedService!!.requirements.any { it.contains("Family Samagra ID") })
        // Verify sensitive documents (like Aadhaar Card) were NOT auto-selected without admin checkbox selection
        assertTrue(publishedService!!.requirements.none { it.contains("Aadhaar Card") })

        // 3. After publishing, screen switches to Manage Services Tab — test Soft-Delete (Archive)
        val firstSvc = DefaultCatalogueSeed.services.first()
        composeTestRule.onNodeWithTag("admin_archive_btn_${firstSvc.id}").performClick()
        composeTestRule.onNodeWithTag("confirm_archive_service_btn").performClick()
        assertNotNull(archivedPair)
        assertEquals(firstSvc.id, archivedPair?.first?.id)
        assertEquals(true, archivedPair?.second)

        // 4. Switch to Display Order Tab and publish service order
        composeTestRule.onNodeWithTag("admin_tab_display_order").performClick()
        composeTestRule.onNodeWithTag("admin_publish_order_btn").performClick()
        assertNotNull(reorderedList)
        assertEquals(DefaultCatalogueSeed.services.size, reorderedList?.size)

        // 5. Switch to Categories Tab and create a new category
        composeTestRule.onNodeWithTag("admin_tab_categories").performClick()
        composeTestRule.onNodeWithTag("cat_manager_name_en_input").performTextInput("Rail & Travel Booking")
        composeTestRule.onNodeWithTag("cat_manager_save_btn").performClick()
        assertNotNull(savedCategory)
        assertEquals("rail_travel_booking", savedCategory?.key)

        // 6. Switch to Contact & Social Tab and publish updated WhatsApp & business settings
        composeTestRule.onNodeWithTag("admin_tab_contact_social").performClick()
        composeTestRule.onNodeWithTag("admin_contact_whatsapp_input").performTextReplacement("9988776655")
        composeTestRule.onNodeWithTag("admin_contact_social_list")
            .performScrollToNode(hasTestTag("admin_publish_contact_settings_btn"))
        composeTestRule.onNodeWithTag("admin_publish_contact_settings_btn").performClick()
        assertNotNull(savedContact)
        assertEquals("9988776655", savedContact?.whatsappNumber)
    }

    @Test
    fun verifyMyOrdersScreen() {
        val sampleOrder = ServiceOrder(
            id = "ord_test_1",
            orderNumber = "AOC-2026-9001",
            userId = "cust_1",
            customerUid = "cust_1",
            customerName = "Aditya Kumar",
            customerPhone = "9876543210",
            customerEmail = "adityakty77@gmail.com",
            sourceChannel = "android",
            serviceId = "svc_resume_pro",
            serviceTitleSnapshot = "Professional Resume & CV Design",
            priceSnapshot = 100L,
            applicantDetailsSummary = "B.Sc Computer Science",
            paymentStatus = "pending_verification",
            status = "processing"
        )

        var clickedOrder: ServiceOrder? = null

        composeTestRule.setContent {
            AdityaOnlineTheme(darkTheme = true) {
                MyOrdersScreen(
                    ordersState = UiState.Success(listOf(sampleOrder)),
                    selectedStatusFilter = "all",
                    isHindi = false,
                    onSelectStatusFilter = {},
                    onSelectOrder = { clickedOrder = it },
                    onBrowseServices = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("order_card_ord_test_1").assertIsDisplayed()
        composeTestRule.onNodeWithText("2. In Progress").assertIsDisplayed()
        composeTestRule.onNodeWithTag("order_card_ord_test_1").performClick()
        assertEquals("AOC-2026-9001", clickedOrder?.orderNumber)
    }

    @Test
    fun verifyProfileAndHelpScreen() {
        var profileSaved = false

        composeTestRule.setContent {
            AdityaOnlineTheme(darkTheme = true) {
                ProfileAndHelpScreen(
                    userProfile = UserProfile(
                        id = "cust_1",
                        userId = "cust_1",
                        name = "Aditya Kumar",
                        email = "adityakty77@gmail.com",
                        phone = "9876543210",
                        address = "Kateya, Gopalganj, Bihar"
                    ),
                    userEmail = "adityakty77@gmail.com",
                    userDisplayName = "Aditya Kumar",
                    isHindi = false,
                    isDarkTheme = true,
                    isAdmin = true,
                    onToggleLanguage = {},
                    onToggleTheme = {},
                    onSaveProfile = { _, _, _, _ -> profileSaved = true },
                    onSignOut = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("save_profile_button")
            .performScrollTo()
            .performClick()
        assertTrue(profileSaved)
    }

    @Test
    fun verifyConnectWithMeAtBottomAndWhatsAppOrderHelper() {
        val firstService = DefaultCatalogueSeed.services.first()
        val contactConfig = CenterContactConfig()
        val waUrl = com.example.data.model.WhatsAppOrderHelper.buildServiceOrderWhatsAppUrl(
            contactConfig = contactConfig,
            service = firstService,
            customerName = "Aditya Kumar",
            customerPhone = "9876543210",
            address = "Kateya, Gopalganj, Bihar",
            serviceDetails = "Need urgent resume in PDF format",
            orderNumber = "AOC-2026-1001",
            isHindi = false
        )
        assertTrue(waUrl.startsWith("https://wa.me/919876543210?text="))
        assertTrue(waUrl.contains("AOC-2026-1001"))

        composeTestRule.setContent {
            AdityaOnlineTheme(darkTheme = true) {
                HomeCatalogueScreen(
                    servicesState = UiState.Success(DefaultCatalogueSeed.services),
                    searchQuery = "",
                    selectedCategory = ServiceCategory.ALL,
                    isHindi = false,
                    isAdmin = false,
                    onSearchQueryChange = {},
                    onSelectCategory = {},
                    onSelectService = {},
                    onSeedCatalogueAsAdmin = {},
                    onSaveServiceAsAdmin = {}
                )
            }
        }

        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()

        // Verify Live Shop Open/Closed & Notice Ticker at top
        composeTestRule.onNodeWithTag("live_shop_notice_ticker_card").assertIsDisplayed()

        // Scroll to Sarkari Job/Admit Card/Result Board
        composeTestRule.onNodeWithTag("home_catalogue_list")
            .performScrollToNode(hasTestTag("sarkari_job_admit_result_board"))
        composeTestRule.onNodeWithTag("sarkari_job_admit_result_board").assertIsDisplayed()

        // Scroll to Smart Cyber Tools & Gov Status Hub
        composeTestRule.onNodeWithTag("home_catalogue_list")
            .performScrollToNode(hasTestTag("cyber_tools_and_status_hub_card"))
        composeTestRule.onNodeWithTag("cyber_tools_and_status_hub_card").assertIsDisplayed()

        // Scroll to the bottom of the Home screen where ConnectWithMeCard resides
        composeTestRule.onNodeWithTag("home_catalogue_list")
            .performScrollToNode(hasTestTag("connect_with_me_card"))

        composeTestRule.onNodeWithTag("connect_with_me_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("connect_whatsapp_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("connect_call_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("connect_email_button").assertIsDisplayed()

        // Verify PromoCouponHelper and OfficialReceiptPdfGenerator
        val (discount, label) = com.example.ui.components.PromoCouponHelper.evaluateDiscount("STUDENT20", 100L)
        assertEquals(20L, discount)
        assertNotNull(label)

        val context = ApplicationProvider.getApplicationContext<Context>()
        val pdfFile = com.example.ui.components.OfficialReceiptPdfGenerator.generateReceiptPdfFile(
            context = context,
            order = ServiceOrder(
                orderNumber = "AOC-2026-9901",
                customerName = "Aditya Kumar",
                customerPhone = "9876543210",
                serviceTitleSnapshot = "Bihar Police Online Form",
                priceSnapshot = 80L,
                paymentStatus = "verified",
                status = "completed"
            )
        )
        assertNotNull(pdfFile)
        assertTrue(pdfFile!!.exists())
    }

    @Test
    fun verifyGoogleLoginAndMobileOtpLoginOnAuthScreen() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        com.example.auth.AuthSessionStore.clearSession(context)

        var customerAuthenticated = false
        var adminPanelAutoOpened = false

        composeTestRule.setContent {
            AdityaOnlineTheme(darkTheme = true) {
                com.example.ui.screens.AuthScreen(
                    isHindi = false,
                    onToggleLanguage = {},
                    onAuthSuccess = { customerAuthenticated = true },
                    onAdminAuthSuccess = { adminPanelAutoOpened = true }
                )
            }
        }

        // Verify clean Login with Google and Mobile Number options are active
        composeTestRule.onNodeWithTag("auth_tab_google").assertIsDisplayed()
        composeTestRule.onNodeWithTag("auth_tab_mobile").assertIsDisplayed()
        composeTestRule.onNodeWithTag("google_sign_in_button").assertIsDisplayed()

        // Test Mobile Number (+91) OTP Login Flow
        composeTestRule.onNodeWithTag("mobile_login_name_input")
            .performScrollTo()
            .performTextInput("Ramesh Kumar")
        composeTestRule.onNodeWithTag("mobile_login_phone_input")
            .performScrollTo()
            .performTextInput("9876543210")
        composeTestRule.onNodeWithTag("send_otp_button")
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()

        // Enter 6-digit OTP and verify login
        composeTestRule.onNodeWithTag("mobile_otp_input")
            .performScrollTo()
            .assertIsDisplayed()
            .performTextInput("123456")
        composeTestRule.onNodeWithTag("verify_otp_button")
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()
        assertTrue(customerAuthenticated)

        // Verify Google Admin Session activation auto-opens Admin Panel for owner email
        com.example.auth.activateGoogleAdminSession(
            context = context,
            email = "adityakty77@gmail.com",
            displayName = "Aditya Kumar (Admin)",
            onSuccess = { adminPanelAutoOpened = true }
        )
        assertTrue(adminPanelAutoOpened)
        assertEquals(true, com.example.auth.AuthSessionStore.sessionFlow.value?.isAdminGoogle)
    }
}

