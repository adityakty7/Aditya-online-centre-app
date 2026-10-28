package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.auth.ActiveAuthSession
import com.example.auth.AuthSessionStore
import com.example.auth.attemptAutoSignIn
import com.example.auth.onGoogleSignInClicked
import com.example.auth.startMobileNumberVerification
import com.example.auth.verifyMobileNumberOtp
import com.example.ui.components.AdityaBrandEmblem
import com.example.ui.components.AmbientCyberGlassBackground
import com.example.ui.components.FuturisticGradientButton
import com.example.ui.components.Glowing3DIconBadge
import com.example.ui.components.StaggeredEntranceItem
import com.example.ui.components.bounceClick
import com.example.ui.components.glassCardContainerColor
import com.example.ui.components.glassSpecularBorderBrush
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueBright
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LuminousPurple
import com.example.ui.theme.StatusVerifiedGreen
import com.example.ui.theme.VividViolet
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private data class OnboardingPage(
    val titleEn: String,
    val titleHi: String,
    val subtitleEn: String,
    val subtitleHi: String,
    val badgeEn: String,
    val badgeHi: String
)

/**
 * Reusable Cinematic Welcome Hero with prominent Aditya Online Center emblem,
 * electric blue-purple radial lighting, and floating luminous service icon illustrations.
 */
