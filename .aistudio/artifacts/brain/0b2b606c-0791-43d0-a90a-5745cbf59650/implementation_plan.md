# Aditya Online Center — Native Android Customer App & Shared Backend Plan

A production-grade native Android customer application built with Kotlin, Jetpack Compose, and Material 3 for **Aditya Online Center (Kateya, Bihar, India)**, connected to the shared Firebase project (`gen-lang-client-0774506168`) for real-time service catalogue synchronization, validated order creation, and live order status tracking.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> **Phase 1 Workspace & Backend Inspection Findings**
> - **Workspace Inspection**: The current AI Studio build container is initialized as a clean native Android (Kotlin + Jetpack Compose) project and does not contain the Vercel website repository files locally. The native Android app will be engineered to connect directly to your existing Firebase project **`gen-lang-client-0774506168`** so both the website (`https://aditya-online-centre-jk87.vercel.app/`) and Android app share one backend.
> - **Authentication Provider Constraint**: In the AI Studio Android runtime, `ProvisionFirebase` provisions **Google Sign-In via Jetpack Credential Manager (`GetSignInWithGoogleOption`)** exclusively. Customer accounts on Android will authenticate with their Google account (`uid`, `email`, `displayName`), matching Firebase Authentication UIDs across web and mobile.
> - **Storage & Document Attachments**: Direct Firebase Cloud Storage bucket provisioning is not available in the container's automated provisioning tool. Service images use standard HTTPS image URLs stored in Firestore (`imageUrl`), and customer document submissions support structured document metadata/links alongside local document URI selection and verification payloads.

- **Shared Collections & Schema Default**: Uses top-level `services` and `orders` collections (plus `users/{uid}` profiles) with backward-compatible field mappings so existing web admin documents deserialize cleanly even if optional fields vary.
- **Visual Theme**: High-contrast **Obsidian Black, Charcoal, Neon Orange (`#FF6D00`), and Crisp Silver** brand theme as primary, with an accessible High-Contrast Light mode ("Cyber Blue & White") toggle in Profile/Settings.
- **Bilingual Support (English & Hindi)**: Instant in-app language switcher (`EN` / `हिंदी`) across navigation, service categories, order forms, status steps, and help/legal guides.
- **Trusted Price & Availability Enforcement**: Order creation reads the authoritative service price and `active` flag from the `services/{serviceId}` snapshot and enforces validation both in the repository transaction/write and in `firestore.rules`. Client-supplied UPI reference numbers are recorded with `paymentStatus = "pending_verification"`—never auto-marked as paid.

---

## 1. Overview & Core Concept

- **What It Does**: Enables customers in Kateya, Bihar and surrounding areas to browse live digital & cyber-cafe services (Resumes, Biodata, PDF Services, Document Formatting, Government Form Assistance, Scholarship Applications, PAN/Aadhaar/Certificate assistance), inspect required documents and transparent pricing, submit validated service applications with unique Order IDs (`AOC-YYYYMMDD-XXXX`), submit UPI transaction references for manual admin verification, and track real-time order progress and completed document delivery links.
- **Target Audience / Persona**: Students, job seekers, scholarship applicants, and citizens in Kateya, Gopalganj, Bihar who need mobile-first, reliable access to Aditya Online Center services in English or Hindi without visiting the physical desk for every status check.
- **Key Value**: One unified business system—when the admin updates a service's price, active status, or an order's fulfillment status in the shared Firestore backend, connected Android customers see the change in real time via Firestore snapshot listeners.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Branded Splash & Onboarding**:
   - Displays the **Aditya Online Center — Kateya, Bihar** identity, bilingual tagline, and a 3-card interactive onboarding walkthrough (*Browse Live Services*, *Apply & Upload Details Safely*, *Real-Time Order Tracking & Delivery*).
2. **Google Sign-In Gate**:
   - Uses Jetpack `CredentialManager` (`GetSignInWithGoogleOption` with silent `GetGoogleIdOption` auto-sign-in and an interactive "Sign in with Google" fallback button). Creates/updates the customer profile in `users/{uid}`.
