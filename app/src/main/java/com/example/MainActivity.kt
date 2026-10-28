package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.runtime.LaunchedEffect
import com.example.auth.AuthSessionStore
import com.example.auth.activateGoogleAdminSession
import com.example.auth.authStateFlow
import com.example.auth.onGoogleSignInClicked
import com.example.auth.signOutUser
import com.example.data.model.BOOTSTRAPPED_ADMIN_EMAIL
import com.example.data.model.DefaultCatalogueSeed
import com.example.data.model.ServiceItem
import com.example.data.model.ServiceOrder
import com.example.data.repository.AdityaRepository
import com.example.ui.components.AdityaBrandEmblem
import com.example.ui.components.AmbientCyberGlassBackground
import com.example.ui.components.Glowing3DIconBadge
import com.example.ui.components.PulsingDotIndicator
import com.example.ui.components.QuickMenuDialogSection
import com.example.ui.components.QuickWhatsAppOrderDialog
import com.example.ui.components.TopBarQuickMenuModalDialog
import com.example.ui.components.WhatsAppBrandGreen
import com.example.ui.components.bounceClick
import com.example.ui.components.glassCardContainerColor
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminDashboardTab
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeCatalogueScreen
import com.example.ui.screens.MyOrdersScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.OrderApplicationScreen
import com.example.ui.screens.OrderConfirmationScreen
import com.example.ui.screens.OrderDetailScreen
import com.example.ui.screens.ProfileAndHelpScreen
import com.example.ui.screens.ServiceDetailScreen
import com.example.ui.theme.AdityaOnlineTheme
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.StatusVerifiedGreen
import com.example.viewmodel.AdityaViewModel
import com.example.viewmodel.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(true) }
            var isHindi by rememberSaveable { mutableStateOf(false) }

            AdityaOnlineTheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    isHindi = isHindi,
                    isDarkTheme = isDarkTheme,
                    onToggleLanguage = { isHindi = !isHindi },
                    onToggleTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }
}

private sealed interface ScreenDestination {
    val navRank: Int

    data object Home : ScreenDestination {
        override val navRank: Int = 10
    }
    data class ServiceDetail(val service: ServiceItem) : ScreenDestination {
        override val navRank: Int = 11
    }
    data class OrderForm(val service: ServiceItem) : ScreenDestination {
        override val navRank: Int = 12
    }
    data class OrderConfirmation(val order: ServiceOrder) : ScreenDestination {
        override val navRank: Int = 13
    }
    data object MyOrders : ScreenDestination {
        override val navRank: Int = 20
    }
    data class OrderDetail(val orderId: String, val initialOrder: ServiceOrder) : ScreenDestination {
        override val navRank: Int = 21
    }
    data object Profile : ScreenDestination {
        override val navRank: Int = 30
    }
    data class AdminDashboard(
        val initialTab: AdminDashboardTab = AdminDashboardTab.OVERVIEW,
        val initialEditingService: ServiceItem? = null
    ) : ScreenDestination {
        override val navRank: Int = 40
    }
}

