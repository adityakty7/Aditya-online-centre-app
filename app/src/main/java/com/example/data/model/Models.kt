package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

const val TENANT_ID_ADITYA = "aditya_online_center"
const val BOOTSTRAPPED_ADMIN_EMAIL = "adityakty77@gmail.com"

@IgnoreExtraProperties
data class ServiceItem(
    @DocumentId val id: String = "",
    val tenantId: String = TENANT_ID_ADITYA,
    val title: String = "",
    val titleHi: String = "",
    val category: String = "resume",
    val description: String = "",
    val descriptionHi: String = "",
    val price: Long = 0L,
    val turnaroundTime: String = "2 - 4 Hours",
    val requirements: List<String> = emptyList(),
    val imageUrl: String = "",
    val active: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun localizedTitle(isHindi: Boolean): String =
        if (isHindi && titleHi.isNotBlank()) titleHi else title

    fun localizedDescription(isHindi: Boolean): String =
        if (isHindi && descriptionHi.isNotBlank()) descriptionHi else description
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
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

enum class ServiceCategory(
    val key: String,
    val labelEn: String,
    val labelHi: String
) {
    ALL("all", "All Services", "सभी सेवाएं"),
    RESUME("resume", "Resume & Biodata", "रिज्यूमे और बायोडाटा"),
    GOV_FORM("gov_form", "Gov & Online Forms", "सरकारी और ऑनलाइन फॉर्म"),
    SCHOLARSHIP("scholarship", "Scholarships", "छात्रवृत्ति फॉर्म"),
    PDF_DOC("pdf_doc", "PDF & Typing", "पीडीएफ और टाइपिंग"),
    CERTIFICATE("certificate", "PAN, ID & RTPS", "पैन, प्रमाण पत्र व आईडी");

    fun label(isHindi: Boolean): String = if (isHindi) labelHi else labelEn

    companion object {
        fun fromKey(key: String): ServiceCategory =
            entries.find { it.key == key } ?: RESUME
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
            description = "Modern, recruiter-ready resume formatted for private sector jobs, fresher placements, and gulf/overseas applications with high-resolution PDF delivery.",
            descriptionHi = "प्राइवेट नौकरी, फ्रेशर्स और विदेश में आवेदन के लिए आकर्षक एवं आधुनिक रिज्यूमे/सीवी डिज़ाइन, तुरंत हाई-क्वालिटी पीडीएफ डिलीवरी के साथ।",
            price = 80L,
            turnaroundTime = "1 - 2 Hours",
            requirements = listOf(
                "Full Name, Mobile Number & Email ID",
                "10th, 12th, Graduation / Diploma Details",
                "Work Experience & Technical Skills",
                "Passport Size Photo (Optional)"
            ),
            imageUrl = "https://images.unsplash.com/photo-1586281380349-632531db7ed4?auto=format&fit=crop&w=800&q=80",
            active = true
        ),
        ServiceItem(
            id = "svc_marriage_biodata",
            tenantId = TENANT_ID_ADITYA,
            title = "Premium Marriage Biodata (Hindi / English)",
            titleHi = "प्रीमियम शादी बायोडाटा डिज़ाइन (हिंदी / अंग्रेजी)",
            category = "resume",
            description = "Culturally traditional or modern clean marriage biodata in Hindi or English with decorative borders, family details, horoscope section, and instant WhatsApp/PDF output.",
            descriptionHi = "हिंदी या अंग्रेजी में सुंदर बॉर्डर, पारिवारिक विवरण और कुंडली विवरण के साथ आकर्षक विवाह बायोडाटा पीडीएफ।",
            price = 100L,
            turnaroundTime = "2 Hours",
            requirements = listOf(
                "Personal Details (Name, DOB, Height, Complexion, Gotra)",
                "Education & Occupation Details",
                "Father, Mother, Brothers & Sisters Details",
                "Contact Address (Village/Post/Police Station - Kateya/Gopalganj)"
            ),
            imageUrl = "https://images.unsplash.com/photo-1517842645767-c639042777db?auto=format&fit=crop&w=800&q=80",
            active = true
        ),
        ServiceItem(
            id = "svc_bihar_scholarship",
            tenantId = TENANT_ID_ADITYA,
            title = "Bihar Post-Matric & NSP Scholarship Form",
            titleHi = "बिहार पोस्ट-मैट्रिक एवं NSP छात्रवृत्ति ऑनलाइन आवेदन",
            category = "scholarship",
            description = "Complete online registration, document resizing, bonafide certificate formatting, and final lock assistance for Bihar PMS, Mukhyamantri Kanya Utthan, and NSP portals.",
            descriptionHi = "बिहार पोस्ट-मैट्रिक स्कॉलरशिप, मुख्यमंत्री कन्या उत्थान और नेशनल स्कॉलरशिप पोर्टल (NSP) का सही-सही ऑनलाइन फॉर्म भरना व डॉक्यूमेंट अपलोड।",
            price = 120L,
            turnaroundTime = "2 - 4 Hours",
            requirements = listOf(
                "Aadhaar Card & Aadhaar-seeded Bank Passbook",
                "Previous Exam Marksheet & Admission Receipt",
                "Valid Caste, Income & Residence Certificate (RTPS)",
                "Bonafide Certificate from School/College"
            ),
            imageUrl = "https://images.unsplash.com/photo-1523240795612-9a054b0db644?auto=format&fit=crop&w=800&q=80",
            active = true
        ),
        ServiceItem(
            id = "svc_gov_exam_form",
            tenantId = TENANT_ID_ADITYA,
            title = "Government Job & Competitive Exam Form Filling",
            titleHi = "सरकारी नौकरी एवं प्रतियोगिता परीक्षा ऑनलाइन फॉर्म",
            category = "gov_form",
            description = "Error-free online application for BSSC, BPSC, Bihar Police, SSC, Railway, Army Agniveer, and University admissions including photo/signature resizing as per official specs.",
            descriptionHi = "BSSC, BPSC, बिहार पुलिस, SSC, रेलवे, अग्निवीर और यूनिवर्सिटी एडमिशन फॉर्म का फोटो-सिग्नेचर रिसाइजिंग सहित सटीक ऑनलाइन आवेदन।",
            price = 70L,
            turnaroundTime = "1 - 3 Hours",
            requirements = listOf(
                "10th / 12th / Graduation Marksheet & Certificate",
                "Aadhaar Card & Active Mobile / Email",
                "Clear Passport Photo & Hindi/English Signatures",
                "Category / EWS / NCL Certificate (if applicable)"
            ),
            imageUrl = "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?auto=format&fit=crop&w=800&q=80",
            active = true
        ),
        ServiceItem(
            id = "svc_rtps_certificates",
            tenantId = TENANT_ID_ADITYA,
            title = "Bihar RTPS Caste, Income & Residence Certificate",
            titleHi = "जाति, आय एवं निवास प्रमाण पत्र (RTPS बिहार)",
            category = "certificate",
            description = "Fast online application on Bihar ServicePlus (RTPS) portal for Caste, Income, Residence, and Non-Creamy Layer (NCL) certificates with acknowledgment slip delivery.",
            descriptionHi = "सर्विस प्लस (RTPS बिहार) पोर्टल के माध्यम से जाति, आय, आवासीय और एनसीएल (NCL) प्रमाण पत्र के लिए ऑनलाइन आवेदन व रिसीविंग।",
            price = 50L,
            turnaroundTime = "1 - 2 Hours",
            requirements = listOf(
                "Applicant Aadhaar Card",
                "Father & Mother Name in Hindi & English",
                "Self-Declaration Form (Shapath Patra)",
                "Passport Size Photograph"
            ),
            imageUrl = "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?auto=format&fit=crop&w=800&q=80",
            active = true
        ),
        ServiceItem(
            id = "svc_pdf_formatting",
            tenantId = TENANT_ID_ADITYA,
            title = "PDF Merge, Compress, Scan Clean-Up & Hindi/English Typing",
            titleHi = "पीडीएफ मर्ज, साइज कम करना, स्कैन क्लीनअप व हिंदी/अंग्रेजी टाइपिंग",
            category = "pdf_doc",
            description = "Convert phone photos into clean A4 PDF documents, compress PDF under 100KB/200KB for portal uploads, merge multiple files, or get custom affidavits/applications typed in Hindi & English.",
            descriptionHi = "मोबाइल फोटो को साफ A4 पीडीएफ में बदलना, फॉर्म अपलोड के लिए पीडीएफ साइज कम करना (100KB/200KB), या हिंदी व अंग्रेजी में आवेदन/शपथ-पत्र टाइपिंग।",
            price = 40L,
            turnaroundTime = "30 - 60 Minutes",
            requirements = listOf(
                "Source Document Photos or PDF Links",
                "Target File Size / Page Format Requirement",
                "Language Preference (KrutiDev / Mangal Hindi or English)"
            ),
            imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=800&q=80",
            active = true
        ),
        ServiceItem(
            id = "svc_pan_assistance",
            tenantId = TENANT_ID_ADITYA,
            title = "PAN Card New Application & Correction Assistance",
            titleHi = "नया पैन कार्ड आवेदन एवं सुधार सेवा (e-PAN + फिजिकल)",
            category = "certificate",
            description = "Assisted PAN card form preparation, photo/signature cropping to 213x213 px @ 300 DPI, and supporting PDF compilation for NSDL/UTIITSL submission.",
            descriptionHi = "नए पैन कार्ड या नाम/जन्मतिथि सुधार के लिए फॉर्म तैयार करना, 300 DPI फोटो/सिग्नेचर सेटिंग और डॉक्यूमेंट पीडीएफ तैयार करना।",
            price = 150L,
            turnaroundTime = "2 - 4 Hours",
            requirements = listOf(
                "Aadhaar Card with Correct DOB",
                "Father's Full Name",
                "2 Passport Photos & Clear Black-Ink Signature"
            ),
            imageUrl = "https://images.unsplash.com/photo-1563013544-824ae1b704d3?auto=format&fit=crop&w=800&q=80",
            active = true
        )
    )
}