3. **Home & Live Service Catalogue**:
   - **Top Bar**: Brand header, Kateya Bihar location badge, instant `EN / हिंदी` language toggle, and connectivity/sync indicator.
   - **Search & Category Filter Bar**: Filter real-time Firestore `services` by category (*All*, *Resume & Biodata*, *Government Forms*, *Scholarships*, *PDF & Formatting*, *Certificates & ID*) and keyword search.
   - **Service Cards**: Displays service image (`Coil`), title (English/Hindi), category badge, authoritative price (`₹`), estimated turnaround time, and active availability badge. If the shared `services` collection is empty on initial connection, provides a one-tap **"Initialize Default Cyber Cafe Catalogue"** action for authorized setup so the catalogue is immediately testable.
4. **Service Detail & Requirements Sheet/Screen**:
   - Full description, step-by-step process, mandatory document checklist (e.g., Aadhaar, Photo, Marksheet, Mobile No.), transparent fee breakdown, and **"Apply Now"** CTA (disabled automatically if `active == false`).
5. **Dynamic Service-Specific Order Application Form**:
   - Adapts fields dynamically based on the service category (e.g., *Resume/Biodata*: education, experience, skills, marital/family details; *Scholarship/Gov Form*: registration number, category, income/caste certificate details; *PDF/Document Service*: page count, formatting instructions).
   - **Document Attachment & Drive/Link Reference**: Customers can attach local document references via Android's zero-permission Document/Photo Picker (extracting file name, size, MIME type, and optional shared Drive/cloud link) with strict validation (no passwords, PINs, or OTPs ever requested).
   - **Manual UPI Payment Verification Section**: Shows Aditya Online Center's UPI payment instructions, authoritative service amount (`₹`), and an optional UPI 12-digit UTR / Reference Number input clearly labeled *"Subject to manual admin verification — screenshot/UTR alone does not confirm payment"*.
   - **Duplicate Submission Guard**: Idempotency key + loading lock prevents accidental double taps.
6. **Order Confirmation Screen**:
   - Displays only after Firestore confirms the write. Shows the generated unique Order ID (`AOC-...`), service & price snapshot, timestamp, and quick actions to view Order Details or contact support.
7. **My Orders & Real-Time Status Tracker**:
   - Lists only the signed-in customer's orders (`whereEqualTo("customerUid", currentUser.uid)`) with status filter chips (*All*, *Pending*, *In Progress*, *Action Required*, *Completed*, *Cancelled*).
   - **Order Detail & Delivery View**: Visual multi-step status timeline (*Submitted → Payment Verification → Processing at Center → Ready / Delivered*), admin remarks, payment verification status badge, and a secure **"Download / Open Completed Document"** action when `deliveryFileUrl` is provided by the admin.
8. **Customer Profile, Admin Sync Inspector & Legal/Help Center**:
   - Displays signed-in customer details, editable contact phone/village/town in Kateya, language switcher (`English` / `हिंदी`), theme mode switcher (*Signature Dark: Black & Neon Orange* vs. *Daylight Blue & White*), direct Call/WhatsApp Help Desk links for Aditya Online Center Kateya, Privacy Policy, Terms of Service, and a live **Backend Sync Diagnostics & Order Status Simulator** (allowing authorized testing of admin status updates and service catalogue toggles directly against Firestore).

### Visual Identity & Theme
- **Aesthetic Direction**: High-precision **Cyber Utility Dark** (Obsidian Black, Charcoal surfaces, Neon Orange primary accents, and Metallic Silver borders/typography), paired with an optional **Daylight Blue & White** high-contrast mode in user preferences.
- **Color Palette Tokens**:
  - `Background`: `#0A0B0E` (Obsidian Black)
  - `Surface / Card`: `#14171F` (Deep Charcoal) & `#1E222D` (Elevated Slate)
  - `Primary`: `#FF6D00` (Neon Cyber Orange) with `#120700` `OnPrimary`
  - `Secondary`: `#C0C7D6` (Brushed Silver)
  - `Tertiary / Trust Accent`: `#00E5FF` (Electric Cyan for verified badges)
  - `Status Semantic Colors`: `#FFB300` (Pending), `#29B6F6` (Processing), `#00E676` (Completed/Verified), `#FF5252` (Rejected/Inactive)
- **Typography & Hierarchy**:
  - Bundled locally in `app/src/main/res/font/` via `font-util`: **Space Grotesk** (or **Montserrat**) for bold display headers, prices, and Order IDs, paired with **Plus Jakarta Sans** (or **Open Sans**) for high-legibility body copy and bilingual Devanagari/Latin rendering.
