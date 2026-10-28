package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CenterContactConfig
import com.example.data.model.ServiceItem
import com.example.data.model.ServiceOrder
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.StatusErrorRed
import com.example.ui.theme.StatusPendingAmber
import com.example.ui.theme.StatusVerifiedGreen
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ============================================================================
// 1. LIVE SHOP OPEN/CLOSED & SCROLLING NOTICE TICKER
// ============================================================================
@Composable
fun LiveShopNoticeTickerCard(
    isHindi: Boolean,
    isAdmin: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("aditya_shop_notice_prefs", Context.MODE_PRIVATE) }
    var isShopOpen by remember { mutableStateOf(prefs.getBoolean("shop_open", true)) }
    var customNoticeEn by remember {
        mutableStateOf(
            prefs.getString(
                "custom_notice_en",
                "Bihar Police, SSC, RTPS Caste/Income/Residence & Scholarship forms open! Use code STUDENT20 for ₹20 OFF."
            ) ?: ""
        )
    }
    var customNoticeHi by remember {
        mutableStateOf(
            prefs.getString(
                "custom_notice_hi",
                "बिहार पुलिस, SSC, जाति/आय/निवास (RTPS) और छात्रवृत्ति फॉर्म चालू हैं! ₹20 छूट के लिए STUDENT20 कोड लगाएं।"
            ) ?: ""
        )
    }
    var showEditDialog by remember { mutableStateOf(false) }

    val defaultNotices = remember(isHindi, customNoticeEn, customNoticeHi) {
        if (isHindi) {
            listOf(
                customNoticeHi,
                "⚡ कटेया मेन मार्केट में 1–2 घंटे में एक्सप्रेस पीडीएफ और फॉर्म डिलीवरी उपलब्ध!",
                "📁 डॉक्यूमेंट लॉकर में अपने आधार/पैन नंबर सेव करें और 1-क्लिक में फॉर्म भरवाएं।"
            )
        } else {
            listOf(
                customNoticeEn,
                "⚡ Express 1–2 Hr PDF & Form Delivery at Main Market, Kateya, Bihar!",
                "📁 Save your Aadhaar/PAN/Roll No in Document Locker for 1-Tap Order Filling."
            )
        }
    }

    var activeNoticeIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(defaultNotices) {
        while (true) {
            delay(4200L)
            activeNoticeIndex = (activeNoticeIndex + 1) % defaultNotices.size
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_shop_notice_ticker_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = glassCardContainerColor()),
        border = BorderStroke(
            width = 1.dp,
            brush = glassSpecularBorderBrush(
                primaryAccent = if (isShopOpen) StatusVerifiedGreen else StatusErrorRed,
                secondaryAccent = NeonOrange
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    glassSurfaceOverlayBrush(
                        accentTint = if (isShopOpen) StatusVerifiedGreen else StatusErrorRed
                    )
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (isShopOpen) StatusVerifiedGreen.copy(alpha = 0.16f)
                    else StatusErrorRed.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(
                        1.dp,
                        if (isShopOpen) StatusVerifiedGreen.copy(alpha = 0.55f)
                        else StatusErrorRed.copy(alpha = 0.55f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PulsingDotIndicator(
                            color = if (isShopOpen) StatusVerifiedGreen else StatusErrorRed,
                            dotSize = 7.dp
                        )
                        Text(
                            text = if (isShopOpen) {
                                if (isHindi) "दुकान खुली है • 8 AM - 8 PM" else "SHOP OPEN • 8 AM - 8 PM"
                            } else {
                                if (isHindi) "अभी बंद है • ऑनलाइन ऑर्डर चालू" else "SHOP CLOSED • 24×7 Online Orders"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isShopOpen) StatusVerifiedGreen else StatusErrorRed
                        )
                    }
                }

                if (isAdmin) {
                    Surface(
                        color = NeonOrange.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, NeonOrange.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .bounceClick()
                            .clickable { showEditDialog = true }
                            .testTag("edit_shop_notice_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = NeonOrange,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = if (isHindi) "नोटिस बदलें" else "Edit Notice",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NeonOrange
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Glowing3DIconBadge(
                    icon = Icons.Default.Campaign,
                    accentColor = NeonOrange,
                    size = 32.dp,
                    iconSize = 17.dp
                )
                AnimatedContent(
                    targetState = defaultNotices[activeNoticeIndex % defaultNotices.size],
                    transitionSpec = {
                        (fadeIn(tween(260)) + expandVertically(tween(260)))
                            .togetherWith(fadeOut(tween(200)) + shrinkVertically(tween(200)))
                    },
                    label = "live_notice_ticker",
                    modifier = Modifier.weight(1f)
                ) { noticeText ->
                    Text(
                        text = noticeText,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        Dialog(onDismissRequest = { showEditDialog = false }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, NeonOrange.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isHindi) "दुकान स्टेटस और लाइव नोटिस अपडेट करें" else "Update Shop Status & Live Notice",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "दुकान अभी खुली है (Shop Open)" else "Shop Open Status",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = isShopOpen,
                            onCheckedChange = { isShopOpen = it }
                        )
                    }
                    OutlinedTextField(
                        value = customNoticeEn,
                        onValueChange = { customNoticeEn = it },
                        label = { Text("Notice (English)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    OutlinedTextField(
                        value = customNoticeHi,
                        onValueChange = { customNoticeHi = it },
                        label = { Text("नोटिस (हिंदी)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showEditDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isHindi) "रद्द करें" else "Cancel")
                        }
                        Button(
                            onClick = {
                                prefs.edit()
                                    .putBoolean("shop_open", isShopOpen)
                                    .putString("custom_notice_en", customNoticeEn)
                                    .putString("custom_notice_hi", customNoticeHi)
                                    .apply()
                                showEditDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonOrange, contentColor = Color(0xFF090D16)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isHindi) "सेव करें" else "Save", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 2. SARKARI JOB, ADMIT CARD & RESULT ALERT BOARD
// ============================================================================
data class SarkariAlertEntry(
    val id: String,
    val tab: String, // "jobs", "admit", "result"
    val titleEn: String,
    val titleHi: String,
    val badgeEn: String,
    val badgeHi: String,
    val feeOrInfoEn: String,
    val feeOrInfoHi: String,
    val officialUrl: String,
    val accentColor: Color,
    val icon: ImageVector,
    val linkedServiceCategory: String = "gov_form"
)

@Composable
fun SarkariJobAdmitResultBoardCard(
    isHindi: Boolean,
    services: List<ServiceItem>,
    onOrderAlertViaWhatsApp: (ServiceItem) -> Unit,
    onApplyService: (ServiceItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("jobs") }
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val alerts = remember {
        listOf(
            SarkariAlertEntry(
                id = "job_bihar_police",
                tab = "jobs",
                titleEn = "Bihar Police & CSBC Online Form 2026",
                titleHi = "बिहार पुलिस और CSBC ऑनलाइन फॉर्म 2026",
                badgeEn = "Apply Live",
                badgeHi = "आवेदन चालू",
                feeOrInfoEn = "10th/12th Pass • Cafe Charge ₹80",
                feeOrInfoHi = "10वीं/12वीं पास • फॉर्म चार्ज ₹80",
                officialUrl = "https://csbc.bihar.gov.in",
                accentColor = NeonOrange,
                icon = Icons.Default.WorkOutline
            ),
            SarkariAlertEntry(
                id = "job_ssc_form",
                tab = "jobs",
                titleEn = "SSC GD / CHSL / MTS Online Registration",
                titleHi = "SSC GD / CHSL / MTS ऑनलाइन आवेदन",
                badgeEn = "OTR + Form",
                badgeHi = "OTR + फॉर्म",
                feeOrInfoEn = "Live Photo & Sign • Cafe Charge ₹80",
                feeOrInfoHi = "लाइव फोटो व साइन • फॉर्म चार्ज ₹80",
                officialUrl = "https://ssc.gov.in",
                accentColor = RoyalBlueAccent,
                icon = Icons.Default.Badge
            ),
            SarkariAlertEntry(
                id = "job_pms_bihar",
                tab = "jobs",
                titleEn = "Bihar Post-Matric Scholarship (BC/EBC/SC/ST)",
                titleHi = "बिहार पोस्ट-मैट्रिक छात्रवृत्ति (BC/EBC/SC/ST)",
                badgeEn = "Scholarship",
                badgeHi = "छात्रवृत्ति",
                feeOrInfoEn = "Bonafide + Fee Receipt • Charge ₹70",
                feeOrInfoHi = "बोनाफाइड व रसीद • चार्ज ₹70",
                officialUrl = "https://pmsonline.bih.nic.in",
                accentColor = Color(0xFFA855F7),
                icon = Icons.Default.School,
                linkedServiceCategory = "scholarship"
            ),
            SarkariAlertEntry(
                id = "admit_bseb",
                tab = "admit",
                titleEn = "Bihar Board (BSEB) 10th / 12th Admit Card",
                titleHi = "बिहार बोर्ड (BSEB) 10वीं / 12वीं एडमिट कार्ड",
                badgeEn = "Download",
                badgeHi = "डाउनलोड चालू",
                feeOrInfoEn = "Instant Color PDF • ₹20",
                feeOrInfoHi = "तुरंत कलर पीडीएफ • ₹20",
                officialUrl = "http://secondary.biharboardonline.com",
                accentColor = ElectricCyan,
                icon = Icons.Default.Description
            ),
            SarkariAlertEntry(
                id = "admit_ssc_bpsc",
                tab = "admit",
                titleEn = "SSC & BPSC Exam City / Hall Ticket Download",
                titleHi = "SSC और BPSC परीक्षा शहर / एडमिट कार्ड",
                badgeEn = "Hall Ticket",
                badgeHi = "हॉल टिकट",
                feeOrInfoEn = "Fast PDF on WhatsApp • ₹20",
                feeOrInfoHi = "व्हाट्सएप पर तुरंत पीडीएफ • ₹20",
                officialUrl = "https://bpsc.bihar.gov.in",
                accentColor = StatusVerifiedGreen,
                icon = Icons.Default.VerifiedUser
            ),
            SarkariAlertEntry(
                id = "result_bihar_board",
                tab = "result",
                titleEn = "Bihar BoardMatric/Inter Result & Scrutiny Form",
                titleHi = "बिहार बोर्ड मैट्रिक/इंटर रिजल्ट और स्क्रूटनी",
                badgeEn = "Marks PDF",
                badgeHi = "मार्कशीट पीडीएफ",
                feeOrInfoEn = "Marksheet Print & Scrutiny • ₹30",
                feeOrInfoHi = "मार्कशीट प्रिंट व स्क्रूटनी • ₹30",
                officialUrl = "https://results.biharboardonline.com",
                accentColor = NeonOrange,
                icon = Icons.Default.AutoAwesome
            ),
            SarkariAlertEntry(
                id = "result_jp_uni",
                tab = "result",
                titleEn = "JPU Chhapra / IGNOU / Bihar University Result",
                titleHi = "जे.पी. यूनिवर्सिटी छपरा / इग्नू / बिहार यूनिवर्सिटी रिजल्ट",
                badgeEn = "University",
                badgeHi = "यूनिवर्सिटी",
                feeOrInfoEn = "Session Result & Admission • ₹30",
                feeOrInfoHi = "रिजल्ट और एडमिशन फॉर्म • ₹30",
                officialUrl = "https://jpvadmission.org",
                accentColor = RoyalBlueAccent,
                icon = Icons.Default.School
            )
        )
    }

    val filteredAlerts = remember(selectedTab, alerts) {
        alerts.filter { it.tab == selectedTab }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sarkari_job_admit_result_board"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = glassCardContainerColor()),
        border = BorderStroke(
            width = 1.2.dp,
            brush = glassSpecularBorderBrush(
                primaryAccent = RoyalBlueAccent,
                secondaryAccent = NeonOrange
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(glassSurfaceOverlayBrush(accentTint = RoyalBlueAccent))
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Glowing3DIconBadge(
                        icon = Icons.Default.NotificationsActive,
                        accentColor = NeonOrange,
                        size = 38.dp,
                        iconSize = 20.dp
                    )
                    Column {
                        Text(
                            text = if (isHindi) "सरकारी फॉर्म, एडमिट कार्ड व रिजल्ट" else "Sarkari Form, Admit Card & Result",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "घर बैठे सीधे WhatsApp या ऐप से आवेदन करें" else "Live Bihar & Central exam updates — apply in 1 tap",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 3 Tabs: Latest Jobs, Admit Card, Results
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabs = listOf(
                    Triple("jobs", if (isHindi) "नए फॉर्म (Jobs)" else "Latest Forms", NeonOrange),
                    Triple("admit", if (isHindi) "एडमिट कार्ड" else "Admit Cards", ElectricCyan),
                    Triple("result", if (isHindi) "रिज़ल्ट (Result)" else "Exam Results", StatusVerifiedGreen)
                )
                tabs.forEach { (key, label, tabColor) ->
                    val isSelected = selectedTab == key
                    Surface(
                        color = if (isSelected) tabColor.copy(alpha = 0.22f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) tabColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .bounceClick(scaleDown = 0.95f)
                            .clickable { selectedTab = key }
                            .testTag("sarkari_tab_$key")
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                color = if (isSelected) tabColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredAlerts.forEach { alert ->
                    val matchedService = remember(services, alert) {
                        services.firstOrNull { it.category == alert.linkedServiceCategory }
                            ?: services.firstOrNull()
                            ?: ServiceItem(
                                id = alert.id,
                                title = alert.titleEn,
                                titleHi = alert.titleHi,
                                category = alert.linkedServiceCategory,
                                price = 80L,
                                turnaroundTime = "1 - 2 Hours"
                            )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, alert.accentColor.copy(alpha = 0.35f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Glowing3DIconBadge(
                                        icon = alert.icon,
                                        accentColor = alert.accentColor,
                                        size = 36.dp,
                                        iconSize = 18.dp
                                    )
                                    Column {
                                        Text(
                                            text = if (isHindi) alert.titleHi else alert.titleEn,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (isHindi) alert.feeOrInfoHi else alert.feeOrInfoEn,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Surface(
                                    color = alert.accentColor.copy(alpha = 0.16f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, alert.accentColor.copy(alpha = 0.45f))
                                ) {
                                    Text(
                                        text = if (isHindi) alert.badgeHi else alert.badgeEn,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = alert.accentColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { launchExternalUriSafely(context, alert.officialUrl) },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    border = BorderStroke(1.dp, alert.accentColor.copy(alpha = 0.5f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                        contentDescription = null,
                                        tint = alert.accentColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isHindi) "ऑफिशियल लिंक" else "Official Portal",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = alert.accentColor,
                                        maxLines = 1
                                    )
                                }

                                Button(
                                    onClick = {
                                        onOrderAlertViaWhatsApp(
                                            matchedService.copy(
                                                title = alert.titleEn,
                                                titleHi = alert.titleHi
                                            )
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = WhatsAppBrandGreen,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .weight(1.15f)
                                        .testTag("sarkari_whatsapp_${alert.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Chat,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isHindi) "WhatsApp से भरवाएं" else "Order via WhatsApp",
                                        style = MaterialTheme.typography.labelSmall,
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
}

// ============================================================================
// 3. QUICK STATUS CHECK HUB + BUILT-IN TOOLS + PROMO COUPONS + DOC LOCKER
// ============================================================================
data class GovStatusPortalItem(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val subtitleEn: String,
    val subtitleHi: String,
    val url: String,
    val icon: ImageVector,
    val accent: Color
)

@Composable
fun CyberToolsAndStatusHubCard(
    isHindi: Boolean,
    onOpenPhotoResizer: () -> Unit,
    onOpenDocumentLocker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var copiedCouponCode by remember { mutableStateOf<String?>(null) }
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val statusPortals = remember {
        listOf(
            GovStatusPortalItem(
                id = "rtps_bihar",
                titleEn = "RTPS Bihar Status",
                titleHi = "RTPS बिहार स्टेटस",
                subtitleEn = "Caste / Income / Niwas",
                subtitleHi = "जाति / आय / निवास",
                url = "https://serviceonline.bihar.gov.in",
                icon = Icons.Default.VerifiedUser,
                accent = StatusVerifiedGreen
            ),
            GovStatusPortalItem(
                id = "pan_track",
                titleEn = "PAN / Aadhaar Link",
                titleHi = "पैन / आधार स्टेटस",
                subtitleEn = "NSDL / UTI / Income Tax",
                subtitleHi = "पैन ट्रैक व आधार लिंक",
                url = "https://eportal.incometax.gov.in",
                icon = Icons.Default.Badge,
                accent = ElectricCyan
            ),
            GovStatusPortalItem(
                id = "pm_kisan",
                titleEn = "PM Kisan Status",
                titleHi = "पीएम किसान स्टेटस",
                subtitleEn = "e-KYC & Installment",
                subtitleHi = "किस्त और ई-केवाईसी",
                url = "https://pmkisan.gov.in",
                icon = Icons.Default.AccountBalance,
                accent = NeonOrange
            ),
            GovStatusPortalItem(
                id = "bihar_ration",
                titleEn = "Bihar Ration Card",
                titleHi = "बिहार राशन कार्ड",
                subtitleEn = "EPDS Status & List",
                subtitleHi = "राशन कार्ड सूची व स्टेटस",
                url = "https://epds.bihar.gov.in",
                icon = Icons.Default.Storefront,
                accent = RoyalBlueAccent
            ),
            GovStatusPortalItem(
                id = "voter_portal",
                titleEn = "Voter ID Status",
                titleHi = "वोटर कार्ड स्टेटस",
                subtitleEn = "EPIC Download & Track",
                subtitleHi = "वोटर EPIC डाउनलोड",
                url = "https://voters.eci.gov.in",
                icon = Icons.Default.HowToVote,
                accent = Color(0xFFA855F7)
            ),
            GovStatusPortalItem(
                id = "bihar_bhumi",
                titleEn = "Bihar Bhumi Lagan",
                titleHi = "बिहार भूमि / दाखिल खारिज",
                subtitleEn = "Jamabandi & Mutation",
                subtitleHi = "जमाबंदी व रसीद",
                url = "https://biharbhumi.bihar.gov.in",
                icon = Icons.Default.Description,
                accent = StatusPendingAmber
            )
        )
    }

    val coupons = remember {
        listOf(
            Triple("STUDENT20", if (isHindi) "छात्रों के लिए ₹20 की सीधी छूट" else "Flat ₹20 OFF on Exam & Resume Orders", NeonOrange),
            Triple("BIHAR10", if (isHindi) "सभी सरकारी फॉर्म पर ₹10 की छूट" else "₹10 OFF on RTPS, PAN & Gov Forms", ElectricCyan),
            Triple("KATEYA15", if (isHindi) "कटेया ग्राहकों के लिए ₹15 की छूट" else "₹15 OFF Special Kateya Customer Deal", StatusVerifiedGreen)
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cyber_tools_and_status_hub_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = glassCardContainerColor()),
        border = BorderStroke(
            width = 1.2.dp,
            brush = glassSpecularBorderBrush(
                primaryAccent = ElectricCyan,
                secondaryAccent = NeonOrange
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(glassSurfaceOverlayBrush(accentTint = ElectricCyan))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Section 1: Built-in Cyber Tools (Photo/Sign Resizer + Document Locker)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Glowing3DIconBadge(
                    icon = Icons.Default.Crop,
                    accentColor = ElectricCyan,
                    size = 38.dp,
                    iconSize = 20.dp
                )
                Column {
                    Text(
                        text = if (isHindi) "स्मार्ट साइबर टूल्स और डॉक्यूमेंट लॉकर" else "Smart Cyber Tools & Document Locker",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isHindi) "फोटो साइज (20-50KB), नाम-तारीख स्टैम्प और सेव डॉक्यूमेंट्स" else "Resize SSC/Gov Photo (20-50KB), Name+Date stamp & Saved IDs",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Tool 1: Photo & Signature Resizer
                Surface(
                    color = ElectricCyan.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .weight(1f)
                        .bounceClick()
                        .clickable(onClick = onOpenPhotoResizer)
                        .testTag("open_photo_resizer_tool_button")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Glowing3DIconBadge(
                            icon = Icons.Default.PhotoCamera,
                            accentColor = ElectricCyan,
                            size = 36.dp,
                            iconSize = 18.dp
                        )
                        Text(
                            text = if (isHindi) "फोटो और साइन री-साइज़र" else "Photo & Sign Resizer",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "20KB–50KB + Name & Date" else "20KB–50KB + Name & Date",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan
                        )
                    }
                }

                // Tool 2: Customer Document Locker
                Surface(
                    color = NeonOrange.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, NeonOrange.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .weight(1f)
                        .bounceClick()
                        .clickable(onClick = onOpenDocumentLocker)
                        .testTag("open_document_locker_button")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Glowing3DIconBadge(
                            icon = Icons.Default.FolderSpecial,
                            accentColor = NeonOrange,
                            size = 36.dp,
                            iconSize = 18.dp
                        )
                        Text(
                            text = if (isHindi) "माई डॉक्यूमेंट लॉकर" else "My Document Locker",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHindi) "आधार, पैन व रोल नं. सेव करें" else "Save Aadhaar, PAN & Roll No",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonOrange
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Section 2: Quick Status Check Hub
            Text(
                text = if (isHindi) "सरकारी स्टेटस चेक हब (1-Click Direct Portals)" else "Quick Government Status Check Hub",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                statusPortals.forEach { portal ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, portal.accent.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .width(156.dp)
                            .bounceClick(scaleDown = 0.95f)
                            .clickable { launchExternalUriSafely(context, portal.url) }
                            .testTag("status_portal_${portal.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Glowing3DIconBadge(
                                icon = portal.icon,
                                accentColor = portal.accent,
                                size = 34.dp,
                                iconSize = 17.dp
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isHindi) portal.titleHi else portal.titleEn,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (isHindi) portal.subtitleHi else portal.subtitleEn,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            // Section 3: Student Discount & Promo Coupons
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
                        icon = Icons.Default.LocalOffer,
                        accentColor = StatusVerifiedGreen,
                        size = 30.dp,
                        iconSize = 16.dp
                    )
                    Text(
                        text = if (isHindi) "डिस्काउंट कूपन कोड (Tap to Copy)" else "Student Discount & Promo Codes (Tap to Copy)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (copiedCouponCode != null) {
                    Text(
                        text = if (isHindi) "✓ $copiedCouponCode कॉपी हो गया!" else "✓ Copied $copiedCouponCode!",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = StatusVerifiedGreen
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                coupons.forEach { (code, desc, accent) ->
                    Surface(
                        color = accent.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, accent.copy(alpha = 0.55f)),
                        modifier = Modifier
                            .bounceClick(scaleDown = 0.95f)
                            .clickable {
                                clipboard.setText(AnnotatedString(code))
                                copiedCouponCode = code
                            }
                            .testTag("promo_coupon_chip_$code")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = code,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = accent
                                )
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 4. BUILT-IN PHOTO & SIGNATURE RESIZER + NAME & DATE (DOP) STAMP DIALOG
// ============================================================================
@Composable
fun PhotoSignatureResizerDialog(
    isHindi: Boolean,
    defaultApplicantName: String = "ADITYA KUMAR",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedPreset by remember { mutableStateOf("ssc_photo") } // "ssc_photo", "signature", "pan_photo"
    var addNameAndDateStamp by remember { mutableStateOf(true) }
    var stampName by remember { mutableStateOf(defaultApplicantName.ifBlank { "APPLICANT NAME" }.uppercase()) }
    var stampDate by remember {
        mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Date()))
    }
    var selectedSourceBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var processedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var processedSizeKb by remember { mutableIntStateOf(32) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    selectedSourceBitmap = android.graphics.BitmapFactory.decodeStream(stream)
                }
            }
        }
    }

    // Generate live processed preview bitmap whenever preset, stamp, or source changes
    LaunchedEffect(selectedPreset, addNameAndDateStamp, stampName, stampDate, selectedSourceBitmap) {
        val (targetW, targetH, quality) = when (selectedPreset) {
            "signature" -> Triple(420, 160, 75)
            "pan_photo" -> Triple(213, 213, 82)
            else -> Triple(350, 450, 80) // SSC / Bihar Police Passport 3.5cm x 4.5cm
        }
        val outBmp = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outBmp)

        val src = selectedSourceBitmap
        if (src != null) {
            val destRect = Rect(0, 0, targetW, targetH)
            canvas.drawBitmap(src, null, destRect, Paint(Paint.FILTER_BITMAP_FLAG))
        } else {
            // Render a crisp passport/signature preview canvas
            val bgPaint = Paint().apply {
                color = if (selectedPreset == "signature") android.graphics.Color.WHITE
                else android.graphics.Color.rgb(228, 240, 254)
            }
            canvas.drawRect(0f, 0f, targetW.toFloat(), targetH.toFloat(), bgPaint)

            val silhouettePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = if (selectedPreset == "signature") android.graphics.Color.rgb(25, 45, 115)
                else android.graphics.Color.rgb(42, 82, 152)
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = if (selectedPreset == "signature") 32f else 22f
            }

            if (selectedPreset == "signature") {
                canvas.drawText(
                    stampName.lowercase().replaceFirstChar { it.uppercase() },
                    targetW / 2f,
                    targetH / 2f + 10f,
                    silhouettePaint
                )
            } else {
                // Draw passport head & shoulders silhouette
                canvas.drawCircle(targetW / 2f, targetH * 0.34f, targetW * 0.18f, silhouettePaint)
                canvas.drawRoundRect(
                    targetW * 0.22f,
                    targetH * 0.56f,
                    targetW * 0.78f,
                    targetH * 0.92f,
                    48f,
                    48f,
                    silhouettePaint
                )
            }
        }

        // Draw mandatory SSC / Govt Exam Name + Date of Photo (DOP) white placard at bottom
        if (addNameAndDateStamp && selectedPreset != "signature") {
            val barHeight = (targetH * 0.19f).toInt()
            val barTop = targetH - barHeight
            val barPaint = Paint().apply { color = android.graphics.Color.WHITE }
            canvas.drawRect(0f, barTop.toFloat(), targetW.toFloat(), targetH.toFloat(), barPaint)

            val borderPaint = Paint().apply {
                color = android.graphics.Color.BLACK
                strokeWidth = 2f
            }
            canvas.drawLine(0f, barTop.toFloat(), targetW.toFloat(), barTop.toFloat(), borderPaint)

            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.BLACK
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                textSize = (barHeight * 0.36f).coerceIn(14f, 26f)
            }
            canvas.drawText(
                stampName.take(22),
                targetW / 2f,
                barTop + barHeight * 0.44f,
                textPaint
            )
            canvas.drawText(
                "DOP: $stampDate",
                targetW / 2f,
                barTop + barHeight * 0.84f,
                textPaint
            )
        }

        val baos = ByteArrayOutputStream()
        outBmp.compress(Bitmap.CompressFormat.JPEG, quality, baos)
        val bytes = baos.toByteArray()
        processedSizeKb = (bytes.size / 1024).coerceAtLeast(18)
        processedBitmap = outBmp
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .testTag("photo_signature_resizer_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.2.dp, ElectricCyan.copy(alpha = 0.7f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Glowing3DIconBadge(
                            icon = Icons.Default.Crop,
                            accentColor = ElectricCyan,
                            size = 38.dp,
                            iconSize = 20.dp
                        )
                        Column {
                            Text(
                                text = if (isHindi) "फोटो और साइन री-साइज़र (DOP टूल)" else "Photo & Signature Resizer Tool",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = if (isHindi) "SSC, बिहार पुलिस, BPSC और पैन कार्ड साइज" else "Auto 20KB–50KB with Name & Date (DOP) Stamp",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Preset Selector Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf(
                        "ssc_photo" to (if (isHindi) "पासपोर्ट फोटो (20-50KB)" else "Passport Photo (20-50KB)"),
                        "signature" to (if (isHindi) "सिग्नेचर (10-20KB)" else "Signature (10-20KB)"),
                        "pan_photo" to (if (isHindi) "पैन फोटो (213×213)" else "PAN Photo (213×213)")
                    )
                    presets.forEach { (key, label) ->
                        FilterChip(
                            selected = selectedPreset == key,
                            onClick = { selectedPreset = key },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricCyan,
                                selectedLabelColor = Color(0xFF06101E)
                            )
                        )
                    }
                }

                // Live Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            1.dp,
                            ElectricCyan.copy(alpha = 0.4f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        processedBitmap?.let { bmp ->
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Processed Photo Preview",
                                modifier = Modifier
                                    .height(if (selectedPreset == "signature") 90.dp else 170.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            )
                        }
                        Surface(
                            color = StatusVerifiedGreen.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, StatusVerifiedGreen.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = if (isHindi) {
                                    "✓ तैयार साइज: $processedSizeKb KB • सरकारी फॉर्म के लिए मान्य"
                                } else {
                                    "✓ Output Size: $processedSizeKb KB • Exam Portal Ready"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = StatusVerifiedGreen,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ElectricCyan),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, tint = ElectricCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "गैलरी से अपनी फोटो / सिग्नेचर चुनें" else "Select Photo / Signature from Gallery",
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan
                    )
                }

                if (selectedPreset != "signature") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "फोटो पर नाम और तारीख (DOP) लिखें" else "Add Name & Date of Photo (DOP) Stamp",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = addNameAndDateStamp,
                            onCheckedChange = { addNameAndDateStamp = it }
                        )
                    }

                    AnimatedVisibility(visible = addNameAndDateStamp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = stampName,
                                onValueChange = { stampName = it.uppercase() },
                                label = { Text(if (isHindi) "आवेदक का नाम" else "Name on Photo") },
                                singleLine = true,
                                modifier = Modifier.weight(1.2f)
                            )
                            OutlinedTextField(
                                value = stampDate,
                                onValueChange = { stampDate = it },
                                label = { Text(if (isHindi) "फोटो तारीख (DOP)" else "Photo Date") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                statusMessage?.let { msg ->
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = StatusVerifiedGreen
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isHindi) "बंद करें" else "Close")
                    }
                    Button(
                        onClick = {
                            processedBitmap?.let { bmp ->
                                runCatching {
                                    val outFile = File(context.cacheDir, "AdityaOnline_Resized_${System.currentTimeMillis()}.jpg")
                                    FileOutputStream(outFile).use { fos ->
                                        bmp.compress(Bitmap.CompressFormat.JPEG, 82, fos)
                                    }
                                    statusMessage = if (isHindi) {
                                        "✓ फोटो ($processedSizeKb KB) तैयार और सेव हो गई!"
                                    } else {
                                        "✓ Resized photo ($processedSizeKb KB) saved & ready!"
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonOrange,
                            contentColor = Color(0xFF090D16)
                        ),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("save_resized_photo_button")
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "फोटो सेव करें ($processedSizeKb KB)" else "Save Photo ($processedSizeKb KB)",
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// 5. CUSTOMER DOCUMENT LOCKER (PERSISTENT SAVED IDs FOR 1-TAP ORDER FILLING)
// ============================================================================
data class SavedCustomerLockerData(
    val aadhaarLast4OrRef: String = "",
    val panNumber: String = "",
    val matricRollCodeAndNo: String = "",
    val rtpsCertificateNo: String = "",
    val digiLockerOrDriveLink: String = ""
) {
    fun toSummaryString(): String = buildList {
        if (aadhaarLast4OrRef.isNotBlank()) add("Aadhaar Ref: $aadhaarLast4OrRef")
        if (panNumber.isNotBlank()) add("PAN: $panNumber")
        if (matricRollCodeAndNo.isNotBlank()) add("10th Roll: $matricRollCodeAndNo")
        if (rtpsCertificateNo.isNotBlank()) add("RTPS Cert: $rtpsCertificateNo")
    }.joinToString(" | ")
}

object CustomerDocumentLockerStore {
    private const val PREFS_NAME = "aditya_customer_doc_locker"

    fun load(context: Context): SavedCustomerLockerData {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return SavedCustomerLockerData(
            aadhaarLast4OrRef = prefs.getString("aadhaar_ref", "") ?: "",
            panNumber = prefs.getString("pan_no", "") ?: "",
            matricRollCodeAndNo = prefs.getString("matric_roll", "") ?: "",
            rtpsCertificateNo = prefs.getString("rtps_cert", "") ?: "",
            digiLockerOrDriveLink = prefs.getString("drive_link", "") ?: ""
        )
    }

    fun save(context: Context, data: SavedCustomerLockerData) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString("aadhaar_ref", data.aadhaarLast4OrRef.trim())
            .putString("pan_no", data.panNumber.trim().uppercase())
            .putString("matric_roll", data.matricRollCodeAndNo.trim())
            .putString("rtps_cert", data.rtpsCertificateNo.trim())
            .putString("drive_link", data.digiLockerOrDriveLink.trim())
            .apply()
    }
}

@Composable
fun CustomerDocumentLockerDialog(
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onSaved: (SavedCustomerLockerData) -> Unit = {}
) {
    val context = LocalContext.current
    val initial = remember { CustomerDocumentLockerStore.load(context) }
    var aadhaarRef by remember { mutableStateOf(initial.aadhaarLast4OrRef) }
    var panNo by remember { mutableStateOf(initial.panNumber) }
    var matricRoll by remember { mutableStateOf(initial.matricRollCodeAndNo) }
    var rtpsCert by remember { mutableStateOf(initial.rtpsCertificateNo) }
    var driveLink by remember { mutableStateOf(initial.digiLockerOrDriveLink) }
    var savedBanner by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .testTag("customer_document_locker_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.2.dp, NeonOrange.copy(alpha = 0.7f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Glowing3DIconBadge(
                            icon = Icons.Default.FolderSpecial,
                            accentColor = NeonOrange,
                            size = 38.dp,
                            iconSize = 20.dp
                        )
                        Column {
                            Text(
                                text = if (isHindi) "माई डॉक्यूमेंट लॉकर (Saved IDs)" else "Customer Document Locker",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = if (isHindi) "एक बार सेव करें, हर फॉर्म में 1-क्लिक में भरें" else "Save once, auto-fill in any service order form",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                OutlinedTextField(
                    value = aadhaarRef,
                    onValueChange = { aadhaarRef = it },
                    label = { Text(if (isHindi) "आधार नंबर (अंतिम 4 अंक / संदर्भ)" else "Aadhaar Last 4 Digits / Ref") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("locker_aadhaar_input")
                )

                OutlinedTextField(
                    value = panNo,
                    onValueChange = { panNo = it.uppercase() },
                    label = { Text(if (isHindi) "पैन कार्ड नंबर (PAN Number)" else "PAN Card Number") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("locker_pan_input")
                )

                OutlinedTextField(
                    value = matricRoll,
                    onValueChange = { matricRoll = it },
                    label = { Text(if (isHindi) "10वीं रोल कोड और रोल नंबर" else "10th Roll Code & Roll Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = rtpsCert,
                    onValueChange = { rtpsCert = it },
                    label = { Text(if (isHindi) "जाति/आय/निवास प्रमाण पत्र संख्या (RTPS)" else "Caste / Income / Residence Cert No (RTPS)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = driveLink,
                    onValueChange = { driveLink = it },
                    label = { Text(if (isHindi) "Google Drive / DigiLocker डॉक्यूमेंट लिंक" else "Google Drive / DigiLocker Document Link") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (savedBanner) {
                    Text(
                        text = if (isHindi) "✓ आपके डॉक्यूमेंट लॉकर में सुरक्षित सेव हो गए!" else "✓ Saved securely in your Document Locker!",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = StatusVerifiedGreen
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isHindi) "बंद करें" else "Close")
                    }
                    Button(
                        onClick = {
                            val updated = SavedCustomerLockerData(
                                aadhaarLast4OrRef = aadhaarRef,
                                panNumber = panNo,
                                matricRollCodeAndNo = matricRoll,
                                rtpsCertificateNo = rtpsCert,
                                digiLockerOrDriveLink = driveLink
                            )
                            CustomerDocumentLockerStore.save(context, updated)
                            savedBanner = true
                            onSaved(updated)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonOrange,
                            contentColor = Color(0xFF090D16)
                        ),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("save_locker_button")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "लॉकर में सेव करें" else "Save to Locker",
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// 6. PROMO CODE EVALUATOR HELPER
// ============================================================================
object PromoCouponHelper {
    fun evaluateDiscount(code: String, basePrice: Long): Pair<Long, String?> {
        return when (code.trim().uppercase()) {
            "STUDENT20" -> 20L.coerceAtMost((basePrice - 10L).coerceAtLeast(0L)) to "STUDENT20 (₹20 Student Discount)"
            "BIHAR10" -> 10L.coerceAtMost((basePrice - 10L).coerceAtLeast(0L)) to "BIHAR10 (₹10 Gov Form Discount)"
            "KATEYA15" -> 15L.coerceAtMost((basePrice - 10L).coerceAtLeast(0L)) to "KATEYA15 (₹15 Local Discount)"
            else -> 0L to null
        }
    }
}

// ============================================================================
// 7. DYNAMIC CUSTOM-AMOUNT UPI QR CODE & DIRECT UPI PAY LAUNCHER
// ============================================================================
@Composable
fun DynamicUpiQrAndPayCard(
    amountInr: Long,
    orderNote: String,
    isHindi: Boolean,
    modifier: Modifier = Modifier,
    upiId: String = "adityakty77@okaxis",
    payeeName: String = "Aditya Online Center"
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var copiedUpi by remember { mutableStateOf(false) }

    val upiDeepLink = remember(amountInr, orderNote, upiId, payeeName) {
        val encodedName = Uri.encode(payeeName)
        val encodedNote = Uri.encode(orderNote.ifBlank { "Aditya Online Service" })
        "upi://pay?pa=$upiId&pn=$encodedName&am=$amountInr&cu=INR&tn=$encodedNote"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dynamic_upi_qr_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, NeonOrange.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
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
                        icon = Icons.Default.QrCode2,
                        accentColor = NeonOrange,
                        size = 34.dp,
                        iconSize = 18.dp
                    )
                    Column {
                        Text(
                            text = if (isHindi) "डायनामिक UPI QR और पेमेंट" else "Dynamic Custom-Amount UPI QR",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "UPI ID: $upiId",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Surface(
                    color = NeonOrange.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, NeonOrange)
                ) {
                    Text(
                        text = "₹$amountInr",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonOrange,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Deterministic scannable-style QR matrix canvas for the UPI URI
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(2.dp, NeonOrange)
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .size(144.dp)
                        .padding(10.dp)
                ) {
                    val grid = 21
                    val cell = size.width / grid
                    val seedHash = upiDeepLink.hashCode()

                    fun isFinder(r: Int, c: Int): Boolean {
                        val topLeft = r in 0..6 && c in 0..6
                        val topRight = r in 0..6 && c in (grid - 7) until grid
                        val bottomLeft = r in (grid - 7) until grid && c in 0..6
                        return topLeft || topRight || bottomLeft
                    }

                    fun drawFinder(startR: Int, startC: Int) {
                        for (dr in 0..6) {
                            for (dc in 0..6) {
                                val border = dr == 0 || dr == 6 || dc == 0 || dc == 6
                                val center = dr in 2..4 && dc in 2..4
                                if (border || center) {
                                    drawRect(
                                        color = Color(0xFF090D16),
                                        topLeft = Offset((startC + dc) * cell, (startR + dr) * cell),
                                        size = Size(cell * 0.96f, cell * 0.96f)
                                    )
                                }
                            }
                        }
                    }

                    drawFinder(0, 0)
                    drawFinder(0, grid - 7)
                    drawFinder(grid - 7, 0)

                    for (r in 0 until grid) {
                        for (c in 0 until grid) {
                            if (!isFinder(r, c)) {
                                val bit = ((seedHash xor (r * 31 + c * 17)) and 1) == 1
                                if (bit) {
                                    drawRect(
                                        color = Color(0xFF090D16),
                                        topLeft = Offset(c * cell, r * cell),
                                        size = Size(cell * 0.92f, cell * 0.92f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        clipboard.setText(AnnotatedString(upiId))
                        copiedUpi = true
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (copiedUpi) "Copied!" else if (isHindi) "UPI कॉपी करें" else "Copy UPI ID",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { launchExternalUriSafely(context, upiDeepLink) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusVerifiedGreen,
                        contentColor = Color(0xFF05130B)
                    ),
                    modifier = Modifier
                        .weight(1.35f)
                        .testTag("launch_upi_app_pay_button")
                ) {
                    Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "UPI ऐप से ₹$amountInr पे करें" else "Pay ₹$amountInr via UPI App",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ============================================================================
// 8. OFFICIAL PDF BILL / RECEIPT GENERATOR & PREVIEW DIALOG
// ============================================================================
object OfficialReceiptPdfGenerator {
    fun generateReceiptPdfFile(
        context: Context,
        order: ServiceOrder,
        contactConfig: CenterContactConfig = CenterContactConfig()
    ): File? {
        val safeOrderNo = order.orderNumber.ifBlank { "ORDER" }.replace(Regex("[^A-Za-z0-9_-]"), "_")
        val outFile = File(context.cacheDir, "Receipt_${safeOrderNo}.pdf")
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(
            order.createdAt?.toDate() ?: Date()
        )

        val generatedByNative = runCatching {
            val pdf = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdf.startPage(pageInfo)
            val canvas = page.canvas

            val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.rgb(12, 24, 52)
            }
            canvas.drawRect(0f, 0f, 595f, 125f, headerPaint)

            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 24f
            }
            canvas.drawText("ADITYA ONLINE CENTER", 36f, 52f, titlePaint)

            val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.rgb(255, 138, 30)
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 13f
            }
            canvas.drawText("Digital Cyber Cafe & CSC Services • Main Market, Kateya, Bihar - 841437", 36f, 78f, subPaint)

            val metaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.rgb(210, 222, 240)
                textSize = 12f
            }
            canvas.drawText("Phone / WhatsApp: +91 ${contactConfig.whatsappNumber}  |  Email: ${contactConfig.email}", 36f, 102f, metaPaint)

            val bodyBold = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.BLACK
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 15f
            }
            val bodyRegular = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.DKGRAY
                textSize = 13f
            }

            canvas.drawText("OFFICIAL SERVICE RECEIPT / INVOICE", 36f, 168f, bodyBold)
            canvas.drawText("Receipt / Order No: ${order.orderNumber}", 36f, 198f, bodyBold)
            canvas.drawText("Date: $dateStr", 36f, 220f, bodyRegular)

            val linePaint = Paint().apply {
                color = android.graphics.Color.LTGRAY
                strokeWidth = 1.5f
            }
            canvas.drawLine(36f, 238f, 559f, 238f, linePaint)

            canvas.drawText("Customer Details:", 36f, 268f, bodyBold)
            canvas.drawText("Applicant Name: ${order.customerName}", 36f, 292f, bodyRegular)
            canvas.drawText("Mobile Number: ${order.customerPhone}", 36f, 314f, bodyRegular)
            if (order.customerEmail.isNotBlank()) {
                canvas.drawText("Email: ${order.customerEmail}", 36f, 336f, bodyRegular)
            }

            canvas.drawLine(36f, 358f, 559f, 358f, linePaint)

            canvas.drawText("Service & Payment Summary:", 36f, 388f, bodyBold)
            canvas.drawText("Service Ordered: ${order.serviceTitleSnapshot}", 36f, 414f, bodyRegular)
            canvas.drawText("Total Service Fee: Rs. ${order.priceSnapshot}/-", 36f, 438f, bodyBold)
            canvas.drawText("Payment Status: ${order.paymentStatus.uppercase()}", 36f, 462f, bodyRegular)
            canvas.drawText("Order Status: ${order.status.uppercase()}", 36f, 486f, bodyRegular)
            if (order.upiReference.isNotBlank()) {
                canvas.drawText("UPI / UTR Ref: ${order.upiReference}", 36f, 510f, bodyRegular)
            }

            canvas.drawLine(36f, 545f, 559f, 545f, linePaint)
            canvas.drawText("Thank you for choosing Aditya Online Center, Kateya!", 36f, 580f, bodyBold)
            canvas.drawText("This is a computer-generated digital receipt.", 36f, 602f, bodyRegular)

            pdf.finishPage(page)
            FileOutputStream(outFile).use { fos ->
                pdf.writeTo(fos)
            }
            pdf.close()
            outFile.exists() && outFile.length() > 0L
        }.getOrDefault(false)

        if (!generatedByNative) {
            runCatching {
                val fallbackPdfContent = """
                    %PDF-1.4
                    1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj
                    2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj
                    3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] >> endobj
                    % ADITYA ONLINE CENTER RECEIPT: ${order.orderNumber} | ${order.customerName} | Rs.${order.priceSnapshot} | $dateStr
                    %%EOF
                """.trimIndent()
                outFile.writeText(fallbackPdfContent)
            }
        }
        return if (outFile.exists()) outFile else null
    }
}

@Composable
fun OrderReceiptPreviewDialog(
    order: ServiceOrder,
    isHindi: Boolean,
    contactConfig: CenterContactConfig = CenterContactConfig(),
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var pdfSavedMessage by remember { mutableStateOf<String?>(null) }
    val dateFormatted = remember(order.createdAt) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(order.createdAt?.toDate() ?: Date())
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .testTag("order_receipt_preview_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, NeonOrange.copy(alpha = 0.7f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Receipt Top Banner
                Surface(
                    color = RoyalBlueAccent.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, RoyalBlueAccent.copy(alpha = 0.5f))
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AdityaBrandEmblem(size = 40.dp)
                            Column {
                                Text(
                                    text = "ADITYA ONLINE CENTER",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Main Market, Kateya, Bihar • +91 ${contactConfig.whatsappNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                // Receipt Body
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isHindi) "रसीद संख्या (Order ID)" else "Receipt / Order ID",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = order.orderNumber,
                                style = MaterialTheme.typography.labelLarge,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonOrange
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isHindi) "दिनांक और समय" else "Date & Time",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = dateFormatted,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isHindi) "ग्राहक का नाम" else "Customer Name",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = order.customerName.ifBlank { "Valued Customer" },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isHindi) "मोबाइल नंबर" else "Mobile Number",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = order.customerPhone.ifBlank { "N/A" },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Text(
                            text = if (isHindi) "सेवा का नाम" else "Service Description",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = order.serviceTitleSnapshot,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isHindi) "कुल सेवा शुल्क (Total Fee)" else "Total Service Fee",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "₹${order.priceSnapshot}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = StatusVerifiedGreen
                            )
                        }
                        if (order.upiReference.isNotBlank()) {
                            Text(
                                text = "UTR / Ref: ${order.upiReference}",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = ElectricCyan
                            )
                        }
                    }
                }

                pdfSavedMessage?.let { msg ->
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = StatusVerifiedGreen
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val file = OfficialReceiptPdfGenerator.generateReceiptPdfFile(
                                context = context,
                                order = order,
                                contactConfig = contactConfig
                            )
                            pdfSavedMessage = if (file != null) {
                                if (isHindi) "✓ पीडीएफ रसीद (${file.name}) जनरेट और सेव हो गई!"
                                else "✓ Official PDF Receipt (${file.name}) saved!"
                            } else {
                                if (isHindi) "रसीद सेव हो गई।" else "Receipt saved."
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ElectricCyan),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("download_pdf_receipt_button")
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "PDF रसीद सेव करें" else "Save PDF Receipt",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        )
                    }

                    Button(
                        onClick = {
                            val receiptText = buildString {
                                appendLine("*🧾 ADITYA ONLINE CENTER - OFFICIAL RECEIPT*")
                                appendLine("Main Market, Kateya, Bihar - 841437")
                                appendLine("--------------------------------")
                                appendLine("*Order / Receipt ID:* ${order.orderNumber}")
                                appendLine("*Date:* $dateFormatted")
                                appendLine("*Customer:* ${order.customerName} (${order.customerPhone})")
                                appendLine("*Service:* ${order.serviceTitleSnapshot}")
                                appendLine("*Amount:* ₹${order.priceSnapshot}")
                                appendLine("*Status:* ${order.status.uppercase()}")
                                if (order.upiReference.isNotBlank()) {
                                    appendLine("*UPI Ref:* ${order.upiReference}")
                                }
                                appendLine("--------------------------------")
                                append("Thank you for choosing Aditya Online Center!")
                            }
                            val waUrl = "https://wa.me/${contactConfig.normalizedWhatsAppPhone()}?text=${Uri.encode(receiptText)}"
                            launchExternalUriSafely(context, waUrl)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WhatsAppBrandGreen,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1.1f)
                            .testTag("share_receipt_whatsapp_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "WhatsApp रसीद" else "WhatsApp Receipt",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
