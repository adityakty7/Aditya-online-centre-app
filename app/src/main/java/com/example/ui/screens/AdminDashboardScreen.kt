package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.CenterContactConfig
import com.example.data.model.DefaultCategorySeed
import com.example.data.model.DocumentLibraryCatalog
import com.example.data.model.DocumentRequirementSpec
import com.example.data.model.ServiceCategoryItem
import com.example.data.model.ServiceItem
import com.example.data.model.ServiceOrder
import com.example.data.model.SocialMediaProfileLink
import com.example.ui.components.ConnectWithMeCard
import com.example.ui.components.PulsingDotIndicator
import com.example.ui.components.WhatsAppBrandGreen
import com.example.ui.components.bounceClick
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.StatusErrorRed
import com.example.ui.theme.StatusPendingAmber
import com.example.ui.theme.StatusVerifiedGreen
import java.io.ByteArrayOutputStream
import kotlin.math.min
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AdminDashboardTab(val key: String, val labelEn: String, val labelHi: String, val icon: ImageVector) {
    OVERVIEW("overview", "Overview", "सारांश", Icons.Default.Dashboard),
    SERVICES("services", "Manage Services", "सेवा प्रबंधन", Icons.Default.FolderSpecial),
    ADD_EDIT("add_edit", "Add / Edit Service", "नई सेवा जोड़ें", Icons.Default.Add),
    DISPLAY_ORDER("display_order", "Display Order", "क्रम और प्रमुख", Icons.Default.SwapVert),
    CATEGORIES("categories", "Categories", "श्रेणियाँ", Icons.Default.Category),
    CONTACT_SOCIAL("contact_social", "Contact & Social", "संपर्क व सोशल", Icons.Default.ContactPhone)
}

data class BuiltInIconOption(
    val key: String,
    val label: String,
    val icon: ImageVector,
    val accent: Color
)

val AdminIconLibrary: List<BuiltInIconOption> = listOf(
    BuiltInIconOption("badge", "Resume / ID Badge", Icons.Default.Badge, ElectricCyan),
    BuiltInIconOption("account_balance", "Gov / Bank Portal", Icons.Default.AccountBalance, RoyalBlueAccent),
    BuiltInIconOption("school", "Scholarship / Exam", Icons.Default.School, Color(0xFFA855F7)),
    BuiltInIconOption("picture_as_pdf", "PDF / Document", Icons.Default.PictureAsPdf, Color(0xFFF43F5E)),
    BuiltInIconOption("verified_user", "Certificate / RTPS", Icons.Default.VerifiedUser, StatusVerifiedGreen),
    BuiltInIconOption("description", "Form / Application", Icons.Default.Description, NeonOrange),
    BuiltInIconOption("credit_card", "PAN / Smart Card", Icons.Default.CreditCard, ElectricCyan),
    BuiltInIconOption("fingerprint", "Aadhaar / Biometric", Icons.Default.Fingerprint, NeonOrange),
    BuiltInIconOption("work", "Job / Career", Icons.Default.Work, RoyalBlueAccent),
    BuiltInIconOption("print", "Print / Scan", Icons.Default.Print, ElectricCyan),
    BuiltInIconOption("gavel", "Affidavit / Legal", Icons.Default.Gavel, StatusPendingAmber),
    BuiltInIconOption("home_work", "Domicile / Land", Icons.Default.HomeWork, StatusVerifiedGreen),
    BuiltInIconOption("menu_book", "Passbook / Record", Icons.Default.MenuBook, Color(0xFFA855F7)),
    BuiltInIconOption("local_library", "Student / Admit Card", Icons.Default.LocalLibrary, ElectricCyan),
    BuiltInIconOption("security", "Insurance / e-KYC", Icons.Default.Security, StatusVerifiedGreen),
    BuiltInIconOption("language", "Online Portal", Icons.Default.Language, RoyalBlueAccent),
    BuiltInIconOption("bolt", "Fast Track Service", Icons.Default.Bolt, NeonOrange),
    BuiltInIconOption("build", "Digital Utility", Icons.Default.Build, ElectricCyan)
)

fun resolveAdminServiceIcon(iconKey: String, categoryKey: String = ""): Pair<ImageVector, Color> {
    val cleanKey = iconKey.trim().lowercase()
    val match = AdminIconLibrary.find { it.key == cleanKey }
    if (match != null) return match.icon to match.accent
    return when (categoryKey.trim().lowercase()) {
        "resume" -> Icons.Default.Badge to ElectricCyan
        "gov_form" -> Icons.Default.AccountBalance to RoyalBlueAccent
        "scholarship" -> Icons.Default.School to Color(0xFFA855F7)
        "pdf_doc" -> Icons.Default.PictureAsPdf to Color(0xFFF43F5E)
        "certificate" -> Icons.Default.VerifiedUser to StatusVerifiedGreen
        else -> Icons.Default.AutoAwesome to NeonOrange
    }
}

private val PresetCoverImages: List<Pair<String, String>> = listOf(
    "Resume & Career" to "https://images.unsplash.com/photo-1586281380349-632531db7ed4?auto=format&fit=crop&w=800&q=80",
    "Marriage Biodata" to "https://images.unsplash.com/photo-1511285560929-80b456fea0bc?auto=format&fit=crop&w=800&q=80",
    "Scholarship Portal" to "https://images.unsplash.com/photo-1523240795612-9a054b0db644?auto=format&fit=crop&w=800&q=80",
    "RTPS Certificate" to "https://images.unsplash.com/photo-1450101499163-c8848c66ca85?auto=format&fit=crop&w=800&q=80",
    "PAN & ID Card" to "https://images.unsplash.com/photo-1554224155-6726b3ff858f?auto=format&fit=crop&w=800&q=80",
    "PDF & Scanning" to "https://images.unsplash.com/photo-1568667256549-094345857637?auto=format&fit=crop&w=800&q=80"
)

private val ObsidianBg = Color(0xFF070D19)
private val CharcoalCard = Color(0xFF0F172A)
private val CharcoalElevated = Color(0xFF1E293B)

/**
 * Reads an image from content URI, validates format and size, and compresses it to a clean
 * data:image/jpeg;base64,... URI under ~80KB so it syncs reliably in Firestore across Android and Web.
 */
private suspend fun encodeUriToCompressedDataUrl(context: Context, uri: Uri): Result<String> =
    withContext(Dispatchers.IO) {
        runCatching {
            val resolver = context.contentResolver
            val mimeType = resolver.getType(uri)?.lowercase() ?: "image/jpeg"
            val allowedMimeTypes = setOf("image/jpeg", "image/jpg", "image/png", "image/webp")
            require(allowedMimeTypes.contains(mimeType)) {
                "Unsupported image format ($mimeType). Please select a JPG, PNG, or WEBP image."
            }

            val rawBytes = resolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IllegalArgumentException("Unable to read selected image file.")
            require(rawBytes.isNotEmpty()) { "Selected image file is empty." }
            require(rawBytes.size <= 8 * 1024 * 1024) {
                "Selected image is too large (> 8 MB). Please select an image under 8 MB."
            }

            val decoded = BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size)
                ?: throw IllegalArgumentException("Could not decode image. File may be corrupted.")

            val maxDim = 640f
            val scale = min(
                1f,
                min(
                    maxDim / decoded.width.coerceAtLeast(1),
                    maxDim / decoded.height.coerceAtLeast(1)
                )
            )
            val targetW = (decoded.width * scale).toInt().coerceAtLeast(1)
            val targetH = (decoded.height * scale).toInt().coerceAtLeast(1)
            val scaledBitmap = if (scale < 1f) {
                Bitmap.createScaledBitmap(decoded, targetW, targetH, true)
            } else {
                decoded
            }

            var quality = 78
            var jpegBytes: ByteArray
            do {
                val out = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
                jpegBytes = out.toByteArray()
                quality -= 12
            } while (jpegBytes.size > 95_000 && quality >= 30)

            val base64Str = Base64.encodeToString(jpegBytes, Base64.NO_WRAP)
            val dataUri = "data:image/jpeg;base64,$base64Str"
            require(dataUri.length <= 240_000) {
                "Compressed image exceeds cloud document limit. Please choose a smaller image."
            }
            dataUri
        }
    }

