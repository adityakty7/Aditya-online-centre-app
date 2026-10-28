package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.CenterContactConfig
import com.example.data.model.DocumentRequirementSpec
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.ServiceOrder
import com.example.data.model.WhatsAppOrderHelper
import com.example.ui.components.CustomerDocumentLockerDialog
import com.example.ui.components.CustomerDocumentLockerStore
import com.example.ui.components.DynamicUpiQrAndPayCard
import com.example.ui.components.FuturisticGradientButton
import com.example.ui.components.OrderReceiptPreviewDialog
import com.example.ui.components.PhotoSignatureResizerDialog
import com.example.ui.components.PromoCouponHelper
import com.example.ui.components.PulsingDotIndicator
import com.example.ui.components.QuickWhatsAppOrderDialog
import com.example.ui.components.StaggeredEntranceItem
import com.example.ui.components.WhatsAppBrandGreen
import com.example.ui.components.bounceClick
import com.example.ui.components.glassCardContainerColor
import com.example.ui.components.launchExternalUriSafely
import com.example.ui.theme.BlueToPurpleGradientBrush
import com.example.ui.theme.CharcoalElevated
import com.example.ui.theme.ElectricBlueBright
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.FuturisticCardBorderBrush
import com.example.ui.theme.LuminousPurple
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.PurpleToCyanGradientBrush
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.StatusErrorRed
import com.example.ui.theme.StatusVerifiedGreen

