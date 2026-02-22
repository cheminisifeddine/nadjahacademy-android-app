# Nadjah Academy — Android Native App

Production-ready e-learning Android app (Kotlin + Jetpack Compose) that connects to the Nadjah Academy Cloudflare Workers backend.

---

## Prerequisites

| Tool | Minimum version |
|------|----------------|
| JDK | 17 |
| Android Studio | Hedgehog (2023.1.1) or newer |
| Android Gradle Plugin | 8.2+ |
| Kotlin | 1.9+ |
| compileSdk | 35 |
| minSdk | 26 (Android 8.0) |

---

## Project Structure

```
android-native/
├── app/                          # Application module (MainActivity, DI root, navigation)
├── core/
│   ├── analytics/                # Firebase Analytics wrapper (AnalyticsTracker)
│   ├── auth/                     # AuthManager, JWT refresh, session state
│   ├── database/                 # Room DB (entities, DAOs, di)
│   ├── datastore/                # DataStore — tokens, theme, language prefs
│   ├── domain/                   # Shared use-case interfaces
│   ├── network/                  # Retrofit, API services, DTO models
│   ├── testing/                  # Shared test utilities
│   └── ui/                       # Design system (colours, typography, Theme, components)
└── feature/
    ├── auth/                     # Onboarding, Login, Register, ForgotPassword
    ├── blog/                     # Blog list + detail
    ├── course/                   # Course detail, curriculum, reviews
    ├── discussion/               # Course discussion threads
    ├── explore/                  # Browse all courses, filters, categories
    ├── home/                     # Dashboard, banners, quick-access
    ├── instructor/               # Instructor public profile
    ├── lesson/                   # Video player (Media3/ExoPlayer), notes
    ├── mylearning/               # Enrolled courses, progress
    ├── notifications/            # In-app notification feed
    ├── payment/                  # Checkout (CIB / BaridiMob / Dahabia)
    ├── profile/                  # User profile, stats, certificates
    ├── quiz/                     # Quiz intro, question flow, result
    ├── search/                   # Full-text course + instructor search
    └── settings/                 # Theme, language, download, playback
```

---

## Setup

### 1 — Clone & open

```bash
git clone <repo-url>
# Open the android-native/ folder in Android Studio (not the repo root)
```

### 2 — Backend URL

Create (or edit) `android-native/local.properties` and add:

```properties
# No trailing slash
BASE_URL=https://nadjahacademy.com/api/v1
```

The value is read in `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "BASE_URL", "\"${localProperties["BASE_URL"]}\"")
```

### 3 — Firebase

1. Create a Firebase project at <https://console.firebase.google.com>.
2. Add an Android app with package `dz.nadjahacademy`.
3. Download `google-services.json` and place it at:

   ```
   android-native/app/google-services.json
   ```

4. Enable **Authentication**, **Cloud Messaging (FCM)**, **Crashlytics**, and **Analytics** in the Firebase console.

### 4 — Signing (release builds)

Create `android-native/keystore.properties`:

```properties
storeFile=../keystore/nadjah-release.jks
storePassword=<your-store-password>
keyAlias=nadjah
keyPassword=<your-key-password>
```

Generate the keystore if needed:

```bash
keytool -genkey -v -keystore android-native/keystore/nadjah-release.jks \
    -alias nadjah -keyalg RSA -keysize 2048 -validity 10000
```

### 5 — Fonts

The design uses **Poppins** (headings) and **Inter** (body). Place the TTF files in:

```
core/ui/src/main/res/font/
  poppins_regular.ttf
  poppins_medium.ttf
  poppins_semibold.ttf
  poppins_bold.ttf
  poppins_extrabold.ttf
  inter_regular.ttf
  inter_medium.ttf
  inter_semibold.ttf
```

Download links:
- **Poppins** — <https://fonts.google.com/specimen/Poppins>
- **Inter** — <https://fonts.google.com/specimen/Inter>