@Composable
fun ServiceCoverImagePreview(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val trimmed = imageUrl.trim()
    if (trimmed.startsWith("data:image/")) {
        val bitmap = remember(trimmed) {
            runCatching {
                val base64Part = trimmed.substringAfter("base64,", "")
                if (base64Part.isNotBlank()) {
                    val bytes = Base64.decode(base64Part, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                } else null
            }.getOrNull()
        }
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = ContentScale.Crop
            )
            return
        }
    }

    if (trimmed.startsWith("https://")) {
        AsyncImage(
            model = trimmed,
            contentDescription = contentDescription,
            placeholder = painterResource(id = R.drawable.img_cyber_hero),
            error = painterResource(id = R.drawable.img_cyber_hero),
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.img_cyber_hero),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun AdminDashboardScreen(
    services: List<ServiceItem>,
    categories: List<ServiceCategoryItem>,
    orders: List<ServiceOrder>,
    contactConfig: CenterContactConfig,
    isHindi: Boolean,
    isAdmin: Boolean,
    initialTab: AdminDashboardTab = AdminDashboardTab.OVERVIEW,
    initialEditingService: ServiceItem? = null,
    onBackToHome: () -> Unit,
    onSaveService: (ServiceItem) -> Unit,
    onArchiveOrRestoreService: (ServiceItem, Boolean) -> Unit,
    onPermanentlyDeleteService: (ServiceItem) -> Unit,
    onPublishServiceOrder: (List<ServiceItem>) -> Unit,
    onSaveCategory: (ServiceCategoryItem) -> Unit,
    onReorderCategories: (List<ServiceCategoryItem>) -> Unit,
    onDeleteCategoryWithReassignment: (ServiceCategoryItem, String) -> Unit,
    onSaveContactConfig: (CenterContactConfig) -> Unit,
    onSeedDefaultCatalogue: () -> Unit,
    onOpenOrderDetail: (ServiceOrder) -> Unit,
    onAdminQuickUpdateOrder: (
        orderId: String,
        newStatus: String,
        newPaymentStatus: String,
        adminNote: String,
        finalDocumentUrl: String
    ) -> Unit
) {
    var activeTab by remember(initialTab) { mutableStateOf(initialTab) }
    var editingServiceDraft by remember(initialEditingService) {
        mutableStateOf(
            initialEditingService ?: ServiceItem(
                id = "",
                title = "",
                titleHi = "",
                category = categories.firstOrNull()?.key ?: "resume",
                shortDescription = "",
                shortDescriptionHi = "",
                description = "",
                descriptionHi = "",
                price = 100L,
                turnaroundTime = "2 Hours",
                requirements = emptyList(),
                imageUrl = PresetCoverImages.first().second,
                iconKey = "badge",
                active = true,
                isFeatured = false,
                featuredOrder = 999L,
                sortOrder = (services.size + 1).toLong(),
                isArchived = false
            )
        )
    }

    LaunchedEffect(initialEditingService) {
        if (initialEditingService != null) {
            editingServiceDraft = initialEditingService
            activeTab = AdminDashboardTab.ADD_EDIT
        }
    }

    val effectiveCategories = remember(categories, services) {
        val base = if (categories.isNotEmpty()) categories else DefaultCategorySeed.categories
        val existingKeys = base.map { it.key.lowercase() }.toSet()
        val dynamicExtras = services.map { it.category.trim().lowercase() }
            .distinct()
            .filter { it.isNotBlank() && it !in existingKeys }
            .mapIndexed { idx, key ->
                val prettyLabel = key.replace("_", " ").replaceFirstChar { c -> c.uppercase() }
                ServiceCategoryItem(
                    id = key,
                    key = key,
                    labelEn = prettyLabel,
                    labelHi = prettyLabel,
                    iconKey = "description",
                    sortOrder = (base.size + idx + 1).toLong(),
                    active = true
                )
            }
        (base + dynamicExtras).sortedBy { it.sortOrder }
    }

    if (!isAdmin) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                border = BorderStroke(1.dp, StatusErrorRed),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = StatusErrorRed,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = if (isHindi) "केवल अधिकृत एडमिन के लिए" else "Authorized Admin Access Only",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isHindi) {
                            "कृपया एडमिन ईमेल (adityakty77@gmail.com) से साइन इन करें।"
                        } else {
                            "Please sign in with the authorized admin account (adityakty77@gmail.com) to manage services."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                    Button(onClick = onBackToHome) {
                        Text(if (isHindi) "होम पर लौटें" else "Back to Home")
                    }
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .testTag("admin_dashboard_screen")
    ) {
        // Top Header Bar
        Surface(
            color = CharcoalCard,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onBackToHome,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("admin_dashboard_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Customer View",
                                tint = NeonOrange
                            )
                        }
                        com.example.ui.components.AdityaBrandEmblem(
                            size = 38.dp,
                            cornerRadius = 10.dp
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHindi) "एडमिन कंट्रोल पैनल • आदित्य ऑनलाइन सेंटर" else "Admin Control Panel • Aditya Online Center",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                PulsingDotIndicator(color = StatusVerifiedGreen, dotSize = 6.dp)
                                Text(
                                    text = if (isHindi) {
                                        "वेबसाइट + एंड्रॉइड लाइव सिंक सक्रिय (${services.count { !it.isArchived }} सेवाएं)"
                                    } else {
                                        "Web + Android Live Sync Active (${services.count { !it.isArchived }} services)"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricCyan,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            editingServiceDraft = ServiceItem(
                                id = "",
                                title = "",
                                titleHi = "",
                                category = effectiveCategories.firstOrNull()?.key ?: "resume",
                                shortDescription = "",
                                shortDescriptionHi = "",
                                description = "",
                                descriptionHi = "",
                                price = 100L,
                                turnaroundTime = "2 Hours",
                                requirements = emptyList(),
                                imageUrl = PresetCoverImages.first().second,
                                iconKey = "badge",
                                active = true,
                                isFeatured = false,
                                featuredOrder = 999L,
                                sortOrder = (services.size + 1).toLong(),
                                isArchived = false
                            )
                            activeTab = AdminDashboardTab.ADD_EDIT
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NeonOrange),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("admin_header_quick_add_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = NeonOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isHindi) "नई सेवा" else "Add Service",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeonOrange,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                // Scrollable Obsidian & Neon Orange Navigation Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminDashboardTab.entries.forEach { tab ->
                        val selected = activeTab == tab
                        Surface(
                            onClick = { activeTab = tab },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) NeonOrange.copy(alpha = 0.20f) else CharcoalElevated,
                            border = BorderStroke(
                                width = if (selected) 1.5.dp else 1.dp,
                                color = if (selected) NeonOrange else Color.White.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .bounceClick(scaleDown = 0.95f)
                                .testTag("admin_tab_${tab.key}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = null,
                                    tint = if (selected) NeonOrange else Color.White.copy(alpha = 0.75f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isHindi) tab.labelHi else tab.labelEn,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (selected) Color.White else Color.White.copy(alpha = 0.80f),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active Tab Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (activeTab) {
                AdminDashboardTab.OVERVIEW -> {
                    AdminOverviewTab(
                        services = services,
                        categories = effectiveCategories,
                        orders = orders,
                        isHindi = isHindi,
                        onNavigateTab = { tab -> activeTab = tab },
                        onStartAddNewService = {
                            editingServiceDraft = ServiceItem(
                                id = "",
                                title = "",
                                titleHi = "",
                                category = effectiveCategories.firstOrNull()?.key ?: "resume",
                                shortDescription = "",
                                shortDescriptionHi = "",
                                description = "",
                                descriptionHi = "",
                                price = 100L,
                                turnaroundTime = "2 Hours",
                                requirements = emptyList(),
                                imageUrl = PresetCoverImages.first().second,
                                iconKey = "badge",
                                active = true,
                                sortOrder = (services.size + 1).toLong()
                            )
                            activeTab = AdminDashboardTab.ADD_EDIT
                        },
                        onSeedDefaultCatalogue = onSeedDefaultCatalogue,
                        onOpenOrderDetail = onOpenOrderDetail,
                        onAdminQuickUpdateOrder = onAdminQuickUpdateOrder
                    )
                }

                AdminDashboardTab.SERVICES -> {
                    AdminManageServicesTab(
                        services = services,
                        categories = effectiveCategories,
                        isHindi = isHindi,
                        onEditService = { svc ->
                            editingServiceDraft = svc
                            activeTab = AdminDashboardTab.ADD_EDIT
                        },
                        onToggleActive = { svc, nextActive ->
                            onSaveService(svc.copy(active = nextActive))
                        },
                        onToggleFeatured = { svc, nextFeatured ->
                            onSaveService(svc.copy(isFeatured = nextFeatured))
                        },
                        onArchiveOrRestore = onArchiveOrRestoreService,
                        onPermanentDelete = onPermanentlyDeleteService,
                        onAddNewClick = {
                            editingServiceDraft = ServiceItem(
                                id = "",
                                title = "",
                                titleHi = "",
                                category = effectiveCategories.firstOrNull()?.key ?: "resume",
                                price = 100L,
                                turnaroundTime = "2 Hours",
                                requirements = emptyList(),
                                imageUrl = PresetCoverImages.first().second,
                                iconKey = "badge",
                                active = true,
                                sortOrder = (services.size + 1).toLong()
                            )
                            activeTab = AdminDashboardTab.ADD_EDIT
                        }
                    )
                }

                AdminDashboardTab.ADD_EDIT -> {
                    AdminAdvancedAddEditServiceTab(
                        initialService = editingServiceDraft,
                        categories = effectiveCategories,
                        existingServicesCount = services.size,
                        isHindi = isHindi,
                        onSaveService = { savedSvc ->
                            onSaveService(savedSvc)
                            activeTab = AdminDashboardTab.SERVICES
                        },
                        onSaveNewCategory = onSaveCategory,
                        onCancel = { activeTab = AdminDashboardTab.SERVICES }
                    )
                }

                AdminDashboardTab.DISPLAY_ORDER -> {
                    AdminDisplayOrderTab(
                        services = services.filter { !it.isArchived },
                        categories = effectiveCategories,
                        isHindi = isHindi,
                        onPublishServiceOrder = onPublishServiceOrder
                    )
                }

                AdminDashboardTab.CATEGORIES -> {
                    AdminCategoryManagerTab(
                        categories = effectiveCategories,
                        services = services,
                        isHindi = isHindi,
                        onSaveCategory = onSaveCategory,
                        onReorderCategories = onReorderCategories,
                        onDeleteCategoryWithReassignment = onDeleteCategoryWithReassignment
                    )
                }

                AdminDashboardTab.CONTACT_SOCIAL -> {
                    AdminContactAndSocialManagerTab(
                        contactConfig = contactConfig,
                        isHindi = isHindi,
                        onSaveContactConfig = onSaveContactConfig
                    )
                }
            }
        }
    }
}

/**
 * TAB 1: MODERN ADMIN OVERVIEW (Summary Cards, Quick Actions, Recent Orders & UPI Verification)
 */
@Composable
private fun AdminOverviewTab(
    services: List<ServiceItem>,
    categories: List<ServiceCategoryItem>,
    orders: List<ServiceOrder>,
    isHindi: Boolean,
    onNavigateTab: (AdminDashboardTab) -> Unit,
    onStartAddNewService: () -> Unit,
    onSeedDefaultCatalogue: () -> Unit,
    onOpenOrderDetail: (ServiceOrder) -> Unit,
    onAdminQuickUpdateOrder: (
        orderId: String,
        newStatus: String,
        newPaymentStatus: String,
        adminNote: String,
        finalDocumentUrl: String
    ) -> Unit
) {
    val nonArchived = remember(services) { services.filter { !it.isArchived } }
    val totalServicesCount = nonArchived.size
    val activeServicesCount = nonArchived.count { it.active }
    val featuredServicesCount = nonArchived.count { it.isFeatured && it.active }
    val pendingOrdersCount = orders.count {
        it.status == "pending" || it.status == "processing" || it.paymentStatus == "pending_verification"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_overview_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Real Database Summary Metric Cards (2x2 Grid)
        item(key = "summary_metrics_grid") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (isHindi) "लाइव डैशबोर्ड सारांश" else "Live Database Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminMetricSummaryCard(
                        title = if (isHindi) "कुल सेवाएं" else "Total Services",
                        value = totalServicesCount.toString(),
                        subtitle = if (isHindi) "${categories.size} श्रेणियाँ" else "Across ${categories.size} categories",
                        accentColor = ElectricCyan,
                        icon = Icons.Default.FolderSpecial,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(AdminDashboardTab.SERVICES) }
                    )
                    AdminMetricSummaryCard(
                        title = if (isHindi) "सक्रिय सेवाएं" else "Active Services",
                        value = activeServicesCount.toString(),
                        subtitle = if (isHindi) "ऑर्डर के लिए उपलब्ध" else "Live for orders",
                        accentColor = StatusVerifiedGreen,
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(AdminDashboardTab.SERVICES) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdminMetricSummaryCard(
                        title = if (isHindi) "प्रमुख सेवाएं" else "Featured Services",
                        value = featuredServicesCount.toString(),
                        subtitle = if (isHindi) "होम स्क्रीन हाइलाइट" else "Home carousel priority",
                        accentColor = NeonOrange,
                        icon = Icons.Default.Star,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateTab(AdminDashboardTab.DISPLAY_ORDER) }
                    )
                    AdminMetricSummaryCard(
                        title = if (isHindi) "लंबित ऑर्डर" else "Pending Orders",
                        value = pendingOrdersCount.toString(),
                        subtitle = if (isHindi) "कुल ${orders.size} ऑर्डर" else "Total ${orders.size} orders",
                        accentColor = StatusPendingAmber,
                        icon = Icons.Default.ReceiptLong,
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    )
                }
            }
        }

        // 2. Quick Actions Panel
        item(key = "quick_actions_card") {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                border = BorderStroke(1.dp, NeonOrange.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isHindi) "त्वरित एडमिन कार्य (Quick Actions)" else "Quick Admin Actions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onStartAddNewService,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonOrange,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("overview_add_service_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "नई सेवा जोड़ें" else "Add Service",
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        OutlinedButton(
                            onClick = { onNavigateTab(AdminDashboardTab.DISPLAY_ORDER) },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ElectricCyan),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("overview_reorder_services_btn")
                        ) {
                            Icon(
                                Icons.Default.SwapVert,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "सेवा क्रम बदलें" else "Display Order",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onNavigateTab(AdminDashboardTab.CONTACT_SOCIAL) },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, WhatsAppBrandGreen),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("overview_contact_social_btn")
                        ) {
                            Icon(
                                Icons.Default.ContactPhone,
                                contentDescription = null,
                                tint = WhatsAppBrandGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "संपर्क और सोशल" else "Contact & Social",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        OutlinedButton(
                            onClick = onSeedDefaultCatalogue,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("overview_sync_defaults_btn")
                        ) {
                            Icon(
                                Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "डिफ़ॉल्ट सिंक करें" else "Sync Defaults",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }

        // 3. Recent Customer Orders & Manual UPI Verification
        item(key = "recent_orders_header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHindi) "हाल के ग्राहक ऑर्डर और भुगतान सत्यापन" else "Recent Orders & Payment Verification",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${orders.size} total",
                    style = MaterialTheme.typography.labelMedium,
                    color = ElectricCyan
                )
            }
        }

        if (orders.isEmpty()) {
            item(key = "no_orders_card") {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isHindi) "अभी कोई ग्राहक ऑर्डर नहीं है" else "No customer orders recorded yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }
            }
        } else {
            itemsIndexed(orders.take(10), key = { _, ord -> "admin_ord_${ord.id}" }) { _, order ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenOrderDetail(order) }
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${order.orderNumber} • ${order.serviceTitleSnapshot}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "₹${order.priceSnapshot}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonOrange
                            )
                        }
                        Text(
                            text = "${order.customerName} (${order.customerPhone}) • Status: ${order.status} • Pay: ${order.paymentStatus}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                        if (order.upiReference.isNotBlank()) {
                            Text(
                                text = "UTR / Ref: ${order.upiReference}",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (order.paymentStatus != "verified") {
                                OutlinedButton(
                                    onClick = {
                                        onAdminQuickUpdateOrder(
                                            order.id,
                                            "processing",
                                            "verified",
                                            order.adminNotes.ifBlank { "UPI Payment verified by Admin" },
                                            order.deliveryFileUrl
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, StatusVerifiedGreen),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = if (isHindi) "भुगतान सत्यापित करें" else "Verify Payment",
                                        color = StatusVerifiedGreen,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }
                            OutlinedButton(
                                onClick = { onOpenOrderDetail(order) },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, ElectricCyan),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (isHindi) "विवरण / डिलीवरी" else "Manage / Deliver",
                                    color = ElectricCyan,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
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
private fun AdminMetricSummaryCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = CharcoalCard),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.78f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * TAB 2: FEATURE 2 — EDIT, DELETE, DISABLE & RESTORE SERVICES
 */
@Composable
private fun AdminManageServicesTab(
    services: List<ServiceItem>,
    categories: List<ServiceCategoryItem>,
    isHindi: Boolean,
    onEditService: (ServiceItem) -> Unit,
    onToggleActive: (ServiceItem, Boolean) -> Unit,
    onToggleFeatured: (ServiceItem, Boolean) -> Unit,
    onArchiveOrRestore: (ServiceItem, Boolean) -> Unit,
    onPermanentDelete: (ServiceItem) -> Unit,
    onAddNewClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryKey by remember { mutableStateOf("all") }
    var selectedStatusFilter by remember { mutableStateOf("all") } // all, active, inactive, featured, archived
    var servicePendingArchive by remember { mutableStateOf<ServiceItem?>(null) }
    var servicePendingPermanentDelete by remember { mutableStateOf<ServiceItem?>(null) }

    val filteredServices = remember(services, searchQuery, selectedCategoryKey, selectedStatusFilter) {
        services
            .filter { svc ->
                val matchesStatus = when (selectedStatusFilter) {
                    "active" -> svc.active && !svc.isArchived
                    "inactive" -> !svc.active && !svc.isArchived
                    "featured" -> svc.isFeatured && !svc.isArchived
                    "archived" -> svc.isArchived
                    else -> !svc.isArchived
                }
                val matchesCat = selectedCategoryKey == "all" || svc.category.equals(selectedCategoryKey, ignoreCase = true)
                val q = searchQuery.trim().lowercase()
                val matchesSearch = q.isEmpty() ||
                    svc.title.lowercase().contains(q) ||
                    svc.titleHi.lowercase().contains(q) ||
                    svc.description.lowercase().contains(q) ||
                    svc.category.lowercase().contains(q)
                matchesStatus && matchesCat && matchesSearch
            }
            .sortedBy { it.sortOrder }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_manage_services_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "search_and_filters") {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "सेवा प्रबंधन तालिका (${filteredServices.size})" else "Service Management (${filteredServices.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Button(
                            onClick = onAddNewClick,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonOrange, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isHindi) "जोड़ें" else "New Service",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                if (isHindi) "सेवा का नाम, श्रेणी या विवरण खोजें…"
                                else "Search service name, category, or description…"
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = NeonOrange)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_manage_services_search")
                    )

                    // Status Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val statuses = listOf(
                            "all" to (if (isHindi) "सभी (All)" else "All Active/Inactive"),
                            "active" to (if (isHindi) "सक्रिय (Active)" else "Active"),
                            "inactive" to (if (isHindi) "निष्क्रिय (Inactive)" else "Inactive"),
                            "featured" to (if (isHindi) "प्रमुख (Featured)" else "Featured"),
                            "archived" to (if (isHindi) "आर्काइव / हटाए गए" else "Archived / Deleted")
                        )
                        statuses.forEach { (key, label) ->
                            FilterChip(
                                selected = selectedStatusFilter == key,
                                onClick = { selectedStatusFilter = key },
                                label = { Text(label, maxLines = 1, softWrap = false) },
                                modifier = Modifier.testTag("admin_status_filter_$key")
                            )
                        }
                    }

                    // Category Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedCategoryKey == "all",
                            onClick = { selectedCategoryKey = "all" },
                            label = { Text(if (isHindi) "सभी श्रेणियाँ" else "All Categories", maxLines = 1) }
                        )
                        categories.forEach { cat ->
                            FilterChip(
                                selected = selectedCategoryKey.equals(cat.key, ignoreCase = true),
                                onClick = { selectedCategoryKey = cat.key },
                                label = { Text(cat.localizedLabel(isHindi), maxLines = 1, softWrap = false) }
                            )
                        }
                    }
                }
            }
        }

        if (filteredServices.isEmpty()) {
            item(key = "empty_services_notice") {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isHindi) "कोई मिलान सेवा नहीं मिली" else "No matching services in this view",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            itemsIndexed(filteredServices, key = { _, item -> "manage_svc_${item.id}" }) { _, svc ->
                val (svcIcon, svcAccent) = resolveAdminServiceIcon(svc.iconKey, svc.category)
                val categoryLabel = categories.find { it.key.equals(svc.category, ignoreCase = true) }
                    ?.localizedLabel(isHindi) ?: svc.category

                Card(
                    colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                    border = BorderStroke(
                        width = 1.dp,
                        color = when {
                            svc.isArchived -> StatusErrorRed.copy(alpha = 0.5f)
                            svc.isFeatured -> NeonOrange.copy(alpha = 0.65f)
                            svc.active -> ElectricCyan.copy(alpha = 0.35f)
                            else -> Color.White.copy(alpha = 0.15f)
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_service_row_${svc.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, svcAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            ) {
                                ServiceCoverImagePreview(
                                    imageUrl = svc.imageUrl,
                                    contentDescription = svc.title,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = svcIcon,
                                        contentDescription = null,
                                        tint = svcAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = svc.localizedTitle(isHindi),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "$categoryLabel • ₹${svc.price} • ${svc.turnaroundTime} • ${svc.requirements.size} Docs",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.72f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val statusText = when {
                                        svc.isArchived -> if (isHindi) "आर्काइव (Archived)" else "Archived"
                                        svc.active -> if (isHindi) "सक्रिय (Active)" else "Active"
                                        else -> if (isHindi) "निष्क्रिय (Inactive)" else "Inactive"
                                    }
                                    val statusColor = when {
                                        svc.isArchived -> StatusErrorRed
                                        svc.active -> StatusVerifiedGreen
                                        else -> StatusPendingAmber
                                    }
                                    Text(
                                        text = statusText,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = statusColor
                                    )
                                    if (svc.isFeatured && !svc.isArchived) {
                                        Text(
                                            text = "• ★ Featured (#${svc.featuredOrder})",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = NeonOrange
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

                        if (svc.isArchived) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { onArchiveOrRestore(svc, false) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = StatusVerifiedGreen,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("admin_restore_service_${svc.id}")
                                ) {
                                    Icon(Icons.Default.RestoreFromTrash, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHindi) "पुनर्स्थापित करें (Restore)" else "Restore Service",
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                                OutlinedButton(
                                    onClick = { servicePendingPermanentDelete = svc },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, StatusErrorRed),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("admin_perm_delete_service_${svc.id}")
                                ) {
                                    Icon(
                                        Icons.Default.DeleteForever,
                                        contentDescription = null,
                                        tint = StatusErrorRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHindi) "स्थायी रूप से हटाएं" else "Delete Forever",
                                        color = StatusErrorRed,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = { onEditService(svc) },
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, NeonOrange),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier
                                            .height(36.dp)
                                            .testTag("admin_edit_btn_${svc.id}")
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = NeonOrange,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isHindi) "संपादित करें" else "Edit",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = { onToggleActive(svc, !svc.active) },
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(
                                            1.dp,
                                            if (svc.active) StatusPendingAmber else StatusVerifiedGreen
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier
                                            .height(36.dp)
                                            .testTag("admin_toggle_active_btn_${svc.id}")
                                    ) {
                                        Icon(
                                            Icons.Default.PowerSettingsNew,
                                            contentDescription = null,
                                            tint = if (svc.active) StatusPendingAmber else StatusVerifiedGreen,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (svc.active) {
                                                if (isHindi) "निष्क्रिय करें" else "Disable"
                                            } else {
                                                if (isHindi) "सक्रिय करें" else "Enable"
                                            },
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { onToggleFeatured(svc, !svc.isFeatured) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("admin_toggle_featured_btn_${svc.id}")
                                    ) {
                                        Icon(
                                            imageVector = if (svc.isFeatured) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = "Toggle Featured",
                                            tint = if (svc.isFeatured) NeonOrange else Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                    IconButton(
                                        onClick = { servicePendingArchive = svc },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("admin_archive_btn_${svc.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Archive,
                                            contentDescription = "Archive / Delete Service",
                                            tint = StatusErrorRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Soft-Delete / Archive Confirmation Dialog
    if (servicePendingArchive != null) {
        val target = servicePendingArchive!!
        AlertDialog(
            onDismissRequest = { servicePendingArchive = null },
            containerColor = CharcoalCard,
            title = {
                Text(
                    text = if (isHindi) "सेवा आर्काइव / हटाएं?" else "Archive / Soft-Delete Service?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isHindi) {
                        "'${target.localizedTitle(true)}' को वेबसाइट और ऐप से छिपा दिया जाएगा। ग्राहकों के पुराने ऑर्डर सुरक्षित रहेंगे और आप इसे कभी भी 'Archived' टैब से वापस ला सकते हैं।"
                    } else {
                        "'${target.title}' will be hidden from new applications on the website and Android app. Existing customer orders remain 100% intact, and you can restore this service anytime from the Archived filter."
                    },
                    color = Color.White.copy(alpha = 0.85f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onArchiveOrRestore(target, true)
                        servicePendingArchive = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusErrorRed, contentColor = Color.White),
                    modifier = Modifier.testTag("confirm_archive_service_btn")
                ) {
                    Text(if (isHindi) "आर्काइव करें" else "Archive Service", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { servicePendingArchive = null }) {
                    Text(if (isHindi) "रद्द करें" else "Cancel")
                }
            }
        )
    }

    // Permanent Delete Warning Dialog
    if (servicePendingPermanentDelete != null) {
        val target = servicePendingPermanentDelete!!
        AlertDialog(
            onDismissRequest = { servicePendingPermanentDelete = null },
            containerColor = CharcoalCard,
            title = {
                Text(
                    text = if (isHindi) "स्थायी रूप से हटाने की चेतावनी!" else "Permanent Deletion Warning!",
                    color = StatusErrorRed,
                    fontWeight = FontWeight.ExtraBold
                )
            },
            text = {
                Text(
                    text = if (isHindi) {
                        "क्या आप वाकई '${target.localizedTitle(true)}' को डेटाबेस से स्थायी रूप से हटाना चाहते हैं? (ग्राहकों के पुराने ऑर्डर रिकॉर्ड सुरक्षित रहेंगे, लेकिन यह सेवा वापस नहीं आएगी)।"
                    } else {
                        "Are you sure you want to permanently delete '${target.title}' from the catalogue? Historical customer orders will not be deleted, but this service template cannot be recovered."
                    },
                    color = Color.White.copy(alpha = 0.85f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onPermanentDelete(target)
                        servicePendingPermanentDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusErrorRed, contentColor = Color.White),
                    modifier = Modifier.testTag("confirm_perm_delete_service_btn")
                ) {
                    Text(if (isHindi) "स्थायी रूप से हटाएं" else "Delete Permanently", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { servicePendingPermanentDelete = null }) {
                    Text(if (isHindi) "रद्द करें" else "Cancel")
                }
            }
        )
    }
}

/**
 * TAB 3: FEATURE 1 — ADVANCED ADD / EDIT SERVICE FORM
 */
@Composable
private fun AdminAdvancedAddEditServiceTab(
    initialService: ServiceItem,
    categories: List<ServiceCategoryItem>,
    existingServicesCount: Int,
    isHindi: Boolean,
    onSaveService: (ServiceItem) -> Unit,
    onSaveNewCategory: (ServiceCategoryItem) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var title by remember(initialService.id) { mutableStateOf(initialService.title) }
    var titleHi by remember(initialService.id) { mutableStateOf(initialService.titleHi) }
    var selectedCategoryKey by remember(initialService.id) {
        mutableStateOf(initialService.category.ifBlank { categories.firstOrNull()?.key ?: "resume" })
    }
    var categorySearchQuery by remember { mutableStateOf("") }
    var showInlineAddCategory by remember { mutableStateOf(false) }
    var newCategoryNameEn by remember { mutableStateOf("") }
    var newCategoryNameHi by remember { mutableStateOf("") }

    var shortDescEn by remember(initialService.id) { mutableStateOf(initialService.shortDescription) }
    var shortDescHi by remember(initialService.id) { mutableStateOf(initialService.shortDescriptionHi) }
    var fullDescEn by remember(initialService.id) { mutableStateOf(initialService.description) }
    var fullDescHi by remember(initialService.id) { mutableStateOf(initialService.descriptionHi) }

    var priceText by remember(initialService.id) { mutableStateOf(initialService.price.toString()) }
    var turnaroundTime by remember(initialService.id) { mutableStateOf(initialService.turnaroundTime.ifBlank { "2 Hours" }) }
    var sortOrderText by remember(initialService.id) { mutableStateOf(initialService.sortOrder.toString()) }
    var featuredOrderText by remember(initialService.id) { mutableStateOf(initialService.featuredOrder.toString()) }
    var isActive by remember(initialService.id) { mutableStateOf(initialService.active) }
    var isFeatured by remember(initialService.id) { mutableStateOf(initialService.isFeatured) }

    // Icon & Cover Image State
    var selectedIconKey by remember(initialService.id) { mutableStateOf(initialService.iconKey.ifBlank { "badge" }) }
    var iconSearchQuery by remember { mutableStateOf("") }
    var imageUrl by remember(initialService.id) { mutableStateOf(initialService.imageUrl) }
    var isUploadingImage by remember { mutableStateOf(false) }
    var imageUploadFeedback by remember { mutableStateOf<String?>(null) }
    var imageUploadIsError by remember { mutableStateOf(false) }

    // 25-Item Reusable Document Library State
    val initialParsedDocs = remember(initialService.id) {
        initialService.requirements.mapIndexed { index, raw ->
            DocumentRequirementSpec.fromFormattedString(raw, index)
        }
    }
    val selectedDocsMap = remember(initialService.id) {
        mutableStateListOf<DocumentRequirementSpec>().apply {
            addAll(initialParsedDocs)
        }
    }
    val customDocCatalog = remember(initialService.id) {
        mutableStateListOf<DocumentRequirementSpec>().apply {
            val stdNames = DocumentLibraryCatalog.defaultDocuments.map { it.labelEn.lowercase() }.toSet()
            initialParsedDocs.filter { it.labelEn.lowercase() !in stdNames }.forEach { add(it) }
        }
    }
    var docSearchQuery by remember { mutableStateOf("") }
    var newCustomDocName by remember { mutableStateOf("") }
    var newCustomDocInstruction by remember { mutableStateOf("") }
    var newCustomDocRequired by remember { mutableStateOf(true) }

    var validationError by remember { mutableStateOf<String?>(null) }

    // Device Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isUploadingImage = true
            imageUploadFeedback = if (isHindi) "छवि की जांच और संपीड़न हो रहा है…" else "Validating & compressing image…"
            imageUploadIsError = false
            coroutineScope.launch {
                val result = encodeUriToCompressedDataUrl(context, uri)
                isUploadingImage = false
                result.onSuccess { dataUri ->
                    imageUrl = dataUri
                    imageUploadIsError = false
                    imageUploadFeedback = if (isHindi) {
                        "छवि सफलतापूर्वक अपलोड और तैयार हो गई (${dataUri.length / 1024} KB)।"
                    } else {
                        "Cover image uploaded & optimized (${dataUri.length / 1024} KB)."
                    }
                }.onFailure { err ->
                    imageUploadIsError = true
                    imageUploadFeedback = err.message ?: "Image upload failed."
                }
            }
        }
    }

    val allLibraryDocs = remember(customDocCatalog.size) {
        DocumentLibraryCatalog.defaultDocuments + customDocCatalog
    }
    val filteredLibraryDocs = remember(allLibraryDocs, docSearchQuery) {
        val q = docSearchQuery.trim().lowercase()
        if (q.isEmpty()) allLibraryDocs
        else allLibraryDocs.filter {
            it.labelEn.lowercase().contains(q) ||
                it.labelHi.lowercase().contains(q) ||
                it.instructions.lowercase().contains(q)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_advanced_add_edit_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Basic Information & Category Management
        item(key = "basic_service_info") {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                border = BorderStroke(1.dp, NeonOrange.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (initialService.id.isBlank()) {
                            if (isHindi) "1. नई सेवा का मुख्य विवरण (Add New Service)" else "1. Service Core Information (Add New)"
                        } else {
                            if (isHindi) "1. सेवा विवरण संपादित करें (Edit Service)" else "1. Edit Service Information"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonOrange
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            validationError = null
                        },
                        label = { Text(if (isHindi) "सेवा का नाम (English) *" else "Service Name (English) *") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("adv_service_title_input")
                    )

                    OutlinedTextField(
                        value = titleHi,
                        onValueChange = { titleHi = it },
                        label = { Text(if (isHindi) "सेवा का नाम (हिंदी में)" else "Service Name (Hindi / हिंदी)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("adv_service_title_hi_input")
                    )

                    // Category Selection with Search + Add New Category
                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "श्रेणी चुनें (Category)" else "Select Category",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        TextButton(
                            onClick = { showInlineAddCategory = !showInlineAddCategory },
                            modifier = Modifier.testTag("adv_toggle_inline_add_category_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isHindi) "नई श्रेणी बनाएं" else "Add New Category",
                                color = ElectricCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedTextField(
                        value = categorySearchQuery,
                        onValueChange = { categorySearchQuery = it },
                        placeholder = { Text(if (isHindi) "श्रेणी खोजें…" else "Search existing categories…") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    val matchingCategories = categories.filter {
                        val q = categorySearchQuery.trim().lowercase()
                        q.isEmpty() || it.labelEn.lowercase().contains(q) || it.labelHi.lowercase().contains(q) || it.key.lowercase().contains(q)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        matchingCategories.forEach { cat ->
                            FilterChip(
                                selected = selectedCategoryKey.equals(cat.key, ignoreCase = true),
                                onClick = { selectedCategoryKey = cat.key },
                                label = { Text(cat.localizedLabel(isHindi), maxLines = 1, softWrap = false) },
                                modifier = Modifier.testTag("adv_category_chip_${cat.key}")
                            )
                        }
                    }

                    AnimatedVisibility(visible = showInlineAddCategory) {
                        Surface(
                            color = CharcoalElevated,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (isHindi) "नई श्रेणी जोड़ें" else "Create New Category",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricCyan
                                )
                                OutlinedTextField(
                                    value = newCategoryNameEn,
                                    onValueChange = { newCategoryNameEn = it },
                                    label = { Text("Category Name (English) *") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("adv_new_category_en_input")
                                )
                                OutlinedTextField(
                                    value = newCategoryNameHi,
                                    onValueChange = { newCategoryNameHi = it },
                                    label = { Text("Category Name (Hindi)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Button(
                                    onClick = {
                                        val cleanEn = newCategoryNameEn.trim()
                                        if (cleanEn.length >= 2) {
                                            val generatedKey = cleanEn.lowercase()
                                                .replace(Regex("[^a-z0-9]+"), "_")
                                                .trim('_')
                                                .take(32)
                                            val existingMatch = categories.firstOrNull {
                                                it.key.equals(generatedKey, ignoreCase = true) ||
                                                    it.labelEn.equals(cleanEn, ignoreCase = true)
                                            }
                                            if (existingMatch != null) {
                                                selectedCategoryKey = existingMatch.key
                                                validationError = if (isHindi) {
                                                    "यह श्रेणी पहले से मौजूद है — इसे चुन लिया गया है।"
                                                } else {
                                                    "Category '${existingMatch.labelEn}' already exists — selected automatically."
                                                }
                                            } else {
                                                val newCat = ServiceCategoryItem(
                                                    id = generatedKey,
                                                    key = generatedKey,
                                                    labelEn = cleanEn,
                                                    labelHi = newCategoryNameHi.trim().ifBlank { cleanEn },
                                                    iconKey = selectedIconKey,
                                                    sortOrder = (categories.size + 1).toLong(),
                                                    active = true
                                                )
                                                onSaveNewCategory(newCat)
                                                selectedCategoryKey = generatedKey
                                            }
                                            newCategoryNameEn = ""
                                            newCategoryNameHi = ""
                                            showInlineAddCategory = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = Color.Black),
                                    modifier = Modifier.testTag("adv_save_inline_category_btn")
                                ) {
                                    Text(if (isHindi) "श्रेणी सहेजें और चुनें" else "Save & Select Category", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Descriptions
                    OutlinedTextField(
                        value = shortDescEn,
                        onValueChange = { shortDescEn = it },
                        label = { Text(if (isHindi) "संक्षिप्त विवरण (Short Summary - English)" else "Short Description / Card Summary (English)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("adv_service_short_desc_input")
                    )

                    OutlinedTextField(
                        value = shortDescHi,
                        onValueChange = { shortDescHi = it },
                        label = { Text(if (isHindi) "संक्षिप्त विवरण (हिंदी में)" else "Short Description (Hindi)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = fullDescEn,
                        onValueChange = {
                            fullDescEn = it
                            validationError = null
                        },
                        label = { Text(if (isHindi) "पूर्ण सेवा विवरण (Full Description - English) *" else "Full Service Description (English) *") },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("adv_service_full_desc_input")
                    )

                    OutlinedTextField(
                        value = fullDescHi,
                        onValueChange = { fullDescHi = it },
                        label = { Text(if (isHindi) "पूर्ण सेवा विवरण (हिंदी में)" else "Full Service Description (Hindi)") },
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Price, Processing Time & Position
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = {
                                priceText = it.filter { ch -> ch.isDigit() }
                                validationError = null
                            },
                            label = { Text(if (isHindi) "शुल्क (₹ INR) *" else "Price (₹ INR) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("adv_service_price_input")
                        )

                        OutlinedTextField(
                            value = turnaroundTime,
                            onValueChange = { turnaroundTime = it },
                            label = { Text(if (isHindi) "प्रोसेसिंग समय *" else "Processing Time *") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("adv_service_time_input")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = sortOrderText,
                            onValueChange = { sortOrderText = it.filter { ch -> ch.isDigit() } },
                            label = { Text(if (isHindi) "प्रदर्शन क्रम (Sort Order)" else "Display Position (Sort)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = featuredOrderText,
                            onValueChange = { featuredOrderText = it.filter { ch -> ch.isDigit() } },
                            label = { Text(if (isHindi) "प्रमुख क्रम (Featured Priority)" else "Featured Priority") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Active & Featured Switches
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHindi) "सेवा सक्रिय (Active Status)" else "Service Active for Orders",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (isHindi) "निष्क्रिय करने पर नए आवेदन बंद रहेंगे" else "When off, hidden from new applications",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                        }
                        Switch(checked = isActive, onCheckedChange = { isActive = it })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHindi) "प्रमुख / हाइलाइट सेवा (Featured)" else "Featured / Highlighted on Home",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = NeonOrange
                            )
                            Text(
                                text = if (isHindi) "होम स्क्रीन के टॉप सेक्शन में दिखाएं" else "Pin to highlighted Home screen carousel",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                        }
                        Switch(
                            checked = isFeatured,
                            onCheckedChange = { isFeatured = it },
                            modifier = Modifier.testTag("adv_service_featured_switch")
                        )
                    }
                }
            }
        }

        // Section 2: Built-in Searchable Icon Picker & Device Cover Image Upload
        item(key = "icon_and_cover_upload") {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isHindi) "2. सेवा आइकन और कवर फोटो अपलोड" else "2. Service Icon Picker & Cover Image Upload",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricCyan
                    )

                    OutlinedTextField(
                        value = iconSearchQuery,
                        onValueChange = { iconSearchQuery = it },
                        placeholder = { Text(if (isHindi) "आइकन खोजें (जैसे: pdf, pan, job, bank)…" else "Search built-in icons (e.g. pdf, pan, job, bank)…") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    val matchingIcons = AdminIconLibrary.filter {
                        val q = iconSearchQuery.trim().lowercase()
                        q.isEmpty() || it.label.lowercase().contains(q) || it.key.lowercase().contains(q)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        matchingIcons.forEach { option ->
                            val isSelected = selectedIconKey == option.key
                            Surface(
                                onClick = { selectedIconKey = option.key },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) option.accent.copy(alpha = 0.22f) else CharcoalElevated,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) option.accent else Color.White.copy(alpha = 0.12f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = option.icon,
                                        contentDescription = option.label,
                                        tint = option.accent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = option.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

                    // Cover Image Preview + Upload from Device
                    Text(
                        text = if (isHindi) "सेवा कवर फोटो (Preview & Upload)" else "Service Cover Image (Preview & Device Upload)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.5.dp, ElectricCyan, RoundedCornerShape(14.dp))
                        ) {
                            ServiceCoverImagePreview(
                                imageUrl = imageUrl,
                                contentDescription = "Cover Preview",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricCyan,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("adv_upload_cover_image_btn")
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "डिवाइस से फोटो चुनें" else "Upload from Device",
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = if (isHindi) {
                                    "समर्थित: JPG, PNG, WEBP (अधिकतम 8MB, ऑटो-ऑप्टिमाइज़्ड)"
                                } else {
                                    "Supports JPG, PNG, WEBP (Validated & optimized for Web + Android)"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                        }
                    }

                    if (isUploadingImage) {
                        LinearProgressIndicator(
                            color = NeonOrange,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (imageUploadFeedback != null) {
                        Text(
                            text = imageUploadFeedback!!,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (imageUploadIsError) StatusErrorRed else StatusVerifiedGreen
                        )
                    }

                    // Preset Cover Picker or Direct HTTPS URL
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PresetCoverImages.forEach { (label, presetUrl) ->
                            FilterChip(
                                selected = imageUrl == presetUrl,
                                onClick = {
                                    imageUrl = presetUrl
                                    imageUploadIsError = false
                                    imageUploadFeedback = "Selected preset: $label"
                                },
                                label = { Text(label, maxLines = 1, softWrap = false) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = if (imageUrl.startsWith("data:image/")) "[Uploaded Device Image Data URI]" else imageUrl,
                        onValueChange = { input ->
                            if (!input.startsWith("[Uploaded")) {
                                imageUrl = input
                            }
                        },
                        label = { Text(if (isHindi) "या इमेज HTTPS URL दर्ज करें" else "Or enter direct HTTPS Image URL") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Section 3: Reusable 25-Item Document Library Selector
        item(key = "document_library_selector") {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                border = BorderStroke(1.dp, StatusVerifiedGreen.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHindi) "3. आवश्यक दस्तावेज़ लाइब्रेरी (Document Library)" else "3. Required Document Selector Library",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = StatusVerifiedGreen
                            )
                            Text(
                                text = if (isHindi) {
                                    "आवश्यक या वैकल्पिक दस्तावेज़ चुनें (${selectedDocsMap.size} चयनित)"
                                } else {
                                    "Select required or optional documents (${selectedDocsMap.size} selected)"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.72f)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = docSearchQuery,
                        onValueChange = { docSearchQuery = it },
                        placeholder = {
                            Text(
                                if (isHindi) "आधार, पैन, फोटो, अंकपत्र, आय प्रमाण पत्र खोजें…"
                                else "Search Aadhaar, PAN, Photo, Marksheet, Income Certificate…"
                            )
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = StatusVerifiedGreen) },
                        trailingIcon = {
                            if (docSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { docSearchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_doc_library_search")
                    )

                    // Add Custom Document Type Row
                    Surface(
                        color = CharcoalElevated,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isHindi) "+ कस्टम दस्तावेज़ जोड़ें (Add Custom Document)" else "+ Add Custom Document Type",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = NeonOrange
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = newCustomDocName,
                                    onValueChange = { newCustomDocName = it },
                                    label = { Text(if (isHindi) "दस्तावेज़ का नाम" else "Document Label") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("admin_add_custom_doc_input")
                                )
                                OutlinedTextField(
                                    value = newCustomDocInstruction,
                                    onValueChange = { newCustomDocInstruction = it },
                                    label = { Text(if (isHindi) "निर्देश (Instruction)" else "Instruction") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = newCustomDocRequired,
                                        onCheckedChange = { newCustomDocRequired = it }
                                    )
                                    Text(
                                        text = if (isHindi) "अनिवार्य (Required)" else "Mandatory (Required)",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White
                                    )
                                }
                                Button(
                                    onClick = {
                                        val cleanName = newCustomDocName.trim()
                                        if (cleanName.length >= 2) {
                                            val alreadyExists = selectedDocsMap.any { it.labelEn.equals(cleanName, ignoreCase = true) }
                                            val customId = "doc_custom_${cleanName.lowercase().replace(Regex("[^a-z0-9]+"), "_").take(20)}"
                                            val spec = DocumentRequirementSpec(
                                                id = customId,
                                                labelEn = cleanName,
                                                labelHi = cleanName,
                                                isRequired = newCustomDocRequired,
                                                instructions = newCustomDocInstruction.trim()
                                            )
                                            if (customDocCatalog.none { it.labelEn.equals(cleanName, ignoreCase = true) } &&
                                                DocumentLibraryCatalog.defaultDocuments.none { it.labelEn.equals(cleanName, ignoreCase = true) }
                                            ) {
                                                customDocCatalog.add(spec)
                                            }
                                            if (!alreadyExists && selectedDocsMap.size < 25) {
                                                selectedDocsMap.add(spec)
                                            }
                                            newCustomDocName = ""
                                            newCustomDocInstruction = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonOrange, contentColor = Color.Black),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("admin_add_custom_doc_btn")
                                ) {
                                    Text(if (isHindi) "सूची में जोड़ें" else "Add Document", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Scrollable Document Checkboxes with Required/Optional toggle & instruction editor
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        filteredLibraryDocs.forEach { libDoc ->
                            val selectedIndex = selectedDocsMap.indexOfFirst {
                                it.id == libDoc.id || it.labelEn.equals(libDoc.labelEn, ignoreCase = true)
                            }
                            val isSelected = selectedIndex >= 0
                            val currentSpec = if (isSelected) selectedDocsMap[selectedIndex] else libDoc

                            Surface(
                                color = if (isSelected) StatusVerifiedGreen.copy(alpha = 0.12f) else CharcoalElevated,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) StatusVerifiedGreen.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    if (isSelected) {
                                                        selectedDocsMap.removeAt(selectedIndex)
                                                    } else if (selectedDocsMap.size < 25) {
                                                        selectedDocsMap.add(libDoc)
                                                    }
                                                }
                                        ) {
                                            Checkbox(
                                                checked = isSelected,
                                                onCheckedChange = { checked ->
                                                    if (checked && !isSelected && selectedDocsMap.size < 25) {
                                                        selectedDocsMap.add(libDoc)
                                                    } else if (!checked && isSelected) {
                                                        selectedDocsMap.removeAt(selectedIndex)
                                                    }
                                                },
                                                modifier = Modifier.testTag("doc_checkbox_${libDoc.labelEn.lowercase().replace(" ", "_")}")
                                            )
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = if (isHindi && currentSpec.labelHi.isNotBlank()) {
                                                        "${currentSpec.labelHi} (${currentSpec.labelEn})"
                                                    } else {
                                                        currentSpec.labelEn
                                                    },
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                if (currentSpec.instructions.isNotBlank()) {
                                                    Text(
                                                        text = currentSpec.instructions,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.White.copy(alpha = 0.68f)
                                                    )
                                                }
                                            }
                                        }

                                        if (isSelected) {
                                            FilterChip(
                                                selected = currentSpec.isRequired,
                                                onClick = {
                                                    selectedDocsMap[selectedIndex] = currentSpec.copy(isRequired = !currentSpec.isRequired)
                                                },
                                                label = {
                                                    Text(
                                                        text = if (currentSpec.isRequired) {
                                                            if (isHindi) "अनिवार्य" else "Required"
                                                        } else {
                                                            if (isHindi) "वैकल्पिक" else "Optional"
                                                        },
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                },
                                                modifier = Modifier.testTag("doc_req_toggle_${libDoc.id}")
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 6.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = currentSpec.labelEn,
                                                onValueChange = { updatedLabel ->
                                                    if (updatedLabel.isNotBlank()) {
                                                        selectedDocsMap[selectedIndex] = currentSpec.copy(labelEn = updatedLabel.take(60))
                                                    }
                                                },
                                                label = {
                                                    Text(if (isHindi) "दस्तावेज़ का नाम" else "Document Label")
                                                },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .weight(0.42f)
                                                    .testTag("doc_label_edit_${libDoc.id}")
                                            )
                                            OutlinedTextField(
                                                value = currentSpec.instructions,
                                                onValueChange = { updatedInstr ->
                                                    selectedDocsMap[selectedIndex] = currentSpec.copy(instructions = updatedInstr.take(90))
                                                },
                                                label = {
                                                    Text(if (isHindi) "निर्देश संपादित करें" else "Custom instruction")
                                                },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .weight(0.58f)
                                                    .testTag("doc_instruction_edit_${libDoc.id}")
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Final Document Checklist Preview before publishing
                    HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
                    Surface(
                        color = ObsidianBg,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_final_doc_checklist_preview")
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isHindi) {
                                    "अंतिम दस्तावेज़ चेकलिस्ट प्रीव्यू (${selectedDocsMap.size}):"
                                } else {
                                    "Final Document Checklist Preview (${selectedDocsMap.size}):"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = ElectricCyan
                            )
                            if (selectedDocsMap.isEmpty()) {
                                Text(
                                    text = if (isHindi) "कृपया कम से कम 1 दस्तावेज़ चुनें।" else "Please select at least 1 document requirement.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = StatusErrorRed
                                )
                            } else {
                                selectedDocsMap.forEachIndexed { idx, spec ->
                                    Text(
                                        text = "${idx + 1}. ${spec.toFormattedRequirementString()}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Validation & Publish Button
        item(key = "publish_service_footer") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (validationError != null) {
                    Surface(
                        color = StatusErrorRed.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, StatusErrorRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = validationError!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = StatusErrorRed,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    ) {
                        Text(if (isHindi) "रद्द करें" else "Cancel", color = Color.White)
                    }

                    Button(
                        onClick = {
                            val parsedPrice = priceText.toLongOrNull() ?: 0L
                            val parsedSort = sortOrderText.toLongOrNull() ?: (existingServicesCount + 1).toLong()
                            val parsedFeaturedOrder = featuredOrderText.toLongOrNull() ?: 999L
                            val encodedDocs = selectedDocsMap.map { it.toFormattedRequirementString() }

                            when {
                                title.trim().length < 2 -> {
                                    validationError = if (isHindi) "कृपया सेवा का वैध नाम लिखें।" else "Please enter a valid Service Name (min 2 characters)."
                                }
                                parsedPrice <= 0L || parsedPrice > 100000L -> {
                                    validationError = if (isHindi) "कृपया ₹1 से ₹1,00,000 के बीच वैध शुल्क लिखें।" else "Please enter a valid Price between ₹1 and ₹1,00,000."
                                }
                                fullDescEn.trim().length < 5 -> {
                                    validationError = if (isHindi) "कृपया पूर्ण सेवा विवरण लिखें।" else "Please enter a full service description (min 5 characters)."
                                }
                                encodedDocs.isEmpty() -> {
                                    validationError = if (isHindi) "कृपया कम से कम 1 आवश्यक दस्तावेज़ चुनें।" else "Please select at least 1 document from the Document Library."
                                }
                                else -> {
                                    val cleanTitleEn = title.trim()
                                    val cleanTitleHi = titleHi.trim().ifBlank { cleanTitleEn }
                                    val cleanFullEn = fullDescEn.trim()
                                    val cleanFullHi = fullDescHi.trim().ifBlank { cleanFullEn }
                                    val cleanShortEn = shortDescEn.trim().ifBlank { cleanFullEn.take(140) }
                                    val cleanShortHi = shortDescHi.trim().ifBlank { cleanFullHi.take(140) }

                                    onSaveService(
                                        initialService.copy(
                                            title = cleanTitleEn,
                                            titleHi = cleanTitleHi,
                                            category = selectedCategoryKey,
                                            shortDescription = cleanShortEn,
                                            shortDescriptionHi = cleanShortHi,
                                            description = cleanFullEn,
                                            descriptionHi = cleanFullHi,
                                            price = parsedPrice,
                                            turnaroundTime = turnaroundTime.trim().ifBlank { "2 Hours" },
                                            requirements = encodedDocs,
                                            imageUrl = imageUrl.trim(),
                                            iconKey = selectedIconKey,
                                            active = isActive,
                                            isFeatured = isFeatured,
                                            featuredOrder = parsedFeaturedOrder,
                                            sortOrder = parsedSort,
                                            isArchived = false
                                        )
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonOrange, contentColor = Color.Black),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(2f)
                            .height(52.dp)
                            .testTag("adv_publish_service_btn")
                    ) {
                        Text(
                            text = if (isHindi) "क्लाउड में सेवा प्रकाशित करें" else "Publish Service to Cloud",
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * TAB 4: FEATURE 4 — SERVICE ORDERING, FEATURED SERVICES & PRIORITY
 */
@Composable
private fun AdminDisplayOrderTab(
    services: List<ServiceItem>,
    categories: List<ServiceCategoryItem>,
    isHindi: Boolean,
    onPublishServiceOrder: (List<ServiceItem>) -> Unit
) {
    var selectedCategoryFilter by remember { mutableStateOf("all") }
    val orderedList = remember(services) {
        mutableStateListOf<ServiceItem>().apply {
            addAll(services.sortedBy { it.sortOrder })
        }
    }

    fun normalizeSortOrders() {
        var featIdx = 1L
        for (i in orderedList.indices) {
            val item = orderedList[i]
            val nextFeatOrder = if (item.isFeatured) featIdx++ else 999L
            orderedList[i] = item.copy(sortOrder = (i + 1).toLong(), featuredOrder = nextFeatOrder)
        }
    }

    val displayedItems = remember(orderedList.toList(), selectedCategoryFilter) {
        if (selectedCategoryFilter == "all") orderedList.toList()
        else orderedList.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
    }

    val featuredPreview = remember(orderedList.toList()) {
        val explicitFeatured = orderedList.filter { it.active && it.isFeatured }.sortedBy { it.featuredOrder }
        explicitFeatured.ifEmpty { orderedList.filter { it.active }.take(3) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_display_order_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "order_header_and_publish") {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                border = BorderStroke(1.dp, NeonOrange.copy(alpha = 0.55f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isHindi) "सेवा प्रदर्शन क्रम और प्रमुख सेवाएं (Display Order)" else "Manage Service Display Order & Featured Priority",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = if (isHindi) {
                            "सेवाओं को ऊपर/नीचे या सबसे ऊपर/नीचे ले जाएं। ★ प्रमुख सेवाएं होम स्क्रीन के शीर्ष पर दिखाई देती हैं।"
                        } else {
                            "Move services to Top, Up, Down, or Bottom, and mark ★ Featured services. Both the Website and Android App follow this exact order."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedCategoryFilter == "all",
                            onClick = { selectedCategoryFilter = "all" },
                            label = { Text(if (isHindi) "सभी सेवाएं" else "All Services", maxLines = 1) }
                        )
                        categories.forEach { cat ->
                            FilterChip(
                                selected = selectedCategoryFilter.equals(cat.key, ignoreCase = true),
                                onClick = { selectedCategoryFilter = cat.key },
                                label = { Text(cat.localizedLabel(isHindi), maxLines = 1, softWrap = false) }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            normalizeSortOrders()
                            onPublishServiceOrder(orderedList.toList())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonOrange, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_publish_order_btn")
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "नया सेवा क्रम क्लाउड में सहेजें" else "Publish Display Order to Cloud",
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // Live Home Screen Featured Ordering Preview
        item(key = "featured_ordering_preview") {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isHindi) "★ प्रमुख (Featured) होम स्क्रीन प्रीव्यू:" else "★ Live Home Featured Carousel Preview:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        featuredPreview.forEachIndexed { idx, svc ->
                            Surface(
                                color = CharcoalElevated,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, NeonOrange.copy(alpha = 0.6f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "#${idx + 1}",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = NeonOrange
                                    )
                                    Text(
                                        text = svc.localizedTitle(isHindi),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        itemsIndexed(displayedItems, key = { _, item -> "order_item_${item.id}" }) { _, svc ->
            val globalIndex = orderedList.indexOfFirst { it.id == svc.id }
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                border = BorderStroke(
                    1.dp,
                    if (svc.isFeatured) NeonOrange.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.12f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            color = NeonOrange.copy(alpha = 0.18f),
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${globalIndex + 1}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeonOrange
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = svc.localizedTitle(isHindi),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${svc.category} • ₹${svc.price}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (globalIndex >= 0) {
                                    orderedList[globalIndex] = svc.copy(isFeatured = !svc.isFeatured)
                                    normalizeSortOrders()
                                }
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = if (svc.isFeatured) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Toggle Featured",
                                tint = if (svc.isFeatured) NeonOrange else Color.White.copy(alpha = 0.55f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Move to Top
                        IconButton(
                            onClick = {
                                if (globalIndex > 0) {
                                    val removed = orderedList.removeAt(globalIndex)
                                    orderedList.add(0, removed)
                                    normalizeSortOrders()
                                }
                            },
                            enabled = globalIndex > 0,
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("move_top_${svc.id}")
                        ) {
                            Icon(
                                Icons.Default.KeyboardDoubleArrowUp,
                                contentDescription = "Move to Top",
                                tint = if (globalIndex > 0) ElectricCyan else Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Move Up
                        IconButton(
                            onClick = {
                                if (globalIndex > 0) {
                                    val removed = orderedList.removeAt(globalIndex)
                                    orderedList.add(globalIndex - 1, removed)
                                    normalizeSortOrders()
                                }
                            },
                            enabled = globalIndex > 0,
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("move_up_${svc.id}")
                        ) {
                            Icon(
                                Icons.Default.ArrowUpward,
                                contentDescription = "Move Up",
                                tint = if (globalIndex > 0) Color.White else Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Move Down
                        IconButton(
                            onClick = {
                                if (globalIndex >= 0 && globalIndex < orderedList.lastIndex) {
                                    val removed = orderedList.removeAt(globalIndex)
                                    orderedList.add(globalIndex + 1, removed)
                                    normalizeSortOrders()
                                }
                            },
                            enabled = globalIndex >= 0 && globalIndex < orderedList.lastIndex,
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("move_down_${svc.id}")
                        ) {
                            Icon(
                                Icons.Default.ArrowDownward,
                                contentDescription = "Move Down",
                                tint = if (globalIndex < orderedList.lastIndex) Color.White else Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Move to Bottom
                        IconButton(
                            onClick = {
                                if (globalIndex >= 0 && globalIndex < orderedList.lastIndex) {
                                    val removed = orderedList.removeAt(globalIndex)
                                    orderedList.add(removed)
                                    normalizeSortOrders()
                                }
                            },
                            enabled = globalIndex >= 0 && globalIndex < orderedList.lastIndex,
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("move_bottom_${svc.id}")
                        ) {
                            Icon(
                                Icons.Default.KeyboardDoubleArrowDown,
                                contentDescription = "Move to Bottom",
                                tint = if (globalIndex < orderedList.lastIndex) ElectricCyan else Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * TAB 5: CATEGORY MANAGEMENT (Add, Edit, Reorder & Safe Delete with Reassignment)
 */
@Composable
private fun AdminCategoryManagerTab(
    categories: List<ServiceCategoryItem>,
    services: List<ServiceItem>,
    isHindi: Boolean,
    onSaveCategory: (ServiceCategoryItem) -> Unit,
    onReorderCategories: (List<ServiceCategoryItem>) -> Unit,
    onDeleteCategoryWithReassignment: (ServiceCategoryItem, String) -> Unit
) {
    var editingCatId by remember { mutableStateOf("") }
    var catNameEn by remember { mutableStateOf("") }
    var catNameHi by remember { mutableStateOf("") }
    var catIconKey by remember { mutableStateOf("badge") }
    var catFeedback by remember { mutableStateOf<String?>(null) }

    var categoryPendingDeletion by remember { mutableStateOf<ServiceCategoryItem?>(null) }
    var reassignmentCategoryKey by remember { mutableStateOf("resume") }

    val orderedCategories = remember(categories) {
        mutableStateListOf<ServiceCategoryItem>().apply {
            addAll(categories.sortedBy { it.sortOrder })
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_category_manager_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "add_edit_category_card") {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                border = BorderStroke(1.dp, NeonOrange.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (editingCatId.isBlank()) {
                            if (isHindi) "नई श्रेणी बनाएं (Add New Category)" else "Add New Service Category"
                        } else {
                            if (isHindi) "श्रेणी संपादित करें (Edit Category)" else "Edit Category ($editingCatId)"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonOrange
                    )

                    OutlinedTextField(
                        value = catNameEn,
                        onValueChange = {
                            catNameEn = it
                            catFeedback = null
                        },
                        label = { Text(if (isHindi) "श्रेणी का नाम (English) *" else "Category Name (English) *") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cat_manager_name_en_input")
                    )

                    OutlinedTextField(
                        value = catNameHi,
                        onValueChange = { catNameHi = it },
                        label = { Text(if (isHindi) "श्रेणी का नाम (हिंदी)" else "Category Name (Hindi)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cat_manager_name_hi_input")
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminIconLibrary.take(10).forEach { opt ->
                            FilterChip(
                                selected = catIconKey == opt.key,
                                onClick = { catIconKey = opt.key },
                                leadingIcon = {
                                    Icon(opt.icon, contentDescription = null, tint = opt.accent, modifier = Modifier.size(16.dp))
                                },
                                label = { Text(opt.label, maxLines = 1) }
                            )
                        }
                    }

                    if (catFeedback != null) {
                        Text(
                            text = catFeedback!!,
                            style = MaterialTheme.typography.labelMedium,
                            color = StatusErrorRed
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (editingCatId.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    editingCatId = ""
                                    catNameEn = ""
                                    catNameHi = ""
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (isHindi) "रद्द करें" else "Cancel Edit", color = Color.White)
                            }
                        }

                        Button(
                            onClick = {
                                val cleanEn = catNameEn.trim()
                                if (cleanEn.length < 2) {
                                    catFeedback = "Please enter a category name (min 2 chars)."
                                    return@Button
                                }
                                val key = if (editingCatId.isNotBlank()) {
                                    editingCatId
                                } else {
                                    cleanEn.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_').take(32)
                                }
                                if (editingCatId.isBlank() && orderedCategories.any {
                                        it.key.equals(key, ignoreCase = true) ||
                                            it.labelEn.equals(cleanEn, ignoreCase = true)
                                    }
                                ) {
                                    catFeedback = "Category '$cleanEn' already exists."
                                    return@Button
                                }
                                val targetSort = orderedCategories.find { it.key == key }?.sortOrder
                                    ?: (orderedCategories.size + 1).toLong()
                                onSaveCategory(
                                    ServiceCategoryItem(
                                        id = key,
                                        key = key,
                                        labelEn = cleanEn,
                                        labelHi = catNameHi.trim().ifBlank { cleanEn },
                                        iconKey = catIconKey,
                                        sortOrder = targetSort,
                                        active = true
                                    )
                                )
                                editingCatId = ""
                                catNameEn = ""
                                catNameHi = ""
                                catFeedback = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonOrange, contentColor = Color.Black),
                            modifier = Modifier
                                .weight(2f)
                                .testTag("cat_manager_save_btn")
                        ) {
                            Text(
                                text = if (editingCatId.isBlank()) {
                                    if (isHindi) "श्रेणी जोड़ें" else "Save Category"
                                } else {
                                    if (isHindi) "अपडेट करें" else "Update Category"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        itemsIndexed(orderedCategories, key = { _, cat -> "cat_row_${cat.key}" }) { idx, cat ->
            val countInCat = services.count { !it.isArchived && it.category.equals(cat.key, ignoreCase = true) }
            val (iconVec, iconAccent) = resolveAdminServiceIcon(cat.iconKey, cat.key)

            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(iconVec, contentDescription = null, tint = iconAccent, modifier = Modifier.size(20.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${cat.labelEn} (${cat.labelHi})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Key: ${cat.key} • $countInCat services",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (idx > 0) {
                                    val item = orderedCategories.removeAt(idx)
                                    orderedCategories.add(idx - 1, item)
                                    onReorderCategories(orderedCategories.toList())
                                }
                            },
                            enabled = idx > 0,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                Icons.Default.ArrowUpward,
                                contentDescription = "Move Category Up",
                                tint = if (idx > 0) Color.White else Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                if (idx < orderedCategories.lastIndex) {
                                    val item = orderedCategories.removeAt(idx)
                                    orderedCategories.add(idx + 1, item)
                                    onReorderCategories(orderedCategories.toList())
                                }
                            },
                            enabled = idx < orderedCategories.lastIndex,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                Icons.Default.ArrowDownward,
                                contentDescription = "Move Category Down",
                                tint = if (idx < orderedCategories.lastIndex) Color.White else Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                editingCatId = cat.key
                                catNameEn = cat.labelEn
                                catNameHi = cat.labelHi
                                catIconKey = cat.iconKey
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Category", tint = NeonOrange, modifier = Modifier.size(18.dp))
                        }

                        if (orderedCategories.size > 1) {
                            IconButton(
                                onClick = {
                                    reassignmentCategoryKey = orderedCategories.firstOrNull { it.key != cat.key }?.key ?: "resume"
                                    categoryPendingDeletion = cat
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.DeleteForever, contentDescription = "Delete Category", tint = StatusErrorRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (categoryPendingDeletion != null) {
        val targetCat = categoryPendingDeletion!!
        val affectedCount = services.count { it.category.equals(targetCat.key, ignoreCase = true) }
        val otherCategories = orderedCategories.filter { it.key != targetCat.key }

        AlertDialog(
            onDismissRequest = { categoryPendingDeletion = null },
            containerColor = CharcoalCard,
            title = {
                Text(
                    text = if (isHindi) "श्रेणी हटाएं और सेवाएं स्थानांतरित करें" else "Delete Category '${targetCat.labelEn}'?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (affectedCount > 0) {
                        Text(
                            text = if (isHindi) {
                                "चेतावनी: इस श्रेणी में $affectedCount सेवाएं हैं। हटाने से पहले कृपया उन्हें किसी अन्य सुरक्षित श्रेणी में स्थानांतरित करें:"
                            } else {
                                "Warning: This category contains $affectedCount service(s). Choose a safe target category to reassign those services before deleting:"
                            },
                            color = StatusPendingAmber
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            otherCategories.forEach { candidate ->
                                FilterChip(
                                    selected = reassignmentCategoryKey == candidate.key,
                                    onClick = { reassignmentCategoryKey = candidate.key },
                                    label = { Text(candidate.localizedLabel(isHindi), maxLines = 1) }
                                )
                            }
                        }
                    } else {
                        Text(
                            text = if (isHindi) "इस श्रेणी में कोई सेवा नहीं है। क्या आप इसे हटाना चाहते हैं?"
                            else "This category has 0 services. Confirm deletion?",
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCategoryWithReassignment(targetCat, reassignmentCategoryKey)
                        categoryPendingDeletion = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusErrorRed, contentColor = Color.White)
                ) {
                    Text(if (isHindi) "पुनर्निर्धारित करें और हटाएं" else "Reassign & Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryPendingDeletion = null }) {
                    Text(if (isHindi) "रद्द करें" else "Cancel")
                }
            }
        )
    }
}

/**
 * TAB 6: FEATURE 3 — CONTACT DETAILS & SOCIAL MEDIA MANAGER
 */
@Composable
private fun AdminContactAndSocialManagerTab(
    contactConfig: CenterContactConfig,
    isHindi: Boolean,
    onSaveContactConfig: (CenterContactConfig) -> Unit
) {
    var centerName by remember(contactConfig) { mutableStateOf(contactConfig.centerName) }
    var ownerName by remember(contactConfig) { mutableStateOf(contactConfig.ownerName) }
    var whatsappNumber by remember(contactConfig) { mutableStateOf(contactConfig.whatsappNumber) }
    var phoneNumber by remember(contactConfig) { mutableStateOf(contactConfig.phoneNumber) }
    var email by remember(contactConfig) { mutableStateOf(contactConfig.email) }
    var location by remember(contactConfig) { mutableStateOf(contactConfig.location) }
    var workingHours by remember(contactConfig) { mutableStateOf(contactConfig.workingHours) }
    var contactInstructions by remember(contactConfig) { mutableStateOf(contactConfig.contactInstructions) }

    val socialLinks = remember(contactConfig) {
        mutableStateListOf<SocialMediaProfileLink>().apply {
            addAll(contactConfig.parsedSocialLinks())
        }
    }

    var newPlatformName by remember { mutableStateOf("") }
    var newDisplayLabel by remember { mutableStateOf("") }
    var newProfileUrl by remember { mutableStateOf("") }
    var validationMessage by remember { mutableStateOf<String?>(null) }

    fun buildUpdatedConfig(): CenterContactConfig {
        val normalizedLinks = socialLinks.mapIndexed { idx, link -> link.copy(sortOrder = idx + 1) }
        val ig = normalizedLinks.find { it.platform.equals("Instagram", true) }?.url ?: contactConfig.instagramUrl
        val fb = normalizedLinks.find { it.platform.equals("Facebook", true) }?.url ?: contactConfig.facebookUrl
        val yt = normalizedLinks.find { it.platform.equals("YouTube", true) }?.url ?: contactConfig.youtubeUrl
        val tg = normalizedLinks.find { it.platform.equals("Telegram", true) }?.url ?: contactConfig.telegramUrl
        val gh = normalizedLinks.find { it.platform.equals("GitHub", true) }?.url ?: contactConfig.githubUrl
        val web = normalizedLinks.find { it.platform.equals("Website", true) }?.url ?: contactConfig.websiteUrl

        return contactConfig.copy(
            centerName = centerName.trim().ifBlank { "Aditya Online Center" },
            ownerName = ownerName.trim().ifBlank { "Aditya Kumar" },
            whatsappNumber = whatsappNumber.filter { it.isDigit() }.takeLast(10),
            phoneNumber = phoneNumber.filter { it.isDigit() }.takeLast(10),
            email = email.trim(),
            location = location.trim().ifBlank { "Kateya, Gopalganj, Bihar" },
            workingHours = workingHours.trim().ifBlank { "Mon - Sat: 8:00 AM - 8:00 PM" },
            contactInstructions = contactInstructions.trim(),
            instagramUrl = ig,
            facebookUrl = fb,
            youtubeUrl = yt,
            telegramUrl = tg,
            githubUrl = gh,
            websiteUrl = web,
            socialLinksSpec = normalizedLinks.map { it.serialize() }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_contact_social_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Business Contact Information Form
        item(key = "business_contact_info_card") {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                border = BorderStroke(1.dp, WhatsAppBrandGreen.copy(alpha = 0.55f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isHindi) "1. व्यवसाय प्रोफ़ाइल और संपर्क विवरण" else "1. Business Profile & Contact Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = WhatsAppBrandGreen
                    )

                    OutlinedTextField(
                        value = centerName,
                        onValueChange = { centerName = it },
                        label = { Text(if (isHindi) "व्यवसाय का नाम *" else "Business Display Name *") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_contact_business_name_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = whatsappNumber,
                            onValueChange = {
                                whatsappNumber = it.filter { ch -> ch.isDigit() }.take(10)
                                validationMessage = null
                            },
                            label = { Text(if (isHindi) "व्हाट्सएप नंबर (10 अंक) *" else "WhatsApp No. (10 digits) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_contact_whatsapp_input")
                        )

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = {
                                phoneNumber = it.filter { ch -> ch.isDigit() }.take(10)
                                validationMessage = null
                            },
                            label = { Text(if (isHindi) "फ़ोन नंबर (10 अंक) *" else "Phone No. (10 digits) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_contact_phone_input")
                        )
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            validationMessage = null
                        },
                        label = { Text(if (isHindi) "सहायता ईमेल पता *" else "Support Email Address *") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_contact_email_input")
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text(if (isHindi) "पता और स्थान *" else "Address & Locality *") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_contact_address_input")
                    )

                    OutlinedTextField(
                        value = workingHours,
                        onValueChange = { workingHours = it },
                        label = { Text(if (isHindi) "कार्य समय (Working Hours)" else "Working Hours") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_contact_hours_input")
                    )

                    OutlinedTextField(
                        value = contactInstructions,
                        onValueChange = { contactInstructions = it },
                        label = { Text(if (isHindi) "संपर्क निर्देश (Optional)" else "Optional Contact Instructions") },
                        minLines = 2,
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 2. Social Media Profiles Manager
        item(key = "social_media_manager_card") {
            Card(
                colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isHindi) "2. सोशल मीडिया प्रोफ़ाइल प्रबंधक" else "2. Social Media Profiles Manager",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricCyan
                    )

                    // Add new social profile row
                    Surface(
                        color = CharcoalElevated,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isHindi) "+ नया सोशल मीडिया लिंक जोड़ें" else "+ Add Social Media Link",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = NeonOrange
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = newPlatformName,
                                    onValueChange = { newPlatformName = it },
                                    label = { Text("Platform (e.g. Instagram, YouTube)") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("admin_new_social_platform_input")
                                )
                                OutlinedTextField(
                                    value = newDisplayLabel,
                                    onValueChange = { newDisplayLabel = it },
                                    label = { Text("Display Label") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            OutlinedTextField(
                                value = newProfileUrl,
                                onValueChange = { newProfileUrl = it },
                                label = { Text("Profile HTTPS URL *") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_new_social_url_input")
                            )
                            Button(
                                onClick = {
                                    val cleanPlat = newPlatformName.trim()
                                    val cleanUrl = newProfileUrl.trim()
                                    if (cleanPlat.length >= 2 && cleanUrl.startsWith("https://")) {
                                        socialLinks.add(
                                            SocialMediaProfileLink(
                                                id = "soc_${cleanPlat.lowercase().replace(" ", "_")}_${socialLinks.size + 1}",
                                                platform = cleanPlat,
                                                label = newDisplayLabel.trim().ifBlank { cleanPlat },
                                                url = cleanUrl,
                                                iconKey = cleanPlat.lowercase(),
                                                enabled = true,
                                                sortOrder = socialLinks.size + 1
                                            )
                                        )
                                        newPlatformName = ""
                                        newDisplayLabel = ""
                                        newProfileUrl = ""
                                        validationMessage = null
                                    } else {
                                        validationMessage = "Please enter a platform name and a valid https:// URL."
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = Color.Black),
                                modifier = Modifier.testTag("admin_add_social_link_btn")
                            ) {
                                Text(if (isHindi) "सोशल लिंक जोड़ें" else "Add Social Profile", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Existing Social Links list with URL editor, visibility toggle, reorder & delete
                    socialLinks.forEachIndexed { idx, link ->
                        Surface(
                            color = CharcoalElevated,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (link.enabled) ElectricCyan.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
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
                                        Text(
                                            text = "${idx + 1}. ${link.platform}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        FilterChip(
                                            selected = link.enabled,
                                            onClick = {
                                                socialLinks[idx] = link.copy(enabled = !link.enabled)
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = if (link.enabled) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            },
                                            label = {
                                                Text(
                                                    if (link.enabled) "Visible" else "Hidden",
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        )
                                    }

                                    Row {
                                        IconButton(
                                            onClick = {
                                                if (idx > 0) {
                                                    val item = socialLinks.removeAt(idx)
                                                    socialLinks.add(idx - 1, item)
                                                }
                                            },
                                            enabled = idx > 0,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                        IconButton(
                                            onClick = {
                                                if (idx < socialLinks.lastIndex) {
                                                    val item = socialLinks.removeAt(idx)
                                                    socialLinks.add(idx + 1, item)
                                                }
                                            },
                                            enabled = idx < socialLinks.lastIndex,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                        IconButton(
                                            onClick = { socialLinks.removeAt(idx) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.DeleteForever, contentDescription = "Remove Social Link", tint = StatusErrorRed, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = link.label,
                                        onValueChange = { updatedLabel ->
                                            socialLinks[idx] = link.copy(label = updatedLabel)
                                        },
                                        label = { Text("Label") },
                                        singleLine = true,
                                        modifier = Modifier.weight(0.38f)
                                    )
                                    OutlinedTextField(
                                        value = link.url,
                                        onValueChange = { updatedUrl ->
                                            socialLinks[idx] = link.copy(url = updatedUrl)
                                        },
                                        label = { Text("HTTPS Profile URL") },
                                        singleLine = true,
                                        modifier = Modifier.weight(0.62f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Live Preview of ConnectWithMeCard before publishing
        item(key = "live_contact_preview") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isHindi) "लाइव संपर्क और सोशल कार्ड प्रीव्यू:" else "Live Preview (Website & Android Footer):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = NeonOrange
                )
                ConnectWithMeCard(
                    contactConfig = buildUpdatedConfig(),
                    isHindi = isHindi,
                    showSocialLinks = true
                )
            }
        }

        // 4. Publish Shared Settings Button
        item(key = "publish_contact_settings_btn_item") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (validationMessage != null) {
                    Text(
                        text = validationMessage!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = StatusErrorRed
                    )
                }

                Button(
                    onClick = {
                        val cleanWa = whatsappNumber.filter { it.isDigit() }
                        val cleanPh = phoneNumber.filter { it.isDigit() }
                        when {
                            cleanWa.length != 10 -> {
                                validationMessage = "Please enter a valid 10-digit WhatsApp number."
                            }
                            cleanPh.length != 10 -> {
                                validationMessage = "Please enter a valid 10-digit Business Phone number."
                            }
                            !email.contains("@") -> {
                                validationMessage = "Please enter a valid Support Email address."
                            }
                            socialLinks.any { !it.url.trim().startsWith("https://") } -> {
                                validationMessage = "All social media profile URLs must start with https://"
                            }
                            else -> {
                                validationMessage = null
                                onSaveContactConfig(buildUpdatedConfig())
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonOrange, contentColor = Color.Black),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("admin_publish_contact_settings_btn")
                ) {
                    Icon(Icons.Default.CloudSync, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "संपर्क और सोशल सेटिंग्स क्लाउड में सहेजें" else "Publish Contact & Social Settings to Cloud",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
