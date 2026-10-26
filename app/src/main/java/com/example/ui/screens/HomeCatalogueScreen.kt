package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.DefaultCatalogueSeed
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.StatusErrorRed
import com.example.ui.theme.StatusVerifiedGreen
import com.example.viewmodel.UiState

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
    onSaveServiceAsAdmin: (ServiceItem) -> Unit
) {
    var editingService by remember { mutableStateOf<ServiceItem?>(null) }

    val displayedServices = remember(servicesState, searchQuery, selectedCategory, isHindi) {
        val rawList = when (servicesState) {
            is UiState.Success -> {
                servicesState.data.ifEmpty { DefaultCatalogueSeed.services }
            }
            else -> emptyList()
        }
        rawList.filter { item ->
            val matchesCategory = selectedCategory == ServiceCategory.ALL || item.category == selectedCategory.key
            val q = searchQuery.trim().lowercase()
            val matchesQuery = q.isEmpty() ||
                item.title.lowercase().contains(q) ||
                item.titleHi.lowercase().contains(q) ||
                item.description.lowercase().contains(q) ||
                item.category.lowercase().contains(q)
            matchesCategory && matchesQuery
        }
    }

    val isUsingCloudDocs = (servicesState as? UiState.Success)?.data?.isNotEmpty() == true

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_catalogue_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, NeonOrange.copy(alpha = 0.45f))
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_cyber_hero),
                        contentDescription = "Aditya Online Center Kateya Bihar banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(155.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.85f),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, ElectricCyan)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = "Realtime Sync",
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isHindi) "लाइव सिंक सक्रिय" else "LIVE FIREBASE SYNC",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = ElectricCyan
                                    )
                                }
                            }

                            if (isAdmin) {
                                Surface(
                                    color = NeonOrange,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = "ADMIN MODE",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isHindi) "आदित्य ऑनलाइन सेंटर, कटेया" else "Aditya Online Center, Kateya",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isHindi) {
                                "रिज्यूमे, बायोडाटा, छात्रवृत्ति, सरकारी फॉर्म और पीडीएफ सेवाएं — घर बैठे ऑर्डर करें"
                            } else {
                                "Order Resumes, Biodata, Scholarships, RTPS & PDF Services from anywhere"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (isAdmin) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    border = BorderStroke(1.dp, NeonOrange)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = NeonOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) {
                                        "एडमिन कैटलॉग नियंत्रण (वेबसाइट + ऐप साझा डेटाबेस)"
                                    } else {
                                        "Shared Catalogue Admin Controls"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onSeedCatalogueAsAdmin,
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("admin_sync_catalogue_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isHindi) "डिफ़ॉल्ट सेवाएं सिंक करें" else "Sync Default Catalogue")
                            }

                            Button(
                                onClick = {
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
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("admin_add_service_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isHindi) "नई सेवा जोड़ें" else "Add Service")
                            }
                        }
                    }
                }
            }
        } else if (!isUsingCloudDocs && servicesState is UiState.Success) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = NeonOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isHindi) {
                                "कैटलॉग प्रीव्यू: एडमिन पैनल (adityakty77@gmail.com) द्वारा सेवाएं सिंक किए जाने के बाद लाइव ऑर्डर स्वीकार किए जाएंगे।"
                            } else {
                                "Shared Catalogue Preview: Sign in with the admin account (adityakty77@gmail.com) or use the web admin panel to publish live services to Firestore."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_services_input"),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Services",
                        tint = NeonOrange
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
                        if (isHindi) "रिज्यूमे, छात्रवृत्ति, जाति/आय, पैन, पीडीएफ खोजें…"
                        else "Search resume, scholarship, PAN, RTPS, PDF…"
                    )
                }
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ServiceCategory.entries.forEach { category ->
                    val selected = category == selectedCategory
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectCategory(category) },
                        label = {
                            Text(
                                text = category.label(isHindi),
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        leadingIcon = if (selected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonOrange,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("category_chip_${category.key}")
                    )
                }
            }
        }

        when (servicesState) {
            is UiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = NeonOrange)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isHindi) "सेवा कैटलॉग लोड हो रहा है…" else "Syncing live service catalogue…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            is UiState.Error -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = servicesState.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            is UiState.Success -> {
                if (displayedServices.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = NeonOrange,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (isHindi) "कोई मिलान सेवा नहीं मिली" else "No matching services found",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isHindi) {
                                        "कृपया दूसरी श्रेणी चुनें या खोज शब्द बदलें।"
                                    } else {
                                        "Try selecting 'All Services' or clearing your search filter."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(displayedServices, key = { it.id }) { service ->
                        ServiceCatalogueCard(
                            service = service,
                            isHindi = isHindi,
                            isAdmin = isAdmin,
                            onClick = { onSelectService(service) },
                            onEditAsAdmin = { editingService = service }
                        )
                    }
                }
            }
        }
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
}

@Composable
private fun ServiceCatalogueCard(
    service: ServiceItem,
    isHindi: Boolean,
    isAdmin: Boolean,
    onClick: () -> Unit,
    onEditAsAdmin: () -> Unit
) {
    val categoryEnum = ServiceCategory.fromKey(service.category)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("service_card_${service.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            width = 1.dp,
            color = if (service.active) MaterialTheme.colorScheme.outline else StatusErrorRed.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (service.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = service.imageUrl,
                            contentDescription = service.localizedTitle(isHindi),
                            placeholder = painterResource(id = R.drawable.img_cyber_hero),
                            error = painterResource(id = R.drawable.img_cyber_hero),
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.img_cyber_hero),
                            contentDescription = service.localizedTitle(isHindi),
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = categoryEnum.label(isHindi),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = NeonOrange,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }

                            Surface(
                                color = if (service.active) StatusVerifiedGreen.copy(alpha = 0.16f)
                                else StatusErrorRed.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (service.active) {
                                        if (isHindi) "उपलब्ध" else "ACTIVE"
                                    } else {
                                        if (isHindi) "बंद है" else "INACTIVE"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (service.active) StatusVerifiedGreen else StatusErrorRed,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = service.localizedTitle(isHindi),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, NeonOrange)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "₹${service.price}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = NeonOrange
                        )
                        Text(
                            text = if (isHindi) "शुल्क" else "Fee",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = service.localizedDescription(isHindi),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = service.turnaroundTime,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = NeonOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHindi) "${service.requirements.size} आवश्यक दस्तावेज़"
                        else "${service.requirements.size} Docs Required",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isAdmin) {
                        IconButton(
                            onClick = onEditAsAdmin,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("edit_service_${service.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Service",
                                tint = NeonOrange
                            )
                        }
                    }

                    Button(
                        onClick = onClick,
                        enabled = service.active,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("view_service_button_${service.id}")
                    ) {
                        Text(
                            text = if (service.active) {
                                if (isHindi) "आवेदन करें" else "Apply"
                            } else {
                                if (isHindi) "अनुपलब्ध" else "Closed"
                            },
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.width(4.dp))
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (initialService.id.isBlank()) {
                    if (isHindi) "नई सेवा जोड़ें" else "Add New Shared Service"
                } else {
                    if (isHindi) "सेवा संपादित करें" else "Edit Shared Service"
                }
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Service Title (English)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
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
                    onValueChange = { priceText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Authoritative Price (₹ INR)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
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
                    onValueChange = { description = it },
                    label = { Text("Service Description") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
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
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = active,
                        onCheckedChange = { active = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedPrice = priceText.toLongOrNull() ?: 50L
                    if (title.trim().length >= 2 && description.trim().length >= 5) {
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
