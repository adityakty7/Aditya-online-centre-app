package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.ServiceOrder
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.StatusErrorRed
import com.example.ui.theme.StatusPendingAmber
import com.example.ui.theme.StatusVerifiedGreen

@Composable
fun ServiceDetailScreen(
    service: ServiceItem,
    isHindi: Boolean,
    onBack: () -> Unit,
    onStartOrder: (ServiceItem) -> Unit
) {
    BackHandler(onBack = onBack)
    val categoryEnum = ServiceCategory.fromKey(service.category)

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
                    .testTag("service_detail_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isHindi) "सेवा विवरण और आवश्यक दस्तावेज़" else "Service Details & Requirements",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                if (service.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = service.imageUrl,
                        contentDescription = service.localizedTitle(isHindi),
                        placeholder = painterResource(id = R.drawable.img_cyber_hero),
                        error = painterResource(id = R.drawable.img_cyber_hero),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.img_cyber_hero),
                        contentDescription = service.localizedTitle(isHindi),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = categoryEnum.label(isHindi),
                            style = MaterialTheme.typography.labelLarge,
                            color = NeonOrange,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = if (service.active) StatusVerifiedGreen.copy(alpha = 0.16f)
                        else StatusErrorRed.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (service.active) {
                                if (isHindi) "ऑर्डर के लिए उपलब्ध" else "AVAILABLE FOR ORDER"
                            } else {
                                if (isHindi) "वर्तमान में बंद है" else "CURRENTLY INACTIVE"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = if (service.active) StatusVerifiedGreen else StatusErrorRed,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = service.localizedTitle(isHindi),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = service.localizedDescription(isHindi),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isHindi) "आधिकारिक सेवा शुल्क" else "Authoritative Service Fee",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${service.price}",
                            style = MaterialTheme.typography.headlineLarge,
                            color = NeonOrange
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (isHindi) "अनुमानित समय" else "Estimated Turnaround",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = service.turnaroundTime,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Required Documents Checklist Card
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
                Text(
                    text = if (isHindi) "आवश्यक दस्तावेज़ और विवरण" else "Required Documents & Information",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                service.requirements.forEach { req ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NeonOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = req,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Button(
            onClick = { onStartOrder(service) },
            enabled = service.active,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .minimumInteractiveComponentSize()
                .testTag("proceed_to_order_form_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = if (service.active) {
                    if (isHindi) "आवेदन फॉर्म भरें (₹${service.price})" else "Fill Order Application Form (₹${service.price})"
                } else {
                    if (isHindi) "यह सेवा अभी बंद है" else "This Service is Currently Inactive"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
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
    ) -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current

    var applicantName by remember(defaultName) { mutableStateOf(defaultName) }
    var applicantPhone by remember(defaultPhone) { mutableStateOf(defaultPhone) }
    var fatherOrGuardianName by remember { mutableStateOf("") }
    var villageOrAddress by remember(defaultAddress) {
        mutableStateOf(defaultAddress.ifBlank { "Kateya, Gopalganj, Bihar" })
    }

    // Dynamic service-specific fields
    var primaryDynamicField by remember { mutableStateOf("") }
    var secondaryDynamicField by remember { mutableStateOf("") }
    var customInstructions by remember { mutableStateOf("") }

    // Document attachments (zero-permission OpenDocument picker + cloud link)
    val attachedDocs = remember { mutableStateListOf<String>() }
    var externalDocLinkInput by remember { mutableStateOf("") }

    // Manual UPI payment reference
    var upiReference by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("order_form_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = if (isHindi) "ऑनलाइन सेवा आवेदन फॉर्म" else "Service Order Application",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${service.localizedTitle(isHindi)} • ₹${service.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonOrange
                )
            }
        }

        // Applicant Personal Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isHindi) "1. आवेदक की जानकारी" else "1. Applicant Information",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonOrange
                )

                OutlinedTextField(
                    value = applicantName,
                    onValueChange = { applicantName = it },
                    label = { Text(if (isHindi) "आवेदक का पूरा नाम *" else "Applicant Full Name *") },
                    singleLine = true,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("order_applicant_phone_input")
                )

                OutlinedTextField(
                    value = fatherOrGuardianName,
                    onValueChange = { fatherOrGuardianName = it },
                    label = { Text(if (isHindi) "पिता / अभिभावक का नाम" else "Father / Guardian Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = villageOrAddress,
                    onValueChange = { villageOrAddress = it },
                    label = { Text(if (isHindi) "ग्राम / पोस्ट / थाना (कटेया, गोपालगंज)" else "Village / Post / Block (Kateya, Gopalganj)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Dynamic Service-Specific Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isHindi) "2. सेवा-विशिष्ट विवरण" else "2. Service-Specific Application Details",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonOrange
                )

                OutlinedTextField(
                    value = primaryDynamicField,
                    onValueChange = { primaryDynamicField = it },
                    label = { Text("$primaryFieldLabel *") },
                    minLines = 2,
                    maxLines = 4,
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
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Document Attachments & Links Card
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
                    text = if (isHindi) "3. सहायक दस्तावेज़ (पीडीएफ / फोटो / ड्राइव लिंक)" else "3. Supporting Documents (PDF / Photo / Drive Link)",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonOrange
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            val trimmed = externalDocLinkInput.trim()
                            if (trimmed.isNotEmpty() && attachedDocs.size < 10) {
                                attachedDocs.add(trimmed)
                                externalDocLinkInput = ""
                            }
                        },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Text(if (isHindi) "जोड़ें" else "Add")
                    }
                }

                OutlinedButton(
                    onClick = {
                        docPickerLauncher.launch(arrayOf("application/pdf", "image/*"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("pick_document_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isHindi) "फोन से पीडीएफ या फोटो चुनें (Select PDF / Image)"
                        else "Select PDF or Image from Device"
                    )
                }

                if (attachedDocs.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        attachedDocs.forEachIndexed { idx, docItem ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp)
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

        // Manual UPI Payment Verification Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, NeonOrange.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = null,
                        tint = NeonOrange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) {
                            "4. यूपीआई (UPI) भुगतान और मैन्युअल सत्यापन (₹${service.price})"
                        } else {
                            "4. UPI Payment & Manual Verification (₹${service.price})"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = if (isHindi) {
                        "आप अभी या ऑर्डर सबमिट करने के बाद UPI के माध्यम से ₹${service.price} का भुगतान कर सकते हैं। सुरक्षा नियम: केवल UTR नंबर डालने से ऑर्डर 'Paid' नहीं माना जाता — एडमिन बैंक स्टेटमेंट से मिलान के बाद ही भुगतान सत्यापित (Verified) करते हैं।"
                    } else {
                        "Pay ₹${service.price} via any UPI app (PhonePe, GPay, Paytm) now or after submitting. Security Rule: Providing a UTR marks payment as 'Pending Verification' until the admin verifies receipt."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = upiReference,
                    onValueChange = { upiReference = it.take(40) },
                    label = {
                        Text(
                            if (isHindi) "12-अंकीय UPI UTR / ट्रांजेक्शन नंबर (वैकल्पिक)"
                            else "12-Digit UPI UTR / Transaction Reference (Optional)"
                        )
                    },
                    singleLine = true,
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
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricCyan
                    )
                }
            }
        }

        if (validationError != null) {
            Surface(
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Text(
                    text = validationError!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }
        }

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
                if (primaryDynamicField.trim().length < 3) {
                    validationError = if (isHindi) "कृपया आवश्यक सेवा विवरण भरें।" else "Please fill in the required service details."
                    return@Button
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
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .minimumInteractiveComponentSize()
                .testTag("submit_order_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(if (isHindi) "सर्वर पर ऑर्डर दर्ज हो रहा है…" else "Confirming Order with Server…")
            } else {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHindi) "आवेदन सबमिट करें (₹${service.price})" else "Submit Confirmed Order (₹${service.price})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OrderConfirmationScreen(
    order: ServiceOrder,
    isHindi: Boolean,
    onTrackOrder: (ServiceOrder) -> Unit,
    onBackToHome: () -> Unit
) {
    BackHandler(onBack = onBackToHome)
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, StatusVerifiedGreen)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Order Confirmed",
                    tint = StatusVerifiedGreen,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isHindi) "आपका ऑर्डर सफलतापूर्वक दर्ज हो गया!" else "Order Confirmed on Shared Backend!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isHindi) {
                        "आदित्य ऑनलाइन सेंटर, कटेया के एडमिन पैनल में आपका आवेदन पहुँच चुका है।"
                    } else {
                        "Your application is now live in the Aditya Online Center admin panel."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(18.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, NeonOrange)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isHindi) "यूनिक ऑर्डर आईडी (Order ID)" else "UNIQUE ORDER ID",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = order.orderNumber,
                                style = MaterialTheme.typography.titleLarge,
                                color = NeonOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(order.orderNumber))
                            },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Order ID",
                                tint = NeonOrange
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

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = { onTrackOrder(order) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("track_confirmed_order_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isHindi) "लाइव स्टेटस ट्रैक करें" else "Track Live Order Status")
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onBackToHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("back_to_home_after_order_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(if (isHindi) "होम पेज पर लौटें" else "Back to Service Catalogue")
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
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