@Composable
fun ServiceDetailScreen(
    service: ServiceItem,
    isHindi: Boolean,
    onBack: () -> Unit,
    onStartOrder: (ServiceItem) -> Unit,
    contactConfig: CenterContactConfig = CenterContactConfig(),
    defaultCustomerName: String = "",
    defaultCustomerPhone: String = "",
    defaultCustomerAddress: String = "Kateya, Gopalganj, Bihar",
    onQuickWhatsAppOrder: (
        customerName: String,
        customerPhone: String,
        detailsSummary: String
    ) -> Unit = { _, _, _ -> }
) {
    BackHandler(onBack = onBack)
    val categoryEnum = ServiceCategory.fromKey(service.category)
    var showWhatsAppOrderDialog by remember { mutableStateOf(false) }
    var isBookmarked by rememberSaveable(service.id) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Futuristic Header with Back Navigation & Favourite/Bookmark Action
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = CharcoalElevated.copy(alpha = 0.85f),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, ElectricBlueBright.copy(alpha = 0.45f))
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .bounceClick()
                            .testTag("service_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isHindi) "सेवा विवरण और दस्तावेज़" else "Service Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = categoryEnum.label(isHindi),
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Surface(
                color = if (isBookmarked) LuminousPurple.copy(alpha = 0.22f) else CharcoalElevated.copy(alpha = 0.85f),
                shape = CircleShape,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isBookmarked) ElectricCyan else ElectricBlueBright.copy(alpha = 0.4f)
                )
            ) {
                IconButton(
                    onClick = { isBookmarked = !isBookmarked },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .bounceClick()
                        .testTag("service_detail_bookmark_button")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isBookmarked) "Remove Bookmark" else "Bookmark Service",
                        tint = if (isBookmarked) ElectricCyan else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Main Hero Service Card
        StaggeredEntranceItem(index = 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.92f)),
                border = BorderStroke(1.2.dp, FuturisticCardBorderBrush)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(176.dp)
                            .clip(RoundedCornerShape(18.dp))
                    ) {
                        if (service.imageUrl.isNotBlank()) {
                            ServiceCoverImagePreview(
                                imageUrl = service.imageUrl,
                                contentDescription = service.localizedTitle(isHindi),
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.img_cyber_hero),
                                contentDescription = service.localizedTitle(isHindi),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Subtle Bottom Vignette Overlay on Hero Image
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0xCC040814)
                                        )
                                    )
                                )
                        )

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = ElectricBlueBright.copy(alpha = 0.28f),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.55f))
                            ) {
                                Text(
                                    text = categoryEnum.label(isHindi),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }

                            Surface(
                                color = if (service.active) StatusVerifiedGreen.copy(alpha = 0.22f)
                                else StatusErrorRed.copy(alpha = 0.22f),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (service.active) StatusVerifiedGreen.copy(alpha = 0.65f)
                                    else StatusErrorRed.copy(alpha = 0.65f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    if (service.active) {
                                        PulsingDotIndicator(color = StatusVerifiedGreen, dotSize = 6.dp)
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
                        }
                    }

                    Text(
                        text = service.localizedTitle(isHindi),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = service.localizedDescription(isHindi),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(color = ElectricBlueBright.copy(alpha = 0.28f))

                    // Transparent Pricing, Processing Time & Required Document Count Summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isHindi) "आधिकारिक सेवा शुल्क" else "Transparent Service Fee",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₹${service.price}",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = ElectricCyan,
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = CharcoalElevated,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, ElectricBlueBright.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = service.turnaroundTime,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }

                            Surface(
                                color = LuminousPurple.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, LuminousPurple.copy(alpha = 0.45f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = LuminousPurple,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = if (isHindi) "${service.requirements.size} आवश्यक दस्तावेज़" else "${service.requirements.size} Docs Required",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Required Documents Checklist Card (Parsed with Required/Optional & Instructions)
        StaggeredEntranceItem(index = 1) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.90f)),
                border = BorderStroke(1.dp, ElectricBlueBright.copy(alpha = 0.42f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "आवश्यक दस्तावेज़ और विवरण" else "Required Documents & Information",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    service.requirements.forEach { rawReq ->
                        val spec = remember(rawReq) { DocumentRequirementSpec.fromFormattedString(rawReq) }
                        Surface(
                            color = CharcoalElevated.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (spec.isRequired) ElectricBlueBright.copy(alpha = 0.35f)
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = if (spec.isRequired) ElectricCyan else LuminousPurple,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (isHindi && spec.labelHi.isNotBlank()) spec.labelHi else spec.labelEn,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (spec.instructions.isNotBlank()) {
                                            Text(
                                                text = spec.instructions,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    color = if (spec.isRequired) ElectricCyan.copy(alpha = 0.14f)
                                    else LuminousPurple.copy(alpha = 0.14f),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = if (spec.isRequired) {
                                            if (isHindi) "अनिवार्य" else "Required"
                                        } else {
                                            if (isHindi) "वैकल्पिक" else "Optional"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (spec.isRequired) ElectricCyan else LuminousPurple,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Clear Service Instructions Card
        StaggeredEntranceItem(index = 2) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.86f)),
                border = BorderStroke(1.dp, LuminousPurple.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "आवेदन कैसे करें (3 आसान चरण)" else "How Application Works (3 Simple Steps)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = if (isHindi) {
                            "1. 'Proceed to Application' दबाएं और अपना नाम, मोबाइल व सेवा विवरण भरें।\n" +
                                "2. आवश्यक दस्तावेज़ (PDF/फोटो/Drive लिंक) जोड़ें और UPI भुगतान करें।\n" +
                                "3. यूनिक ऑर्डर आईडी प्राप्त करें और 'मेरे ऑर्डर' से फाइनल पीडीएफ डाउनलोड करें।"
                        } else {
                            "1. Tap 'Proceed to Application' and enter applicant details.\n" +
                                "2. Attach supporting documents (PDF/Photo/Drive link) & pay via UPI.\n" +
                                "3. Receive your unique Order ID and download the completed PDF in 'My Orders'."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Prominent Bottom Action Buttons (Apply Now + WhatsApp Order)
        StaggeredEntranceItem(index = 3) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FuturisticGradientButton(
                    onClick = { onStartOrder(service) },
                    enabled = service.active,
                    cornerRadius = 50.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("proceed_to_order_form_button")
                ) {
                    Text(
                        text = if (service.active) {
                            if (isHindi) "आवेदन फॉर्म भरें (₹${service.price})" else "Proceed to Application (₹${service.price})"
                        } else {
                            if (isHindi) "यह सेवा अभी बंद है" else "Currently Inactive"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = { showWhatsAppOrderDialog = true },
                    enabled = service.active,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .minimumInteractiveComponentSize()
                        .bounceClick()
                        .testTag("service_detail_whatsapp_order_button"),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WhatsAppBrandGreen,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) {
                            "व्हाट्सएप द्वारा ऑर्डर करें (₹${service.price})"
                        } else {
                            "Order through WhatsApp (₹${service.price})"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    if (showWhatsAppOrderDialog) {
        QuickWhatsAppOrderDialog(
            service = service,
            contactConfig = contactConfig,
            defaultName = defaultCustomerName,
            defaultPhone = defaultCustomerPhone,
            defaultAddress = defaultCustomerAddress,
            isHindi = isHindi,
            onDismiss = { showWhatsAppOrderDialog = false },
            onOrderCreatedThroughWhatsApp = { customerName, customerPhone, detailsSummary ->
                showWhatsAppOrderDialog = false
                onQuickWhatsAppOrder(customerName, customerPhone, detailsSummary)
            }
        )
    }
}

@Composable
fun OrderApplicationScreen(
    service: ServiceItem,
    defaultName: String,
    defaultPhone: String,
    defaultAddress: String,
    isHindi: Boolean,
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onSubmitOrder: (
        customerName: String,
        customerPhone: String,
        applicantDetailsSummary: String,
        documentLinks: List<String>,
        upiReference: String
    ) -> Unit,
    contactConfig: CenterContactConfig = CenterContactConfig()
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current

    var applicantName by remember(defaultName) { mutableStateOf(defaultName) }
    var applicantPhone by remember(defaultPhone) { mutableStateOf(defaultPhone) }
    var fatherOrGuardianName by remember { mutableStateOf("") }
    var villageOrAddress by remember(defaultAddress) {
        mutableStateOf(defaultAddress.ifBlank { "Kateya, Gopalganj, Bihar" })
    }

    var primaryDynamicField by remember { mutableStateOf("") }
    var secondaryDynamicField by remember { mutableStateOf("") }
    var customInstructions by remember { mutableStateOf("") }

    val attachedDocs = remember { mutableStateListOf<String>() }
    var externalDocLinkInput by remember { mutableStateOf("") }

    var promoCodeInput by remember { mutableStateOf("") }
    val (discountAmount, appliedPromoLabel) = remember(promoCodeInput, service.price) {
        PromoCouponHelper.evaluateDiscount(promoCodeInput, service.price)
    }
    val effectiveFee = remember(service.price, discountAmount) {
        (service.price - discountAmount).coerceAtLeast(10L)
    }

    var showPhotoResizerInOrder by remember { mutableStateOf(false) }
    var showLockerInOrder by remember { mutableStateOf(false) }

    var upiReference by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val step1Ready = applicantName.trim().length >= 2 && applicantPhone.trim().length == 10
    val step2Ready = primaryDynamicField.trim().length >= 3
    val step3Ready = attachedDocs.isNotEmpty() || upiReference.trim().isNotEmpty()

    val targetProgress = when {
        step1Ready && step2Ready && step3Ready -> 1.0f
        step1Ready && step2Ready -> 0.78f
        step1Ready -> 0.45f
        else -> 0.2f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing),
        label = "apply_step_progress"
    )

    val docPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val label = uri.lastPathSegment?.substringAfterLast('/') ?: "document_${attachedDocs.size + 1}.pdf"
            if (attachedDocs.size < 10 && !attachedDocs.contains(label)) {
                attachedDocs.add(label)
            }
        }
    }

    val primaryFieldLabel = when (service.category) {
        "resume" -> if (isHindi) "शिक्षा और योग्यता (उदा. 12वीं, B.A., कंप्यूटर ज्ञान)" else "Qualifications & Skills (e.g., 12th, B.Sc, Tally, DCA)"
        "scholarship" -> if (isHindi) "कॉलेज/स्कूल का नाम, सत्र और बैंक खाता विवरण" else "School/College Name, Course Session & Category"
        "gov_form" -> if (isHindi) "परीक्षा/विज्ञापन नाम और श्रेणी (UR/OBC/EBC/SC/ST)" else "Exam / Portal Name & Category (UR/OBC/EBC/SC/ST)"
        "certificate" -> if (isHindi) "प्रमाण पत्र का प्रकार (जाति / आय / निवास / पैन) व माता-पिता का नाम" else "Certificate Type (Caste/Income/Residence/PAN) & Parents Name"
        else -> if (isHindi) "पीडीएफ पेज संख्या / भाषा (हिंदी या अंग्रेजी)" else "Page Count / Target PDF Size / Language (Hindi or English)"
    }

    val secondaryFieldLabel = when (service.category) {
        "resume" -> if (isHindi) "जन्मतिथि, कार्य अनुभव या वैवाहिक विवरण (बायोडाटा हेतु)" else "Date of Birth, Work Experience or Family/Gotra Details"
        "scholarship", "gov_form", "certificate" -> if (isHindi) "पंजीकरण संख्या / RTPS संख्या / जन्मतिथि" else "Registration No. / Old Certificate No. / Date of Birth"
        else -> if (isHindi) "फॉर्मेटिंग निर्देश (जैसे 100KB से कम पीडीएफ)" else "Formatting Details (e.g., Compress under 100KB, A4 Layout)"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                color = CharcoalElevated.copy(alpha = 0.85f),
                shape = CircleShape,
                border = BorderStroke(1.dp, ElectricBlueBright.copy(alpha = 0.45f))
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .bounceClick()
                        .testTag("order_form_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isHindi) "ऑनलाइन सेवा आवेदन फॉर्म" else "Order Application",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${service.localizedTitle(isHindi)} • ₹${service.price}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElectricCyan,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Animated Live Step Progress Card
        StaggeredEntranceItem(index = 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.90f)),
                border = BorderStroke(1.dp, FuturisticCardBorderBrush)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "आवेदन प्रगति (Application Progress)" else "Application Progress",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (step1Ready && step2Ready) StatusVerifiedGreen else ElectricCyan
                        )
                    }
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50)),
                        color = if (step1Ready && step2Ready) StatusVerifiedGreen else ElectricCyan,
                        trackColor = CharcoalElevated
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StepIndicatorChip(
                            label = if (isHindi) "1. आवेदक" else "1. Applicant",
                            done = step1Ready
                        )
                        StepIndicatorChip(
                            label = if (isHindi) "2. विवरण" else "2. Details",
                            done = step2Ready
                        )
                        StepIndicatorChip(
                            label = if (isHindi) "3. दस्तावेज़/UPI" else "3. Docs & UPI",
                            done = step3Ready
                        )
                    }
                }
            }
        }

        // 1. Applicant Personal Details Card
        StaggeredEntranceItem(index = 1) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.90f)),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (step1Ready) StatusVerifiedGreen.copy(alpha = 0.6f)
                    else ElectricBlueBright.copy(alpha = 0.38f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isHindi) "1. आवेदक की जानकारी" else "1. Applicant Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )

                    OutlinedTextField(
                        value = applicantName,
                        onValueChange = { applicantName = it },
                        label = { Text(if (isHindi) "आवेदक का पूरा नाम *" else "Applicant Full Name *") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("order_applicant_name_input")
                    )

                    OutlinedTextField(
                        value = applicantPhone,
                        onValueChange = { input ->
                            applicantPhone = input.filter { it.isDigit() }.take(10)
                        },
                        label = { Text(if (isHindi) "10-अंकीय मोबाइल/व्हाट्सएप नंबर *" else "10-Digit Mobile / WhatsApp Number *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("order_applicant_phone_input")
                    )

                    OutlinedTextField(
                        value = fatherOrGuardianName,
                        onValueChange = { fatherOrGuardianName = it },
                        label = { Text(if (isHindi) "पिता / अभिभावक का नाम" else "Father / Guardian Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = villageOrAddress,
                        onValueChange = { villageOrAddress = it },
                        label = { Text(if (isHindi) "ग्राम / पोस्ट / थाना (कटेया, गोपालगंज)" else "Village / Post / Block (Kateya, Gopalganj)") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 2. Dynamic Service-Specific Details Card
        StaggeredEntranceItem(index = 2) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.90f)),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (step2Ready) StatusVerifiedGreen.copy(alpha = 0.6f)
                    else ElectricBlueBright.copy(alpha = 0.38f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isHindi) "2. सेवा-विशिष्ट विवरण" else "2. Service-Specific Application Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )

                    OutlinedTextField(
                        value = primaryDynamicField,
                        onValueChange = { primaryDynamicField = it },
                        label = { Text("$primaryFieldLabel *") },
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("order_primary_details_input")
                    )

                    OutlinedTextField(
                        value = secondaryDynamicField,
                        onValueChange = { secondaryDynamicField = it },
                        label = { Text(secondaryFieldLabel) },
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = customInstructions,
                        onValueChange = { customInstructions = it },
                        label = {
                            Text(
                                if (isHindi) "अतिरिक्त निर्देश (यदि कोई हो)"
                                else "Additional Formatting Instructions (Optional)"
                            )
                        },
                        minLines = 2,
                        maxLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 3. Document Attachments & Links Card
        StaggeredEntranceItem(index = 3) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(animationSpec = tween(240)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.90f)),
                border = BorderStroke(1.dp, ElectricBlueBright.copy(alpha = 0.38f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isHindi) "3. सहायक दस्तावेज़ (पीडीएफ / फोटो / लिंक)" else "3. Supporting Documents (PDF / Photo / Link)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )

                    OutlinedTextField(
                        value = externalDocLinkInput,
                        onValueChange = { externalDocLinkInput = it },
                        label = {
                            Text(
                                if (isHindi) "Google Drive लिंक या दस्तावेज़ नाम जोड़ें"
                                else "Paste Drive Link or Document Reference"
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                docPickerLauncher.launch(arrayOf("application/pdf", "image/*"))
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.55f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .minimumInteractiveComponentSize()
                                .bounceClick()
                                .testTag("pick_document_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "फाइल चुनें" else "Pick File",
                                color = ElectricCyan,
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        Button(
                            onClick = {
                                val trimmed = externalDocLinkInput.trim()
                                if (trimmed.isNotEmpty() && attachedDocs.size < 10) {
                                    attachedDocs.add(trimmed)
                                    externalDocLinkInput = ""
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricBlueBright,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .minimumInteractiveComponentSize()
                                .bounceClick()
                        ) {
                            Text(
                                text = if (isHindi) "लिंक जोड़ें" else "Add Link",
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    // 1-Tap Auto-Fill from Saved Document Locker & Built-in Photo Resizer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val saved = CustomerDocumentLockerStore.load(context)
                                val summary = saved.toSummaryString()
                                if (summary.isNotBlank()) {
                                    if (!attachedDocs.contains(summary)) attachedDocs.add(summary)
                                    if (saved.digiLockerOrDriveLink.isNotBlank() && !attachedDocs.contains(saved.digiLockerOrDriveLink)) {
                                        attachedDocs.add(saved.digiLockerOrDriveLink)
                                    }
                                } else {
                                    showLockerInOrder = true
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, LuminousPurple.copy(alpha = 0.65f)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("autofill_from_locker_button")
                        ) {
                            Text(
                                text = if (isHindi) "📁 लॉकर से भरें" else "📁 Auto-Fill Locker",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = LuminousPurple,
                                maxLines = 1
                            )
                        }

                        OutlinedButton(
                            onClick = { showPhotoResizerInOrder = true },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.65f)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("order_open_photo_resizer_button")
                        ) {
                            Text(
                                text = if (isHindi) "📷 फोटो 20-50KB टूल" else "📷 Resize Photo/Sign",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan,
                                maxLines = 1
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = attachedDocs.isNotEmpty(),
                        enter = expandVertically(tween(220)) + fadeIn(tween(200)),
                        exit = shrinkVertically(tween(180)) + fadeOut(tween(160))
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            attachedDocs.forEachIndexed { idx, docItem ->
                                Surface(
                                    color = CharcoalElevated,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, ElectricBlueBright.copy(alpha = 0.35f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${idx + 1}. $docItem",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(
                                            onClick = { attachedDocs.removeAt(idx) },
                                            modifier = Modifier.minimumInteractiveComponentSize()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove attachment",
                                                modifier = Modifier.size(18.dp)
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

        // 4. Promo Code & Dynamic UPI QR Payment Card
        StaggeredEntranceItem(index = 4) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.90f)),
                border = BorderStroke(1.dp, LuminousPurple.copy(alpha = 0.55f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = null,
                            tint = ElectricCyan
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) {
                                "4. कूपन छूट और UPI भुगतान (₹$effectiveFee)"
                            } else {
                                "4. Promo Discount & UPI Payment (₹$effectiveFee)"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Promo Code Input
                    OutlinedTextField(
                        value = promoCodeInput,
                        onValueChange = { promoCodeInput = it.uppercase() },
                        label = {
                            Text(
                                if (isHindi) "प्रोमो / छात्र डिस्काउंट कोड (STUDENT20 / BIHAR10)"
                                else "Promo / Student Coupon Code (e.g. STUDENT20, BIHAR10)"
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("order_promo_code_input")
                    )

                    if (appliedPromoLabel != null) {
                        Text(
                            text = if (isHindi) {
                                "🎉 कूपन लागू: $appliedPromoLabel — ₹$discountAmount की बचत! अब केवल ₹$effectiveFee"
                            } else {
                                "🎉 Applied $appliedPromoLabel — Saved ₹$discountAmount! Payable: ₹$effectiveFee"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = StatusVerifiedGreen
                        )
                    }

                    // Dynamic Custom-Amount UPI QR Code & 1-Tap Pay via UPI App
                    DynamicUpiQrAndPayCard(
                        amountInr = effectiveFee,
                        orderNote = "${service.title} - ${applicantName.ifBlank { "Order" }}",
                        isHindi = isHindi
                    )

                    Text(
                        text = if (isHindi) {
                            "आप अभी या ऑर्डर सबमिट करने के बाद UPI के माध्यम से ₹$effectiveFee का भुगतान कर सकते हैं। सुरक्षा नियम: केवल UTR नंबर डालने से ऑर्डर 'Paid' नहीं माना जाता — एडमिन सत्यापन के बाद ही भुगतान Verified होता है।"
                        } else {
                            "Pay ₹$effectiveFee via any UPI app now or after submitting. Security Rule: Providing a UTR marks payment as 'Pending Verification' until verified by the admin."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = upiReference,
                        onValueChange = { upiReference = it.take(40) },
                        label = {
                            Text(
                                if (isHindi) "12-अंकीय UPI UTR / ट्रांजेक्शन नंबर (वैकल्पिक)"
                                else "12-Digit UPI UTR Reference (Optional)"
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("order_upi_reference_input")
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) {
                                "कभी भी अपना UPI पिन, पासवर्ड या ओटीपी यहाँ दर्ज न करें।"
                            } else {
                                "Never enter your UPI PIN, bank password, or OTP in any form."
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan
                        )
                    }
                }
            }
        }

        // 5. Pre-Submission Review Summary Card
        StaggeredEntranceItem(index = 5) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("order_review_summary_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalElevated.copy(alpha = 0.88f)),
                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.45f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isHindi) "आवेदन सारांश समीक्षा (Review Before Submit)" else "Review Summary Before Submitting",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricCyan
                    )
                    OrderSummaryLine(
                        label = if (isHindi) "चयनित सेवा" else "Selected Service",
                        value = service.localizedTitle(isHindi)
                    )
                    OrderSummaryLine(
                        label = if (isHindi) "आवेदक" else "Applicant",
                        value = if (applicantName.isBlank()) "—" else "$applicantName (${applicantPhone.ifBlank { "—" }})"
                    )
                    OrderSummaryLine(
                        label = if (isHindi) "संलग्न दस्तावेज़" else "Attached Documents",
                        value = "${attachedDocs.size} ${if (isHindi) "फाइल/लिंक" else "items"}"
                    )
                    OrderSummaryLine(
                        label = if (isHindi) "देय सेवा शुल्क" else "Payable Fee",
                        value = "₹$effectiveFee"
                    )
                }
            }
        }

        AnimatedVisibility(visible = validationError != null) {
            Surface(
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.14f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Text(
                    text = validationError ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }
        }

        FuturisticGradientButton(
            onClick = {
                validationError = null
                if (applicantName.trim().length < 2) {
                    validationError = if (isHindi) "कृपया आवेदक का पूरा नाम लिखें।" else "Please enter applicant full name."
                    return@FuturisticGradientButton
                }
                if (applicantPhone.trim().length != 10) {
                    validationError = if (isHindi) "कृपया वैध 10-अंकीय मोबाइल नंबर दर्ज करें।" else "Please enter a valid 10-digit mobile number."
                    return@FuturisticGradientButton
                }
                if (primaryDynamicField.trim().length < 3) {
                    validationError = if (isHindi) "कृपया आवश्यक सेवा विवरण भरें।" else "Please fill in the required service details."
                    return@FuturisticGradientButton
                }

                val combinedDetails = buildString {
                    append("Primary Details: ").append(primaryDynamicField.trim())
                    if (fatherOrGuardianName.isNotBlank()) {
                        append(" | Guardian: ").append(fatherOrGuardianName.trim())
                    }
                    if (villageOrAddress.isNotBlank()) {
                        append(" | Address: ").append(villageOrAddress.trim())
                    }
                    if (secondaryDynamicField.isNotBlank()) {
                        append(" | Additional Info: ").append(secondaryDynamicField.trim())
                    }
                    if (customInstructions.isNotBlank()) {
                        append(" | Instructions: ").append(customInstructions.trim())
                    }
                }

                onSubmitOrder(
                    applicantName.trim(),
                    applicantPhone.trim(),
                    combinedDetails,
                    attachedDocs.toList(),
                    upiReference.trim()
                )
            },
            enabled = !isSubmitting && service.active,
            cornerRadius = 50.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("submit_order_button")
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = if (isSubmitting) {
                    if (isHindi) "सर्वर पर ऑर्डर दर्ज हो रहा है…" else "Confirming Order…"
                } else {
                    if (isHindi) "आवेदन सबमिट करें (₹${service.price})" else "Submit Order (₹${service.price})"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Order through WhatsApp with Applicant Details Button
        Button(
            onClick = {
                validationError = null
                if (applicantName.trim().length < 2) {
                    validationError = if (isHindi) "कृपया आवेदक का पूरा नाम लिखें।" else "Please enter applicant full name."
                    return@Button
                }
                if (applicantPhone.trim().length != 10) {
                    validationError = if (isHindi) "कृपया वैध 10-अंकीय मोबाइल नंबर दर्ज करें।" else "Please enter a valid 10-digit mobile number."
                    return@Button
                }
                val effectivePrimary = primaryDynamicField.trim().ifBlank {
                    if (isHindi) "व्हाट्सएप पर विवरण साझा किया गया" else "Details shared via WhatsApp"
                }

                val combinedDetails = buildString {
                    append("WhatsApp Order | Primary Details: ").append(effectivePrimary)
                    if (fatherOrGuardianName.isNotBlank()) {
                        append(" | Guardian: ").append(fatherOrGuardianName.trim())
                    }
                    if (villageOrAddress.isNotBlank()) {
                        append(" | Address: ").append(villageOrAddress.trim())
                    }
                    if (secondaryDynamicField.isNotBlank()) {
                        append(" | Additional Info: ").append(secondaryDynamicField.trim())
                    }
                    if (customInstructions.isNotBlank()) {
                        append(" | Instructions: ").append(customInstructions.trim())
                    }
                }

                val waUrl = WhatsAppOrderHelper.buildServiceOrderWhatsAppUrl(
                    contactConfig = contactConfig,
                    service = service,
                    customerName = applicantName.trim(),
                    customerPhone = applicantPhone.trim(),
                    fatherOrGuardianName = fatherOrGuardianName.trim(),
                    address = villageOrAddress.trim(),
                    serviceDetails = buildString {
                        append(effectivePrimary)
                        if (secondaryDynamicField.isNotBlank()) append(" | ").append(secondaryDynamicField.trim())
                        if (customInstructions.isNotBlank()) append(" | ").append(customInstructions.trim())
                    },
                    documentLinks = attachedDocs.toList(),
                    upiReference = upiReference.trim(),
                    isHindi = isHindi
                )

                onSubmitOrder(
                    applicantName.trim(),
                    applicantPhone.trim(),
                    combinedDetails,
                    attachedDocs.toList(),
                    upiReference.trim()
                )
                launchExternalUriSafely(context, waUrl)
            },
            enabled = !isSubmitting && service.active,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .minimumInteractiveComponentSize()
                .bounceClick()
                .testTag("submit_order_via_whatsapp_button"),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = WhatsAppBrandGreen,
                contentColor = Color.Black
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isHindi) {
                    "व्हाट्सएप से विवरण सहित ऑर्डर करें"
                } else {
                    "Order through WhatsApp with Details"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    if (showPhotoResizerInOrder) {
        PhotoSignatureResizerDialog(
            isHindi = isHindi,
            defaultApplicantName = applicantName.ifBlank { "ADITYA KUMAR" },
            onDismiss = { showPhotoResizerInOrder = false }
        )
    }

    if (showLockerInOrder) {
        CustomerDocumentLockerDialog(
            isHindi = isHindi,
            onDismiss = { showLockerInOrder = false },
            onSaved = { saved ->
                val summary = saved.toSummaryString()
                if (summary.isNotBlank() && !attachedDocs.contains(summary)) {
                    attachedDocs.add(summary)
                }
                showLockerInOrder = false
            }
        )
    }
}

@Composable
private fun StepIndicatorChip(label: String, done: Boolean) {
    val chipColor by animateColorAsState(
        targetValue = if (done) StatusVerifiedGreen else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(250),
        label = "step_chip_color"
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = chipColor,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (done) FontWeight.Bold else FontWeight.Medium,
            color = chipColor,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun OrderConfirmationScreen(
    order: ServiceOrder,
    isHindi: Boolean,
    onTrackOrder: (ServiceOrder) -> Unit,
    onBackToHome: () -> Unit,
    contactConfig: CenterContactConfig = CenterContactConfig()
) {
    BackHandler(onBack = onBackToHome)
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var badgeVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        badgeVisible = true
    }
    val badgeScale by animateFloatAsState(
        targetValue = if (badgeVisible) 1f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "order_confirm_badge_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StaggeredEntranceItem(index = 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.94f)),
                border = BorderStroke(1.2.dp, FuturisticCardBorderBrush)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = StatusVerifiedGreen.copy(alpha = 0.16f),
                        shape = CircleShape,
                        border = BorderStroke(1.5.dp, StatusVerifiedGreen.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .size(72.dp)
                            .graphicsLayer {
                                scaleX = badgeScale
                                scaleY = badgeScale
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Order Confirmed",
                                tint = StatusVerifiedGreen,
                                modifier = Modifier.size(42.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isHindi) "आपका ऑर्डर सफलतापूर्वक दर्ज हो गया!" else "Order Confirmed!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isHindi) {
                            "आदित्य ऑनलाइन सेंटर, कटेया के एडमिन पैनल में आपका आवेदन पहुँच चुका है।"
                        } else {
                            "Your application is now live in the Aditya Online Center admin panel."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Surface(
                        color = CharcoalElevated,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.65f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isHindi) "यूनिक ऑर्डर आईडी" else "UNIQUE ORDER ID",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = order.orderNumber,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = ElectricCyan,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(order.orderNumber))
                                },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .bounceClick()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Order ID",
                                    tint = ElectricCyan
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OrderSummaryLine(
                        label = if (isHindi) "सेवा" else "Service",
                        value = order.serviceTitleSnapshot
                    )
                    OrderSummaryLine(
                        label = if (isHindi) "आवेदक" else "Applicant",
                        value = "${order.customerName} (${order.customerPhone})"
                    )
                    OrderSummaryLine(
                        label = if (isHindi) "सत्यापित शुल्क" else "Locked Fee Snapshot",
                        value = "₹${order.priceSnapshot}"
                    )
                    OrderSummaryLine(
                        label = if (isHindi) "चैनल" else "Source Channel",
                        value = order.sourceChannel.uppercase()
                    )
                    OrderSummaryLine(
                        label = if (isHindi) "भुगतान स्थिति" else "Payment Status",
                        value = order.paymentStatus.replace("_", " ").uppercase()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val waUrl = WhatsAppOrderHelper.buildConfirmedOrderWhatsAppUrl(
                                contactConfig = contactConfig,
                                order = order,
                                isHindi = isHindi
                            )
                            launchExternalUriSafely(context, waUrl)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WhatsAppBrandGreen,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .minimumInteractiveComponentSize()
                            .bounceClick()
                            .testTag("share_confirmed_order_whatsapp_button"),
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "व्हाट्सएप पर ऑर्डर विवरण भेजें" else "Send Order Details on WhatsApp",
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    FuturisticGradientButton(
                        onClick = { onTrackOrder(order) },
                        cornerRadius = 50.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("track_confirmed_order_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "लाइव स्टेटस ट्रैक करें" else "Track Live Order Status",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    var showReceiptModal by remember { mutableStateOf(false) }

                    OutlinedButton(
                        onClick = { showReceiptModal = true },
                        border = BorderStroke(1.dp, LuminousPurple),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .minimumInteractiveComponentSize()
                            .bounceClick()
                            .testTag("download_official_receipt_button"),
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = LuminousPurple,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "आधिकारिक बिल रसीद (PDF) देखें / डाउनलोड करें" else "View & Download Official PDF Receipt",
                            fontWeight = FontWeight.Bold,
                            color = LuminousPurple,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    if (showReceiptModal) {
                        OrderReceiptPreviewDialog(
                            order = order,
                            isHindi = isHindi,
                            contactConfig = contactConfig,
                            onDismiss = { showReceiptModal = false }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onBackToHome,
                        border = BorderStroke(1.dp, ElectricBlueBright.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .minimumInteractiveComponentSize()
                            .bounceClick()
                            .testTag("back_to_home_after_order_button"),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = if (isHindi) "होम पेज पर लौटें" else "Back to Service Catalogue",
                            color = Color.White,
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
private fun OrderSummaryLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.42f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.58f)
        )
    }
}
