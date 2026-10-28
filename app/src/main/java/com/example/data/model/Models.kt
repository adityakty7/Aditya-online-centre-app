package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

const val TENANT_ID_ADITYA = "aditya_online_center"
const val BOOTSTRAPPED_ADMIN_EMAIL = "adityakty77@gmail.com"
const val BUSINESS_SETTINGS_DOC_ID = "business_profile"

@IgnoreExtraProperties
data class ServiceItem(
    @DocumentId val id: String = "",
    val tenantId: String = TENANT_ID_ADITYA,
    val title: String = "",
    val titleHi: String = "",
    val category: String = "resume",
    val shortDescription: String = "",
    val shortDescriptionHi: String = "",
    val description: String = "",
    val descriptionHi: String = "",
    val price: Long = 0L,
    val turnaroundTime: String = "2 - 4 Hours",
    val iconKey: String = "description",
    val requirements: List<String> = emptyList(),
    val imageUrl: String = "",
    val active: Boolean = true,
    val isFeatured: Boolean = false,
    val featuredOrder: Long = 999L,
    val sortOrder: Long = 100L,
    val isArchived: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun localizedTitle(isHindi: Boolean): String =
        if (isHindi && titleHi.isNotBlank()) titleHi else title

    fun localizedShortDescription(isHindi: Boolean): String {
        val candidate = if (isHindi && shortDescriptionHi.isNotBlank()) {
            shortDescriptionHi
        } else if (shortDescription.isNotBlank()) {
            shortDescription
        } else {
            ""
        }
        return candidate.ifBlank { localizedDescription(isHindi) }
    }

    fun localizedDescription(isHindi: Boolean): String =
        if (isHindi && descriptionHi.isNotBlank()) descriptionHi else description
}

@IgnoreExtraProperties
data class ServiceCategoryItem(
    @DocumentId val id: String = "",
    val tenantId: String = TENANT_ID_ADITYA,
    val key: String = "",
    val labelEn: String = "",
    val labelHi: String = "",
    val iconKey: String = "category",
    val sortOrder: Long = 10L,
    val active: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun localizedLabel(isHindi: Boolean): String =
        if (isHindi && labelHi.isNotBlank()) labelHi else labelEn
}

data class DocumentRequirementSpec(
    val id: String,
    val labelEn: String,
    val labelHi: String = "",
    val isRequired: Boolean = true,
    val instructions: String = ""
) {
    fun toFormattedRequirementString(): String {
        val reqTag = if (isRequired) "[Required]" else "[Optional]"
        val cleanInstr = instructions.trim()
        return if (cleanInstr.isNotBlank()) {
            "${labelEn.trim()} $reqTag - $cleanInstr".take(280)
        } else {
            "${labelEn.trim()} $reqTag".take(280)
        }
    }

    companion object {
        fun fromFormattedString(raw: String, index: Int = 0): DocumentRequirementSpec {
            val trimmed = raw.trim()
            val isOptional = trimmed.contains("[Optional]", ignoreCase = true)
            val isRequired = !isOptional
            val withoutTag = trimmed
                .replace("[Required]", "", ignoreCase = true)
                .replace("[Optional]", "", ignoreCase = true)
                .trim()
            val parts = withoutTag.split(" - ", limit = 2)
            val label = parts.firstOrNull()?.trim().orEmpty().ifBlank { trimmed }
            val instr = if (parts.size > 1) parts[1].trim() else ""
            val matchedDefault = DocumentLibraryCatalog.defaultDocuments.find {
                it.labelEn.equals(label, ignoreCase = true)
            }
            return DocumentRequirementSpec(
                id = matchedDefault?.id ?: "doc_custom_${index}_${label.lowercase().replace(Regex("[^a-z0-9]+"), "_").take(20)}",
                labelEn = label,
                labelHi = matchedDefault?.labelHi ?: label,
                isRequired = isRequired,
                instructions = instr
            )
        }
    }
}

