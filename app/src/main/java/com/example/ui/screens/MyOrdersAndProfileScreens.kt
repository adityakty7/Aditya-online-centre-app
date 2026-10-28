package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.CenterContactConfig
import com.example.data.model.ServiceOrder
import com.example.data.model.UserProfile
import com.example.data.model.WhatsAppOrderHelper
import com.example.ui.components.ConnectWithMeCard
import com.example.ui.components.DynamicUpiQrAndPayCard
import com.example.ui.components.OrderReceiptPreviewDialog
import com.example.ui.components.PulsingDotIndicator
import com.example.ui.components.StaggeredEntranceItem
import com.example.ui.components.WhatsAppBrandGreen
import com.example.ui.components.bounceClick
import com.example.ui.components.launchExternalUriSafely
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.StatusErrorRed
import com.example.ui.theme.StatusPendingAmber
import com.example.ui.theme.StatusProcessingBlue
import com.example.ui.theme.StatusVerifiedGreen
import com.example.viewmodel.UiState
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MyOrdersScreen(
    ordersState: UiState<List<ServiceOrder>>,
    selectedStatusFilter: String,
    isHindi: Boolean,
    onSelectStatusFilter: (String) -> Unit,
    onSelectOrder: (ServiceOrder) -> Unit,
    onBrowseServices: () -> Unit
) {
    val statusFilters = remember {
        listOf(
            "all" to ("All Orders" to "सभी ऑर्डर"),
            "pending" to ("Submitted" to "लंबित"),
            "processing" to ("In Progress" to "प्रक्रिया में"),
            "action_required" to ("Action Needed" to "दस्तावेज़ आवश्यक"),
            "completed" to ("Completed" to "पूर्ण"),
            "cancelled" to ("Cancelled" to "रद्द")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("my_orders_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "orders_header") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isHindi) "मेरे ऑर्डर और लाइव ट्रैकिंग" else "My Orders",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isHindi) {
                        "वेबसाइट या ऐप से किए गए आपके सभी आवेदनों की लाइव स्थिति यहाँ देखें।"
                    } else {
                        "Track live status updates and download completed documents."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item(key = "orders_filter_row") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statusFilters.forEach { (key, labels) ->
                    val selected = key == selectedStatusFilter
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectStatusFilter(key) },
                        shape = RoundedCornerShape(12.dp),
                        label = {
                            Text(
                                text = if (isHindi) labels.second else labels.first,
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1,
                                softWrap = false
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedContainerColor = NeonOrange,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = MaterialTheme.colorScheme.outline,
                            selectedBorderColor = NeonOrange
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .bounceClick(scaleDown = 0.94f)
                            .testTag("order_filter_chip_$key")
                    )
                }
            }
        }

        when (ordersState) {
            is UiState.Loading -> {
                item(key = "orders_loading") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 44.dp, horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = NeonOrange)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isHindi) "ऑर्डर लोड हो रहे हैं…" else "Loading your orders…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            is UiState.Error -> {
                item(key = "orders_error") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = ordersState.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            is UiState.Success -> {
                val filtered = if (selectedStatusFilter == "all") {
                    ordersState.data
                } else {
                    ordersState.data.filter { it.status == selectedStatusFilter }
                }

                if (filtered.isEmpty()) {
                    item(key = "orders_empty") {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape,
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.ReceiptLong,
                                            contentDescription = null,
                                            tint = NeonOrange,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = if (isHindi) "कोई ऑर्डर नहीं मिला" else "No Orders Found",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isHindi) {
                                        "अपना पहला रिज्यूमे, बायोडाटा, छात्रवृत्ति या प्रमाण पत्र आवेदन अभी शुरू करें।"
                                    } else {
                                        "Start your first Resume, Biodata, Scholarship, or RTPS order from the catalogue."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(18.dp))
                                Button(
                                    onClick = onBrowseServices,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .minimumInteractiveComponentSize()
                                        .bounceClick()
                                        .testTag("empty_orders_browse_button")
                                ) {
                                    Text(
                                        text = if (isHindi) "सेवाएं देखें (Browse Services)" else "Browse Services",
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    }
                } else {
                    itemsIndexed(filtered, key = { _, it -> it.id }) { index, order ->
                        StaggeredEntranceItem(
                            index = index,
                            modifier = Modifier.animateItem()
                        ) {
                            CustomerOrderCard(
                                order = order,
                                isHindi = isHindi,
                                onClick = { onSelectOrder(order) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerOrderCard(
    order: ServiceOrder,
    isHindi: Boolean,
    onClick: () -> Unit
) {
    val statusColor = statusBadgeColor(order.status)
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.US) }
    val dateStr = order.createdAt?.toDate()?.let { dateFormatter.format(it) } ?: "Just now"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(scaleDown = 0.97f)
            .clickable(onClick = onClick)
            .testTag("order_card_${order.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Row: Order ID + Source Pill + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = order.orderNumber,
                        style = MaterialTheme.typography.labelLarge,
                        color = NeonOrange,
                        maxLines = 1,
                        softWrap = false
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = order.sourceChannel.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    color = statusColor.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        PulsingDotIndicator(color = statusColor, dotSize = 6.dp)
                        Text(
                            text = localizedOrderStatus(order.status, isHindi),
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            // Middle: Service Name & Timestamp
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = order.serviceTitleSnapshot,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Visual 3-Stage Order Status Progression (Submitted -> In Progress -> Completed)
            CompactOrderProgressionStrip(
                status = order.status,
                isHindi = isHindi
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f))

            // Footer: Price + Payment Verification Status + View Arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "₹${order.priceSnapshot}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NeonOrange,
                        maxLines = 1,
                        softWrap = false
                    )
                    Surface(
                        color = if (order.paymentStatus == "verified") {
                            StatusVerifiedGreen.copy(alpha = 0.14f)
                        } else {
                            StatusPendingAmber.copy(alpha = 0.14f)
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = localizedPaymentStatus(order.paymentStatus, isHindi),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (order.paymentStatus == "verified") StatusVerifiedGreen else StatusPendingAmber,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (isHindi) "ट्रैक करें" else "Details",
                        style = MaterialTheme.typography.labelMedium,
                        color = NeonOrange,
                        maxLines = 1,
                        softWrap = false
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = NeonOrange,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactOrderProgressionStrip(
    status: String,
    isHindi: Boolean
) {
    if (status == "cancelled") {
        Surface(
            color = StatusErrorRed.copy(alpha = 0.12f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    tint = StatusErrorRed,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = if (isHindi) "यह ऑर्डर रद्द कर दिया गया है" else "Order Cancelled",
                    style = MaterialTheme.typography.labelSmall,
                    color = StatusErrorRed
                )
            }
        }
        return
    }

    val activeStage = when (status) {
        "completed" -> 3
        "processing", "action_required" -> 2
        else -> 1
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ProgressionStagePill(
                label = if (isHindi) "1. प्राप्त" else "1. Submitted",
                isDone = activeStage >= 1,
                isActive = activeStage == 1
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
                    .height(2.dp)
                    .background(
                        if (activeStage >= 2) NeonOrange else MaterialTheme.colorScheme.outline
                    )
            )
            ProgressionStagePill(
                label = if (isHindi) "2. प्रगति पर" else "2. In Progress",
                isDone = activeStage >= 2,
                isActive = activeStage == 2
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
                    .height(2.dp)
                    .background(
                        if (activeStage >= 3) StatusVerifiedGreen else MaterialTheme.colorScheme.outline
                    )
            )
            ProgressionStagePill(
                label = if (isHindi) "3. पूर्ण" else "3. Completed",
                isDone = activeStage >= 3,
                isActive = activeStage == 3,
                isFinal = true
            )
        }
    }
}

@Composable
private fun ProgressionStagePill(
    label: String,
    isDone: Boolean,
    isActive: Boolean,
    isFinal: Boolean = false
) {
    val tintColor = when {
        isFinal && isDone -> StatusVerifiedGreen
        isActive -> NeonOrange
        isDone -> StatusVerifiedGreen
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(tintColor)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isActive || isDone) FontWeight.Bold else FontWeight.Normal,
            color = tintColor,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun OrderDetailScreen(
    order: ServiceOrder,
    isHindi: Boolean,
    isAdmin: Boolean,
    onBack: () -> Unit,
    onSubmitUpiReference: (orderId: String, upiRef: String) -> Unit,
    onCancelOrder: (orderId: String) -> Unit,
    onAdminUpdateOrder: (
        orderId: String,
        status: String,
        paymentStatus: String,
        adminNotes: String,
        deliveryFileUrl: String
    ) -> Unit,
    contactConfig: CenterContactConfig = CenterContactConfig()
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var upiInput by remember(order.upiReference) { mutableStateOf(order.upiReference) }

    // Admin fulfillment state
    var adminStatus by remember(order.status) { mutableStateOf(order.status) }
    var adminPaymentStatus by remember(order.paymentStatus) { mutableStateOf(order.paymentStatus) }
    var adminNotesInput by remember(order.adminNotes) { mutableStateOf(order.adminNotes) }
    var adminDeliveryUrlInput by remember(order.deliveryFileUrl) { mutableStateOf(order.deliveryFileUrl) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("order_detail_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.orderNumber,
                    style = MaterialTheme.typography.titleLarge,
                    color = NeonOrange,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = order.serviceTitleSnapshot,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 1. Live Order Progress Stepper
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "लाइव आवेदन स्थिति" else "Live Order Progress",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        color = statusBadgeColor(order.status).copy(alpha = 0.16f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, statusBadgeColor(order.status))
                    ) {
                        Text(
                            text = localizedOrderStatus(order.status, isHindi),
                            style = MaterialTheme.typography.labelSmall,
                            color = statusBadgeColor(order.status),
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                val currentStepIndex = when (order.status) {
                    "pending" -> 1
                    "action_required" -> 2
                    "processing" -> 3
                    "completed" -> 4
                    else -> 0
                }

                TimelineStepRow(
                    stepNumber = 1,
                    title = if (isHindi) "आवेदन प्राप्त हुआ" else "Order Submitted (${order.sourceChannel.uppercase()})",
                    subtitle = if (isHindi) "यूनिक आईडी: ${order.orderNumber}" else "Order ID: ${order.orderNumber}",
                    isCompleted = currentStepIndex >= 1,
                    isCurrent = currentStepIndex == 1
                )
                TimelineStepRow(
                    stepNumber = 2,
                    title = if (isHindi) "भुगतान और दस्तावेज़ जाँच" else "UPI Payment & Document Verification",
                    subtitle = localizedPaymentStatus(order.paymentStatus, isHindi),
                    isCompleted = order.paymentStatus == "verified" || currentStepIndex >= 3,
                    isCurrent = currentStepIndex == 2
                )
                TimelineStepRow(
                    stepNumber = 3,
                    title = if (isHindi) "सेंटर पर कार्य प्रगति पर" else "Processing at Aditya Online Center",
                    subtitle = if (isHindi) "कटेया डेस्क पर फॉर्म/पीडीएफ तैयार किया जा रहा है" else "Operator preparing your document/application",
                    isCompleted = currentStepIndex >= 3,
                    isCurrent = currentStepIndex == 3
                )
                TimelineStepRow(
                    stepNumber = 4,
                    title = if (isHindi) "पूर्ण एवं डाउनलोड हेतु तैयार" else "Completed & Document Delivered",
                    subtitle = if (order.deliveryFileUrl.isNotBlank()) {
                        if (isHindi) "तैयार दस्तावेज़ डाउनलोड के लिए उपलब्ध है" else "Final document link ready for download"
                    } else {
                        if (isHindi) "पूरा होने पर लिंक यहाँ दिखाई देगा" else "Download link appears here upon completion"
                    },
                    isCompleted = order.status == "completed",
                    isCurrent = order.status == "completed"
                )
            }
        }

        // 2. Final Document Delivery Download Card
        if (order.deliveryFileUrl.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = StatusVerifiedGreen.copy(alpha = 0.12f)
                ),
                border = BorderStroke(1.5.dp, StatusVerifiedGreen)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isHindi) "तैयार दस्तावेज़ डाउनलोड करें" else "Completed Document Delivery",
                        style = MaterialTheme.typography.titleLarge,
                        color = StatusVerifiedGreen
                    )
                    Text(
                        text = order.deliveryFileUrl,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(order.deliveryFileUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                clipboardManager.setText(AnnotatedString(order.deliveryFileUrl))
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("download_completed_doc_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusVerifiedGreen,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "तैयार पीडीएफ खोलें" else "Download Completed Document",
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        // 3. Admin Remarks (if any)
        if (order.adminNotes.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, NeonOrange)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "एडमिन संदेश / निर्देश" else "Message from Aditya Online Center Admin",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonOrange
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = order.adminNotes,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // 4. Application Snapshot & Customer UPI Reference Update
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isHindi) "आवेदन और शुल्क विवरण" else "Application & Payment Details",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonOrange
                )
                Text(
                    text = "${order.customerName} • ${order.customerPhone}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = order.applicantDetailsSummary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (order.documentLinks.isNotEmpty()) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = if (isHindi) "संलग्न दस्तावेज़:" else "Attached Document References:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    order.documentLinks.forEach { doc ->
                        Text(
                            text = "• $doc",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ElectricCyan
                        )
                    }
                }

                if (order.status in listOf("pending", "action_required") && order.paymentStatus != "verified") {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    DynamicUpiQrAndPayCard(
                        amountInr = order.priceSnapshot.coerceAtLeast(10L),
                        orderNote = order.orderNumber,
                        isHindi = isHindi
                    )
                    Text(
                        text = if (isHindi) {
                            "UPI भुगतान रेफरेंस (UTR) अपडेट करें (शुल्क: ₹${order.priceSnapshot})"
                        } else {
                            "Submit / Update UPI UTR Reference (Fee: ₹${order.priceSnapshot})"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = upiInput,
                            onValueChange = { upiInput = it.take(40) },
                            label = { Text("12-Digit UPI UTR") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("update_upi_input")
                        )
                        Button(
                            onClick = {
                                onSubmitUpiReference(order.id, upiInput)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .minimumInteractiveComponentSize()
                                .testTag("submit_upi_update_button")
                        ) {
                            Text(
                                text = if (isHindi) "UTR रेफरेंस भेजें" else "Submit UTR Reference",
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                var showOrderReceiptDialog by remember { mutableStateOf(false) }

                OutlinedButton(
                    onClick = { showOrderReceiptDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, NeonOrange),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .minimumInteractiveComponentSize()
                        .bounceClick()
                        .testTag("order_detail_receipt_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = NeonOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "आधिकारिक बिल रसीद (PDF) देखें / सेव करें" else "View & Save Official PDF Receipt",
                        fontWeight = FontWeight.Bold,
                        color = NeonOrange,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                if (showOrderReceiptDialog) {
                    OrderReceiptPreviewDialog(
                        order = order,
                        isHindi = isHindi,
                        contactConfig = contactConfig,
                        onDismiss = { showOrderReceiptDialog = false }
                    )
                }

                Button(
                    onClick = {
                        val waUrl = WhatsAppOrderHelper.buildConfirmedOrderWhatsAppUrl(
                            contactConfig = contactConfig,
                            order = order,
                            isHindi = isHindi
                        )
                        launchExternalUriSafely(context, waUrl)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WhatsAppBrandGreen,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .minimumInteractiveComponentSize()
                        .bounceClick()
                        .testTag("order_detail_whatsapp_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "व्हाट्सएप पर ऑर्डर विवरण भेजें" else "Send Order Details on WhatsApp",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                if (order.status == "pending") {
                    OutlinedButton(
                        onClick = { onCancelOrder(order.id) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("cancel_pending_order_button"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusErrorRed),
                        border = BorderStroke(1.dp, StatusErrorRed.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "लंबित ऑर्डर रद्द करें" else "Cancel Pending Order",
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        // 5. Authorized Admin Fulfillment Controls
        if (isAdmin) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, NeonOrange.copy(alpha = 0.65f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = NeonOrange
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "एडमिन ऑर्डर प्रबंधन (Admin Tools)" else "Admin Order Fulfillment Tools",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "Order Status:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("pending", "processing", "action_required", "completed", "cancelled").forEach { st ->
                            FilterChip(
                                selected = adminStatus == st,
                                onClick = { adminStatus = st },
                                label = {
                                    Text(
                                        text = st.uppercase(),
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            )
                        }
                    }

                    Text(
                        text = "Manual UPI Payment Verification:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("unpaid", "pending_verification", "verified", "rejected").forEach { pst ->
                            FilterChip(
                                selected = adminPaymentStatus == pst,
                                onClick = { adminPaymentStatus = pst },
                                label = {
                                    Text(
                                        text = pst.uppercase(),
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = adminNotesInput,
                        onValueChange = { adminNotesInput = it },
                        label = { Text("Admin Remarks / Customer Instructions") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = adminDeliveryUrlInput,
                        onValueChange = { adminDeliveryUrlInput = it },
                        label = { Text("Final Completed Document HTTPS URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            onAdminUpdateOrder(
                                order.id,
                                adminStatus,
                                adminPaymentStatus,
                                adminNotesInput,
                                adminDeliveryUrlInput
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("admin_save_order_update_button")
                    ) {
                        Text(
                            text = if (isHindi) "स्टेटस और डिलीवरी अपडेट करें" else "Publish Order Update",
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineStepRow(
    stepNumber: Int,
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    isCurrent: Boolean
) {
    val circleColor = when {
        isCompleted -> StatusVerifiedGreen
        isCurrent -> NeonOrange
        else -> MaterialTheme.colorScheme.outline
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(circleColor),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = stepNumber.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ProfileAndHelpScreen(
    userProfile: UserProfile?,
    userEmail: String?,
    userDisplayName: String?,
    isHindi: Boolean,
    isDarkTheme: Boolean,
    isAdmin: Boolean,
    onToggleLanguage: () -> Unit,
    onToggleTheme: () -> Unit,
    onSaveProfile: (name: String, phone: String, address: String, preferredLanguage: String) -> Unit,
    onSignOut: () -> Unit,
    contactConfig: CenterContactConfig = CenterContactConfig(),
    onUpdateContactConfig: (CenterContactConfig) -> Unit = {},
    onOpenAuthOptions: () -> Unit = {},
    onGoogleAdminLoginAndOpenPanel: () -> Unit = {},
    onOpenAdminDashboard: () -> Unit = {}
) {
    val context = LocalContext.current
    var nameInput by remember(userProfile, userDisplayName) {
        mutableStateOf(userProfile?.name?.ifBlank { userDisplayName ?: "" } ?: (userDisplayName ?: ""))
    }
    var phoneInput by remember(userProfile) {
        mutableStateOf(userProfile?.phone ?: "")
    }
    var addressInput by remember(userProfile) {
        mutableStateOf(userProfile?.address ?: "Kateya, Gopalganj, Bihar")
    }
    var profileValidationMessage by remember { mutableStateOf<String?>(null) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var isEditingSocialLinks by remember { mutableStateOf(false) }

    var whatsappInput by remember(contactConfig) { mutableStateOf(contactConfig.whatsappNumber) }
    var directPhoneInput by remember(contactConfig) { mutableStateOf(contactConfig.phoneNumber) }
    var emailInput by remember(contactConfig) { mutableStateOf(contactConfig.email) }
    var instagramInput by remember(contactConfig) { mutableStateOf(contactConfig.instagramUrl) }
    var facebookInput by remember(contactConfig) { mutableStateOf(contactConfig.facebookUrl) }
    var youtubeInput by remember(contactConfig) { mutableStateOf(contactConfig.youtubeUrl) }
    var telegramInput by remember(contactConfig) { mutableStateOf(contactConfig.telegramUrl) }
    var githubInput by remember(contactConfig) { mutableStateOf(contactConfig.githubUrl) }
    var websiteInput by remember(contactConfig) { mutableStateOf(contactConfig.websiteUrl) }

    val displayInitial = remember(nameInput, userEmail) {
        nameInput.trim().firstOrNull()?.uppercaseChar()?.toString()
            ?: userEmail?.firstOrNull()?.uppercaseChar()?.toString()
            ?: "A"
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isCompactNarrow = maxWidth < 360.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isHindi) "प्रोफ़ाइल और सहायता केंद्र" else "Profile & Help",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isHindi) {
                        "अपना खाता विवरण, व्हाट्सएप नंबर, सोशल मीडिया लिंक और सहायता संपर्क प्रबंधित करें।"
                    } else {
                        "Manage your account details, WhatsApp contact, social media links, and preferences."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 0. Connect With Me (WhatsApp, Phone, Email) & Social Media Profile Links
            ConnectWithMeCard(
                contactConfig = contactConfig,
                isHindi = isHindi,
                showSocialLinks = true,
                onEditContactsClick = if (isAdmin) ({ isEditingSocialLinks = !isEditingSocialLinks }) else null
            )

            AnimatedVisibility(visible = isAdmin && isEditingSocialLinks) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, NeonOrange.copy(alpha = 0.65f))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isHindi) "व्हाट्सएप, फोन और सोशल मीडिया लिंक अपडेट करें"
                            else "Customize Connect & Social Media Links",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeonOrange
                        )

                        OutlinedTextField(
                            value = whatsappInput,
                            onValueChange = { whatsappInput = it.filter { ch -> ch.isDigit() }.take(12) },
                            label = { Text(if (isHindi) "व्हाट्सएप नंबर (WhatsApp Number)" else "WhatsApp Number (10 digits)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("edit_whatsapp_number_input")
                        )

                        OutlinedTextField(
                            value = directPhoneInput,
                            onValueChange = { directPhoneInput = it.filter { ch -> ch.isDigit() }.take(12) },
                            label = { Text(if (isHindi) "कॉलिंग नंबर (Phone Number)" else "Phone Call Number") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text(if (isHindi) "संपर्क ईमेल (Email)" else "Official Contact Email") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = instagramInput,
                            onValueChange = { instagramInput = it },
                            label = { Text("Instagram Profile URL") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = facebookInput,
                            onValueChange = { facebookInput = it },
                            label = { Text("Facebook Profile URL") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = youtubeInput,
                            onValueChange = { youtubeInput = it },
                            label = { Text("YouTube Channel URL") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = telegramInput,
                            onValueChange = { telegramInput = it },
                            label = { Text("Telegram Channel / Profile URL") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = githubInput,
                            onValueChange = { githubInput = it },
                            label = { Text("GitHub Profile URL") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = websiteInput,
                            onValueChange = { websiteInput = it },
                            label = { Text("Official Website URL") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                onUpdateContactConfig(
                                    contactConfig.copy(
                                        whatsappNumber = whatsappInput.trim().ifBlank { "9876543210" },
                                        phoneNumber = directPhoneInput.trim().ifBlank { "9876543210" },
                                        email = emailInput.trim().ifBlank { "adityakty77@gmail.com" },
                                        instagramUrl = instagramInput.trim(),
                                        facebookUrl = facebookInput.trim(),
                                        youtubeUrl = youtubeInput.trim(),
                                        telegramUrl = telegramInput.trim(),
                                        githubUrl = githubInput.trim(),
                                        websiteUrl = websiteInput.trim()
                                    )
                                )
                                isEditingSocialLinks = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .bounceClick()
                                .testTag("save_connect_links_button")
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isHindi) "संपर्क और सोशल लिंक सेव करें" else "Save Contact & Social Links")
                        }
                    }
                }
            }

            // 1. Compact Account Header + Profile Editing Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape,
                            border = BorderStroke(1.dp, NeonOrange),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = displayInitial,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = NeonOrange,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = nameInput.ifBlank {
                                    if (isHindi) "ग्राहक प्रोफ़ाइल" else "Customer Account"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = userEmail ?: "Signed-In Account",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Surface(
                            color = if (isAdmin) MaterialTheme.colorScheme.primaryContainer
                            else StatusVerifiedGreen.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = if (isAdmin) "Admin" else if (isHindi) "सत्यापित" else "Verified",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isAdmin) NeonOrange else StatusVerifiedGreen,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (isAdmin) {
                        Button(
                            onClick = onOpenAdminDashboard,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricCyan,
                                contentColor = Color(0xFF06101E)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .bounceClick()
                                .testTag("profile_google_admin_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "एडमिन पैनल खोलें (Open Admin Panel)" else "Open Admin Panel",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = {
                            nameInput = it
                            profileValidationMessage = null
                        },
                        label = { Text(if (isHindi) "आपका पूरा नाम" else "Full Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_name_input")
                    )

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = {
                            phoneInput = it.filter { ch -> ch.isDigit() }.take(10)
                            profileValidationMessage = null
                        },
                        label = { Text(if (isHindi) "10-अंकीय मोबाइल नंबर" else "10-Digit Mobile Number") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_phone_input")
                    )

                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = {
                            addressInput = it
                            profileValidationMessage = null
                        },
                        label = { Text(if (isHindi) "पता (कटेया, गोपालगंज, बिहार)" else "Address / Locality (Kateya, Bihar)") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_address_input")
                    )

                    if (profileValidationMessage != null) {
                        Text(
                            text = profileValidationMessage!!,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = {
                            if (nameInput.trim().length < 2) {
                                profileValidationMessage = if (isHindi) {
                                    "कृपया कम से कम 2 अक्षरों का नाम लिखें।"
                                } else {
                                    "Please enter your full name (at least 2 characters)."
                                }
                                return@Button
                            }
                            if (phoneInput.isNotBlank() && phoneInput.length != 10) {
                                profileValidationMessage = if (isHindi) {
                                    "कृपया वैध 10-अंकीय मोबाइल नंबर लिखें।"
                                } else {
                                    "Please enter a valid 10-digit mobile number."
                                }
                                return@Button
                            }
                            onSaveProfile(
                                nameInput.trim(),
                                phoneInput.trim(),
                                addressInput.trim(),
                                if (isHindi) "hi" else "en"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("save_profile_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "प्रोफ़ाइल सेव करें" else "Save Profile",
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            // 2. Grouped Preferences Card (Language, Appearance & Accessibility)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isHindi) "भाषा, थीम और एक्सेसिबिलिटी" else "Preferences & Accessibility",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = NeonOrange
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "ऐप की भाषा" else "App Language",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = if (isHindi) "हिंदी (Hindi)" else "English",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = onToggleLanguage,
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("profile_language_toggle_button")
                        ) {
                            Text(
                                text = if (isHindi) "English" else "हिंदी",
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DarkMode,
                                contentDescription = null,
                                tint = ElectricCyan
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "डार्क थीम (Obsidian Dark)" else "Obsidian Dark Theme",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = if (isDarkTheme) "Obsidian Black & Neon Orange" else "Daylight Blue & White",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { onToggleTheme() },
                            modifier = Modifier.testTag("theme_mode_switch")
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessibilityNew,
                            contentDescription = null,
                            tint = StatusVerifiedGreen
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHindi) "स्पष्ट पठनीयता और टच कंट्रोल" else "Accessible Touch & Font Scaling",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = if (isHindi) {
                                    "48dp बटन, हाई-कंट्रास्ट बॉर्डर और TalkBack लेबल सक्रिय हैं।"
                                } else {
                                    "48dp touch targets, high-contrast tokens, and TalkBack labels active."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 3. Aditya Online Center Kateya Help Desk & Contact Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        com.example.ui.components.AdityaBrandEmblem(
                            size = 36.dp,
                            cornerRadius = 10.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isHindi) "आदित्य ऑनलाइन सेंटर सहायता केंद्र" else "Aditya Online Center • Help Desk",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) {
                                "मुख्य बाज़ार, कटेया, जिला - गोपालगंज, बिहार (841437)"
                            } else {
                                "Main Market, Kateya, Dist. Gopalganj, Bihar — 841437"
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = NeonOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "समय: सुबह 8:00 बजे से शाम 8:00 बजे तक (सोमवार - शनिवार)"
                            else "Hours: 8:00 AM – 8:00 PM (Mon – Sat)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (isCompactNarrow) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:adityakty77@gmail.com")
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .minimumInteractiveComponentSize()
                                    .testTag("contact_email_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "ईमेल सहायता" else "Email Support",
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }

                            OutlinedButton(
                                onClick = { showPrivacyDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .minimumInteractiveComponentSize()
                                    .testTag("privacy_policy_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Policy,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "गोपनीयता नीति" else "Privacy & Terms",
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
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:adityakty77@gmail.com")
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .minimumInteractiveComponentSize()
                                    .testTag("contact_email_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "ईमेल सहायता" else "Email Support",
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }

                            OutlinedButton(
                                onClick = { showPrivacyDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .minimumInteractiveComponentSize()
                                    .testTag("privacy_policy_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Policy,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "गोपनीयता नीति" else "Privacy & Terms",
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }

            // 4. Sign Out Action
            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("sign_out_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusErrorRed),
                border = BorderStroke(1.dp, StatusErrorRed.copy(alpha = 0.7f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Sign Out",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHindi) "लॉग आउट करें (Sign Out)" else "Sign Out of Account",
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text(if (isHindi) "गोपनीयता नीति और शर्तें" else "Privacy Policy & Terms of Service")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isHindi) {
                            "1. डेटा सुरक्षा: आपके सभी ऑर्डर और प्रोफ़ाइल विवरण केवल आपके Google खाते (UID) और आदित्य ऑनलाइन सेंटर के अधिकृत एडमिन तक ही सीमित हैं।\n" +
                                "2. पासवर्ड या ओटीपी निषेध: हम कभी भी बैंकिंग पिन, पासवर्ड या ओटीपी संग्रहीत नहीं करते हैं।\n" +
                                "3. भुगतान सत्यापन: UPI UTR नंबर केवल मैन्युअल एडमिन सत्यापन के बाद ही 'Verified' माना जाता है।"
                        } else {
                            "1. Customer Isolation: Your orders and documents are strictly scoped to your authenticated Google UID and authorized Aditya Online Center admins.\n" +
                                "2. Zero Sensitive Credentials: We never request or store banking PINs, passwords, or OTPs.\n" +
                                "3. Manual UPI Verification: Submitting a UPI reference marks payment as 'Pending Verification' until verified against bank records by the admin."
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

private fun statusBadgeColor(status: String): Color = when (status) {
    "completed" -> StatusVerifiedGreen
    "processing" -> StatusProcessingBlue
    "action_required" -> NeonOrange
    "cancelled" -> StatusErrorRed
    else -> StatusPendingAmber
}

private fun localizedOrderStatus(status: String, isHindi: Boolean): String = when (status) {
    "pending" -> if (isHindi) "लंबित" else "Submitted"
    "processing" -> if (isHindi) "प्रक्रिया में" else "In Progress"
    "action_required" -> if (isHindi) "दस्तावेज़ आवश्यक" else "Action Needed"
    "completed" -> if (isHindi) "पूर्ण" else "Completed"
    "cancelled" -> if (isHindi) "रद्द" else "Cancelled"
    else -> status.replaceFirstChar { it.uppercase() }
}

private fun localizedPaymentStatus(paymentStatus: String, isHindi: Boolean): String = when (paymentStatus) {
    "verified" -> if (isHindi) "भुगतान सत्यापित" else "Payment Verified"
    "pending_verification" -> if (isHindi) "UTR जाँच में" else "UTR Pending"
    "rejected" -> if (isHindi) "भुगतान अस्वीकृत" else "Payment Rejected"
    else -> if (isHindi) "भुगतान शेष" else "Unpaid"
}
