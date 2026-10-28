package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CenterContactConfig
import com.example.data.model.ServiceItem
import com.example.data.model.WhatsAppOrderHelper
import com.example.ui.theme.BlueToPurpleGradientBrush
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueBright
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LuminousPurple
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.PurpleToCyanGradientBrush
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.StatusVerifiedGreen
import com.example.ui.theme.VividViolet
import kotlinx.coroutines.delay

val WhatsAppBrandGreen = Color(0xFF25D366)

fun launchExternalUriSafely(context: Context, uriString: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}

/**
 * Deep Blue-Black glassmorphic card surface with restrained translucency over the midnight navy backdrop.
 */
@Composable
fun glassCardContainerColor(alpha: Float = 0.82f): Color =
    MaterialTheme.colorScheme.surface.copy(alpha = alpha)

fun glassSurfaceOverlayBrush(
    topHighlightAlpha: Float = 0.09f,
    accentTint: Color = ElectricBlueBright,
    accentAlpha: Float = 0.10f
): Brush = Brush.linearGradient(
    colors = listOf(
        Color.White.copy(alpha = topHighlightAlpha),
        ElectricBlue.copy(alpha = 0.04f),
        accentTint.copy(alpha = accentAlpha)
    )
)

fun glassSpecularBorderBrush(
    primaryAccent: Color = ElectricBlueBright,
    secondaryAccent: Color = VividViolet
): Brush = Brush.linearGradient(
    colors = listOf(
        ElectricCyan.copy(alpha = 0.55f),
        primaryAccent.copy(alpha = 0.62f),
        secondaryAccent.copy(alpha = 0.58f)
    )
)

/**
 * Reusable Blue-to-Purple or Purple-to-Cyan Gradient Pill Button for primary futuristic CTAs.
 */
@Composable
fun FuturisticGradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    usePurpleToCyan: Boolean = false,
    cornerRadius: Dp = 16.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    content: @Composable RowScope.() -> Unit
) {
    val brush = if (!enabled) {
        Brush.horizontalGradient(
            listOf(
                Color(0xFF1E293B),
                Color(0xFF334155)
            )
        )
    } else if (usePurpleToCyan) {
        PurpleToCyanGradientBrush
    } else {
        BlueToPurpleGradientBrush
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .bounceClick()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush, shape = RoundedCornerShape(cornerRadius))
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            ElectricCyan.copy(alpha = if (enabled) 0.65f else 0.2f),
                            LuminousPurple.copy(alpha = if (enabled) 0.55f else 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                content = content
            )
        }
    }
}

/**
 * Full-screen Midnight Navy background with soft Electric Blue, Vivid Violet, and Luminous Cyan
 * gradient lighting pools and a subtle architectural grid.
 */
@Composable
fun AmbientCyberGlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_aurora_bg")
    val driftProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aurora_drift"
    )
    val secondaryDrift by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aurora_secondary_drift"
    )

    val baseBg = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseBg)
            .drawBehind {
                val w = size.width
                val h = size.height

                // Top-left Electric Blue & Luminous Cyan radial glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ElectricBlue.copy(alpha = 0.26f),
                            ElectricCyan.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = Offset(
                            x = w * (0.16f + 0.14f * driftProgress),
                            y = h * (0.10f + 0.06f * secondaryDrift)
                        ),
                        radius = w.coerceAtLeast(h) * 0.55f
                    ),
                    radius = w.coerceAtLeast(h) * 0.55f,
                    center = Offset(
                        x = w * (0.16f + 0.14f * driftProgress),
                        y = h * (0.10f + 0.06f * secondaryDrift)
                    )
                )

                // Mid-right Vivid Violet & Purple ambient lighting
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            VividViolet.copy(alpha = 0.24f),
                            LuminousPurple.copy(alpha = 0.11f),
                            Color.Transparent
                        ),
                        center = Offset(
                            x = w * (0.86f - 0.14f * driftProgress),
                            y = h * (0.40f + 0.10f * driftProgress)
                        ),
                        radius = w.coerceAtLeast(h) * 0.52f
                    ),
                    radius = w.coerceAtLeast(h) * 0.52f,
                    center = Offset(
                        x = w * (0.86f - 0.14f * driftProgress),
                        y = h * (0.40f + 0.10f * driftProgress)
                    )
                )

                // Bottom-left Cyan & Electric Blue horizon pool
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ElectricCyan.copy(alpha = 0.18f),
                            ElectricBlueBright.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        center = Offset(
                            x = w * (0.24f + 0.16f * secondaryDrift),
                            y = h * (0.84f - 0.08f * driftProgress)
                        ),
                        radius = w.coerceAtLeast(h) * 0.48f
                    ),
                    radius = w.coerceAtLeast(h) * 0.48f,
                    center = Offset(
                        x = w * (0.24f + 0.16f * secondaryDrift),
                        y = h * (0.84f - 0.08f * driftProgress)
                    )
                )

                // Subtle futuristic matrix grid lines
                val step = 68.dp.toPx()
                val gridColor = ElectricBlueBright.copy(alpha = 0.03f)
                var x = 0f
                while (x < w) {
                    drawLine(
                        color = gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 1f
                    )
                    x += step
                }
                var y = 0f
                while (y < h) {
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1f
                    )
                    y += step
                }
            }
    ) {
        content()
    }
}