@Composable
fun AppNavigation(
    isHindi: Boolean,
    isDarkTheme: Boolean,
    onToggleLanguage: () -> Unit,
    onToggleTheme: () -> Unit,
    auth: FirebaseAuth = Firebase.auth
) {
    val context = LocalContext.current
    LaunchedEffect(context) {
        AuthSessionStore.initialize(context)
    }
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val activeSession by AuthSessionStore.sessionFlow.collectAsStateWithLifecycle()

    // Activate Login with Google & Login with Mobile Number gate
    var bypassLoginGate by rememberSaveable { mutableStateOf(false) }
    var hasSeenOnboarding by rememberSaveable { mutableStateOf(true) }
    var autoOpenAdminPanelOnEnter by rememberSaveable { mutableStateOf(false) }

    val user = currentUser
    val session = activeSession
    val isAuthenticated = user != null || session != null || bypassLoginGate

    val rootStage = when {
        isAuthenticated -> 2
        !hasSeenOnboarding -> 0
        else -> 1
    }

    AnimatedContent(
        targetState = rootStage,
        transitionSpec = {
            val forward = targetState >= initialState
            (slideInHorizontally(
                animationSpec = tween(320, easing = FastOutSlowInEasing),
                initialOffsetX = { fullWidth -> if (forward) fullWidth / 4 else -fullWidth / 4 }
            ) + fadeIn(animationSpec = tween(280)) + scaleIn(
                initialScale = 0.96f,
                animationSpec = tween(320, easing = FastOutSlowInEasing)
            )).togetherWith(
                slideOutHorizontally(
                    animationSpec = tween(260, easing = FastOutSlowInEasing),
                    targetOffsetX = { fullWidth -> if (forward) -fullWidth / 5 else fullWidth / 5 }
                ) + fadeOut(animationSpec = tween(220))
            )
        },
        label = "root_portal_transition"
    ) { stage ->
        when (stage) {
            0 -> OnboardingScreen(
                isHindi = isHindi,
                onToggleLanguage = onToggleLanguage,
                onFinishOnboarding = {
                    hasSeenOnboarding = true
                }
            )
            1 -> AuthScreen(
                isHindi = isHindi,
                onToggleLanguage = onToggleLanguage,
                onAuthSuccess = {
                    bypassLoginGate = true
                },
                onAdminAuthSuccess = {
                    autoOpenAdminPanelOnEnter = true
                    bypassLoginGate = true
                }
            )
            else -> {
                val resolvedUid = user?.uid ?: session?.uid ?: "customer_session"
                val resolvedEmail = user?.email ?: session?.email
                val resolvedName = user?.displayName ?: session?.displayName ?: ""
                val resolvedPhone = user?.phoneNumber ?: session?.phoneNumber
                val isGoogleAdminUser = session?.isAdminGoogle == true ||
                    resolvedEmail?.equals(BOOTSTRAPPED_ADMIN_EMAIL, ignoreCase = true) == true

                AuthenticatedPortalScreen(
                    currentUserId = resolvedUid,
                    currentUserEmail = resolvedEmail,
                    currentUserDisplayName = resolvedName,
                    currentUserPhone = resolvedPhone,
                    initialIsAdmin = isGoogleAdminUser,
                    autoOpenAdminPanel = autoOpenAdminPanelOnEnter,
                    onConsumeAutoOpenAdminPanel = {
                        autoOpenAdminPanelOnEnter = false
                    },
                    isHindi = isHindi,
                    isDarkTheme = isDarkTheme,
                    onToggleLanguage = onToggleLanguage,
                    onToggleTheme = onToggleTheme,
                    onShowSignInScreen = {
                        AuthSessionStore.clearSession(context)
                        bypassLoginGate = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticatedPortalScreen(
    currentUserId: String,
    currentUserEmail: String?,
    currentUserDisplayName: String?,
    currentUserPhone: String? = null,
    initialIsAdmin: Boolean = false,
    autoOpenAdminPanel: Boolean = false,
    onConsumeAutoOpenAdminPanel: () -> Unit = {},
    isHindi: Boolean,
    isDarkTheme: Boolean,
    onToggleLanguage: () -> Unit,
    onToggleTheme: () -> Unit,
    onShowSignInScreen: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val viewModel: AdityaViewModel = viewModel(
        key = "${currentUserId}_${initialIsAdmin}",
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                AdityaViewModel(
                    repository = AdityaRepository(db, Firebase.auth),
                    currentUserId = currentUserId,
                    currentUserEmail = currentUserEmail,
                    currentUserDisplayName = currentUserDisplayName,
                    currentUserPhone = currentUserPhone,
                    initialIsAdmin = initialIsAdmin
                )
            }
        }
    )

    val servicesState by viewModel.servicesState.collectAsStateWithLifecycle()
    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()
    val ordersState by viewModel.ordersState.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfileState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedCategoryKey by viewModel.selectedCategoryKey.collectAsStateWithLifecycle()
    val selectedOrderStatusFilter by viewModel.selectedOrderStatusFilter.collectAsStateWithLifecycle()
    val isSubmittingOrder by viewModel.isSubmittingOrder.collectAsStateWithLifecycle()
    val statusBannerMessage by viewModel.statusBannerMessage.collectAsStateWithLifecycle()
    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()
    val contactConfig by viewModel.centerContactConfig.collectAsStateWithLifecycle()

    var currentDestination by remember {
        mutableStateOf<ScreenDestination>(
            if (autoOpenAdminPanel) ScreenDestination.AdminDashboard(AdminDashboardTab.OVERVIEW)
            else ScreenDestination.Home
        )
    }
    var isTopMoreMenuExpanded by remember { mutableStateOf(false) }
    var activeQuickMenuSection by remember { mutableStateOf<QuickMenuDialogSection?>(null) }
    var quickWhatsAppServiceFromMenu by remember { mutableStateOf<ServiceItem?>(null) }
    var openAddServiceTrigger by remember { mutableStateOf(false) }

    val triggerGoogleAdminSignInAndOpenPanel: () -> Unit = {
        onGoogleSignInClicked(
            context = context,
            credentialManager = credentialManager,
            requestAdminActivation = false,
            onAuthSuccess = { isAdminLogin ->
                if (isAdminLogin) {
                    viewModel.activateAdminAccess(currentUserEmail ?: BOOTSTRAPPED_ADMIN_EMAIL)
                    currentDestination = ScreenDestination.AdminDashboard(AdminDashboardTab.OVERVIEW)
                }
            },
            onAuthError = {},
            scope = coroutineScope,
            onAuthCancelled = {}
        )
    }

    LaunchedEffect(autoOpenAdminPanel) {
        if (autoOpenAdminPanel) {
            viewModel.activateAdminAccess(currentUserEmail ?: BOOTSTRAPPED_ADMIN_EMAIL)
            currentDestination = ScreenDestination.AdminDashboard(AdminDashboardTab.OVERVIEW)
            onConsumeAutoOpenAdminPanel()
        }
    }

    val availableServices = remember(servicesState) {
        (servicesState as? UiState.Success)?.data?.ifEmpty { DefaultCatalogueSeed.services }
            ?: DefaultCatalogueSeed.services
    }
    val availableOrders = remember(ordersState) {
        (ordersState as? UiState.Success)?.data ?: emptyList()
    }
    val cloudDocCount = (servicesState as? UiState.Success)?.data?.size ?: 0

    val activeTab = when (currentDestination) {
        is ScreenDestination.Home,
        is ScreenDestination.ServiceDetail,
        is ScreenDestination.OrderForm,
        is ScreenDestination.OrderConfirmation,
        is ScreenDestination.AdminDashboard -> 0
        is ScreenDestination.MyOrders,
        is ScreenDestination.OrderDetail -> 1
        is ScreenDestination.Profile -> 2
    }

    if (currentDestination !is ScreenDestination.Home) {
        BackHandler {
            currentDestination = when (val dest = currentDestination) {
                is ScreenDestination.OrderForm -> ScreenDestination.ServiceDetail(dest.service)
                is ScreenDestination.OrderDetail -> ScreenDestination.MyOrders
                else -> ScreenDestination.Home
            }
        }
    }

    val topBarAnim = rememberInfiniteTransition(label = "top_bar_glow_line")
    val topBarSweep by topBarAnim.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "top_bar_sweep"
    )

    AmbientCyberGlassBackground(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideScreen = maxWidth >= 640.dp

            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                containerColor = Color.Transparent,
                topBar = {
                    Column {
                        TopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    AdityaBrandEmblem(size = 40.dp, cornerRadius = 12.dp)
                                    Column(modifier = Modifier.weight(1f, fill = false)) {
                                        Text(
                                            text = if (isHindi) "आदित्य ऑनलाइन सेंटर" else "Aditya Online Center",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            PulsingDotIndicator(color = ElectricCyan, dotSize = 6.dp)
                                            Text(
                                                text = if (isHindi) "कटेया, गोपालगंज • बिहार" else "Kateya, Gopalganj • Bihar",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = ElectricCyan,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            },
                            actions = {
                                OutlinedButton(
                                    onClick = onToggleLanguage,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .padding(end = 4.dp)
                                        .minimumInteractiveComponentSize()
                                        .bounceClick()
                                        .testTag("top_bar_language_toggle")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = null,
                                        tint = NeonOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHindi) "EN" else "हिंदी",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }

                                // Top-Right Three-Dot Icon Menu (Contact Me, Social Media, Order Services, Admin Panel)
                                Box {
                                    IconButton(
                                        onClick = { isTopMoreMenuExpanded = true },
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .testTag("top_bar_more_menu_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = "Quick Menu",
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = isTopMoreMenuExpanded,
                                        onDismissRequest = { isTopMoreMenuExpanded = false },
                                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                                        )
                                    ) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = if (isHindi) "मुझसे संपर्क करें (Contact Me)" else "Contact Me",
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                                    contentDescription = null,
                                                    tint = WhatsAppBrandGreen,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            },
                                            onClick = {
                                                isTopMoreMenuExpanded = false
                                                activeQuickMenuSection = QuickMenuDialogSection.CONTACT_ME
                                            },
                                            modifier = Modifier.testTag("menu_item_contact_me")
                                        )

                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = if (isHindi) "सोशल मीडिया (Social Media)" else "Social Media",
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.PhotoCamera,
                                                    contentDescription = null,
                                                    tint = Color(0xFFE1306C),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            },
                                            onClick = {
                                                isTopMoreMenuExpanded = false
                                                activeQuickMenuSection = QuickMenuDialogSection.SOCIAL_MEDIA
                                            },
                                            modifier = Modifier.testTag("menu_item_social_media")
                                        )

                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = if (isHindi) "सेवा ऑर्डर करें (Order Services)" else "Order Services",
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.ShoppingBag,
                                                    contentDescription = null,
                                                    tint = NeonOrange,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            },
                                            onClick = {
                                                isTopMoreMenuExpanded = false
                                                activeQuickMenuSection = QuickMenuDialogSection.ORDER_SERVICES
                                            },
                                            modifier = Modifier.testTag("menu_item_order_services")
                                        )

                                        if (isAdmin) {
                                            HorizontalDivider(
                                                color = Color.White.copy(alpha = 0.14f),
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )

                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = if (isHindi) "एडमिन पैनल (Admin Panel)" else "Admin Panel",
                                                        fontWeight = FontWeight.Bold,
                                                        color = ElectricCyan
                                                    )
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Default.AdminPanelSettings,
                                                        contentDescription = null,
                                                        tint = ElectricCyan,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                },
                                                onClick = {
                                                    isTopMoreMenuExpanded = false
                                                    activeQuickMenuSection = QuickMenuDialogSection.ADMIN_PANEL
                                                },
                                                modifier = Modifier.testTag("menu_item_admin_panel")
                                            )
                                        }
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = glassCardContainerColor(alpha = 0.78f)
                            )
                        )
                        // Animated Shimmering Neon Top Bar Accent Line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            ElectricCyan,
                                            NeonOrange,
                                            RoyalBlueAccent,
                                            ElectricCyan
                                        ),
                                        start = Offset(topBarSweep * 500f, 0f),
                                        end = Offset(500f + topBarSweep * 500f, 0f)
                                    )
                                )
                        )
                    }
                },
                bottomBar = {
                    if (!isWideScreen) {
                        Column {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = ElectricCyan.copy(alpha = 0.25f)
                            )
                            NavigationBar(
                                containerColor = glassCardContainerColor(alpha = 0.92f),
                                tonalElevation = 0.dp
                            ) {
                                NavigationBarItem(
                                    selected = activeTab == 0,
                                    onClick = { currentDestination = ScreenDestination.Home },
                                    icon = {
                                        Icon(
                                            imageVector = if (activeTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                                            contentDescription = "Services"
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = if (isHindi) "सेवाएं" else "Services",
                                            fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ElectricCyan,
                                        selectedTextColor = ElectricCyan,
                                        indicatorColor = RoyalBlueAccent.copy(alpha = 0.25f),
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("nav_tab_home")
                                )
                                NavigationBarItem(
                                    selected = activeTab == 1,
                                    onClick = { currentDestination = ScreenDestination.MyOrders },
                                    icon = {
                                        Icon(
                                            imageVector = if (activeTab == 1) Icons.AutoMirrored.Filled.ReceiptLong else Icons.AutoMirrored.Outlined.ReceiptLong,
                                            contentDescription = "My Orders"
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = if (isHindi) "मेरे ऑर्डर" else "My Orders",
                                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ElectricCyan,
                                        selectedTextColor = ElectricCyan,
                                        indicatorColor = RoyalBlueAccent.copy(alpha = 0.25f),
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("nav_tab_orders")
                                )
                                NavigationBarItem(
                                    selected = activeQuickMenuSection == QuickMenuDialogSection.CONTACT_ME,
                                    onClick = { activeQuickMenuSection = QuickMenuDialogSection.CONTACT_ME },
                                    icon = {
                                        Icon(
                                            imageVector = if (activeQuickMenuSection == QuickMenuDialogSection.CONTACT_ME) {
                                                Icons.AutoMirrored.Filled.Chat
                                            } else {
                                                Icons.AutoMirrored.Outlined.Chat
                                            },
                                            contentDescription = "Support"
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = if (isHindi) "सहायता" else "Support",
                                            fontWeight = if (activeQuickMenuSection == QuickMenuDialogSection.CONTACT_ME) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = NeonOrange,
                                        selectedTextColor = NeonOrange,
                                        indicatorColor = NeonOrange.copy(alpha = 0.20f),
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("nav_tab_support")
                                )
                                NavigationBarItem(
                                    selected = activeTab == 2,
                                    onClick = { currentDestination = ScreenDestination.Profile },
                                    icon = {
                                        Icon(
                                            imageVector = if (activeTab == 2) Icons.Filled.Person else Icons.Outlined.Person,
                                            contentDescription = "Profile and Help"
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = if (isHindi) "प्रोफ़ाइल" else "Profile",
                                            fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ElectricCyan,
                                        selectedTextColor = ElectricCyan,
                                        indicatorColor = RoyalBlueAccent.copy(alpha = 0.25f),
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("nav_tab_profile")
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (isWideScreen) {
                        NavigationRail(
                            containerColor = glassCardContainerColor(alpha = 0.88f),
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            NavigationRailItem(
                                selected = activeTab == 0,
                                onClick = { currentDestination = ScreenDestination.Home },
                                icon = {
                                    Icon(
                                        imageVector = if (activeTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                                        contentDescription = "Services"
                                    )
                                },
                                label = { Text(if (isHindi) "सेवाएं" else "Services") }
                            )
                            NavigationRailItem(
                                selected = activeTab == 1,
                                onClick = { currentDestination = ScreenDestination.MyOrders },
                                icon = {
                                    Icon(
                                        imageVector = if (activeTab == 1) Icons.AutoMirrored.Filled.ReceiptLong else Icons.AutoMirrored.Outlined.ReceiptLong,
                                        contentDescription = "My Orders"
                                    )
                                },
                                label = { Text(if (isHindi) "मेरे ऑर्डर" else "My Orders") }
                            )
                            NavigationRailItem(
                                selected = activeQuickMenuSection == QuickMenuDialogSection.CONTACT_ME,
                                onClick = { activeQuickMenuSection = QuickMenuDialogSection.CONTACT_ME },
                                icon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Chat,
                                        contentDescription = "Support"
                                    )
                                },
                                label = { Text(if (isHindi) "सहायता" else "Support") }
                            )
                            NavigationRailItem(
                                selected = activeTab == 2,
                                onClick = { currentDestination = ScreenDestination.Profile },
                                icon = {
                                    Icon(
                                        imageVector = if (activeTab == 2) Icons.Filled.Person else Icons.Outlined.Person,
                                        contentDescription = "Profile"
                                    )
                                },
                                label = { Text(if (isHindi) "प्रोफ़ाइल" else "Profile") }
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                    ) {
                        AnimatedVisibility(
                            visible = statusBannerMessage != null,
                            enter = expandVertically(animationSpec = tween(250)) + fadeIn(tween(250)),
                            exit = shrinkVertically(animationSpec = tween(200)) + fadeOut(tween(200))
                        ) {
                            Surface(
                                color = glassCardContainerColor(alpha = 0.85f),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.2.dp, NeonOrange.copy(alpha = 0.85f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = statusBannerMessage ?: "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { viewModel.clearBannerMessage() },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Dismiss notification",
                                            tint = NeonOrange
                                        )
                                    }
                                }
                            }
                        }

                        AnimatedContent(
                            targetState = currentDestination,
                            transitionSpec = {
                                val isForward = targetState.navRank >= initialState.navRank
                                val enterSlide = slideInHorizontally(
                                    animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
                                    initialOffsetX = { fullWidth -> if (isForward) fullWidth / 3 else -fullWidth / 3 }
                                ) + fadeIn(
                                    animationSpec = tween(durationMillis = 280)
                                ) + scaleIn(
                                    initialScale = if (isForward) 0.94f else 1.03f,
                                    animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
                                )

                                val exitSlide = slideOutHorizontally(
                                    animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
                                    targetOffsetX = { fullWidth -> if (isForward) -fullWidth / 4 else fullWidth / 4 }
                                ) + fadeOut(
                                    animationSpec = tween(durationMillis = 200)
                                ) + scaleOut(
                                    targetScale = if (isForward) 0.96f else 0.94f,
                                    animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                                )

                                enterSlide.togetherWith(exitSlide).using(SizeTransform(clip = false))
                            },
                            modifier = Modifier.fillMaxSize(),
                            label = "screen_open_close_transition"
                        ) { destination ->
                            when (destination) {
                                is ScreenDestination.Home -> {
                                    HomeCatalogueScreen(
                                        servicesState = servicesState,
                                        searchQuery = searchQuery,
                                        selectedCategory = selectedCategory,
                                        isHindi = isHindi,
                                        isAdmin = isAdmin,
                                        onSearchQueryChange = viewModel::updateSearchQuery,
                                        onSelectCategory = viewModel::selectCategory,
                                        onSelectService = { service ->
                                            currentDestination = ScreenDestination.ServiceDetail(service)
                                        },
                                        onSeedCatalogueAsAdmin = viewModel::seedDefaultServicesAsAdmin,
                                        onSaveServiceAsAdmin = { svc ->
                                            viewModel.saveServiceAsAdmin(svc)
                                        },
                                        contactConfig = contactConfig,
                                        defaultCustomerName = userProfile?.name?.ifBlank { currentUserDisplayName ?: "" }
                                            ?: (currentUserDisplayName ?: ""),
                                        defaultCustomerPhone = userProfile?.phone?.ifBlank { currentUserPhone ?: "" }
                                            ?: (currentUserPhone ?: ""),
                                        defaultCustomerAddress = userProfile?.address ?: "Kateya, Gopalganj, Bihar",
                                        onQuickWhatsAppOrder = { service, customerName, customerPhone, detailsSummary ->
                                            viewModel.submitOrder(
                                                serviceId = service.id,
                                                customerName = customerName,
                                                customerPhone = customerPhone,
                                                applicantDetailsSummary = detailsSummary,
                                                documentLinks = emptyList(),
                                                upiReference = "",
                                                onOrderConfirmed = { confirmedOrder ->
                                                    currentDestination = ScreenDestination.OrderConfirmation(confirmedOrder)
                                                }
                                            )
                                        },
                                        onOpenConnectProfile = {
                                            currentDestination = ScreenDestination.Profile
                                        },
                                        openAddServiceTrigger = openAddServiceTrigger,
                                        onConsumeAddServiceTrigger = {
                                            openAddServiceTrigger = false
                                        },
                                        categories = categoriesState,
                                        selectedCategoryKey = selectedCategoryKey,
                                        onSelectCategoryKey = viewModel::selectCategoryByKey,
                                        onOpenAdminDashboard = { svcToEdit ->
                                            currentDestination = ScreenDestination.AdminDashboard(
                                                initialTab = if (svcToEdit != null) AdminDashboardTab.ADD_EDIT else AdminDashboardTab.OVERVIEW,
                                                initialEditingService = svcToEdit
                                            )
                                        }
                                    )
                                }

                                is ScreenDestination.ServiceDetail -> {
                                    val latestService = (servicesState as? UiState.Success)
                                        ?.data
                                        ?.find { it.id == destination.service.id }
                                        ?: destination.service

                                    ServiceDetailScreen(
                                        service = latestService,
                                        isHindi = isHindi,
                                        onBack = { currentDestination = ScreenDestination.Home },
                                        onStartOrder = { svc ->
                                            currentDestination = ScreenDestination.OrderForm(svc)
                                        },
                                        contactConfig = contactConfig,
                                        defaultCustomerName = userProfile?.name?.ifBlank { currentUserDisplayName ?: "" }
                                            ?: (currentUserDisplayName ?: ""),
                                        defaultCustomerPhone = userProfile?.phone?.ifBlank { currentUserPhone ?: "" }
                                            ?: (currentUserPhone ?: ""),
                                        defaultCustomerAddress = userProfile?.address ?: "Kateya, Gopalganj, Bihar",
                                        onQuickWhatsAppOrder = { customerName, customerPhone, detailsSummary ->
                                            viewModel.submitOrder(
                                                serviceId = latestService.id,
                                                customerName = customerName,
                                                customerPhone = customerPhone,
                                                applicantDetailsSummary = detailsSummary,
                                                documentLinks = emptyList(),
                                                upiReference = "",
                                                onOrderConfirmed = { confirmedOrder ->
                                                    currentDestination = ScreenDestination.OrderConfirmation(confirmedOrder)
                                                }
                                            )
                                        }
                                    )
                                }

                                is ScreenDestination.OrderForm -> {
                                    val latestService = (servicesState as? UiState.Success)
                                        ?.data
                                        ?.find { it.id == destination.service.id }
                                        ?: destination.service

                                    OrderApplicationScreen(
                                        service = latestService,
                                        defaultName = userProfile?.name?.ifBlank { currentUserDisplayName ?: "" }
                                            ?: (currentUserDisplayName ?: ""),
                                        defaultPhone = userProfile?.phone?.ifBlank { currentUserPhone ?: "" }
                                            ?: (currentUserPhone ?: ""),
                                        defaultAddress = userProfile?.address ?: "Kateya, Gopalganj, Bihar",
                                        isHindi = isHindi,
                                        isSubmitting = isSubmittingOrder,
                                        onBack = {
                                            currentDestination = ScreenDestination.ServiceDetail(latestService)
                                        },
                                        onSubmitOrder = { name, phone, summary, docLinks, upiRef ->
                                            viewModel.submitOrder(
                                                serviceId = latestService.id,
                                                customerName = name,
                                                customerPhone = phone,
                                                applicantDetailsSummary = summary,
                                                documentLinks = docLinks,
                                                upiReference = upiRef,
                                                onOrderConfirmed = { confirmedOrder ->
                                                    currentDestination = ScreenDestination.OrderConfirmation(confirmedOrder)
                                                }
                                            )
                                        },
                                        contactConfig = contactConfig
                                    )
                                }

                                is ScreenDestination.OrderConfirmation -> {
                                    OrderConfirmationScreen(
                                        order = destination.order,
                                        isHindi = isHindi,
                                        onTrackOrder = { confirmedOrder ->
                                            currentDestination = ScreenDestination.OrderDetail(
                                                orderId = confirmedOrder.id,
                                                initialOrder = confirmedOrder
                                            )
                                        },
                                        onBackToHome = {
                                            currentDestination = ScreenDestination.Home
                                        },
                                        contactConfig = contactConfig
                                    )
                                }

                                is ScreenDestination.MyOrders -> {
                                    MyOrdersScreen(
                                        ordersState = ordersState,
                                        selectedStatusFilter = selectedOrderStatusFilter,
                                        isHindi = isHindi,
                                        onSelectStatusFilter = viewModel::selectOrderStatusFilter,
                                        onSelectOrder = { order ->
                                            currentDestination = ScreenDestination.OrderDetail(
                                                orderId = order.id,
                                                initialOrder = order
                                            )
                                        },
                                        onBrowseServices = {
                                            currentDestination = ScreenDestination.Home
                                        }
                                    )
                                }

                                is ScreenDestination.OrderDetail -> {
                                    val liveOrder = (ordersState as? UiState.Success)
                                        ?.data
                                        ?.find { it.id == destination.orderId }
                                        ?: destination.initialOrder

                                    OrderDetailScreen(
                                        order = liveOrder,
                                        isHindi = isHindi,
                                        isAdmin = isAdmin,
                                        onBack = { currentDestination = ScreenDestination.MyOrders },
                                        onSubmitUpiReference = viewModel::submitUpiReference,
                                        onCancelOrder = viewModel::cancelOrder,
                                        onAdminUpdateOrder = viewModel::adminUpdateOrder,
                                        contactConfig = contactConfig
                                    )
                                }

                                is ScreenDestination.Profile -> {
                                    ProfileAndHelpScreen(
                                        userProfile = userProfile,
                                        userEmail = currentUserEmail ?: currentUserPhone?.let { "+91 $it" },
                                        userDisplayName = currentUserDisplayName,
                                        isHindi = isHindi,
                                        isDarkTheme = isDarkTheme,
                                        isAdmin = isAdmin,
                                        onToggleLanguage = onToggleLanguage,
                                        onToggleTheme = onToggleTheme,
                                        onSaveProfile = viewModel::saveUserProfile,
                                        onSignOut = {
                                            signOutUser(
                                                context = context,
                                                credentialManager = credentialManager,
                                                onSignOutComplete = {
                                                    onShowSignInScreen()
                                                },
                                                scope = coroutineScope
                                            )
                                        },
                                        contactConfig = contactConfig,
                                        onUpdateContactConfig = viewModel::updateCenterContactConfig,
                                        onOpenAuthOptions = onShowSignInScreen,
                                        onGoogleAdminLoginAndOpenPanel = triggerGoogleAdminSignInAndOpenPanel,
                                        onOpenAdminDashboard = {
                                            currentDestination = ScreenDestination.AdminDashboard(AdminDashboardTab.OVERVIEW)
                                        }
                                    )
                                }

                                is ScreenDestination.AdminDashboard -> {
                                    AdminDashboardScreen(
                                        services = availableServices,
                                        categories = categoriesState,
                                        orders = availableOrders,
                                        contactConfig = contactConfig,
                                        isHindi = isHindi,
                                        isAdmin = isAdmin,
                                        initialTab = destination.initialTab,
                                        initialEditingService = destination.initialEditingService,
                                        onBackToHome = {
                                            currentDestination = ScreenDestination.Home
                                        },
                                        onSaveService = viewModel::saveServiceAsAdmin,
                                        onArchiveOrRestoreService = viewModel::archiveOrRestoreServiceAsAdmin,
                                        onPermanentlyDeleteService = viewModel::permanentlyDeleteServiceAsAdmin,
                                        onPublishServiceOrder = viewModel::reorderServicesAsAdmin,
                                        onSaveCategory = viewModel::saveCategoryAsAdmin,
                                        onReorderCategories = viewModel::reorderCategoriesAsAdmin,
                                        onDeleteCategoryWithReassignment = { cat, targetKey ->
                                            viewModel.deleteCategoryWithReassignmentAsAdmin(cat.key, targetKey)
                                        },
                                        onSaveContactConfig = viewModel::updateCenterContactConfig,
                                        onSeedDefaultCatalogue = viewModel::seedDefaultServicesAsAdmin,
                                        onOpenOrderDetail = { order ->
                                            currentDestination = ScreenDestination.OrderDetail(
                                                orderId = order.id,
                                                initialOrder = order
                                            )
                                        },
                                        onAdminQuickUpdateOrder = viewModel::adminUpdateOrder
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (activeQuickMenuSection != null) {
                TopBarQuickMenuModalDialog(
                    section = activeQuickMenuSection!!,
                    contactConfig = contactConfig,
                    services = availableServices,
                    isHindi = isHindi,
                    isAdmin = isAdmin,
                    cloudDocCount = cloudDocCount,
                    onDismiss = { activeQuickMenuSection = null },
                    onEditContactsInProfile = {
                        currentDestination = ScreenDestination.Profile
                    },
                    onSelectServiceForApply = { svc ->
                        currentDestination = ScreenDestination.ServiceDetail(svc)
                    },
                    onSelectServiceForWhatsApp = { svc ->
                        quickWhatsAppServiceFromMenu = svc
                    },
                    onAddNewServiceAsAdmin = {
                        currentDestination = ScreenDestination.Home
                        openAddServiceTrigger = true
                    },
                    onSyncDefaultCatalogueAsAdmin = {
                        viewModel.seedDefaultServicesAsAdmin()
                    },
                    onOpenOrdersManagement = {
                        currentDestination = ScreenDestination.MyOrders
                    },
                    onOpenAdminDashboard = {
                        viewModel.activateAdminAccess(currentUserEmail ?: BOOTSTRAPPED_ADMIN_EMAIL)
                        currentDestination = ScreenDestination.AdminDashboard()
                    },
                    onAdminGoogleLoginAndOpenDashboard = triggerGoogleAdminSignInAndOpenPanel
                )
            }

            if (quickWhatsAppServiceFromMenu != null) {
                val targetSvc = quickWhatsAppServiceFromMenu!!
                QuickWhatsAppOrderDialog(
                    service = targetSvc,
                    contactConfig = contactConfig,
                    defaultName = userProfile?.name?.ifBlank { currentUserDisplayName ?: "" }
                        ?: (currentUserDisplayName ?: ""),
                    defaultPhone = userProfile?.phone?.ifBlank { currentUserPhone ?: "" }
                        ?: (currentUserPhone ?: ""),
                    defaultAddress = userProfile?.address ?: "Kateya, Gopalganj, Bihar",
                    isHindi = isHindi,
                    onDismiss = { quickWhatsAppServiceFromMenu = null },
                    onOrderCreatedThroughWhatsApp = { customerName, customerPhone, detailsSummary ->
                        viewModel.submitOrder(
                            serviceId = targetSvc.id,
                            customerName = customerName,
                            customerPhone = customerPhone,
                            applicantDetailsSummary = detailsSummary,
                            documentLinks = emptyList(),
                            upiReference = "",
                            onOrderConfirmed = { confirmedOrder ->
                                currentDestination = ScreenDestination.OrderConfirmation(confirmedOrder)
                            }
                        )
                        quickWhatsAppServiceFromMenu = null
                    }
                )
            }
        }
    }
}

