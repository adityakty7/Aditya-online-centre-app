# Implementation Plan: Modern Midnight Navy Theme, Custom AO Emblem App Icon & Full-App Motion System

## 1. Overview & User Decisions

Based on your selections, we will upgrade **Aditya Online Center** (`Aditya Online`) into a modern, fluid, production-grade digital services app while preserving all live Firebase Authentication, Cloud Firestore (`/services`, `/orders`, `/users`, `/admins`), and bilingual (`EN / हिंदी`) functionality:

1. **Default Theme — Sleek Midnight Navy with Royal Blue & Vibrant Orange Accents**:
   - **Midnight Navy Canvas**: Rich deep navy-slate backgrounds (`#080D1A` base, `#0F172A` elevated cards, `#17223B` interactive surfaces, `#263554` subtle crisp borders) that look modern, trustworthy, and high-contrast like top Indian fintech apps.
   - **Dual-Tone Accent Hierarchy**: Vibrant **Electric Orange (`#FF6B00`)** for primary CTAs (`Apply`, `Submit Application`, active badges) paired with **Royal Cyber Blue (`#3B82F6` / `#60A5FA`)** for navigation highlights, category chips, verified status indicators, and subtle header gradients.
   - **Refined Daylight Theme Option**: Upgraded crisp porcelain-white & royal-blue light mode (`#F8FAFC` background, `#FFFFFF` cards, `#1E40AF` blue & `#EA580C` orange accents) accessible anytime via the theme toggle.

2. **New Custom App Icon — Premium Modern "AO" Cyber Emblem**:
   - Generate a high-definition modern **AO (Aditya Online)** 3D-styled geometric emblem on a pinned solid `#0A1124` Midnight Navy background using `generate_image`.
   - Configure the Android Adaptive Icon (`res/drawable/ic_launcher_foreground.xml` centered in the `66dp` safe zone + `res/drawable/ic_launcher_background.xml` in `#0A1124`) and generate properly masked square (`ic_launcher.png`) and circular (`ic_launcher_round.png`) raster icons across all `mipmap-*` density folders (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`).
   - Mirror the refreshed emblem style in the top app bar and onboarding/auth brand header.

3. **Full-App Motion & Animation System**:
   - **Smooth Screen Open & Close Transitions (`MainActivity.kt`)**:
     - Directional slide + fade + subtle scale transitions (`AnimatedContent` with `slideInHorizontally`, `slideOutHorizontally`, `fadeIn`, `fadeOut`, and `scaleIn`) when switching bottom navigation tabs (`Services`, `My Orders`, `Profile`) and when opening/closing `ServiceDetail`, `OrderForm`, `OrderConfirmation`, and `OrderDetail`.
   - **Animated Step-by-Step Service Apply & Order Confirmation Flow (`ServiceDetailAndOrderScreen.kt`)**:
     - Interactive animated progress indicator at the top of the **Apply for Service** screen that dynamically fills (`animateFloatAsState`) as the customer completes Applicant Details, Document Links/Uploads, and UPI Reference.
     - Spring-animated document attachment chips and an animated celebratory check-burst + pulsing badge on the **Order Confirmation** screen.
   - **Scrolling & Staggered List Entry Animations (`HomeCatalogueScreen.kt`, `MyOrdersAndProfileScreens.kt`)**:
     - **Collapsible Dynamic Welcome Header**: As the user scrolls down the service catalogue (`LazyGridState`), the welcome banner smoothly collapses/fades so more services are visible on screen, and smoothly expands back when scrolling up.
     - **Staggered Card Entrance & Placement Animations**: Service cards, Featured carousel cards, and Order cards animate in with a smooth slide-up + fade entrance and `Modifier.animateItem()` when filtering by category, searching, or sorting status filters.
   - **Interactive Spring Bounce Micro-Interactions**:
     - Reusable `bounceClick` modifier (`animateFloatAsState` with `Spring.DampingRatioMediumBouncy`) applied to `Apply` buttons, category/status filter chips, Featured service cards, and Bottom Navigation items for tactile feedback on every tap.

---

## 2. File-by-File Implementation Steps

1. **App Icon Generation & Adaptive Resource Setup**
   - Generate `app_icon_ao_emblem` via `generate_image` on solid `#0A1124` background.
   - Update `/app/src/main/res/drawable/ic_launcher_foreground.xml` and `/app/src/main/res/drawable/ic_launcher_background.xml`.
   - Convert and resize `ic_launcher.png` and circular-masked `ic_launcher_round.png` into all `mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}` folders.

2. **Midnight Navy & Royal Blue + Orange Theme (`Color.kt`, `Theme.kt`)**
   - Update `/app/src/main/java/com/example/ui/theme/Color.kt` and `/app/src/main/java/com/example/ui/theme/Theme.kt` with the Midnight Navy palette (`#080D1A` background, `#0F172A` surface, `#17223B` surfaceVariant, `#FF6B00` primary orange, `#3B82F6` secondary royal blue, `#38BDF8` tertiary cyan-blue) and animated color transitions (`animateColorAsState`) when toggling Dark/Light mode or language.

3. **Reusable Animation Utilities (`ui/components/MotionComponents.kt`)**
   - Create a dedicated motion helper file providing:
     - `Modifier.bounceClick()` — tactile spring scale on press (`0.96f` -> `1.0f`).
     - `AnimatedStaggeredItem(index)` — smooth slide-up + fade-in entrance for list/grid items.
     - `PulsingStatusDot` — subtle animated pulse for active order and live cloud indicators.

4. **Screen Open/Close Transitions & Animated Navigation (`MainActivity.kt`)**
   - Replace instant screen switches with directional `AnimatedContent` transitions that distinguish between forward drill-downs (`ServiceDetail`, `OrderForm`, `OrderDetail` sliding in from right with fade/scale), back navigation (sliding out to right), and horizontal tab switches (`Home` ↔ `Orders` ↔ `Profile`).

5. **Scroll-Reactive Home Catalogue & Animated Service Cards (`HomeCatalogueScreen.kt`)**
   - Add scroll-reactive collapsible behavior to the Welcome Banner (`derivedStateOf` on `LazyGridState`) so scrolling down smoothly condenses the header.
   - Add `animateItem()` and entrance animations to Featured Service cards and Service Catalogue cards, plus spring-bounce interactions on Category chips and the `Apply` button.

6. **Animated Service Apply Flow & Order Tracking (`ServiceDetailAndOrderScreen.kt`, `MyOrdersAndProfileScreens.kt`)**
   - Add a live animated completion progress bar (`animateFloatAsState`) on the `OrderApplicationScreen` showing completion across the 3 steps (Details → Documents → Payment).
   - Add an animated spring scale-in badge on `OrderConfirmationScreen` and animated progress bars on `MyOrdersScreen` and `OrderDetailScreen`.

7. **Verification & APK Update**
   - Run Robolectric tests (`gradle :app:testDebugUnitTest`) and `compile_applet`, and refresh `/apk_chunks/` so your GitHub Actions APK download stays in sync.
