package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.unit.dp
import com.example.data.model.ServiceOrder
import com.example.data.model.UserProfile
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
            "pending" to ("Pending" to "लंबित (Pending)"),
            "processing" to ("In Progress" to "प्रक्रिया में"),
            "action_required" to ("Action Needed" to "दस्तावेज़ आवश्यक"),
            "completed" to ("Completed" to "पूर्ण (Ready)"),
            "cancelled" to ("Cancelled" to "रद्द")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("my_orders_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = if (isHindi) "मेरे ऑर्डर और लाइव स्टेटस" else "My Orders & Real-Time Status",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isHindi) {
                        "वेबसाइट या एंड्रॉइड ऐप से किए गए आपके सभी ऑर्डर यहाँ लाइव दिखाई देते हैं।"
                    } else {
                        "Orders placed from either the website or Android app appear here in real time."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
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
                        label = {
                            Text(if (isHindi) labels.second else labels.first)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonOrange,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("order_filter_chip_$key")
                    )
                }
            }
        }

        when (ordersState) {
            is UiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NeonOrange)
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
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(10.dp))
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
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = NeonOrange,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
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
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = onBrowseServices,
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("empty_orders_browse_button")
                                ) {
                                    Text(if (isHindi) "सेवाएं देखें" else "Browse Services")
                                }
                            }
                        }
                    }
                } else {
                    items(filtered, key = { it.id }) { order ->
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

@Composable
private fun CustomerOrderCard(
    order: ServiceOrder,
    isHindi: Boolean,
    onClick: () -> Unit
) {
    val statusColor = statusBadgeColor(order.status)
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US) }
    val dateStr = order.createdAt?.toDate()?.let { dateFormatter.format(it) } ?: "Just now"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("order_card_${order.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                        text = order.orderNumber,
                        style = MaterialTheme.typography.labelLarge,
                        color = NeonOrange
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = order.sourceChannel.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = ElectricCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    color = statusColor.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, statusColor)
                ) {
                    Text(
                        text = localizedOrderStatus(order.status, isHindi),
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = order.serviceTitleSnapshot,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "₹${order.priceSnapshot} • ${localizedPaymentStatus(order.paymentStatus, isHindi)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (order.paymentStatus == "verified") StatusVerifiedGreen else StatusPendingAmber
                )
            }
        }
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
    ) -> Unit
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
            Column {
                Text(
                    text = order.orderNumber,
                    style = MaterialTheme.typography.titleLarge,
                    color = NeonOrange
                )
                Text(
                    text = order.serviceTitleSnapshot,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            style = MaterialTheme.typography.labelLarge,
                            color = statusBadgeColor(order.status),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
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

        // 2. Final Document Delivery Download Card (Visible when completed or deliveryFileUrl present)
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
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        modifier = Modifier
                            .fillMaxWidth()
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
                            text = if (isHindi) "तैयार पीडीएफ / सर्टिफिकेट खोलें" else "Open / Download Completed Document",
                            fontWeight = FontWeight.Bold
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
            shape = RoundedCornerShape(18.dp),
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
                    Text(
                        text = if (isHindi) {
                            "UPI भुगतान रेफरेंस (UTR) अपडेट करें (शुल्क: ₹${order.priceSnapshot})"
                        } else {
                            "Submit / Update UPI UTR Reference (Fee: ₹${order.priceSnapshot})"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = upiInput,
                            onValueChange = { upiInput = it.take(40) },
                            label = { Text("12-Digit UPI UTR") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("update_upi_input")
                        )
                        Button(
                            onClick = {
                                onSubmitUpiReference(order.id, upiInput)
                            },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("submit_upi_update_button")
                        ) {
                            Text(if (isHindi) "भेजें" else "Submit UTR")
                        }
                    }
                }

                if (order.status == "pending") {
                    OutlinedButton(
                        onClick = { onCancelOrder(order.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("cancel_pending_order_button"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusErrorRed)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isHindi) "लंबित ऑर्डर रद्द करें" else "Cancel Pending Order")
                    }
                }
            }
        }

        // 5. Authorized Admin Fulfillment Controls (Only visible when signed in as Admin)
        if (isAdmin) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                border = BorderStroke(1.5.dp, NeonOrange)
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
                            text = if (isHindi) "एडमिन ऑर्डर प्रबंधन और डिलीवरी" else "Admin Order Fulfillment & Payment Verification",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
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
                                label = { Text(st.uppercase()) }
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
                                label = { Text(pst.uppercase()) }
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("admin_save_order_update_button")
                    ) {
                        Text(if (isHindi) "स्टेटस और डिलीवरी अपडेट करें" else "Publish Order Update to Customer")
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
                .size(30.dp)
                .clip(CircleShape)
                .background(circleColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
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
    onSignOut: () -> Unit
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
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isHindi) "प्रोफ़ाइल, सेटिंग्स और सहायता केंद्र" else "Customer Profile & Help Center",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        // 1. Customer Profile Editor Card
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = NeonOrange
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = userEmail ?: "Signed-In Customer",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isAdmin) "Role: Authorized Administrator" else "Role: Verified Customer",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isAdmin) NeonOrange else ElectricCyan
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text(if (isHindi) "आपका नाम" else "Full Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_name_input")
                )

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it.filter { ch -> ch.isDigit() }.take(10) },
                    label = { Text(if (isHindi) "10-अंकीय मोबाइल नंबर" else "10-Digit Mobile Number") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_phone_input")
                )

                OutlinedTextField(
                    value = addressInput,
                    onValueChange = { addressInput = it },
                    label = { Text(if (isHindi) "पता (कटेया, गोपालगंज, बिहार)" else "Address / Locality (Kateya, Bihar)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_address_input")
                )

                Button(
                    onClick = {
                        onSaveProfile(
                            nameInput,
                            phoneInput,
                            addressInput,
                            if (isHindi) "hi" else "en"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
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
                    Text(if (isHindi) "प्रोफ़ाइल सेव करें" else "Save Profile to Cloud")
                }
            }
        }

        // 2. Language & Visual Theme Preferences Card
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = NeonOrange
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isHindi) "ऐप की भाषा (Language)" else "App Language (हिंदी / English)",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = if (isHindi) "वर्तमान: हिंदी" else "Current: English",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = onToggleLanguage,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("profile_language_toggle_button")
                    ) {
                        Text(if (isHindi) "Switch to English" else "हिंदी में बदलें")
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = ElectricCyan
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isHindi) "सिग्नेचर डार्क थीम (Black & Neon Orange)" else "Signature Cyber Dark Theme",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = if (isDarkTheme) "Obsidian Black, Charcoal & Neon Orange" else "Daylight Cyber Blue & White",
                                style = MaterialTheme.typography.bodyMedium,
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
            }
        }

        // 3. Aditya Online Center Kateya Help Desk & Contact Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = NeonOrange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "आदित्य ऑनलाइन सेंटर सहायता केंद्र" else "Aditya Online Center — Help & Contact",
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
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("contact_email_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isHindi) "ईमेल सहायता" else "Email Support")
                    }

                    OutlinedButton(
                        onClick = { showPrivacyDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("privacy_policy_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Policy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isHindi) "गोपनीयता नीति" else "Privacy & Terms")
                    }
                }
            }
        }

        // 4. Sign Out Button
        OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .minimumInteractiveComponentSize()
                .testTag("sign_out_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusErrorRed),
            border = BorderStroke(1.dp, StatusErrorRed)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Sign Out"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isHindi) "लॉग आउट करें (Sign Out)" else "Sign Out of Account",
                style = MaterialTheme.typography.labelLarge
            )
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
    "pending" -> if (isHindi) "लंबित (Pending)" else "PENDING"
    "processing" -> if (isHindi) "प्रक्रिया में" else "IN PROGRESS"
    "action_required" -> if (isHindi) "ध्यान दें" else "ACTION REQUIRED"
    "completed" -> if (isHindi) "पूर्ण (Ready)" else "COMPLETED"
    "cancelled" -> if (isHindi) "रद्द" else "CANCELLED"
    else -> status.uppercase()
}

private fun localizedPaymentStatus(paymentStatus: String, isHindi: Boolean): String = when (paymentStatus) {
    "verified" -> if (isHindi) "भुगतान सत्यापित" else "PAYMENT VERIFIED"
    "pending_verification" -> if (isHindi) "UTR जाँच में" else "UTR PENDING VERIFICATION"
    "rejected" -> if (isHindi) "अस्वीकृत" else "PAYMENT REJECTED"
    else -> if (isHindi) "भुगतान शेष" else "UNPAID"
}