- **Accessibility**:
  - Minimum `48.dp` interactive touch targets (`minimumInteractiveComponentSize()`), explicit `contentDescription` and `testTag` on all primary controls, scalable `sp` typography, and edge-to-edge safe drawing insets.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Backward-Compatible Firestore Models (`@IgnoreExtraProperties`)**
  - *Chosen Approach*: All Kotlin Firestore data classes use `@IgnoreExtraProperties`, default property values, and flexible field resolvers (e.g., reading `title` or `name`, `price` as `Double`/`Long`, `active` or `isActive`, `customerUid` or `userId`).
  - *Why*: Ensures 100% compatibility with documents created by the Vercel website admin panel without breaking existing web queries or requiring destructive migrations.
- **Decision 2: Authoritative Pricing & Manual Payment Verification**
  - *Chosen Approach*: When submitting an order, the repository fetches the latest `services/{serviceId}` document from Firestore to verify `active == true` and locks in the server-side `price` and `title` snapshot. Any UPI UTR entered by the customer sets `paymentStatus = "pending_verification"` rather than `"paid"`.
  - *Why*: Prevents client-side price tampering and fake payment confirmation while giving the admin full visibility in the shared dashboard.
- **Decision 3: Enforced Security Rules with Local Emulator Verification**
  - *Chosen Approach*: Author zero-trust `firestore.rules` where anyone authenticated can read `services`, only authorized admins can create/update/delete `services`, customers can only `create` and `read` their own `orders` (where `customerUid == request.auth.uid` and `sourceChannel == "android"` or `"website"`), and only admins can update order status or payment verification fields.
  - *Why*: Protects customer privacy and prevents unauthorized catalogue edits while preserving full website + Android interoperability.

---

## 4. Technical Architecture & Data Strategy

### Architecture & Component Diagram

```
┌────────────────────────────────────────────────────────────────────────────┐
│                   SHARED FIREBASE BACKEND (gen-lang-client-0774506168)     │
│  ┌──────────────────────┐  ┌──────────────────────┐  ┌──────────────────┐  │
│  │  Firebase Auth       │  │  Cloud Firestore     │  │ Security Rules   │  │
│  │  (Google Sign-In /   │  │  • services/{id}     │  │ • Owner isolation│  │
│  │   Shared User UIDs)  │  │  • orders/{orderId}  │  │ • Admin RBAC     │  │
│  │                      │  │  • users/{uid}       │  │ • Schema checks  │  │
│  └──────────┬───────────┘  └──────────┬───────────┘  └────────┬─────────┘  │
└─────────────┼─────────────────────────┼───────────────────────┼────────────┘
              │                         │ Real-time Snapshots   │
      ┌───────┴─────────────────────────┴───────────────┐       │
      ▼                                                 ▼       ▼
┌───────────────────────────────┐        ┌───────────────────────────────────┐
│ Existing Website & Admin      │        │ Native Android Customer App       │
│ (aditya-online-centre-jk87)   │        │ (Kotlin + Jetpack Compose + M3)   │
│ • Manages Services & Pricing  │        │                                   │
│ • Views Web + Android Orders  │        │ ┌───────────────────────────────┐ │
│ • Verifies UPI & Updates      │        │ │ Data & Repository Layer       │ │
│   Order Status / Delivery URL │        │ │ • AuthRepository (CredMgr)    │ │
└───────────────────────────────┘        │ │ • ServiceRepository (Flow)    │ │
                                         │ │ • OrderRepository (Flow/Tx)   │ │
                                         │ └───────────────┬───────────────┘ │
                                         │                 ▼                 │
                                         │ ┌───────────────────────────────┐ │
                                         │ │ MVVM ViewModel Layer          │ │
                                         │ │ • AppViewModel (Auth/Locale)  │ │
                                         │ │ • CatalogueViewModel          │ │
                                         │ │ • OrderViewModel              │ │
                                         │ └───────────────┬───────────────┘ │
                                         │                 ▼                 │
                                         │ ┌───────────────────────────────┐ │
                                         │ │ Compose UI Screens            │ │
                                         │ │ • Splash & Onboarding         │ │
                                         │ │ • Google Sign-In Gate         │ │
                                         │ │ • Home & Live Catalogue       │ │
                                         │ │ • Service Detail & Checklist  │ │
                                         │ │ • Dynamic Order Form + UPI    │ │
                                         │ │ • Order Confirmation          │ │
                                         │ │ • My Orders & Status Timeline │ │
                                         │ │ • Profile, Help & Legal       │ │
                                         │ └───────────────────────────────┘ │
                                         └───────────────────────────────────┘
```