/**
 * Luminous futuristic service icon badge with subtle neon edge border and deep navy glass backing.
 */
@Composable
fun Glowing3DIconBadge(
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    iconSize: Dp = 18.dp,
    cornerRadius: Dp = 10.dp,
    contentDescription: String? = null,
    animateGlow: Boolean = false
) {
    val effectiveRadius = if (cornerRadius > 0.dp) cornerRadius else (size.value * 0.30f).dp
    val effectiveBoxSize = size.coerceIn(22.dp, 54.dp)
    val effectiveIconSize = iconSize.coerceIn(13.dp, (effectiveBoxSize.value * 0.62f).dp)

    Surface(
        modifier = modifier.size(effectiveBoxSize),
        shape = RoundedCornerShape(effectiveRadius),
        color = accentColor.copy(alpha = if (animateGlow) 0.22f else 0.14f),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    ElectricCyan.copy(alpha = if (animateGlow) 0.70f else 0.40f),
                    accentColor.copy(alpha = if (animateGlow) 0.85f else 0.55f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = accentColor,
                modifier = Modifier.size(effectiveIconSize)
            )
        }
    }
}

/**
 * Adds a tactile spring-bounce scale animation when the user presses down on any interactive
 * card, chip, or button without intercepting its internal click handler.
 */
fun Modifier.bounceClick(scaleDown: Float = 0.95f): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "bounce_click_scale"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            while (true) {
                awaitPointerEventScope {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    waitForUpOrCancellation()
                    isPressed = false
                }
            }
        }
}

/**
 * Animates a list/grid item into view with a smooth staggered upward slide + fade.
 */
@Composable
fun StaggeredEntranceItem(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val staggerDelay = (index.coerceAtMost(8) * 35L)
        if (staggerDelay > 0) delay(staggerDelay)
        visible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "stagger_alpha"
    )
    val translationY by animateFloatAsState(
        targetValue = if (visible) 0f else 24f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "stagger_translation_y"
    )

    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha
            this.translationY = translationY
        }
    ) {
        content()
    }
}

/**
 * Luminous status indicator dot for live Firestore sync and active order stages.
 */
@Composable
fun PulsingDotIndicator(
    color: Color,
    dotSize: Dp = 8.dp
) {
    Box(
        modifier = Modifier
            .size(dotSize)
            .clip(CircleShape)
            .background(color)
    )
}

/**
 * Official Aditya Online Center Brand Logo Emblem using the uploaded custom artwork
 * (`R.drawable.aditya_brand_logo`) framed in a deep midnight-navy glass badge with a subtle neon edge.
 */