object DocumentLibraryCatalog {
    val defaultDocuments: List<DocumentRequirementSpec> = listOf(
        DocumentRequirementSpec("doc_aadhaar", "Aadhaar Card", "आधार कार्ड", true, "Front & Back clear copy"),
        DocumentRequirementSpec("doc_pan", "PAN Card", "पैन कार्ड", true, "Clear copy of PAN card"),
        DocumentRequirementSpec("doc_voter_id", "Voter ID", "वोटर आईडी कार्ड (EPIC)", false, "EPIC card front & back"),
        DocumentRequirementSpec("doc_passport", "Passport", "पासपोर्ट", false, "Bio page scan"),
        DocumentRequirementSpec("doc_photograph", "Photograph", "फोटोग्राफ", true, "Recent clear colour photo"),
        DocumentRequirementSpec("doc_signature", "Signature", "हस्ताक्षर (Signature)", true, "Black or blue ink on white paper"),
        DocumentRequirementSpec("doc_mobile", "Mobile Number", "मोबाइल नंबर", true, "Active 10-digit WhatsApp/SMS number"),
        DocumentRequirementSpec("doc_email", "Email Address", "ईमेल आईडी", false, "Active email ID for portal OTP"),
        DocumentRequirementSpec("doc_bank_passbook", "Bank Passbook", "बैंक पासबुक", false, "First page showing Account No. & IFSC"),
        DocumentRequirementSpec("doc_bank_details", "Bank Account Details", "बैंक खाता विवरण", false, "Account Number, IFSC & Branch name"),
        DocumentRequirementSpec("doc_income_cert", "Income Certificate", "आय प्रमाण पत्र (RTPS)", false, "Valid Bihar RTPS Income Certificate"),
        DocumentRequirementSpec("doc_caste_cert", "Caste Certificate", "जाति प्रमाण पत्र (RTPS)", false, "Valid Bihar RTPS Caste Certificate"),
        DocumentRequirementSpec("doc_residence_cert", "Residence/Domicile Certificate", "निवास / आवासीय प्रमाण पत्र", false, "Valid Bihar RTPS Residence Certificate"),
        DocumentRequirementSpec("doc_birth_cert", "Birth Certificate", "जन्म प्रमाण पत्र", false, "Official birth certificate"),
        DocumentRequirementSpec("doc_death_cert", "Death Certificate", "मृत्यु प्रमाण पत्र", false, "Official death certificate"),
        DocumentRequirementSpec("doc_class10", "Class 10 Marksheet", "10वीं (मैट्रिक) मार्कशीट", false, "Board marksheet / certificate"),
        DocumentRequirementSpec("doc_class12", "Class 12 Marksheet", "12वीं (इंटर) मार्कशीट", false, "Intermediate marksheet"),
        DocumentRequirementSpec("doc_graduation", "Graduation Certificate", "स्नातक (Graduation) प्रमाण पत्र", false, "Final year marksheet / degree"),
        DocumentRequirementSpec("doc_school_id", "School/College ID", "स्कूल / कॉलेज आईडी कार्ड", false, "Current session ID or admission receipt"),
        DocumentRequirementSpec("doc_bonafide", "Bonafide Certificate", "बोनाफाइड प्रमाण पत्र", false, "Signed & stamped by Principal/Institute"),
        DocumentRequirementSpec("doc_disability", "Disability Certificate", "दिव्यांग प्रमाण पत्र (UDID)", false, "Medical board disability certificate"),
        DocumentRequirementSpec("doc_ration_card", "Ration Card", "राशन कार्ड", false, "Family ration card copy"),
        DocumentRequirementSpec("doc_driving_licence", "Driving Licence", "ड्राइविंग लाइसेंस", false, "Valid LMV/MCWG driving licence"),
        DocumentRequirementSpec("doc_passport_photo", "Passport-size Photo", "पासपोर्ट साइज फोटो", true, "Light background, both ears visible"),
        DocumentRequirementSpec("doc_other_custom", "Other / Custom Document", "अन्य / कस्टम दस्तावेज़", false, "Specify custom requirement instructions")
    )
}

data class SocialMediaProfileLink(
    val id: String,
    val platform: String,
    val label: String,
    val url: String,
    val iconKey: String = "public",
    val enabled: Boolean = true,
    val sortOrder: Int = 1
) {
    fun serialize(): String {
        val safePlatform = platform.replace("|", " ").trim().ifBlank { "Social" }
        val safeLabel = label.replace("|", " ").trim().ifBlank { safePlatform }
        val safeUrl = url.replace("|", "").trim()
        val safeIcon = iconKey.replace("|", "").trim().ifBlank { "public" }
        return "${id.replace("|", "")}|$safePlatform|$safeLabel|$safeUrl|$safeIcon|$enabled|$sortOrder"
    }

    companion object {
        fun deserialize(spec: String, fallbackOrder: Int = 1): SocialMediaProfileLink? {
            val parts = spec.split("|")
            if (parts.size < 4) return null
            return if (parts.size >= 7) {
                SocialMediaProfileLink(
                    id = parts[0].ifBlank { "soc_$fallbackOrder" },
                    platform = parts[1].ifBlank { "Social" },
                    label = parts[2].ifBlank { parts[1] },
                    url = parts[3],
                    iconKey = parts[4].ifBlank { "public" },
                    enabled = parts[5].toBooleanStrictOrNull() ?: true,
                    sortOrder = parts[6].toIntOrNull() ?: fallbackOrder
                )
            } else {
                // 6-part legacy format: id|label|url|iconKey|enabled|sortOrder
                SocialMediaProfileLink(
                    id = parts[0].ifBlank { "soc_$fallbackOrder" },
                    platform = parts[1].ifBlank { "Social" },
                    label = parts[1].ifBlank { "Social" },
                    url = parts[2],
                    iconKey = parts.getOrNull(3)?.ifBlank { "public" } ?: "public",
                    enabled = parts.getOrNull(4)?.toBooleanStrictOrNull() ?: true,
                    sortOrder = parts.getOrNull(5)?.toIntOrNull() ?: fallbackOrder
                )
            }
        }
    }
}