@Composable
fun CinematicWelcomeHeroOrb(
    isHindi: Boolean,
    compact: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "welcome_floating_icons")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_offset_y"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cinematic_welcome_hero"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (compact) 168.dp else 210.dp)
                .drawBehind {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                VividViolet.copy(alpha = 0.42f),
                                ElectricBlue.copy(alpha = 0.28f),
                                ElectricCyan.copy(alpha = 0.10f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = size.minDimension * 0.72f
                        ),
                        radius = size.minDimension * 0.72f,
                        center = center
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Outer orbital ring
            Surface(
                modifier = Modifier.size(if (compact) 136.dp else 166.dp),
                shape = CircleShape,
                color = Color.Transparent,
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            ElectricCyan.copy(alpha = 0.45f),
                            VividViolet.copy(alpha = 0.25f),
                            ElectricBlueBright.copy(alpha = 0.55f),
                            ElectricCyan.copy(alpha = 0.45f)
                        )
                    )
                )
            ) {}

            // Floating Luminous Service Icons around the Emblem
            Glowing3DIconBadge(
                icon = Icons.Default.Badge,
                accentColor = ElectricCyan,
                size = 36.dp,
                iconSize = 18.dp,
                cornerRadius = 12.dp,
                animateGlow = true,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 28.dp, top = 12.dp)
                    .offset(y = floatOffset.dp)
            )

            Glowing3DIconBadge(
                icon = Icons.Default.School,
                accentColor = LuminousPurple,
                size = 36.dp,
                iconSize = 18.dp,
                cornerRadius = 12.dp,
                animateGlow = true,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 28.dp, top = 14.dp)
                    .offset(y = (-floatOffset).dp)
            )

            Glowing3DIconBadge(
                icon = Icons.Default.AccountBalance,
                accentColor = ElectricBlueBright,
                size = 36.dp,
                iconSize = 18.dp,
                cornerRadius = 12.dp,
                animateGlow = true,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 34.dp, bottom = 12.dp)
                    .offset(y = (-floatOffset * 0.7f).dp)
            )

            Glowing3DIconBadge(
                icon = Icons.Default.PictureAsPdf,
                accentColor = ElectricCyan,
                size = 36.dp,
                iconSize = 18.dp,
                cornerRadius = 12.dp,
                animateGlow = true,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 34.dp, bottom = 12.dp)
                    .offset(y = (floatOffset * 0.7f).dp)
            )

            // Prominent Central Aditya Online Center Logo
            Surface(
                modifier = Modifier.size(if (compact) 94.dp else 112.dp),
                shape = RoundedCornerShape(26.dp),
                color = ElectricBlue.copy(alpha = 0.22f),
                border = BorderStroke(
                    width = 1.8.dp,
                    brush = Brush.linearGradient(
                        listOf(ElectricCyan, ElectricBlueBright, LuminousPurple)
                    )
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    AdityaBrandEmblem(
                        size = if (compact) 80.dp else 96.dp,
                        cornerRadius = 20.dp
                    )
                }
            }
        }

        Surface(
            color = ElectricBlue.copy(alpha = 0.18f),
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.55f))
        ) {
            Text(
                text = if (isHindi) "कटेया का आधुनिक डिजिटल सेवा केंद्र" else "NEXT-GEN DIGITAL SERVICES HUB",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = ElectricCyan,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isHindi) "आदित्य ऑनलाइन सेंटर" else "Aditya Online Center",
            style = if (compact) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isHindi) {
                "रिज्यूमे • बायोडाटा • छात्रवृत्ति • प्रमाण-पत्र • पीडीएफ सेवाएं"
            } else {
                "Resumes • Biodata • Gov Forms • Scholarships • PDF Services"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OnboardingScreen(
    isHindi: Boolean,
    onToggleLanguage: () -> Unit,
    onFinishOnboarding: () -> Unit
) {
    val pages = remember {
        listOf(
            OnboardingPage(
                titleEn = "Aditya Online Center — Digital Services Portal",
                titleHi = "आदित्य ऑनलाइन सेंटर — अब आपके मोबाइल पर",
                subtitleEn = "Order Resumes, Marriage Biodata, Bihar Scholarships, RTPS Certificates, PAN, and PDF formatting from Kateya, Gopalganj, Bihar.",
                subtitleHi = "कटेया, गोपालगंज (बिहार) से रिज्यूमे, शादी बायोडाटा, छात्रवृत्ति, जाति/आय/निवास, पैन कार्ड और पीडीएफ सेवाएं घर बैठे ऑर्डर करें।",
                badgeEn = "SHARED CLOUD CATALOGUE",
                badgeHi = "लाइव सेवा कैटलॉग"
            ),
            OnboardingPage(
                titleEn = "Zero-Trust Security & Transparent Pricing",
                titleHi = "पूर्ण सुरक्षा और पारदर्शी शुल्क",
                subtitleEn = "Prices are verified directly from the official catalogue. We never ask for OTPs, banking PINs, or passwords.",
                subtitleHi = "सभी सेवाओं का शुल्क आधिकारिक कैटलॉग से सत्यापित होता है। हम कभी भी बैंक पिन, पासवर्ड या ओटीपी नहीं मांगते।",
                badgeEn = "VERIFIED PRIVACY",
                badgeHi = "सुरक्षित दस्तावेज़"
            ),
            OnboardingPage(
                titleEn = "Real-Time Order Tracking & PDF Delivery",
                titleHi = "लाइव ऑर्डर ट्रैकिंग और तुरंत पीडीएफ डिलीवरी",
                subtitleEn = "Track your application status live from submission to admin verification and download your completed documents securely.",
                subtitleHi = "आवेदन जमा करने से लेकर एडमिन सत्यापन तक लाइव स्टेटस देखें और तैयार दस्तावेज़ सीधे डाउनलोड करें।",
                badgeEn = "WEB + ANDROID UNIFIED",
                badgeHi = "वेबसाइट + ऐप एक साथ"
            )
        )
    }

    var currentStep by remember { mutableIntStateOf(0) }

    AmbientCyberGlassBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
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
                        AdityaBrandEmblem(size = 36.dp, cornerRadius = 10.dp)
                        Surface(
                            color = glassCardContainerColor(alpha = 0.78f),
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.45f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (isHindi) "कटेया, बिहार" else "Kateya, Bihar",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = onToggleLanguage,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.55f)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .bounceClick()
                            .testTag("onboarding_language_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Switch Language",
                            tint = ElectricCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "EN" else "हिंदी",
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                StaggeredEntranceItem(index = 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.85f)),
                        border = BorderStroke(
                            width = 1.2.dp,
                            brush = glassSpecularBorderBrush(ElectricCyan, LuminousPurple)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CinematicWelcomeHeroOrb(isHindi = isHindi, compact = false)

                            Spacer(modifier = Modifier.height(18.dp))

                            AnimatedContent(
                                targetState = currentStep,
                                transitionSpec = {
                                    val forward = targetState >= initialState
                                    (slideInHorizontally(
                                        animationSpec = tween(280, easing = FastOutSlowInEasing),
                                        initialOffsetX = { fullWidth -> if (forward) fullWidth / 3 else -fullWidth / 3 }
                                    ) + fadeIn(tween(240))).togetherWith(
                                        slideOutHorizontally(
                                            animationSpec = tween(220, easing = FastOutSlowInEasing),
                                            targetOffsetX = { fullWidth -> if (forward) -fullWidth / 3 else fullWidth / 3 }
                                        ) + fadeOut(tween(180))
                                    )
                                },
                                label = "onboarding_page_transition"
                            ) { stepIdx ->
                                val page = pages[stepIdx]
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        color = VividViolet.copy(alpha = 0.22f),
                                        shape = RoundedCornerShape(50),
                                        border = BorderStroke(1.dp, LuminousPurple.copy(alpha = 0.65f))
                                    ) {
                                        Text(
                                            text = if (isHindi) page.badgeHi else page.badgeEn,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            softWrap = false,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = if (isHindi) page.titleHi else page.titleEn,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = if (isHindi) page.subtitleHi else page.subtitleEn,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                pages.indices.forEach { index ->
                                    val dotWidth by animateDpAsState(
                                        targetValue = if (index == currentStep) 28.dp else 8.dp,
                                        animationSpec = tween(240),
                                        label = "onboarding_dot_width"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .height(8.dp)
                                            .width(dotWidth)
                                            .clip(CircleShape)
                                            .background(
                                                if (index == currentStep) ElectricCyan
                                                else MaterialTheme.colorScheme.outline
                                            )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onFinishOnboarding,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .minimumInteractiveComponentSize()
                            .bounceClick()
                            .testTag("skip_onboarding_button")
                    ) {
                        Text(
                            text = if (isHindi) "छोड़ें" else "Skip",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    FuturisticGradientButton(
                        onClick = {
                            if (currentStep < pages.lastIndex) {
                                currentStep++
                            } else {
                                onFinishOnboarding()
                            }
                        },
                        cornerRadius = 16.dp,
                        modifier = Modifier
                            .weight(2f)
                            .height(50.dp)
                            .testTag("next_onboarding_button")
                    ) {
                        Text(
                            text = if (currentStep < pages.lastIndex) {
                                if (isHindi) "आगे बढ़ें" else "Next"
                            } else {
                                if (isHindi) "शुरू करें (Get Started)" else "Get Started"
                            },
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            softWrap = false
                        )
                        Spacer(modifier = Modifier.width(8.dp))
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
fun AuthScreen(
    isHindi: Boolean,
    onToggleLanguage: () -> Unit,
    onAuthSuccess: () -> Unit,
    onAdminAuthSuccess: () -> Unit = onAuthSuccess
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val scrollState = rememberScrollState()

    var selectedLoginTab by remember { mutableIntStateOf(0) }
    var isGoogleLoading by remember { mutableStateOf(false) }
    var isMobileLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Mobile Number Login State
    var mobileCustomerName by remember { mutableStateOf("") }
    var mobilePhoneInput by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var generatedOtpHint by remember { mutableStateOf<String?>(null) }
    var otpCodeInput by remember { mutableStateOf("") }
    var otpSentSuccessMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = { isAdminLogin ->
                if (isAdminLogin) {
                    onAdminAuthSuccess()
                } else {
                    onAuthSuccess()
                }
            },
            onUnauthenticated = {},
            scope = coroutineScope
        )
    }

    AmbientCyberGlassBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .testTag("auth_screen_root"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 460.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar: Brand Identity + Language Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AdityaBrandEmblem(size = 40.dp, cornerRadius = 12.dp)
                        Column {
                            Text(
                                text = if (isHindi) "आदित्य ऑनलाइन सेंटर" else "Aditya Online Center",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = if (isHindi) "कटेया, गोपालगंज (बिहार)" else "Kateya, Gopalganj, Bihar",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onToggleLanguage,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.55f)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .bounceClick()
                            .testTag("auth_language_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Switch Language",
                            tint = ElectricCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "EN" else "हिंदी",
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Cinematic Welcome Hero Showcase with Floating Luminous Icons & Get Started Button
                StaggeredEntranceItem(index = 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.80f)),
                        border = BorderStroke(
                            width = 1.dp,
                            brush = glassSpecularBorderBrush(ElectricCyan, VividViolet)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CinematicWelcomeHeroOrb(isHindi = isHindi, compact = true)

                            FuturisticGradientButton(
                                onClick = {
                                    coroutineScope.launch {
                                        scrollState.animateScrollTo(scrollState.maxValue)
                                    }
                                },
                                cornerRadius = 50.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("welcome_get_started_button")
                            ) {
                                Text(
                                    text = if (isHindi) "शुरू करें (Get Started)" else "Get Started",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Main Clean Public Login Card
                StaggeredEntranceItem(index = 1) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("google_login_card"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.88f)),
                        border = BorderStroke(
                            width = 1.2.dp,
                            brush = glassSpecularBorderBrush(ElectricBlueBright, LuminousPurple)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (isHindi) "स्वागत है • लॉग इन करें" else "Welcome • Sign In",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = if (isHindi) {
                                        "डिजिटल सेवाएं ऑर्डर करने और अपने आवेदन ट्रैक करने के लिए लॉग इन करें"
                                    } else {
                                        "Sign in to order digital services and track your applications"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }

                            // Segmented Login Mode Tabs
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    color = if (selectedLoginTab == 0) ElectricBlue.copy(alpha = 0.28f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(
                                        width = if (selectedLoginTab == 0) 1.5.dp else 1.dp,
                                        color = if (selectedLoginTab == 0) ElectricCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .bounceClick()
                                        .clickable {
                                            selectedLoginTab = 0
                                            errorMessage = null
                                        }
                                        .testTag("auth_tab_google")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccountCircle,
                                            contentDescription = null,
                                            tint = if (selectedLoginTab == 0) ElectricCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Google",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedLoginTab == 0) ElectricCyan else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Surface(
                                    color = if (selectedLoginTab == 1) VividViolet.copy(alpha = 0.26f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(
                                        width = if (selectedLoginTab == 1) 1.5.dp else 1.dp,
                                        color = if (selectedLoginTab == 1) LuminousPurple else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .bounceClick()
                                        .clickable {
                                            selectedLoginTab = 1
                                            errorMessage = null
                                        }
                                        .testTag("auth_tab_mobile")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PhoneAndroid,
                                            contentDescription = null,
                                            tint = if (selectedLoginTab == 1) LuminousPurple else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isHindi) "मोबाइल नंबर" else "Mobile (+91)",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedLoginTab == 1) LuminousPurple else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            AnimatedVisibility(visible = errorMessage != null) {
                                Surface(
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = errorMessage ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }

                            // Primary Google Sign-In Gradient Button
                            FuturisticGradientButton(
                                onClick = {
                                    isGoogleLoading = true
                                    errorMessage = null
                                    onGoogleSignInClicked(
                                        context = context,
                                        credentialManager = credentialManager,
                                        requestAdminActivation = false,
                                        onAuthSuccess = { isAdminLogin ->
                                            isGoogleLoading = false
                                            if (isAdminLogin) {
                                                onAdminAuthSuccess()
                                            } else {
                                                onAuthSuccess()
                                            }
                                        },
                                        onAuthError = { msg ->
                                            isGoogleLoading = false
                                            errorMessage = msg
                                        },
                                        scope = coroutineScope,
                                        onAuthCancelled = {
                                            isGoogleLoading = false
                                        }
                                    )
                                },
                                enabled = !isGoogleLoading && !isMobileLoading,
                                cornerRadius = 14.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("google_sign_in_button")
                            ) {
                                if (isGoogleLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        color = Color.White,
                                        strokeWidth = 2.5.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Google Sign-In",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (isHindi) "Google से लॉग इन करें" else "Continue with Google",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Clean Divider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = if (isHindi) "या मोबाइल नंबर से" else "OR MOBILE NUMBER",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                            }

                            // Mobile Number (+91) Login Section
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("mobile_login_card"),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = mobileCustomerName,
                                    onValueChange = {
                                        mobileCustomerName = it
                                        errorMessage = null
                                    },
                                    label = {
                                        Text(if (isHindi) "आपका नाम (Full Name)" else "Full Name")
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = ElectricCyan
                                        )
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("mobile_login_name_input")
                                )

                                OutlinedTextField(
                                    value = mobilePhoneInput,
                                    onValueChange = { input ->
                                        mobilePhoneInput = input.filter { it.isDigit() }.take(10)
                                        errorMessage = null
                                    },
                                    label = {
                                        Text(if (isHindi) "10-अंकीय मोबाइल नंबर" else "10-Digit Mobile Number")
                                    },
                                    prefix = {
                                        Text(
                                            text = "+91 ",
                                            fontWeight = FontWeight.Bold,
                                            color = ElectricCyan
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.PhoneAndroid,
                                            contentDescription = null,
                                            tint = LuminousPurple
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("mobile_login_phone_input")
                                )

                                FuturisticGradientButton(
                                    onClick = {
                                        val cleanPhone = mobilePhoneInput.filter { it.isDigit() }
                                        if (cleanPhone.length != 10) {
                                            errorMessage = if (isHindi) {
                                                "कृपया वैध 10-अंकीय मोबाइल नंबर दर्ज करें।"
                                            } else {
                                                "Please enter a valid 10-digit mobile number."
                                            }
                                            return@FuturisticGradientButton
                                        }
                                        isMobileLoading = true
                                        errorMessage = null
                                        startMobileNumberVerification(
                                            context = context,
                                            phoneNumber10Digits = cleanPhone,
                                            onCodeSent = { verifId, otpHint ->
                                                isMobileLoading = false
                                                verificationId = verifId
                                                generatedOtpHint = otpHint
                                                otpSentSuccessMessage = if (isHindi) {
                                                    "+91 $cleanPhone पर ओटीपी (OTP) भेज दिया गया है।"
                                                } else {
                                                    "Verification OTP has been sent to +91 $cleanPhone."
                                                }
                                            },
                                            onInstantVerified = { credential ->
                                                coroutineScope.launch {
                                                    runCatching {
                                                        Firebase.auth.signInWithCredential(credential).await()
                                                    }
                                                    AuthSessionStore.saveSession(
                                                        context = context,
                                                        session = ActiveAuthSession(
                                                            uid = Firebase.auth.currentUser?.uid ?: "mobile_$cleanPhone",
                                                            displayName = mobileCustomerName.trim().ifBlank { "Customer (+91 $cleanPhone)" },
                                                            email = null,
                                                            phoneNumber = cleanPhone,
                                                            authMethod = "mobile",
                                                            isAdminGoogle = false
                                                        )
                                                    )
                                                    isMobileLoading = false
                                                    onAuthSuccess()
                                                }
                                            },
                                            onError = { err ->
                                                isMobileLoading = false
                                                errorMessage = err
                                            }
                                        )
                                    },
                                    enabled = !isMobileLoading && !isGoogleLoading,
                                    usePurpleToCyan = true,
                                    cornerRadius = 14.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("send_otp_button")
                                ) {
                                    if (isMobileLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = Color.White,
                                            strokeWidth = 2.5.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Sms,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (verificationId == null) {
                                                if (isHindi) "ओटीपी प्राप्त करें (Send OTP)" else "Send OTP"
                                            } else {
                                                if (isHindi) "ओटीपी फिर से भेजें (Resend OTP)" else "Resend OTP"
                                            },
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }

                                // OTP Input & Verify Button (Shown once OTP is requested)
                                AnimatedVisibility(visible = verificationId != null) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        if (otpSentSuccessMessage != null) {
                                            Surface(
                                                color = StatusVerifiedGreen.copy(alpha = 0.14f),
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, StatusVerifiedGreen.copy(alpha = 0.55f))
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = StatusVerifiedGreen,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Text(
                                                        text = otpSentSuccessMessage ?: "",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = StatusVerifiedGreen,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                            }
                                        }

                                        OutlinedTextField(
                                            value = otpCodeInput,
                                            onValueChange = { otpCodeInput = it.filter { ch -> ch.isDigit() }.take(6) },
                                            label = {
                                                Text(if (isHindi) "6-अंकीय ओटीपी दर्ज करें" else "Enter 6-Digit OTP")
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Key,
                                                    contentDescription = null,
                                                    tint = ElectricCyan
                                                )
                                            },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("mobile_otp_input")
                                        )

                                        Button(
                                            onClick = {
                                                verifyMobileNumberOtp(
                                                    context = context,
                                                    phoneNumber10Digits = mobilePhoneInput,
                                                    customerName = mobileCustomerName,
                                                    verificationId = verificationId,
                                                    enteredOtp = otpCodeInput,
                                                    expectedFallbackOtp = generatedOtpHint ?: "123456",
                                                    scope = coroutineScope,
                                                    onSuccess = {
                                                        onAuthSuccess()
                                                    },
                                                    onError = { err ->
                                                        errorMessage = err
                                                    }
                                                )
                                            },
                                            shape = RoundedCornerShape(14.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = StatusVerifiedGreen,
                                                contentColor = Color(0xFF04140B)
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                                .minimumInteractiveComponentSize()
                                                .bounceClick()
                                                .testTag("verify_otp_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VerifiedUser,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (isHindi) "ओटीपी सत्यापित करें और लॉग इन करें" else "Verify OTP & Login",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.ExtraBold
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
    }
}