### Data Model & Shared Firestore Schema

1. **`services/{serviceId}` (Shared Service Catalogue)**
   - `id` (`@DocumentId String`): Unique service document ID
   - `title` (`String`): Service name in English (e.g., `"Professional Resume / CV Design"`)
   - `titleHi` (`String`): Service name in Hindi (e.g., `"प्रोफेशनल रिज्यूमे / बायोडाटा डिज़ाइन"`)
   - `category` (`String`): Category key (`"resume"`, `"gov_form"`, `"scholarship"`, `"pdf_doc"`, `"certificate"`)
   - `description` (`String`): Full description and scope of work
   - `descriptionHi` (`String`): Hindi description
   - `price` (`Long` / `Double`): Current authoritative price in INR (`₹`)
   - `turnaroundTime` (`String`): Estimated completion time (e.g., `"2 - 4 Hours"`)
   - `requirements` (`List<String>`): Required customer documents/details
   - `imageUrl` (`String`): HTTPS URL for service banner/illustration
   - `active` (`Boolean`): Whether the service can currently be ordered
   - `createdAt` / `updatedAt` (`@ServerTimestamp Timestamp?`)

2. **`orders/{orderId}` (Shared Authoritative Order System)**
   - `id` (`@DocumentId String`): Firestore document ID
   - `orderNumber` (`String`): Human-readable unique ID (`"AOC-20261026-4829"`)
   - `customerUid` (`String`): Authenticated Firebase User UID (`request.auth.uid`)
   - `userId` (`String`): Mirrored UID field for compatibility with rules/queries expecting `userId`
   - `customerName` (`String`): Applicant full name
   - `customerPhone` (`String`): Validated 10-digit Indian mobile number
   - `customerEmail` (`String`): Signed-in email address
   - `sourceChannel` (`String`): `"android"` (or `"website"` for web orders)
   - `serviceId` (`String`): Referenced service ID
   - `serviceTitleSnapshot` (`String`): Service title captured at time of ordering
   - `priceSnapshot` (`Long`): Authoritative service price (`₹`) captured at time of ordering
   - `formDetails` (`Map<String, String>`): Service-specific application fields (e.g., Qualification, DOB, Registration No., Address, Instructions)
   - `documentAttachments` (`List<String>`): Selected file names / cloud document links provided by customer
   - `upiReference` (`String`): Optional 12-digit UTR / transaction reference for manual verification
   - `paymentStatus` (`String`): `"pending_verification"`, `"unpaid"`, or `"verified"` (admin-controlled)
   - `status` (`String`): `"pending"`, `"processing"`, `"action_required"`, `"completed"`, or `"cancelled"`
   - `adminNotes` (`String`): Status remarks or instructions from the admin panel
   - `deliveryFileUrl` (`String`): Secure download link for the final completed PDF/document
   - `createdAt` / `updatedAt` (`@ServerTimestamp Timestamp?`)

3. **`users/{uid}` (Customer Profile)**
   - `uid` (`String`), `name` (`String`), `email` (`String`), `phone` (`String`), `address` (`String`), `preferredLanguage` (`String`), `role` (`String`: `"customer"` or `"admin"`), `updatedAt` (`Timestamp?`)

### Interactive Component & State Mapping
- **Real-Time Catalogue Sync (`CatalogueViewModel`)**: Subscribes to `db.collection("services").snapshots()`, mapping emissions to `UiState.Loading`, `UiState.Success(services)`, or `UiState.Error(message)` with instant search, category chip filtering, and retry handling.
- **Validated Order Submission (`OrderViewModel` + `OrderRepository`)**:
  - Validates applicant name, 10-digit phone number, required dynamic fields, and optional 12-digit UPI UTR format.
  - Queries `services/{serviceId}` on submission to verify `active == true` and read the trusted `price` and `title`.
  - Writes the order document with `FieldValue.serverTimestamp()` and `sourceChannel = "android"`, waiting for `.await()` confirmation before navigating to `OrderConfirmationScreen`.
- **Order Tracking & Delivery (`MyOrdersScreen` & `OrderDetailScreen`)**:
  - Subscribes to `db.collection("orders").whereEqualTo("customerUid", uid).snapshots()` so any status change, payment verification, or `deliveryFileUrl` update made in the admin panel reflects immediately on the customer's screen.