@IgnoreExtraProperties
data class ServiceOrder(
    @DocumentId val id: String = "",
    val orderNumber: String = "",
    val userId: String = "",
    val customerUid: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val customerEmail: String = "",
    val sourceChannel: String = "android",
    val serviceId: String = "",
    val serviceTitleSnapshot: String = "",
    val priceSnapshot: Long = 0L,
    val applicantDetailsSummary: String = "",
    val documentLinks: List<String> = emptyList(),
    val upiReference: String = "",
    val paymentStatus: String = "unpaid",
    val status: String = "pending",
    val adminNotes: String = "",
    val deliveryFileUrl: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "Kateya, Gopalganj, Bihar",
    val preferredLanguage: String = "en",
    val whatsappNumber: String = "9876543210",
    val instagramUrl: String = "https://www.instagram.com/adityakty77",
    val facebookUrl: String = "https://www.facebook.com/adityakty77",
    val youtubeUrl: String = "https://www.youtube.com/@adityakty77",
    val telegramUrl: String = "https://t.me/adityakty77",
    val githubUrl: String = "https://github.com/adityakty7",
    val websiteUrl: String = "https://ais-pre-cmbpndycglfr6alxk6xzvv-470845985162.asia-east1.run.app",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CenterContactConfig(
    @DocumentId val id: String = BUSINESS_SETTINGS_DOC_ID,
    val tenantId: String = TENANT_ID_ADITYA,
    val centerName: String = "Aditya Online Center",
    val ownerName: String = "Aditya Kumar",
    val whatsappNumber: String = "9876543210",
    val phoneNumber: String = "9876543210",
    val email: String = BOOTSTRAPPED_ADMIN_EMAIL,
    val location: String = "Main Market, Kateya, Gopalganj, Bihar - 841437",
    val workingHours: String = "Mon - Sat: 8:00 AM - 8:00 PM | Sun: 9:00 AM - 2:00 PM",
    val contactInstructions: String = "Send your documents on WhatsApp with your Order ID for fast processing.",
    val instagramUrl: String = "https://www.instagram.com/adityakty77",
    val facebookUrl: String = "https://www.facebook.com/adityakty77",
    val youtubeUrl: String = "https://www.youtube.com/@adityakty77",
    val telegramUrl: String = "https://t.me/adityakty77",
    val githubUrl: String = "https://github.com/adityakty7",
    val websiteUrl: String = "https://ais-pre-cmbpndycglfr6alxk6xzvv-470845985162.asia-east1.run.app",
    val socialLinksSpec: List<String> = defaultSocialLinksSpec(
        whatsappNumber = "9876543210",
        instagramUrl = "https://www.instagram.com/adityakty77",
        facebookUrl = "https://www.facebook.com/adityakty77",
        youtubeUrl = "https://www.youtube.com/@adityakty77",
        telegramUrl = "https://t.me/adityakty77",
        githubUrl = "https://github.com/adityakty7",
        websiteUrl = "https://ais-pre-cmbpndycglfr6alxk6xzvv-470845985162.asia-east1.run.app"
    ),
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun normalizedWhatsAppPhone(): String {
        val digits = whatsappNumber.filter { it.isDigit() }
        return when {
            digits.length == 10 -> "91$digits"
            digits.length > 10 -> digits
            else -> "919876543210"
        }
    }

    fun normalizedDialPhone(): String {
        val digits = phoneNumber.filter { it.isDigit() }
        return if (digits.length == 10) "+91$digits" else "+919876543210"
    }

    fun parsedSocialLinks(): List<SocialMediaProfileLink> {
        val parsed = socialLinksSpec.mapIndexedNotNull { idx, raw ->
            SocialMediaProfileLink.deserialize(raw, idx + 1)
        }.sortedBy { it.sortOrder }
        if (parsed.isNotEmpty()) return parsed
        return defaultSocialLinks(
            whatsappNumber = whatsappNumber,
            instagramUrl = instagramUrl,
            facebookUrl = facebookUrl,
            youtubeUrl = youtubeUrl,
            telegramUrl = telegramUrl,
            githubUrl = githubUrl,
            websiteUrl = websiteUrl
        )
    }

    companion object {
        fun defaultSocialLinks(
            whatsappNumber: String = "9876543210",
            instagramUrl: String = "https://www.instagram.com/adityakty77",
            facebookUrl: String = "https://www.facebook.com/adityakty77",
            youtubeUrl: String = "https://www.youtube.com/@adityakty77",
            telegramUrl: String = "https://t.me/adityakty77",
            githubUrl: String = "https://github.com/adityakty7",
            websiteUrl: String = "https://ais-pre-cmbpndycglfr6alxk6xzvv-470845985162.asia-east1.run.app"
        ): List<SocialMediaProfileLink> = listOf(
            SocialMediaProfileLink("soc_instagram", "Instagram", "Instagram", instagramUrl, "camera", instagramUrl.isNotBlank(), 1),
            SocialMediaProfileLink("soc_facebook", "Facebook", "Facebook", facebookUrl, "people", facebookUrl.isNotBlank(), 2),
            SocialMediaProfileLink("soc_youtube", "YouTube", "YouTube", youtubeUrl, "video", youtubeUrl.isNotBlank(), 3),
            SocialMediaProfileLink("soc_telegram", "Telegram", "Telegram", telegramUrl, "send", telegramUrl.isNotBlank(), 4),
            SocialMediaProfileLink("soc_whatsapp", "WhatsApp", "WhatsApp Channel", "https://wa.me/91${whatsappNumber.filter { it.isDigit() }.takeLast(10)}", "chat", true, 5),
            SocialMediaProfileLink("soc_github", "GitHub", "GitHub", githubUrl, "code", githubUrl.isNotBlank(), 6),
            SocialMediaProfileLink("soc_website", "Website", "Website", websiteUrl, "language", websiteUrl.isNotBlank(), 7)
        )

        fun defaultSocialLinksSpec(
            whatsappNumber: String = "9876543210",
            instagramUrl: String = "https://www.instagram.com/adityakty77",
            facebookUrl: String = "https://www.facebook.com/adityakty77",
            youtubeUrl: String = "https://www.youtube.com/@adityakty77",
            telegramUrl: String = "https://t.me/adityakty77",
            githubUrl: String = "https://github.com/adityakty7",
            websiteUrl: String = "https://ais-pre-cmbpndycglfr6alxk6xzvv-470845985162.asia-east1.run.app"
        ): List<String> = defaultSocialLinks(
            whatsappNumber = whatsappNumber,
            instagramUrl = instagramUrl,
            facebookUrl = facebookUrl,
            youtubeUrl = youtubeUrl,
            telegramUrl = telegramUrl,
            githubUrl = githubUrl,
            websiteUrl = websiteUrl
        ).map { it.serialize() }
    }
}

object WhatsAppOrderHelper {
    fun buildDirectChatUrl(
        contactConfig: CenterContactConfig,
        isHindi: Boolean
    ): String {
        val greeting = if (isHindi) {
            "नमस्ते आदित्य ऑनलाइन सेंटर (कटेया), मुझे ऑनलाइन सेवा के बारे में जानकारी चाहिए।"
        } else {
            "Hello Aditya Online Center (Kateya), I would like to inquire about your digital services."
        }
        val encoded = android.net.Uri.encode(greeting)
        return "https://wa.me/${contactConfig.normalizedWhatsAppPhone()}?text=$encoded"
    }

    fun buildServiceOrderWhatsAppUrl(
        contactConfig: CenterContactConfig,
        service: ServiceItem,
        customerName: String,
        customerPhone: String,
        fatherOrGuardianName: String = "",
        address: String = "",
        serviceDetails: String = "",
        documentLinks: List<String> = emptyList(),
        upiReference: String = "",
        orderNumber: String = "",
        isHindi: Boolean = false
    ): String {
        val message = buildString {
            if (isHindi) {
                appendLine("*नमस्ते आदित्य ऑनलाइन सेंटर (कटेया, बिहार)*")
                appendLine("मैं व्हाट्सएप के माध्यम से नीचे दी गई सेवा बुक करना चाहता/चाहती हूँ:")
                appendLine()
                if (orderNumber.isNotBlank()) {
                    appendLine("*ऑर्डर आईडी:* $orderNumber")
                }
                appendLine("*सेवा का नाम:* ${service.localizedTitle(true)}")
                appendLine("*सेवा शुल्क:* ₹${service.price}")
                appendLine("*समय सीमा:* ${service.turnaroundTime}")
                appendLine()
                appendLine("*--- आवेदक का विवरण ---*")
                appendLine("*नाम:* ${customerName.ifBlank { "N/A" }}")
                appendLine("*मोबाइल नंबर:* ${customerPhone.ifBlank { "N/A" }}")
                if (fatherOrGuardianName.isNotBlank()) {
                    appendLine("*पिता/अभिभावक:* $fatherOrGuardianName")
                }
                if (address.isNotBlank()) {
                    appendLine("*पता:* $address")
                }
                if (serviceDetails.isNotBlank()) {
                    appendLine("*सेवा विवरण:* $serviceDetails")
                }
                if (documentLinks.isNotEmpty()) {
                    appendLine("*संलग्न दस्तावेज़:* ${documentLinks.joinToString(", ")}")
                }
                if (upiReference.isNotBlank()) {
                    appendLine("*UPI UTR संदर्भ:* $upiReference")
                }
                appendLine()
                append("कृपया मेरा ऑर्डर कन्फर्म करें। धन्यवाद!")
            } else {
                appendLine("*Hello Aditya Online Center (Kateya, Bihar)*")
                appendLine("I would like to place an order for the following service via WhatsApp:")
                appendLine()
                if (orderNumber.isNotBlank()) {
                    appendLine("*Order ID:* $orderNumber")
                }
                appendLine("*Service:* ${service.title}")
                appendLine("*Fee:* ₹${service.price}")
                appendLine("*Turnaround:* ${service.turnaroundTime}")
                appendLine()
                appendLine("*--- Applicant Details ---*")
                appendLine("*Name:* ${customerName.ifBlank { "N/A" }}")
                appendLine("*Mobile:* ${customerPhone.ifBlank { "N/A" }}")
                if (fatherOrGuardianName.isNotBlank()) {
                    appendLine("*Guardian Name:* $fatherOrGuardianName")
                }
                if (address.isNotBlank()) {
                    appendLine("*Address:* $address")
                }
                if (serviceDetails.isNotBlank()) {
                    appendLine("*Application Details:* $serviceDetails")
                }
                if (documentLinks.isNotEmpty()) {
                    appendLine("*Documents:* ${documentLinks.joinToString(", ")}")
                }
                if (upiReference.isNotBlank()) {
                    appendLine("*UPI UTR Ref:* $upiReference")
                }
                appendLine()
                append("Please confirm my service order. Thank you!")
            }
        }
        val encoded = android.net.Uri.encode(message)
        return "https://wa.me/${contactConfig.normalizedWhatsAppPhone()}?text=$encoded"
    }

    fun buildConfirmedOrderWhatsAppUrl(
        contactConfig: CenterContactConfig,
        order: ServiceOrder,
        isHindi: Boolean = false
    ): String {
        val message = buildString {
            if (isHindi) {
                appendLine("*आदित्य ऑनलाइन सेंटर - ऑर्डर विवरण*")
                appendLine("*ऑर्डर आईडी:* ${order.orderNumber}")
                appendLine("*सेवा:* ${order.serviceTitleSnapshot}")
                appendLine("*शुल्क:* ₹${order.priceSnapshot}")
                appendLine("*आवेदक:* ${order.customerName} (${order.customerPhone})")
                appendLine("*विवरण:* ${order.applicantDetailsSummary}")
                if (order.upiReference.isNotBlank()) {
                    appendLine("*UPI UTR:* ${order.upiReference}")
                }
                append("कृपया मेरे ऑर्डर की स्थिति अपडेट करें।")
            } else {
                appendLine("*Aditya Online Center - Order Details*")
                appendLine("*Order ID:* ${order.orderNumber}")
                appendLine("*Service:* ${order.serviceTitleSnapshot}")
                appendLine("*Fee:* ₹${order.priceSnapshot}")
                appendLine("*Applicant:* ${order.customerName} (${order.customerPhone})")
                appendLine("*Details:* ${order.applicantDetailsSummary}")
                if (order.upiReference.isNotBlank()) {
                    appendLine("*UPI UTR:* ${order.upiReference}")
                }
                append("Please check my order and share updates.")
            }
        }
        val encoded = android.net.Uri.encode(message)
        return "https://wa.me/${contactConfig.normalizedWhatsAppPhone()}?text=$encoded"
    }
}

enum class ServiceCategory(
    val key: String,
    val labelEn: String,
    val labelHi: String,
    val defaultIconKey: String,
    val defaultSortOrder: Long
) {
    ALL("all", "All Services", "सभी सेवाएं", "apps", 0L),
    RESUME("resume", "Resume & Biodata", "रिज्यूमे और बायोडाटा", "description", 1L),
    GOV_FORM("gov_form", "Gov & Online Forms", "सरकारी और ऑनलाइन फॉर्म", "account_balance", 2L),
    SCHOLARSHIP("scholarship", "Scholarships", "छात्रवृत्ति फॉर्म", "school", 3L),
    PDF_DOC("pdf_doc", "PDF & Typing", "पीडीएफ और टाइपिंग", "picture_as_pdf", 4L),
    CERTIFICATE("certificate", "PAN, ID & RTPS", "पैन, प्रमाण पत्र व आईडी", "badge", 5L);

    fun label(isHindi: Boolean): String = if (isHindi) labelHi else labelEn

    companion object {
        fun fromKey(key: String): ServiceCategory =
            entries.find { it.key == key } ?: RESUME
    }
}

object DefaultCategorySeed {
    val categories: List<ServiceCategoryItem> = ServiceCategory.entries
        .filter { it != ServiceCategory.ALL }
        .map { cat ->
            ServiceCategoryItem(
                id = cat.key,
                tenantId = TENANT_ID_ADITYA,
                key = cat.key,
                labelEn = cat.labelEn,
                labelHi = cat.labelHi,
                iconKey = cat.defaultIconKey,
                sortOrder = cat.defaultSortOrder,
                active = true
            )
        }
}

object DefaultCatalogueSeed {
    val services: List<ServiceItem> = listOf(
        ServiceItem(
            id = "svc_resume_modern",
            tenantId = TENANT_ID_ADITYA,
            title = "Professional ATS Resume & CV Design",
            titleHi = "प्रोफेशनल एटीएस (ATS) रिज्यूमे और सीवी डिज़ाइन",
            category = "resume",
            shortDescription = "Recruiter-ready modern ATS resume with fast PDF delivery.",
            shortDescriptionHi = "नौकरी और फ्रेशर्स के लिए आधुनिक एटीएस रिज्यूमे पीडीएफ।",
            description = "Modern, recruiter-ready resume formatted for private sector jobs, fresher placements, and gulf/overseas applications with high-resolution PDF delivery.",
            descriptionHi = "प्राइवेट नौकरी, फ्रेशर्स और विदेश में आवेदन के लिए आकर्षक एवं आधुनिक रिज्यूमे/सीवी डिज़ाइन, तुरंत हाई-क्वालिटी पीडीएफ डिलीवरी के साथ।",
            price = 80L,
            turnaroundTime = "1 - 2 Hours",
            iconKey = "description",
            requirements = listOf(
                "Mobile Number [Required] - Active 10-digit WhatsApp/SMS number",
                "Class 10 Marksheet [Required] - Qualification details",
                "Graduation Certificate [Optional] - Work experience & skills",
                "Passport-size Photo [Optional] - Clear background"
            ),
            imageUrl = "https://images.unsplash.com/photo-1586281380349-632531db7ed4?auto=format&fit=crop&w=800&q=80",
            active = true,
            isFeatured = true,
            featuredOrder = 1L,
            sortOrder = 1L,
            isArchived = false
        ),
        ServiceItem(
            id = "svc_marriage_biodata",
            tenantId = TENANT_ID_ADITYA,
            title = "Premium Marriage Biodata (Hindi / English)",
            titleHi = "प्रीमियम शादी बायोडाटा डिज़ाइन (हिंदी / अंग्रेजी)",
            category = "resume",
            shortDescription = "Traditional & modern marriage biodata in Hindi/English PDF.",
            shortDescriptionHi = "सुंदर बॉर्डर और पारिवारिक विवरण के साथ शादी बायोडाटा पीडीएफ।",
            description = "Culturally traditional or modern clean marriage biodata in Hindi or English with decorative borders, family details, horoscope section, and instant WhatsApp/PDF output.",
            descriptionHi = "हिंदी या अंग्रेजी में सुंदर बॉर्डर, पारिवारिक विवरण और कुंडली विवरण के साथ आकर्षक विवाह बायोडाटा पीडीएफ।",
            price = 100L,
            turnaroundTime = "2 Hours",
            iconKey = "favorite",
            requirements = listOf(
                "Photograph [Optional] - Full or passport size photo",
                "Mobile Number [Required] - Family contact number",
                "Other / Custom Document [Required] - Personal, Education & Family details"
            ),
            imageUrl = "https://images.unsplash.com/photo-1517842645767-c639042777db?auto=format&fit=crop&w=800&q=80",
            active = true,
            isFeatured = true,
            featuredOrder = 2L,
            sortOrder = 2L,
            isArchived = false
        ),
        ServiceItem(
            id = "svc_bihar_scholarship",
            tenantId = TENANT_ID_ADITYA,
            title = "Bihar Post-Matric & NSP Scholarship Form",
            titleHi = "बिहार पोस्ट-मैट्रिक एवं NSP छात्रवृत्ति ऑनलाइन आवेदन",
            category = "scholarship",
            shortDescription = "Bihar PMS, Kanya Utthan & NSP scholarship registration.",
            shortDescriptionHi = "बिहार पोस्ट-मैट्रिक, कन्या उत्थान और NSP छात्रवृत्ति ऑनलाइन फॉर्म।",
            description = "Complete online registration, document resizing, bonafide certificate formatting, and final lock assistance for Bihar PMS, Mukhyamantri Kanya Utthan, and NSP portals.",
            descriptionHi = "बिहार पोस्ट-मैट्रिक स्कॉलरशिप, मुख्यमंत्री कन्या उत्थान और नेशनल स्कॉलरशिप पोर्टल (NSP) का सही-सही ऑनलाइन फॉर्म भरना व डॉक्यूमेंट अपलोड।",
            price = 120L,
            turnaroundTime = "2 - 4 Hours",
            iconKey = "school",
            requirements = listOf(
                "Aadhaar Card [Required] - Linked with mobile number",
                "Bank Passbook [Required] - Aadhaar-seeded DBT account",
                "Income Certificate [Required] - Current year RTPS",
                "Caste Certificate [Required] - Bihar RTPS",
                "Residence/Domicile Certificate [Required] - Bihar RTPS",
                "Bonafide Certificate [Required] - Signed by college/school"
            ),
            imageUrl = "https://images.unsplash.com/photo-1523240795612-9a054b0db644?auto=format&fit=crop&w=800&q=80",
            active = true,
            isFeatured = true,
            featuredOrder = 3L,
            sortOrder = 3L,
            isArchived = false
        ),
        ServiceItem(
            id = "svc_gov_exam_form",
            tenantId = TENANT_ID_ADITYA,
            title = "Government Job & Competitive Exam Form Filling",
            titleHi = "सरकारी नौकरी एवं प्रतियोगिता परीक्षा ऑनलाइन फॉर्म",
            category = "gov_form",
            shortDescription = "BSSC, BPSC, Bihar Police, SSC, Railway & Agniveer forms.",
            shortDescriptionHi = "BSSC, BPSC, बिहार पुलिस, SSC, रेलवे व अग्निवीर ऑनलाइन आवेदन।",
            description = "Error-free online application for BSSC, BPSC, Bihar Police, SSC, Railway, Army Agniveer, and University admissions including photo/signature resizing as per official specs.",
            descriptionHi = "BSSC, BPSC, बिहार पुलिस, SSC, रेलवे, अग्निवीर और यूनिवर्सिटी एडमिशन फॉर्म का फोटो-सिग्नेचर रिसाइजिंग सहित सटीक ऑनलाइन आवेदन।",
            price = 70L,
            turnaroundTime = "1 - 3 Hours",
            iconKey = "work",
            requirements = listOf(
                "Class 10 Marksheet [Required] - For exact name & DOB",
                "Class 12 Marksheet [Optional] - As per exam eligibility",
                "Aadhaar Card [Required] - Front & Back",
                "Passport-size Photo [Required] - Clear light background",
                "Signature [Required] - Hindi & English signatures"
            ),
            imageUrl = "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?auto=format&fit=crop&w=800&q=80",
            active = true,
            isFeatured = false,
            featuredOrder = 4L,
            sortOrder = 4L,
            isArchived = false
        ),
        ServiceItem(
            id = "svc_rtps_certificates",
            tenantId = TENANT_ID_ADITYA,
            title = "Bihar RTPS Caste, Income & Residence Certificate",
            titleHi = "जाति, आय एवं निवास प्रमाण पत्र (RTPS बिहार)",
            category = "certificate",
            shortDescription = "ServicePlus Bihar Caste, Income, Residence & NCL apply.",
            shortDescriptionHi = "सर्विस प्लस बिहार जाति, आय, निवास एवं NCL प्रमाण पत्र आवेदन।",
            description = "Fast online application on Bihar ServicePlus (RTPS) portal for Caste, Income, Residence, and Non-Creamy Layer (NCL) certificates with acknowledgment slip delivery.",
            descriptionHi = "सर्विस प्लस (RTPS बिहार) पोर्टल के माध्यम से जाति, आय, आवासीय और एनसीएल (NCL) प्रमाण पत्र के लिए ऑनलाइन आवेदन व रिसीविंग।",
            price = 50L,
            turnaroundTime = "1 - 2 Hours",
            iconKey = "verified",
            requirements = listOf(
                "Aadhaar Card [Required] - Applicant Aadhaar card",
                "Passport-size Photo [Required] - Self-attested photo",
                "Mobile Number [Required] - For RTPS SMS receipt"
            ),
            imageUrl = "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&w=800&q=80",
            active = true,
            isFeatured = false,
            featuredOrder = 5L,
            sortOrder = 5L,
            isArchived = false
        ),
        ServiceItem(
            id = "svc_pdf_formatting",
            tenantId = TENANT_ID_ADITYA,
            title = "PDF Merge, Compress, Scan Clean-Up & Hindi/English Typing",
            titleHi = "पीडीएफ मर्ज, साइज कम करना, स्कैन क्लीनअप व हिंदी/अंग्रेजी टाइपिंग",
            category = "pdf_doc",
            shortDescription = "Convert photos to A4 PDF, resize under 100KB & Hindi typing.",
            shortDescriptionHi = "मोबाइल फोटो से साफ A4 पीडीएफ, साइज कम करना और हिंदी टाइपिंग।",
            description = "Convert phone photos into clean A4 PDF documents, compress PDF under 100KB/200KB for portal uploads, merge multiple files, or get custom affidavits/applications typed in Hindi & English.",
            descriptionHi = "मोबाइल फोटो को साफ A4 पीडीएफ में बदलना, फॉर्म अपलोड के लिए पीडीएफ साइज कम करना (100KB/200KB), या हिंदी व अंग्रेजी में आवेदन/शपथ-पत्र टाइपिंग।",
            price = 40L,
            turnaroundTime = "30 - 60 Minutes",
            iconKey = "picture_as_pdf",
            requirements = listOf(
                "Other / Custom Document [Required] - Source document photos or draft text",
                "Mobile Number [Required] - WhatsApp number for PDF delivery"
            ),
            imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=800&q=80",
            active = true,
            isFeatured = false,
            featuredOrder = 6L,
            sortOrder = 6L,
            isArchived = false
        ),
        ServiceItem(
            id = "svc_pan_assistance",
            tenantId = TENANT_ID_ADITYA,
            title = "PAN Card New Application & Correction Assistance",
            titleHi = "नया पैन कार्ड आवेदन एवं सुधार सेवा (e-PAN + फिजिकल)",
            category = "certificate",
            shortDescription = "New PAN card & name/DOB correction with 300 DPI cropping.",
            shortDescriptionHi = "नया पैन कार्ड एवं नाम/जन्मतिथि सुधार (e-PAN व फिजिकल कार्ड)।",
            description = "Assisted PAN card form preparation, photo/signature cropping to 213x213 px @ 300 DPI, and supporting PDF compilation for NSDL/UTIITSL submission.",
            descriptionHi = "नए पैन कार्ड या नाम/जन्मतिथि सुधार के लिए फॉर्म तैयार करना, 300 DPI फोटो/सिग्नेचर सेटिंग और डॉक्यूमेंट पीडीएफ तैयार करना।",
            price = 150L,
            turnaroundTime = "2 - 4 Hours",
            iconKey = "badge",
            requirements = listOf(
                "Aadhaar Card [Required] - With correct Date of Birth",
                "Passport-size Photo [Required] - 2 clear colour photos",
                "Signature [Required] - Black ink signature on white paper"
            ),
            imageUrl = "https://images.unsplash.com/photo-1563013544-824ae1b704d3?auto=format&fit=crop&w=800&q=80",
            active = true,
            isFeatured = false,
            featuredOrder = 7L,
            sortOrder = 7L,
            isArchived = false
        )
    )
}