@Composable
fun AdityaBrandEmblem(
    size: Dp = 40.dp,
    cornerRadius: Dp = 12.dp
) {
    Surface(
        modifier = Modifier.size(size),
        shape = RoundedCornerShape(cornerRadius),
        color = Color(0xFF070D1F),
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    ElectricCyan.copy(alpha = 0.75f),
                    ElectricBlueBright.copy(alpha = 0.65f),
                    LuminousPurple.copy(alpha = 0.75f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ElectricBlue.copy(alpha = 0.28f),
                            Color(0xFF070D1F)
                        )
                    )
                )
                .padding((size.value * 0.06f).dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.aditya_brand_logo),
                contentDescription = "Aditya Online Center Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Glassmorphic "Connect With Me" & Social Media Profile Links card.
 * Displayed at the bottom of the Home screen, inside the Support/Profile screen, and inside the Quick Hub dialog.
 */
@Composable
fun ConnectWithMeCard(
    contactConfig: CenterContactConfig,
    isHindi: Boolean,
    modifier: Modifier = Modifier,
    showSocialLinks: Boolean = true,
    onEditContactsClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("connect_with_me_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = glassCardContainerColor(alpha = 0.86f)),
        border = BorderStroke(
            width = 1.2.dp,
            brush = glassSpecularBorderBrush(
                primaryAccent = ElectricCyan,
                secondaryAccent = VividViolet
            )
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    glassSurfaceOverlayBrush(
                        topHighlightAlpha = 0.08f,
                        accentTint = ElectricBlueBright,
                        accentAlpha = 0.10f
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AdityaBrandEmblem(
                        size = 42.dp,
                        cornerRadius = 12.dp
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PulsingDotIndicator(color = StatusVerifiedGreen, dotSize = 7.dp)
                            Text(
                                text = if (isHindi) "मुझसे जुड़ें (Connect With Me)" else "Connect With Me • ${contactConfig.centerName}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "WhatsApp: +91 ${contactConfig.whatsappNumber} • ${contactConfig.email}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (contactConfig.workingHours.isNotBlank() || contactConfig.location.isNotBlank()) {
                            Text(
                                text = "${contactConfig.location} • ${contactConfig.workingHours}",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                if (onEditContactsClick != null) {
                    TextButton(
                        onClick = onEditContactsClick,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .bounceClick()
                            .testTag("edit_connect_details_button")
                    ) {
                        Text(
                            text = if (isHindi) "बदलें" else "Edit",
                            color = ElectricCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (contactConfig.contactInstructions.isNotBlank()) {
                Text(
                    text = contactConfig.contactInstructions,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Direct Contact Buttons: WhatsApp, Call, Email
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .height(48.dp)
                        .minimumInteractiveComponentSize()
                        .bounceClick()
                        .clickable {
                            val url = WhatsAppOrderHelper.buildDirectChatUrl(contactConfig, isHindi)
                            launchExternalUriSafely(context, url)
                        }
                        .testTag("connect_whatsapp_button"),
                    shape = RoundedCornerShape(14.dp),
                    color = WhatsAppBrandGreen.copy(alpha = 0.18f),
                    border = BorderStroke(1.2.dp, WhatsAppBrandGreen.copy(alpha = 0.75f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Glowing3DIconBadge(
                            icon = Icons.AutoMirrored.Filled.Chat,
                            accentColor = WhatsAppBrandGreen,
                            size = 28.dp,
                            iconSize = 15.dp,
                            cornerRadius = 9.dp
                        )
                        Text(
                            text = if (isHindi) "व्हाट्सएप चैट" else "WhatsApp",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .height(48.dp)
                        .minimumInteractiveComponentSize()
                        .bounceClick()
                        .clickable {
                            launchExternalUriSafely(context, "tel:${contactConfig.normalizedDialPhone()}")
                        }
                        .testTag("connect_call_button"),
                    shape = RoundedCornerShape(14.dp),
                    color = ElectricCyan.copy(alpha = 0.16f),
                    border = BorderStroke(1.2.dp, ElectricCyan.copy(alpha = 0.75f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Glowing3DIconBadge(
                            icon = Icons.Default.Call,
                            accentColor = ElectricCyan,
                            size = 28.dp,
                            iconSize = 15.dp,
                            cornerRadius = 9.dp
                        )
                        Text(
                            text = if (isHindi) "कॉल करें" else "Call Now",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .height(48.dp)
                        .minimumInteractiveComponentSize()
                        .bounceClick()
                        .clickable {
                            launchExternalUriSafely(context, "mailto:${contactConfig.email}")
                        }
                        .testTag("connect_email_button"),
                    shape = RoundedCornerShape(14.dp),
                    color = LuminousPurple.copy(alpha = 0.18f),
                    border = BorderStroke(1.2.dp, LuminousPurple.copy(alpha = 0.75f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Glowing3DIconBadge(
                            icon = Icons.Default.Email,
                            accentColor = LuminousPurple,
                            size = 28.dp,
                            iconSize = 15.dp,
                            cornerRadius = 9.dp
                        )
                        Text(
                            text = if (isHindi) "ईमेल भेजें" else "Email Me",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            if (showSocialLinks) {
                HorizontalDivider(color = ElectricBlueBright.copy(alpha = 0.18f))

                Text(
                    text = if (isHindi) "सोशल मीडिया और आधिकारिक प्रोफ़ाइल लिंक:" else "Social Media & Official Profiles:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                SocialMediaBadgesRow(
                    contactConfig = contactConfig,
                    isHindi = isHindi
                )
            }
        }
    }
}

@Composable
fun SocialMediaBadgesRow(
    contactConfig: CenterContactConfig,
    isHindi: Boolean
) {
    val context = LocalContext.current
    val enabledSocialLinks = remember(contactConfig) {
        contactConfig.parsedSocialLinks().filter { it.enabled }.sortedBy { it.sortOrder }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        enabledSocialLinks.forEach { link ->
            val keyLower = link.platform.trim().lowercase()
            val (iconVec, accentColor, tagKey) = when {
                keyLower.contains("instagram") -> Triple(Icons.Default.PhotoCamera, LuminousPurple, "social_link_instagram")
                keyLower.contains("facebook") -> Triple(Icons.Default.People, ElectricBlueBright, "social_link_facebook")
                keyLower.contains("youtube") -> Triple(Icons.Default.OndemandVideo, Color(0xFFF43F5E), "social_link_youtube")
                keyLower.contains("telegram") -> Triple(Icons.AutoMirrored.Filled.Send, ElectricCyan, "social_link_telegram")
                keyLower.contains("whatsapp") -> Triple(Icons.AutoMirrored.Filled.Chat, WhatsAppBrandGreen, "social_link_whatsapp")
                keyLower.contains("github") -> Triple(Icons.Default.Code, ElectricCyan, "social_link_github")
                keyLower.contains("web") -> Triple(Icons.Default.Language, ElectricBlueBright, "social_link_website")
                else -> Triple(Icons.Default.Language, ElectricCyan, "social_link_${keyLower.replace(" ", "_")}")
            }
            val displayLabel = if (keyLower == "website" && isHindi) "वेबसाइट" else link.label.ifBlank { link.platform }

            SocialProfileBadge(
                label = displayLabel,
                icon = iconVec,
                accentColor = accentColor,
                testTag = tagKey,
                onClick = { launchExternalUriSafely(context, link.url) }
            )
        }
    }
}

@Composable
private fun SocialProfileBadge(
    label: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .bounceClick(scaleDown = 0.93f)
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = accentColor.copy(alpha = 0.14f),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    ElectricCyan.copy(alpha = 0.45f),
                    accentColor.copy(alpha = 0.80f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Glowing3DIconBadge(
                icon = icon,
                accentColor = accentColor,
                size = 26.dp,
                iconSize = 14.dp,
                cornerRadius = 8.dp
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

enum class QuickMenuDialogSection {
    CONTACT_ME,
    SOCIAL_MEDIA,
    ORDER_SERVICES,
    ADMIN_PANEL
}

/**
 * Glassmorphic dialog opened from the Top-Right 3-Dot Menu for:
 * - Contact Me
 * - Social Media Profiles
 * - Order Services (WhatsApp / Online)
 * - Admin Panel
 */
@Composable
fun TopBarQuickMenuModalDialog(
    section: QuickMenuDialogSection,
    contactConfig: CenterContactConfig,
    services: List<ServiceItem>,
    isHindi: Boolean,
    isAdmin: Boolean,
    cloudDocCount: Int,
    onDismiss: () -> Unit,
    onEditContactsInProfile: () -> Unit,
    onSelectServiceForApply: (ServiceItem) -> Unit,
    onSelectServiceForWhatsApp: (ServiceItem) -> Unit,
    onAddNewServiceAsAdmin: () -> Unit,
    onSyncDefaultCatalogueAsAdmin: () -> Unit,
    onOpenOrdersManagement: () -> Unit,
    onOpenAdminDashboard: () -> Unit = {},
    onAdminGoogleLoginAndOpenDashboard: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val (icon, color, titleText) = when (section) {
                    QuickMenuDialogSection.CONTACT_ME -> Triple(
                        Icons.AutoMirrored.Filled.Chat,
                        ElectricCyan,
                        if (isHindi) "मुझसे संपर्क करें (Contact Me)" else "Contact Aditya Online Center"
                    )
                    QuickMenuDialogSection.SOCIAL_MEDIA -> Triple(
                        Icons.Default.PhotoCamera,
                        LuminousPurple,
                        if (isHindi) "सोशल मीडिया प्रोफ़ाइल" else "Social Media & Official Links"
                    )
                    QuickMenuDialogSection.ORDER_SERVICES -> Triple(
                        Icons.Default.ShoppingBag,
                        ElectricBlueBright,
                        if (isHindi) "सेवा ऑर्डर करें (Order Services)" else "Quick Order Services"
                    )
                    QuickMenuDialogSection.ADMIN_PANEL -> Triple(
                        Icons.Default.AdminPanelSettings,
                        ElectricCyan,
                        if (isHindi) "एडमिन पैनल (Admin Panel)" else "Admin Control Panel"
                    )
                }
                AdityaBrandEmblem(
                    size = 38.dp,
                    cornerRadius = 11.dp
                )
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (section) {
                    QuickMenuDialogSection.CONTACT_ME -> {
                        ConnectWithMeCard(
                            contactConfig = contactConfig,
                            isHindi = isHindi,
                            showSocialLinks = false,
                            onEditContactsClick = if (isAdmin) {
                                {
                                    onDismiss()
                                    onEditContactsInProfile()
                                }
                            } else {
                                null
                            }
                        )
                    }

                    QuickMenuDialogSection.SOCIAL_MEDIA -> {
                        Text(
                            text = if (isHindi) {
                                "हमारे आधिकारिक सोशल मीडिया अकाउंट और वेबसाइट से जुड़ने के लिए किसी भी बैज पर टैप करें:"
                            } else {
                                "Tap any badge below to visit our official social media profiles & web portal:"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        SocialMediaBadgesRow(
                            contactConfig = contactConfig,
                            isHindi = isHindi
                        )
                        if (isAdmin) {
                            OutlinedButton(
                                onClick = {
                                    onDismiss()
                                    onEditContactsInProfile()
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isHindi) "प्रोफ़ाइल में सोशल लिंक संपादित करें" else "Customize Social Links in Profile",
                                    color = ElectricCyan
                                )
                            }
                        }
                    }

                    QuickMenuDialogSection.ORDER_SERVICES -> {
                        Text(
                            text = if (isHindi) {
                                "किसी भी सेवा को तुरंत व्हाट्सएप या ऑनलाइन फॉर्म से ऑर्डर करें:"
                            } else {
                                "Order any digital service directly via WhatsApp or online application form:"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        services.filter { it.active && !it.isArchived }.sortedBy { it.sortOrder }.forEach { svc ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = glassCardContainerColor(alpha = 0.85f),
                                border = BorderStroke(
                                    width = 1.dp,
                                    brush = glassSpecularBorderBrush(ElectricCyan, VividViolet)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = svc.localizedTitle(isHindi),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "₹${svc.price}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = ElectricCyan
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                onDismiss()
                                                onSelectServiceForWhatsApp(svc)
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, WhatsAppBrandGreen),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                                contentDescription = null,
                                                tint = WhatsAppBrandGreen,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = "WhatsApp",
                                                color = WhatsAppBrandGreen,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                        }
                                        FuturisticGradientButton(
                                            onClick = {
                                                onDismiss()
                                                onSelectServiceForApply(svc)
                                            },
                                            cornerRadius = 12.dp,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                        ) {
                                            Text(
                                                text = if (isHindi) "आवेदन करें" else "Apply",
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    QuickMenuDialogSection.ADMIN_PANEL -> {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = glassCardContainerColor(alpha = 0.82f),
                            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (!isAdmin) {
                                    Text(
                                        text = if (isHindi) {
                                            "एडमिन पैनल खोलने के लिए अपने अधिकृत एडमिन Google खाते से लॉग इन करें:"
                                        } else {
                                            "Sign in with your authorized Admin Google account to unlock the Admin Panel:"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    FuturisticGradientButton(
                                        onClick = {
                                            onDismiss()
                                            onAdminGoogleLoginAndOpenDashboard()
                                        },
                                        cornerRadius = 12.dp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("menu_admin_google_login_btn")
                                    ) {
                                        Icon(Icons.Default.AccountCircle, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isHindi) "Google से एडमिन लॉग इन करें" else "Admin Login with Google",
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        PulsingDotIndicator(color = StatusVerifiedGreen, dotSize = 8.dp)
                                        Text(
                                            text = if (isHindi) {
                                                "एडमिन कंट्रोल सक्रिय • $cloudDocCount क्लाउड सेवाएं"
                                            } else {
                                                "Admin Access Active • $cloudDocCount Cloud Services"
                                            },
                                            style = MaterialTheme.typography.labelLarge,
                                            color = StatusVerifiedGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    FuturisticGradientButton(
                                        onClick = {
                                            onDismiss()
                                            onOpenAdminDashboard()
                                        },
                                        cornerRadius = 12.dp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("menu_open_full_admin_dashboard_btn")
                                    ) {
                                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isHindi) "पूर्ण एडमिन डैशबोर्ड खोलें" else "Open Advanced Admin Dashboard",
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            onDismiss()
                                            onAddNewServiceAsAdmin()
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("menu_admin_add_service_btn")
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isHindi) "नई सेवा जोड़ें (Add New Service)" else "Add New Service",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            onDismiss()
                                            onSyncDefaultCatalogueAsAdmin()
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, ElectricCyan),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.CloudSync,
                                            contentDescription = null,
                                            tint = ElectricCyan
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isHindi) "डिफ़ॉल्ट कैटलॉग क्लाउड में सिंक करें" else "Sync Default Catalogue to Cloud",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            onDismiss()
                                            onOpenOrdersManagement()
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, LuminousPurple),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.ShoppingBag,
                                            contentDescription = null,
                                            tint = LuminousPurple
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isHindi) "सभी ऑर्डर और भुगतान प्रबंधित करें" else "Manage Orders & Verify Payments",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            onDismiss()
                                            onEditContactsInProfile()
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                    ) {
                                        Text(
                                            text = if (isHindi) "व्हाट्सएप और सोशल लिंक अपडेट करें" else "Update WhatsApp & Social Links",
                                            color = ElectricCyan
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (isHindi) "बंद करें" else "Close",
                    color = ElectricCyan,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

/**
 * Modal dialog allowing any customer to order a service directly through WhatsApp
 * with their personal and service-specific details.
 */
@Composable
fun QuickWhatsAppOrderDialog(
    service: ServiceItem,
    contactConfig: CenterContactConfig,
    defaultName: String,
    defaultPhone: String,
    defaultAddress: String,
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onOrderCreatedThroughWhatsApp: (
        customerName: String,
        customerPhone: String,
        detailsSummary: String
    ) -> Unit
) {
    val context = LocalContext.current
    var customerName by remember(defaultName) { mutableStateOf(defaultName) }
    var customerPhone by remember(defaultPhone) { mutableStateOf(defaultPhone) }
    var customerAddress by remember(defaultAddress) {
        mutableStateOf(defaultAddress.ifBlank { "Kateya, Gopalganj, Bihar" })
    }
    var serviceDetails by remember { mutableStateOf("") }
    var validationMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Glowing3DIconBadge(
                        icon = Icons.AutoMirrored.Filled.Chat,
                        accentColor = WhatsAppBrandGreen,
                        size = 36.dp,
                        iconSize = 18.dp,
                        animateGlow = true
                    )
                    Text(
                        text = if (isHindi) "व्हाट्सएप द्वारा ऑर्डर करें" else "Order via WhatsApp",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${service.localizedTitle(isHindi)} • ₹${service.price}",
                    style = MaterialTheme.typography.labelLarge,
                    color = ElectricCyan
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isHindi) {
                        "अपना विवरण भरें। 'व्हाट्सएप पर भेजें' दबाते ही आपका ऑर्डर दर्ज हो जाएगा और पूरा विवरण हमारे व्हाट्सएप (+91 ${contactConfig.whatsappNumber}) पर खुल जाएगा।"
                    } else {
                        "Fill in your details below. Tapping 'Send on WhatsApp' records your order and opens WhatsApp (+91 ${contactConfig.whatsappNumber}) with your complete application details."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = customerName,
                    onValueChange = {
                        customerName = it
                        validationMessage = null
                    },
                    label = { Text(if (isHindi) "आपका पूरा नाम *" else "Your Full Name *") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("whatsapp_dialog_name_input")
                )

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { input ->
                        customerPhone = input.filter { it.isDigit() }.take(10)
                        validationMessage = null
                    },
                    label = { Text(if (isHindi) "10-अंकीय मोबाइल / व्हाट्सएप नंबर *" else "10-Digit Mobile / WhatsApp No. *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("whatsapp_dialog_phone_input")
                )

                OutlinedTextField(
                    value = customerAddress,
                    onValueChange = { customerAddress = it },
                    label = { Text(if (isHindi) "ग्राम / पता (कटेया, गोपालगंज)" else "Village / Address (Kateya, Bihar)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = serviceDetails,
                    onValueChange = {
                        serviceDetails = it
                        validationMessage = null
                    },
                    label = {
                        Text(
                            if (isHindi) "सेवा विवरण / आवश्यक जानकारी लिखें *"
                            else "Service Details / Requirements *"
                        )
                    },
                    placeholder = {
                        Text(
                            if (isHindi) "जैसे: योग्यता, प्रमाण-पत्र प्रकार या फॉर्म का नाम..."
                            else "e.g. Qualifications, Certificate type, Exam name..."
                        )
                    },
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("whatsapp_dialog_details_input")
                )

                AnimatedVisibility(visible = validationMessage != null) {
                    Text(
                        text = validationMessage ?: "",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (customerName.trim().length < 2) {
                        validationMessage = if (isHindi) "कृपया अपना नाम लिखें।" else "Please enter your full name."
                        return@Button
                    }
                    if (customerPhone.trim().length != 10) {
                        validationMessage = if (isHindi) "कृपया 10-अंकीय मोबाइल नंबर लिखें।" else "Please enter a valid 10-digit mobile number."
                        return@Button
                    }
                    val effectiveDetails = serviceDetails.trim().ifBlank {
                        if (isHindi) "व्हाट्सएप पर दस्तावेज़ और विवरण साझा किए जाएंगे"
                        else "Details & documents to be shared on WhatsApp chat"
                    }
                    val combinedSummary = "WhatsApp Order | Details: $effectiveDetails | Address: ${customerAddress.trim()}"
                    val waUrl = WhatsAppOrderHelper.buildServiceOrderWhatsAppUrl(
                        contactConfig = contactConfig,
                        service = service,
                        customerName = customerName.trim(),
                        customerPhone = customerPhone.trim(),
                        address = customerAddress.trim(),
                        serviceDetails = effectiveDetails,
                        isHindi = isHindi
                    )
                    onOrderCreatedThroughWhatsApp(
                        customerName.trim(),
                        customerPhone.trim(),
                        combinedSummary
                    )
                    launchExternalUriSafely(context, waUrl)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = WhatsAppBrandGreen,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .bounceClick()
                    .testTag("confirm_whatsapp_order_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isHindi) "व्हाट्सएप पर भेजें" else "Send on WhatsApp",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isHindi) "रद्द करें" else "Cancel")
            }
        }
    )
}
