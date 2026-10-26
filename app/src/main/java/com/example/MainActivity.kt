package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.auth.authStateFlow
import com.example.auth.signOutUser
import com.example.data.model.ServiceItem
import com.example.data.model.ServiceOrder
import com.example.data.repository.AdityaRepository
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
import com.example.ui.theme.NeonOrange
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
    data object Home : ScreenDestination
    data class ServiceDetail(val service: ServiceItem) : ScreenDestination
    data class OrderForm(val service: ServiceItem) : ScreenDestination
    data class OrderConfirmation(val order: ServiceOrder) : ScreenDestination
    data object MyOrders : ScreenDestination
    data class OrderDetail(val orderId: String, val initialOrder: ServiceOrder) : ScreenDestination
    data object Profile : ScreenDestination
}

@Composable
fun AppNavigation(
    isHindi: Boolean,
    isDarkTheme: Boolean,
    onToggleLanguage: () -> Unit,
    onToggleTheme: () -> Unit,
    auth: FirebaseAuth = Firebase.auth
) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    var hasSeenOnboarding by rememberSaveable { mutableStateOf(false) }

    val user = currentUser
    if (!hasSeenOnboarding && user == null) {
        OnboardingScreen(
            isHindi = isHindi,
            onToggleLanguage = onToggleLanguage,
            onFinishOnboarding = { hasSeenOnboarding = true }
        )
    } else if (user == null) {
        AuthScreen(
            isHindi = isHindi,
            onToggleLanguage = onToggleLanguage,
            onAuthSuccess = { /* authStateFlow automatically transitions when user != null */ }
        )
    } else {
        AuthenticatedPortalScreen(
            currentUserId = user.uid,
            currentUserEmail = user.email,
            currentUserDisplayName = user.displayName,
            isHindi = isHindi,
            isDarkTheme = isDarkTheme,
            onToggleLanguage = onToggleLanguage,
            onToggleTheme = onToggleTheme
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticatedPortalScreen(
    currentUserId: String,
    currentUserEmail: String?,
    currentUserDisplayName: String?,
    isHindi: Boolean,
    isDarkTheme: Boolean,
    onToggleLanguage: () -> Unit,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val viewModel: AdityaViewModel = viewModel(
        key = currentUserId,
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
                    currentUserDisplayName = currentUserDisplayName
                )
            }
        }
    )

    val servicesState by viewModel.servicesState.collectAsStateWithLifecycle()
    val ordersState by viewModel.ordersState.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfileState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedOrderStatusFilter by viewModel.selectedOrderStatusFilter.collectAsStateWithLifecycle()
    val isSubmittingOrder by viewModel.isSubmittingOrder.collectAsStateWithLifecycle()
    val statusBannerMessage by viewModel.statusBannerMessage.collectAsStateWithLifecycle()
    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()

    var currentDestination by remember { mutableStateOf<ScreenDestination>(ScreenDestination.Home) }

    val activeTab = when (currentDestination) {
        is ScreenDestination.Home,
        is ScreenDestination.ServiceDetail,
        is ScreenDestination.OrderForm,
        is ScreenDestination.OrderConfirmation -> 0
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

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 640.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = if (isHindi) "आदित्य ऑनलाइन सेंटर" else "ADITYA ONLINE CENTER",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = NeonOrange
                            )
                            Text(
                                text = if (isHindi) "कटेया, गोपालगंज (बिहार)" else "Kateya, Gopalganj, Bihar",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        OutlinedButton(
                            onClick = onToggleLanguage,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .minimumInteractiveComponentSize()
                                .testTag("top_bar_language_toggle")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Toggle Language",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isHindi) "EN" else "हिंदी")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        NavigationBarItem(
                            selected = activeTab == 0,
                            onClick = { currentDestination = ScreenDestination.Home },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Services"
                                )
                            },
                            label = { Text(if (isHindi) "सेवाएं" else "Services") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = NeonOrange,
                                indicatorColor = NeonOrange
                            ),
                            modifier = Modifier.testTag("nav_tab_home")
                        )
                        NavigationBarItem(
                            selected = activeTab == 1,
                            onClick = { currentDestination = ScreenDestination.MyOrders },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = "My Orders"
                                )
                            },
                            label = { Text(if (isHindi) "मेरे ऑर्डर" else "My Orders") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = NeonOrange,
                                indicatorColor = NeonOrange
                            ),
                            modifier = Modifier.testTag("nav_tab_orders")
                        )
                        NavigationBarItem(
                            selected = activeTab == 2,
                            onClick = { currentDestination = ScreenDestination.Profile },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile and Help"
                                )
                            },
                            label = { Text(if (isHindi) "प्रोफ़ाइल" else "Profile & Help") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = NeonOrange,
                                indicatorColor = NeonOrange
                            ),
                            modifier = Modifier.testTag("nav_tab_profile")
                        )
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
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        NavigationRailItem(
                            selected = activeTab == 0,
                            onClick = { currentDestination = ScreenDestination.Home },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Services") },
                            label = { Text(if (isHindi) "सेवाएं" else "Services") }
                        )
                        NavigationRailItem(
                            selected = activeTab == 1,
                            onClick = { currentDestination = ScreenDestination.MyOrders },
                            icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "My Orders") },
                            label = { Text(if (isHindi) "मेरे ऑर्डर" else "My Orders") }
                        )
                        NavigationRailItem(
                            selected = activeTab == 2,
                            onClick = { currentDestination = ScreenDestination.Profile },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text(if (isHindi) "प्रोफ़ाइल" else "Profile") }
                        )
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    // Status / Notification Banner
                    AnimatedVisibility(visible = statusBannerMessage != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, NeonOrange),
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

                    Box(modifier = Modifier.fillMaxSize()) {
                        when (val destination = currentDestination) {
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
                                    }
                                )
                            }

                            is ScreenDestination.ServiceDetail -> {
                                // Resolve latest live service state if available
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
                                    defaultPhone = userProfile?.phone ?: "",
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
                                    }
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
                                    }
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
                                    onAdminUpdateOrder = viewModel::adminUpdateOrder
                                )
                            }

                            is ScreenDestination.Profile -> {
                                ProfileAndHelpScreen(
                                    userProfile = userProfile,
                                    userEmail = currentUserEmail,
                                    userDisplayName = currentUserDisplayName,
                                    isHindi = isHindi,
                                    isDarkTheme = isDarkTheme,
                                    isAdmin = isAdmin,
                                    onToggleLanguage = onToggleLanguage,
                                    onToggleTheme = onToggleTheme,
                                    onSaveProfile = viewModel::saveUserProfile,
                                    onSignOut = {
                                        signOutUser(
                                            credentialManager = credentialManager,
                                            onSignOutComplete = {
                                                currentDestination = ScreenDestination.Home
                                            },
                                            scope = coroutineScope
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
