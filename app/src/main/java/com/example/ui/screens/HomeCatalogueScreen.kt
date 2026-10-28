package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.CenterContactConfig
import com.example.data.model.DefaultCatalogueSeed
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceCategoryItem
import com.example.data.model.ServiceItem
import com.example.ui.components.ConnectWithMeCard
import com.example.ui.components.CustomerDocumentLockerDialog
import com.example.ui.components.CyberToolsAndStatusHubCard
import com.example.ui.components.FuturisticGradientButton
import com.example.ui.components.Glowing3DIconBadge
import com.example.ui.components.LiveShopNoticeTickerCard
import com.example.ui.components.PhotoSignatureResizerDialog
import com.example.ui.components.PulsingDotIndicator
import com.example.ui.components.QuickWhatsAppOrderDialog
import com.example.ui.components.SarkariJobAdmitResultBoardCard
import com.example.ui.components.StaggeredEntranceItem
import com.example.ui.components.WhatsAppBrandGreen
import com.example.ui.components.bounceClick
import com.example.ui.components.glassCardContainerColor
import com.example.ui.components.glassSpecularBorderBrush
import com.example.ui.components.glassSurfaceOverlayBrush
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueBright
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LuminousPurple
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.StatusErrorRed
import com.example.ui.theme.StatusVerifiedGreen
import com.example.ui.theme.VividViolet
import com.example.viewmodel.UiState

fun ServiceCategory.iconAndAccent(): Pair<ImageVector, Color> = when (this) {
    ServiceCategory.ALL -> Icons.Default.AutoAwesome to ElectricCyan
    ServiceCategory.RESUME -> Icons.Default.Badge to ElectricCyan
    ServiceCategory.GOV_FORM -> Icons.Default.AccountBalance to ElectricBlueBright
    ServiceCategory.SCHOLARSHIP -> Icons.Default.School to LuminousPurple
    ServiceCategory.PDF_DOC -> Icons.Default.PictureAsPdf to Color(0xFF38BDF8)
    ServiceCategory.CERTIFICATE -> Icons.Default.VerifiedUser to StatusVerifiedGreen
}