> The placeholder XML font descriptors in `core/ui/src/main/res/font/` will fall back to system sans-serif until the TTF files are added.

---

## Build

```bash
# Debug APK
./gradlew :app:assembleDebug

# Release APK (requires keystore.properties)
./gradlew :app:assembleRelease

# Release AAB (for Play Store)
./gradlew :app:bundleRelease

# Run all unit tests
./gradlew test

# Run lint
./gradlew lint
```

---

## Architecture

```
UI (Compose) ─► ViewModel ─► Repository / UseCase ─► API Service / Room DAO
                    │
                    └► DataStore (theme, language, tokens)
```

- **Single-activity**: `MainActivity` hosts a `NavHost` covering all destinations.
- **State**: `MutableStateFlow<UiState>` in every ViewModel; Compose collects via `collectAsState()`.
- **DI**: Hilt — `@HiltAndroidApp`, `@HiltViewModel`, singleton modules in each `core/*` module.
- **Network**: Retrofit + KotlinX Serialization. Auth interceptor auto-attaches Bearer token; authenticator transparently retries after refreshing expired tokens.
- **Caching**: Room caches courses, enrollments, lessons, notifications, and search history. DataStore persists user prefs.
- **Images**: Coil, sharing the same `OkHttpClient` used by Retrofit (cache headers respected).
- **Video**: Media3/ExoPlayer via `PlayerView`; supports background playback and Picture-in-Picture.

---

## Key dependencies (Version Catalog — `gradle/libs.versions.toml`)

| Library | Usage |
|---------|-------|
| `androidx.compose.bom` | Compose BOM for aligned versions |
| `androidx.hilt:hilt-navigation-compose` | Hilt ViewModel injection in NavGraph |
| `com.squareup.retrofit2` | HTTP client |
| `org.jetbrains.kotlinx:kotlinx-serialization-json` | JSON serialisation |
| `androidx.room:room-ktx` | Local SQLite cache |
| `androidx.datastore:datastore-preferences` | Preferences persistence |
| `io.coil-kt:coil-compose` | Async image loading |
| `androidx.media3:media3-exoplayer` | Video playback |
| `com.google.dagger:hilt-android` | Dependency injection |
| `com.google.firebase:firebase-bom` | Firebase BOM |

---

## Backend (Cloudflare Workers)

The backend lives in the repository root under `workers/`. See the top-level `README.md` for deployment instructions. In brief:

```bash
cd workers/
npm install
wrangler secret put JWT_SECRET
wrangler secret put STRIPE_SECRET_KEY
wrangler deploy
```

---

## Localization

The app supports **English**, **Arabic (RTL)**, and **French**. String resources:

```
app/src/main/res/values/strings.xml          # en (default)
app/src/main/res/values-ar/strings.xml       # ar — add when translating
app/src/main/res/values-fr/strings.xml       # fr — add when translating
```

Language switching at runtime is handled by `SettingsViewModel` + `TokenStore.setLanguage()`.

---

## Notifications

1. FCM token is registered in `NadjahFirebaseMessagingService.onNewToken()` and sent to the backend (`PUT /users/me/fcm-token`).
2. Foreground notifications are posted on channel `nadjah_notifications`.
3. Tapping a notification opens `MainActivity` with `deepLink` extra for in-app routing.

---

## Proguard / R8

Release builds use R8 (full mode). Rules in `app/proguard-rules.pro` cover:

- Retrofit + OkHttp reflection
- KotlinX Serialization `@Serializable` classes
- Hilt generated components
- Firebase / Crashlytics
- Coil request/transformation classes
- All model DTOs in `dz.nadjahacademy.core.network.model`

---

## Contributing

1. Branch off `main` with `feature/<name>` or `fix/<name>`.
2. Keep each feature in its own Gradle module.
3. Run `./gradlew lint test` before opening a PR.
4. ViewModels must be covered by unit tests in `core/testing` or the feature's `test/` source set.