@Composable
fun HomeCatalogueScreen(
    servicesState: UiState<List<ServiceItem>>,
    searchQuery: String,
    selectedCategory: ServiceCategory,
    isHindi: Boolean,
    isAdmin: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (ServiceCategory) -> Unit,
    onSelectService: (ServiceItem) -> Unit,
    onSeedCatalogueAsAdmin: () -> Unit,
    onSaveServiceAsAdmin: (ServiceItem) -> Unit,
    contactConfig: CenterContactConfig = CenterContactConfig(),
    defaultCustomerName: String = "",
    defaultCustomerPhone: String = "",
    defaultCustomerAddress: String = "Kateya, Gopalganj, Bihar",
    onQuickWhatsAppOrder: (
        service: ServiceItem,
        customerName: String,
        customerPhone: String,
        detailsSummary: String
    ) -> Unit = { _, _, _, _ -> },
    onOpenConnectProfile: (() -> Unit)? = null,
    openAddServiceTrigger: Boolean = false,
    onConsumeAddServiceTrigger: () -> Unit = {},
    categories: List<ServiceCategoryItem> = emptyList(),
    onOpenAdminDashboard: (initialServiceToEdit: ServiceItem?) -> Unit = {},
    selectedCategoryKey: String = selectedCategory.key,
    onSelectCategoryKey: (String) -> Unit = { key ->
        onSelectCategory(ServiceCategory.fromKey(key))
    }
) {
    var editingService by remember { mutableStateOf<ServiceItem?>(null) }
    var pendingStatusToggleService by remember { mutableStateOf<ServiceItem?>(null) }
    var quickWhatsAppService by remember { mutableStateOf<ServiceItem?>(null) }
    var showPhotoResizerDialog by remember { mutableStateOf(false) }
    var showDocumentLockerDialog by remember { mutableStateOf(false) }
    var isAdminToolsExpanded by remember { mutableStateOf(true) }

    val dynamicCategories = remember(categories) {
        val allChip = ServiceCategoryItem(
            id = "all",
            key = "all",
            labelEn = "All Services",
            labelHi = "सभी सेवाएं",
            iconKey = "auto_awesome",
            sortOrder = 0L,
            active = true
        )
        val baseCats = if (categories.isNotEmpty()) {
            categories.filter { it.active && !it.key.equals("all", ignoreCase = true) }.sortedBy { it.sortOrder }
        } else {
            com.example.data.model.DefaultCategorySeed.categories.sortedBy { it.sortOrder }
        }
        listOf(allChip) + baseCats
    }

    LaunchedEffect(openAddServiceTrigger) {
        if (openAddServiceTrigger) {
            editingService = ServiceItem(
                id = "",
                title = "",
                titleHi = "",
                category = "resume",
                description = "",
                descriptionHi = "",
                price = 100L,
                turnaroundTime = "2 Hours",
                requirements = emptyList(),
                imageUrl = "https://images.unsplash.com/photo-1586281380349-632531db7ed4?auto=format&fit=crop&w=800&q=80",
                active = true
            )
            onConsumeAddServiceTrigger()
        }
    }

    val listState = rememberLazyListState()
    val isHeaderCompact by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 100
        }
    }

    val displayedServices = remember(servicesState, searchQuery, selectedCategoryKey, isHindi, isAdmin) {
        val rawList = when (servicesState) {
            is UiState.Success -> {
                servicesState.data.ifEmpty { DefaultCatalogueSeed.services }
            }
            else -> emptyList()
        }
        rawList
            .filter { item ->
                val notArchived = !item.isArchived
                val visibleToCustomer = isAdmin || item.active
                val matchesCategory = selectedCategoryKey.equals("all", ignoreCase = true) ||
                    item.category.equals(selectedCategoryKey, ignoreCase = true)
                val q = searchQuery.trim().lowercase()
                val matchesQuery = q.isEmpty() ||
                    item.title.lowercase().contains(q) ||
                    item.titleHi.lowercase().contains(q) ||
                    item.shortDescription.lowercase().contains(q) ||
                    item.description.lowercase().contains(q) ||
                    item.category.lowercase().contains(q)
                notArchived && visibleToCustomer && matchesCategory && matchesQuery
            }
            .sortedBy { it.sortOrder }
    }

    val featuredServices = remember(displayedServices, selectedCategoryKey, searchQuery) {
        if (selectedCategoryKey.equals("all", ignoreCase = true) && searchQuery.isBlank()) {
            val activeOnly = displayedServices.filter { it.active && !it.isArchived }
            val explicitlyFeatured = activeOnly.filter { it.isFeatured }.sortedBy { it.featuredOrder }
            explicitlyFeatured.ifEmpty { activeOnly.take(3) }
        } else {
            emptyList()
        }
    }

    val isUsingCloudDocs = (servicesState as? UiState.Success)?.data?.isNotEmpty() == true
    val cloudDocCount = (servicesState as? UiState.Success)?.data?.size ?: 0

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val isTwoColumnGrid = screenWidth >= 600.dp
        val isCompactNarrow = screenWidth < 360.dp

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_catalogue_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 0. Live Shop Open/Closed & Scrolling Notice Ticker
            item(key = "live_shop_notice_ticker") {
                LiveShopNoticeTickerCard(
                    isHindi = isHindi,
                    isAdmin = isAdmin
                )
            }

            // 1. Futuristic Electric-Blue & Purple Hero Banner
            item(key = "welcome_banner") {
                WelcomeBannerCard(
                    isHindi = isHindi,
                    isUsingCloudDocs = isUsingCloudDocs,
                    isAdmin = isAdmin,
                    isCompactByScroll = isHeaderCompact
                )
            }

            // 2. Discreet Admin Toolbar (Only visible to authorized admins)
            if (isAdmin) {
                item(key = "admin_toolbar") {
                    DiscreetAdminToolbar(
                        isHindi = isHindi,
                        isExpanded = isAdminToolsExpanded,
                        cloudDocCount = cloudDocCount,
                        isCompactNarrow = isCompactNarrow,
                        onToggleExpand = { isAdminToolsExpanded = !isAdminToolsExpanded },
                        onSeedCatalogue = onSeedCatalogueAsAdmin,
                        onOpenFullAdminDashboard = { onOpenAdminDashboard(null) },
                        onAddService = {
                            editingService = ServiceItem(
                                id = "",
                                title = "",
                                titleHi = "",
                                category = "resume",
                                description = "",
                                descriptionHi = "",
                                price = 100L,
                                turnaroundTime = "2 Hours",
                                requirements = listOf("Aadhaar Card", "Applicant Photo"),
                                imageUrl = "https://images.unsplash.com/photo-1586281380349-632531db7ed4?auto=format&fit=crop&w=800&q=80",
                                active = true
                            )
                        }
                    )
                }
            }

            // 3. Search Bar & Horizontal Quick-Access Service Categories + Service Categories Grid
            item(key = "search_and_categories") {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_services_input"),
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = glassCardContainerColor(alpha = 0.85f),
                            unfocusedContainerColor = glassCardContainerColor(alpha = 0.72f),
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = ElectricBlueBright.copy(alpha = 0.45f)
                        ),
                        leadingIcon = {
                            Glowing3DIconBadge(
                                icon = Icons.Default.Search,
                                accentColor = ElectricCyan,
                                size = 32.dp,
                                iconSize = 16.dp,
                                cornerRadius = 10.dp
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { onSearchQueryChange("") },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear Search"
                                    )
                                }
                            }
                        },
                        placeholder = {
                            Text(
                                text = if (isHindi) "रिज्यूमे, छात्रवृत्ति, जाति/आय, पैन, पीडीएफ खोजें…"
                                else "Search resume, scholarship, PAN, RTPS, PDF…",
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )

                    // Horizontal Quick-Access Service Category Pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        dynamicCategories.forEach { categoryItem ->
                            val selected = categoryItem.key.equals(selectedCategoryKey, ignoreCase = true)
                            val fallbackEnum = ServiceCategory.fromKey(categoryItem.key)
                            val (catIcon, catAccent) = if (categoryItem.key.equals("all", ignoreCase = true)) {
                                ServiceCategory.ALL.iconAndAccent()
                            } else {
                                resolveAdminServiceIcon(categoryItem.iconKey, categoryItem.key)
                            }

                            Surface(
                                onClick = {
                                    onSelectCategoryKey(categoryItem.key)
                                    if (categoryItem.key.equals("all", ignoreCase = true)) {
                                        onSelectCategory(ServiceCategory.ALL)
                                    } else {
                                        onSelectCategory(fallbackEnum)
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                color = if (selected) {
                                    ElectricBlue.copy(alpha = 0.35f)
                                } else {
                                    glassCardContainerColor(alpha = 0.72f)
                                },
                                border = BorderStroke(
                                    width = if (selected) 1.5.dp else 1.dp,
                                    brush = if (selected) {
                                        Brush.linearGradient(
                                            listOf(ElectricCyan, ElectricBlueBright, LuminousPurple)
                                        )
                                    } else {
                                        Brush.linearGradient(
                                            listOf(
                                                ElectricBlueBright.copy(alpha = 0.35f),
                                                catAccent.copy(alpha = 0.45f)
                                            )
                                        )
                                    }
                                ),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .bounceClick(scaleDown = 0.94f)
                                    .testTag("category_chip_${categoryItem.key}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Glowing3DIconBadge(
                                        icon = catIcon,
                                        accentColor = catAccent,
                                        size = 26.dp,
                                        iconSize = 14.dp,
                                        cornerRadius = 8.dp,
                                        animateGlow = selected
                                    )
                                    Text(
                                        text = categoryItem.localizedLabel(isHindi),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Horizontal Featured Services Section (Futuristic Cards with Electric-Blue/Purple Borders)
            if (servicesState is UiState.Success && featuredServices.isNotEmpty()) {
                item(key = "featured_carousel_section") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Glowing3DIconBadge(
                                    icon = Icons.Default.Star,
                                    accentColor = LuminousPurple,
                                    size = 30.dp,
                                    iconSize = 16.dp,
                                    cornerRadius = 9.dp,
                                    animateGlow = true
                                )
                                Text(
                                    text = if (isHindi) "प्रमुख डिजिटल सेवाएं" else "Featured Services",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Surface(
                                color = ElectricCyan.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.55f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (isHindi) "त्वरित आवेदन" else "Fast Turnaround",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricCyan
                                    )
                                }
                            }
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(end = 4.dp),
                            modifier = Modifier.testTag("featured_services_row")
                        ) {
                            itemsIndexed(featuredServices, key = { _, svc -> "featured_${svc.id}" }) { index, service ->
                                StaggeredEntranceItem(
                                    index = index,
                                    modifier = Modifier.animateItem()
                                ) {
                                    FeaturedServiceHighlightCard(
                                        service = service,
                                        isHindi = isHindi,
                                        onClick = { onSelectService(service) },
                                        onWhatsAppOrderClick = { quickWhatsAppService = service }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4b. Futuristic Service Categories Bento Grid (Inspired by the reference phone dashboard)
            item(key = "service_categories_grid_section") {
                ServiceCategoriesBentoGrid(
                    categories = dynamicCategories.take(6),
                    selectedCategoryKey = selectedCategoryKey,
                    isHindi = isHindi,
                    onSelectCategory = { catItem ->
                        onSelectCategoryKey(catItem.key)
                        if (catItem.key.equals("all", ignoreCase = true)) {
                            onSelectCategory(ServiceCategory.ALL)
                        } else {
                            onSelectCategory(ServiceCategory.fromKey(catItem.key))
                        }
                    }
                )
            }

            // Section Title for All Services
            if (servicesState is UiState.Success && displayedServices.isNotEmpty()) {
                item(key = "all_services_header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val activeCatItem = dynamicCategories.firstOrNull {
                                it.key.equals(selectedCategoryKey, ignoreCase = true)
                            }
                            val (headerIcon, headerAccent) = if (selectedCategoryKey.equals("all", ignoreCase = true)) {
                                ServiceCategory.ALL.iconAndAccent()
                            } else {
                                resolveAdminServiceIcon(activeCatItem?.iconKey ?: "", selectedCategoryKey)
                            }
                            Glowing3DIconBadge(
                                icon = headerIcon,
                                accentColor = headerAccent,
                                size = 30.dp,
                                iconSize = 16.dp,
                                cornerRadius = 9.dp
                            )
                            Text(
                                text = if (selectedCategoryKey.equals("all", ignoreCase = true)) {
                                    if (isHindi) "सभी उपलब्ध सेवाएं" else "All Digital Services"
                                } else {
                                    activeCatItem?.localizedLabel(isHindi) ?: selectedCategory.label(isHindi)
                                },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Surface(
                            color = glassCardContainerColor(alpha = 0.78f),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = if (isHindi) "${displayedServices.size} सेवाएं" else "${displayedServices.size} services",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = ElectricCyan,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }

            // 5. Loading, Error, Empty, or Responsive Futuristic Service Catalogue Cards
            when (servicesState) {
                is UiState.Loading -> {
                    item(key = "loading_state") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = glassCardContainerColor()),
                            border = BorderStroke(1.dp, glassSpecularBorderBrush())
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 44.dp, horizontal = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = ElectricCyan)
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = if (isHindi) "सेवा कैटलॉग लोड हो रहा है…" else "Loading service catalogue…",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    item(key = "error_state") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.14f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Glowing3DIconBadge(
                                        icon = Icons.Default.ErrorOutline,
                                        accentColor = MaterialTheme.colorScheme.error,
                                        size = 32.dp,
                                        iconSize = 18.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (isHindi) "कैटलॉग लोड करने में समस्या" else "Unable to load services",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = servicesState.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                is UiState.Success -> {
                    if (displayedServices.isEmpty()) {
                        item(key = "empty_state") {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = glassCardContainerColor()),
                                border = BorderStroke(1.dp, glassSpecularBorderBrush())
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Glowing3DIconBadge(
                                        icon = Icons.Default.Search,
                                        accentColor = ElectricCyan,
                                        size = 48.dp,
                                        iconSize = 24.dp,
                                        cornerRadius = 15.dp,
                                        animateGlow = true
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = if (isHindi) "कोई मिलान सेवा नहीं मिली" else "No matching services found",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (isHindi) {
                                            "कृपया दूसरी श्रेणी चुनें या खोज शब्द हटाएं।"
                                        } else {
                                            "Try selecting 'All Services' or clearing your search filter."
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (searchQuery.isNotEmpty() || !selectedCategoryKey.equals("all", ignoreCase = true)) {
                                        Spacer(modifier = Modifier.height(14.dp))
                                        OutlinedButton(
                                            onClick = {
                                                onSearchQueryChange("")
                                                onSelectCategoryKey("all")
                                                onSelectCategory(ServiceCategory.ALL)
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .minimumInteractiveComponentSize()
                                                .bounceClick()
                                        ) {
                                            Text(if (isHindi) "सभी सेवाएं दिखाएं" else "Reset Filters")
                                        }
                                    }
                                }
                            }
                        }
                    } else if (isTwoColumnGrid) {
                        val rows = displayedServices.chunked(2)
                        itemsIndexed(rows, key = { _, row -> row.joinToString("_") { it.id } }) { rowIndex, rowItems ->
                            StaggeredEntranceItem(
                                index = rowIndex,
                                modifier = Modifier.animateItem()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    rowItems.forEach { service ->
                                        Box(modifier = Modifier.weight(1f)) {
                                            ServiceCatalogueCard(
                                                service = service,
                                                isHindi = isHindi,
                                                isAdmin = isAdmin,
                                                isCompactNarrow = false,
                                                onClick = { onSelectService(service) },
                                                onWhatsAppOrderClick = { quickWhatsAppService = service },
                                                onEditAsAdmin = { editingService = service },
                                                onToggleActiveAsAdmin = { pendingStatusToggleService = service }
                                            )
                                        }
                                    }
                                    if (rowItems.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    } else {
                        itemsIndexed(displayedServices, key = { _, it -> it.id }) { index, service ->
                            StaggeredEntranceItem(
                                index = index,
                                modifier = Modifier.animateItem()
                            ) {
                                ServiceCatalogueCard(
                                    service = service,
                                    isHindi = isHindi,
                                    isAdmin = isAdmin,
                                    isCompactNarrow = isCompactNarrow,
                                    onClick = { onSelectService(service) },
                                    onWhatsAppOrderClick = { quickWhatsAppService = service },
                                    onEditAsAdmin = { editingService = service },
                                    onToggleActiveAsAdmin = { pendingStatusToggleService = service }
                                )
                            }
                        }
                    }
                }
            }

            // 6. Sarkari Job, Admit Card & Result Live Alert Board
            item(key = "sarkari_job_admit_result_board") {
                SarkariJobAdmitResultBoardCard(
                    isHindi = isHindi,
                    services = displayedServices,
                    onOrderAlertViaWhatsApp = { alertService ->
                        quickWhatsAppService = alertService
                    },
                    onApplyService = { alertService ->
                        onSelectService(alertService)
                    }
                )
            }

            // 7. Smart Cyber Tools (Photo/Sign Resizer + Document Locker), Quick Gov Status Hub & Promo Coupons
            item(key = "cyber_tools_and_status_hub") {
                CyberToolsAndStatusHubCard(
                    isHindi = isHindi,
                    onOpenPhotoResizer = { showPhotoResizerDialog = true },
                    onOpenDocumentLocker = { showDocumentLockerDialog = true }
                )
            }

            // 8. Bottom Footer: Connect With Me (WhatsApp, Phone, Email) & Social Media Profile Links
            item(key = "connect_with_me_card") {
                ConnectWithMeCard(
                    contactConfig = contactConfig,
                    isHindi = isHindi,
                    showSocialLinks = true,
                    onEditContactsClick = if (isAdmin) onOpenConnectProfile else null
                )
            }
        }
    }

    if (showPhotoResizerDialog) {
        PhotoSignatureResizerDialog(
            isHindi = isHindi,
            defaultApplicantName = defaultCustomerName.ifBlank { "ADITYA KUMAR" },
            onDismiss = { showPhotoResizerDialog = false }
        )
    }

    if (showDocumentLockerDialog) {
        CustomerDocumentLockerDialog(
            isHindi = isHindi,
            onDismiss = { showDocumentLockerDialog = false }
        )
    }

    if (quickWhatsAppService != null) {
        val selectedSvc = quickWhatsAppService!!
        QuickWhatsAppOrderDialog(
            service = selectedSvc,
            contactConfig = contactConfig,
            defaultName = defaultCustomerName,
            defaultPhone = defaultCustomerPhone,
            defaultAddress = defaultCustomerAddress,
            isHindi = isHindi,
            onDismiss = { quickWhatsAppService = null },
            onOrderCreatedThroughWhatsApp = { customerName, customerPhone, detailsSummary ->
                onQuickWhatsAppOrder(selectedSvc, customerName, customerPhone, detailsSummary)
                quickWhatsAppService = null
            }
        )
    }

    if (editingService != null) {
        AdminServiceEditorDialog(
            initialService = editingService!!,
            isHindi = isHindi,
            onDismiss = { editingService = null },
            onSave = { updated ->
                onSaveServiceAsAdmin(updated)
                editingService = null
            }
        )
    }

    if (pendingStatusToggleService != null) {
        val targetService = pendingStatusToggleService!!
        val nextActive = !targetService.active
        AlertDialog(
            onDismissRequest = { pendingStatusToggleService = null },
            title = {
                Text(
                    if (nextActive) {
                        if (isHindi) "सेवा सक्रिय करें?" else "Activate Service?"
                    } else {
                        if (isHindi) "सेवा निष्क्रिय करें?" else "Deactivate Service?"
                    }
                )
            },
            text = {
                Text(
                    if (nextActive) {
                        if (isHindi) {
                            "क्या आप '${targetService.localizedTitle(true)}' को ग्राहकों के नए ऑर्डर के लिए उपलब्ध करना चाहते हैं?"
                        } else {
                            "Make '${targetService.title}' available for new customer orders on the website and Android app?"
                        }
                    } else {
                        if (isHindi) {
                            "क्या आप '${targetService.localizedTitle(true)}' को अस्थायी रूप से बंद करना चाहते हैं? ग्राहक नए ऑर्डर नहीं कर पाएंगे।"
                        } else {
                            "Temporarily close '${targetService.title}'? Customers will not be able to place new orders until reactivated."
                        }
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveServiceAsAdmin(targetService.copy(active = nextActive))
                        pendingStatusToggleService = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (nextActive) StatusVerifiedGreen else StatusErrorRed,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        if (nextActive) {
                            if (isHindi) "सक्रिय करें" else "Activate"
                        } else {
                            if (isHindi) "बंद करें" else "Deactivate"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingStatusToggleService = null }) {
                    Text(if (isHindi) "रद्द करें" else "Cancel")
                }
            }
        )
    }
}

/**
 * Futuristic 3-column Bento Grid of Service Categories with luminous icons and neon borders.
 */
@Composable
private fun ServiceCategoriesBentoGrid(
    categories: List<ServiceCategoryItem>,
    selectedCategoryKey: String,
    isHindi: Boolean,
    onSelectCategory: (ServiceCategoryItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("service_categories_grid"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.78f)),
        border = BorderStroke(
            width = 1.dp,
            brush = glassSpecularBorderBrush(ElectricBlueBright, VividViolet)
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    glassSurfaceOverlayBrush(
                        topHighlightAlpha = 0.07f,
                        accentTint = VividViolet,
                        accentAlpha = 0.08f
                    )
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Glowing3DIconBadge(
                        icon = Icons.Default.GridView,
                        accentColor = ElectricCyan,
                        size = 26.dp,
                        iconSize = 14.dp,
                        cornerRadius = 8.dp
                    )
                    Text(
                        text = if (isHindi) "सेवा श्रेणियाँ (Service Categories)" else "Service Categories",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = if (isHindi) "टैप कर चुनें" else "Tap to filter",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricCyan
                )
            }

            val rows = categories.chunked(3)
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { catItem ->
                        val isSelected = catItem.key.equals(selectedCategoryKey, ignoreCase = true)
                        val (catIcon, catAccent) = if (catItem.key.equals("all", ignoreCase = true)) {
                            ServiceCategory.ALL.iconAndAccent()
                        } else {
                            resolveAdminServiceIcon(catItem.iconKey, catItem.key)
                        }

                        Surface(
                            onClick = { onSelectCategory(catItem) },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) {
                                ElectricBlue.copy(alpha = 0.28f)
                            } else {
                                Color(0xFF070E22).copy(alpha = 0.78f)
                            },
                            border = BorderStroke(
                                width = if (isSelected) 1.4.dp else 1.dp,
                                brush = if (isSelected) {
                                    Brush.linearGradient(listOf(ElectricCyan, LuminousPurple))
                                } else {
                                    Brush.linearGradient(
                                        listOf(
                                            catAccent.copy(alpha = 0.40f),
                                            ElectricBlueBright.copy(alpha = 0.22f)
                                        )
                                    )
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .bounceClick(scaleDown = 0.95f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Glowing3DIconBadge(
                                    icon = catIcon,
                                    accentColor = catAccent,
                                    size = 34.dp,
                                    iconSize = 17.dp,
                                    cornerRadius = 11.dp,
                                    animateGlow = isSelected
                                )
                                Text(
                                    text = catItem.localizedLabel(isHindi),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeBannerCard(
    isHindi: Boolean,
    isUsingCloudDocs: Boolean,
    isAdmin: Boolean,
    isCompactByScroll: Boolean
) {
    val bannerScale by animateFloatAsState(
        targetValue = if (isCompactByScroll) 0.985f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "welcome_banner_scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "hero_glow_anim")
    val sweepFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_sweep_fraction"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = bannerScale
                scaleY = bannerScale
            }
            .animateContentSize(animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.84f)),
        border = BorderStroke(
            width = 1.4.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    ElectricCyan.copy(alpha = 0.80f),
                    ElectricBlueBright.copy(alpha = 0.85f),
                    LuminousPurple.copy(alpha = 0.85f),
                    VividViolet.copy(alpha = 0.75f)
                ),
                start = Offset(sweepFraction * 400f, 0f),
                end = Offset(400f + sweepFraction * 400f, 300f)
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                VividViolet.copy(alpha = 0.34f),
                                ElectricBlue.copy(alpha = 0.22f),
                                ElectricCyan.copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            center = Offset(
                                x = size.width * (0.20f + 0.60f * sweepFraction),
                                y = size.height * 0.35f
                            ),
                            radius = size.width * 0.68f
                        ),
                        radius = size.width * 0.68f,
                        center = Offset(
                            x = size.width * (0.20f + 0.60f * sweepFraction),
                            y = size.height * 0.35f
                        )
                    )
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_cyber_hero),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCompactByScroll) 104.dp else 182.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.18f
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                ElectricBlue.copy(alpha = 0.14f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
                            )
                        )
                    )
                    .padding(horizontal = 18.dp, vertical = if (isCompactByScroll) 12.dp else 18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Glowing3DIconBadge(
                                icon = Icons.Default.LocationOn,
                                accentColor = ElectricCyan,
                                size = 28.dp,
                                iconSize = 15.dp,
                                cornerRadius = 9.dp,
                                animateGlow = true
                            )
                            Text(
                                text = if (isHindi) "कटेया, गोपालगंज • डिजिटल सेवा पोर्टल"
                                else "Kateya, Gopalganj • Digital Cyber Hub",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (isUsingCloudDocs) {
                            Surface(
                                color = StatusVerifiedGreen.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, StatusVerifiedGreen.copy(alpha = 0.6f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    PulsingDotIndicator(color = StatusVerifiedGreen, dotSize = 7.dp)
                                    Text(
                                        text = if (isHindi) "क्लाउड लाइव" else "Live Cloud",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusVerifiedGreen,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        } else if (isAdmin) {
                            Surface(
                                color = ElectricCyan.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = "Admin",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricCyan,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        com.example.ui.components.AdityaBrandEmblem(
                            size = if (isCompactByScroll) 44.dp else 54.dp,
                            cornerRadius = 14.dp
                        )
                        Text(
                            text = if (isHindi) "आदित्य ऑनलाइन सेंटर में आपका स्वागत है"
                            else "Aditya Online Center • Digital Cyber Hub",
                            style = if (isCompactByScroll) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    AnimatedVisibility(
                        visible = !isCompactByScroll,
                        enter = expandVertically(tween(240)) + fadeIn(tween(220)),
                        exit = shrinkVertically(tween(200)) + fadeOut(tween(180))
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = if (isHindi) {
                                    "रिज्यूमे, शादी बायोडाटा, छात्रवृत्ति, जाति/आय/निवास (RTPS), पैन कार्ड और पीडीएफ सेवाएं घर बैठे ऑर्डर करें।"
                                } else {
                                    "Order ATS Resumes, Marriage Biodata, Scholarships, RTPS Certificates & PDF formatting via App or WhatsApp."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Hero3DFeaturePill(
                                    icon = Icons.Default.Bolt,
                                    label = if (isHindi) "1–2 घंटे में डिलीवरी" else "1–2 Hr Delivery",
                                    accentColor = ElectricCyan
                                )
                                Hero3DFeaturePill(
                                    icon = Icons.AutoMirrored.Filled.Chat,
                                    label = if (isHindi) "व्हाट्सएप ऑर्डर" else "WhatsApp Order",
                                    accentColor = WhatsAppBrandGreen
                                )
                                Hero3DFeaturePill(
                                    icon = Icons.Default.VerifiedUser,
                                    label = if (isHindi) "सत्यापित सेवाएं" else "100% Verified",
                                    accentColor = LuminousPurple
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Hero3DFeaturePill(
    icon: ImageVector,
    label: String,
    accentColor: Color
) {
    Surface(
        color = accentColor.copy(alpha = 0.14f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                listOf(ElectricCyan.copy(alpha = 0.45f), accentColor.copy(alpha = 0.75f))
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Glowing3DIconBadge(
                icon = icon,
                accentColor = accentColor,
                size = 22.dp,
                iconSize = 12.dp,
                cornerRadius = 7.dp
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
private fun DiscreetAdminToolbar(
    isHindi: Boolean,
    isExpanded: Boolean,
    cloudDocCount: Int,
    isCompactNarrow: Boolean,
    onToggleExpand: () -> Unit,
    onSeedCatalogue: () -> Unit,
    onOpenFullAdminDashboard: () -> Unit = {},
    onAddService: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(250)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.84f)),
        border = BorderStroke(
            width = 1.2.dp,
            brush = glassSpecularBorderBrush(ElectricCyan, LuminousPurple)
        )
    ) {
        Column(
            modifier = Modifier
                .background(glassSurfaceOverlayBrush(accentTint = VividViolet, accentAlpha = 0.08f))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand)
                    .testTag("admin_tools_toggle_bar"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Glowing3DIconBadge(
                        icon = Icons.Default.AdminPanelSettings,
                        accentColor = ElectricCyan,
                        size = 34.dp,
                        iconSize = 18.dp,
                        cornerRadius = 10.dp
                    )
                    Column {
                        Text(
                            text = if (isHindi) "एडमिन टूल्स (Admin Tools)" else "Admin Tools",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (cloudDocCount > 0) {
                                if (isHindi) "$cloudDocCount लाइव सेवाएं सिंक हैं" else "$cloudDocCount live cloud services"
                            } else {
                                if (isHindi) "साझा कैटलॉग प्रबंधन" else "Shared catalogue management"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse Admin Tools" else "Expand Admin Tools",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(tween(240)) + fadeIn(tween(220)),
                exit = shrinkVertically(tween(200)) + fadeOut(tween(180))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = ElectricBlueBright.copy(alpha = 0.18f))
                    Spacer(modifier = Modifier.height(4.dp))

                    FuturisticGradientButton(
                        onClick = onOpenFullAdminDashboard,
                        cornerRadius = 12.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("open_admin_dashboard_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "पूर्ण एडमिन डैशबोर्ड खोलें (Admin Panel)" else "Open Full Admin Dashboard",
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    if (isCompactNarrow) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onAddService,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .minimumInteractiveComponentSize()
                                    .bounceClick()
                                    .testTag("admin_add_service_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "नई सेवा जोड़ें" else "Add Service",
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }

                            OutlinedButton(
                                onClick = onSeedCatalogue,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .minimumInteractiveComponentSize()
                                    .bounceClick()
                                    .testTag("admin_sync_catalogue_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "डिफ़ॉल्ट सेवाएं सिंक करें" else "Sync Default Catalogue",
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onSeedCatalogue,
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .minimumInteractiveComponentSize()
                                    .bounceClick()
                                    .testTag("admin_sync_catalogue_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "डिफ़ॉल्ट सिंक करें" else "Sync Defaults",
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Button(
                                onClick = onAddService,
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .minimumInteractiveComponentSize()
                                    .bounceClick()
                                    .testTag("admin_add_service_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "नई सेवा जोड़ें" else "Add Service",
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedServiceHighlightCard(
    service: ServiceItem,
    isHindi: Boolean,
    onClick: () -> Unit,
    onWhatsAppOrderClick: () -> Unit
) {
    val categoryEnum = ServiceCategory.fromKey(service.category)
    val (catIcon, catAccent) = resolveAdminServiceIcon(service.iconKey, service.category)

    Card(
        modifier = Modifier
            .width(284.dp)
            .bounceClick(scaleDown = 0.96f)
            .clickable(onClick = onClick)
            .testTag("featured_card_${service.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.86f)),
        border = BorderStroke(
            width = 1.3.dp,
            brush = glassSpecularBorderBrush(
                primaryAccent = catAccent,
                secondaryAccent = LuminousPurple
            )
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    glassSurfaceOverlayBrush(
                        topHighlightAlpha = 0.10f,
                        accentTint = catAccent,
                        accentAlpha = 0.12f
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Glowing3DIconBadge(
                        icon = catIcon,
                        accentColor = catAccent,
                        size = 32.dp,
                        iconSize = 16.dp,
                        cornerRadius = 10.dp
                    )
                    Text(
                        text = categoryEnum.label(isHindi),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Surface(
                    color = VividViolet.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(ElectricCyan, LuminousPurple)
                        )
                    )
                ) {
                    Text(
                        text = "₹${service.price}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricCyan,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = service.localizedTitle(isHindi),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .bounceClick(scaleDown = 0.92f)
                        .clickable(enabled = service.active, onClick = onWhatsAppOrderClick),
                    color = WhatsAppBrandGreen.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, WhatsAppBrandGreen.copy(alpha = 0.80f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Glowing3DIconBadge(
                            icon = Icons.AutoMirrored.Filled.Chat,
                            accentColor = WhatsAppBrandGreen,
                            size = 22.dp,
                            iconSize = 12.dp,
                            cornerRadius = 7.dp
                        )
                        Text(
                            text = "WhatsApp",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isHindi) "आवेदन करें" else "Apply",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan,
                        maxLines = 1,
                        softWrap = false
                    )
                    Glowing3DIconBadge(
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        accentColor = ElectricCyan,
                        size = 24.dp,
                        iconSize = 13.dp,
                        cornerRadius = 8.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceCatalogueCard(
    service: ServiceItem,
    isHindi: Boolean,
    isAdmin: Boolean,
    isCompactNarrow: Boolean,
    onClick: () -> Unit,
    onWhatsAppOrderClick: () -> Unit,
    onEditAsAdmin: () -> Unit,
    onToggleActiveAsAdmin: () -> Unit
) {
    val categoryEnum = ServiceCategory.fromKey(service.category)
    val (catIcon, catAccent) = resolveAdminServiceIcon(service.iconKey, service.category)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(scaleDown = 0.97f)
            .clickable(onClick = onClick)
            .testTag("service_card_${service.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.86f)),
        border = BorderStroke(
            width = 1.2.dp,
            brush = if (service.active) {
                glassSpecularBorderBrush(
                    primaryAccent = catAccent,
                    secondaryAccent = VividViolet
                )
            } else {
                Brush.linearGradient(
                    listOf(StatusErrorRed.copy(alpha = 0.6f), StatusErrorRed.copy(alpha = 0.3f))
                )
            }
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    glassSurfaceOverlayBrush(
                        topHighlightAlpha = 0.09f,
                        accentTint = catAccent,
                        accentAlpha = 0.10f
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ZONE 1: Top Header (Luminous Category Badge + Active Status Badge + Admin Controls)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Glowing3DIconBadge(
                        icon = catIcon,
                        accentColor = catAccent,
                        size = 32.dp,
                        iconSize = 16.dp,
                        cornerRadius = 10.dp
                    )
                    Surface(
                        color = catAccent.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, catAccent.copy(alpha = 0.50f))
                    ) {
                        Text(
                            text = categoryEnum.label(isHindi),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = if (service.active) StatusVerifiedGreen.copy(alpha = 0.16f)
                        else StatusErrorRed.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (service.active) StatusVerifiedGreen.copy(alpha = 0.55f)
                            else StatusErrorRed.copy(alpha = 0.55f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            if (service.active) {
                                PulsingDotIndicator(color = StatusVerifiedGreen, dotSize = 6.dp)
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(StatusErrorRed)
                                )
                            }
                            Text(
                                text = if (service.active) {
                                    if (isHindi) "उपलब्ध" else "Active"
                                } else {
                                    if (isHindi) "बंद है" else "Inactive"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (service.active) StatusVerifiedGreen else StatusErrorRed,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    if (isAdmin) {
                        IconButton(
                            onClick = onToggleActiveAsAdmin,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("toggle_active_service_${service.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = if (service.active) "Deactivate Service" else "Activate Service",
                                tint = if (service.active) StatusVerifiedGreen else StatusErrorRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = onEditAsAdmin,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("edit_service_${service.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Service",
                                tint = ElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // ZONE 2: Neon-Framed Thumbnail + Controlled Title & Description
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 1.4.dp,
                            brush = Brush.linearGradient(
                                listOf(ElectricCyan, catAccent, LuminousPurple)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    ServiceCoverImagePreview(
                        imageUrl = service.imageUrl,
                        contentDescription = service.localizedTitle(isHindi),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = service.localizedTitle(isHindi),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = service.localizedShortDescription(isHindi),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = if (isHindi) "विवरण और आवश्यक दस्तावेज़ देखें →" else "View full details & checklist →",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = ElectricCyan,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            // ZONE 3: Midnight Glass Turnaround Time & Required Documents Strip
            Surface(
                color = Color(0xFF060D20).copy(alpha = 0.72f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, ElectricBlueBright.copy(alpha = 0.22f))
            ) {
                if (isCompactNarrow) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Glowing3DIconBadge(
                                icon = Icons.Default.Schedule,
                                accentColor = ElectricCyan,
                                size = 24.dp,
                                iconSize = 13.dp,
                                cornerRadius = 7.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "समय: ${service.turnaroundTime}" else "Time: ${service.turnaroundTime}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Glowing3DIconBadge(
                                icon = Icons.Default.Description,
                                accentColor = LuminousPurple,
                                size = 24.dp,
                                iconSize = 13.dp,
                                cornerRadius = 7.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "${service.requirements.size} आवश्यक दस्तावेज़"
                                else "${service.requirements.size} Required Documents",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Glowing3DIconBadge(
                                icon = Icons.Default.Schedule,
                                accentColor = ElectricCyan,
                                size = 26.dp,
                                iconSize = 14.dp,
                                cornerRadius = 8.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = service.turnaroundTime,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Glowing3DIconBadge(
                                icon = Icons.Default.Description,
                                accentColor = LuminousPurple,
                                size = 26.dp,
                                iconSize = 14.dp,
                                cornerRadius = 8.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "${service.requirements.size} आवश्यक दस्तावेज़"
                                else "${service.requirements.size} Docs Required",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }

            // ZONE 4: Full-Width Action Footer (Price Pill + WhatsApp Order + Gradient Apply Button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = ElectricBlue.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        width = 1.2.dp,
                        brush = Brush.linearGradient(
                            listOf(ElectricCyan, LuminousPurple)
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "₹${service.price}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElectricCyan,
                            maxLines = 1,
                            softWrap = false
                        )
                        Text(
                            text = if (isHindi) "शुल्क" else "Fee",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onWhatsAppOrderClick,
                        enabled = service.active,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, WhatsAppBrandGreen.copy(alpha = 0.85f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = WhatsAppBrandGreen.copy(alpha = 0.14f),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .minimumInteractiveComponentSize()
                            .bounceClick()
                            .testTag("whatsapp_order_button_${service.id}")
                    ) {
                        Glowing3DIconBadge(
                            icon = Icons.AutoMirrored.Filled.Chat,
                            accentColor = WhatsAppBrandGreen,
                            size = 26.dp,
                            iconSize = 14.dp,
                            cornerRadius = 8.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "व्हाट्सएप" else "WhatsApp",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    FuturisticGradientButton(
                        onClick = onClick,
                        enabled = service.active,
                        cornerRadius = 14.dp,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("view_service_button_${service.id}")
                    ) {
                        Text(
                            text = if (service.active) {
                                if (isHindi) "आवेदन" else "Apply"
                            } else {
                                if (isHindi) "बंद" else "Closed"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminServiceEditorDialog(
    initialService: ServiceItem,
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onSave: (ServiceItem) -> Unit
) {
    var title by remember { mutableStateOf(initialService.title) }
    var titleHi by remember { mutableStateOf(initialService.titleHi) }
    var priceText by remember { mutableStateOf(initialService.price.toString()) }
    var description by remember { mutableStateOf(initialService.description) }
    var turnaround by remember { mutableStateOf(initialService.turnaroundTime) }
    var imageUrl by remember { mutableStateOf(initialService.imageUrl) }
    var active by remember { mutableStateOf(initialService.active) }
    var category by remember { mutableStateOf(initialService.category) }
    var validationMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialService.id.isBlank()) {
                    if (isHindi) "नई सेवा जोड़ें" else "Add New Shared Service"
                } else {
                    if (isHindi) "सेवा संपादित करें" else "Edit Shared Service"
                },
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (isHindi) "श्रेणी चुनें (Category)" else "Service Category",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ServiceCategory.entries.filter { it != ServiceCategory.ALL }.forEach { cat ->
                        FilterChip(
                            selected = category == cat.key,
                            onClick = { category = cat.key },
                            label = {
                                Text(
                                    text = cat.label(isHindi),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        validationMessage = null
                    },
                    label = { Text("Service Title (English) *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_service_title_input")
                )
                OutlinedTextField(
                    value = titleHi,
                    onValueChange = { titleHi = it },
                    label = { Text("Service Title (Hindi / हिंदी)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = priceText,
                    onValueChange = {
                        priceText = it.filter { ch -> ch.isDigit() }
                        validationMessage = null
                    },
                    label = { Text("Authoritative Price (₹ INR) *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_service_price_input")
                )
                OutlinedTextField(
                    value = turnaround,
                    onValueChange = { turnaround = it },
                    label = { Text("Turnaround Time (e.g., 2 Hours)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        validationMessage = null
                    },
                    label = { Text("Service Description *") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_service_desc_input")
                )
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Service Image HTTPS URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "ग्राहकों के लिए उपलब्ध (Active)" else "Active for Customer Orders",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = active,
                        onCheckedChange = { active = it }
                    )
                }

                if (validationMessage != null) {
                    Text(
                        text = validationMessage!!,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedPrice = priceText.toLongOrNull() ?: 0L
                    when {
                        title.trim().length < 2 -> {
                            validationMessage = if (isHindi) "कृपया सेवा का नाम दर्ज करें।" else "Please enter a valid service title."
                        }
                        parsedPrice <= 0L -> {
                            validationMessage = if (isHindi) "कृपया वैध शुल्क (₹1 से अधिक) दर्ज करें।" else "Please enter a valid price greater than ₹0."
                        }
                        description.trim().length < 5 -> {
                            validationMessage = if (isHindi) "कृपया कम से कम 5 अक्षरों का विवरण लिखें।" else "Please enter a description (min 5 chars)."
                        }
                        else -> {
                            onSave(
                                initialService.copy(
                                    title = title.trim(),
                                    titleHi = titleHi.trim().ifBlank { title.trim() },
                                    category = category,
                                    price = parsedPrice,
                                    turnaroundTime = turnaround.trim().ifBlank { "2 Hours" },
                                    description = description.trim(),
                                    descriptionHi = initialService.descriptionHi.ifBlank { description.trim() },
                                    imageUrl = imageUrl.trim(),
                                    active = active
                                )
                            )
                        }
                    }
                },
                modifier = Modifier.testTag("admin_save_service_dialog_button")
            ) {
                Text(if (isHindi) "सेव करें" else "Save to Cloud")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isHindi) "रद्द करें" else "Cancel")
            }
        }
    )
}
