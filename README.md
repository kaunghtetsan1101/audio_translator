# 🎙️ AudioTranslator

### Speak. Transcribe. Translate. — Instantly, on Android & iOS.

A **Kotlin Multiplatform** mobile application that records or imports audio, then uses an AI-powered backend to transcribe speech to text or translate it into dozens of languages — all from a single shared codebase.

[![Build Status](https://img.shields.io/github/actions/workflow/status/kaunghtetsan1101/audio_translator/build.yml?branch=master&style=flat-square)](https://github.com/kaunghtetsan1101/audio_translator/actions)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?style=flat-square&logo=kotlin)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/platform-Android%20%7C%20iOS-green?style=flat-square)](https://github.com/kaunghtetsan1101/audio_translator)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)
[![Version](https://img.shields.io/badge/version-1.0.0-blue?style=flat-square)](https://github.com/kaunghtetsan1101/audio_translator/releases)

---

## 📋 Table of Contents

1. [About The Project](#-about-the-project)
   - [Why This Project?](#why-this-project)
   - [Built With](#built-with)
2. [Getting Started](#-getting-started)
   - [Prerequisites](#prerequisites)
   - [Installation](#installation)
   - [Environment Variables](#environment-variables)
3. [Usage](#-usage)
4. [Architecture & Core Concepts](#-architecture--core-concepts)
5. [Testing](#-testing)
6. [Deployment](#-deployment)
7. [Contributing](#-contributing)
8. [License](#-license)
9. [Contact](#-contact)

---

## 📖 About The Project

**AudioTranslator** lets users record live audio or import an existing audio file on their phone, then choose between two modes:

| Mode | What it does |
|---|---|
| **Transcribe** | Converts speech to text, detects the source language automatically |
| **Translate** | Transcribes speech *and* translates it to a chosen target language, with optional TTS voice playback |

The core business logic, networking, and state management live in a **shared Kotlin module** that compiles to both Android and iOS — meaning features are written once and run natively on both platforms.

### Why This Project?

Existing translation apps are either locked to one platform, require paid subscriptions for basic functionality, or bury the core feature under unnecessary complexity. AudioTranslator is a lean, open-source alternative that demonstrates how KMP can deliver a genuinely native cross-platform experience without sacrificing code quality or user experience.

### Built With

| Layer | Technology |
|---|---|
| **Language** | [Kotlin Multiplatform (KMP)](https://kotlinlang.org/docs/multiplatform.html) |
| **UI** | [Compose Multiplatform](https://www.jetbrains.com/linters/compose-multiplatform/) |
| **Networking** | [Ktor Client](https://ktor.io/docs/client-create-new-application.html) |
| **Dependency Injection** | [Koin](https://insert-koin.io/) |
| **State Management** | `ViewModel` + `StateFlow` (AndroidX Lifecycle KMP) |
| **Audio (Android)** | `MediaRecorder` / `MediaPlayer` |
| **Audio (iOS)** | `AVAudioRecorder` / `AVAudioPlayer` |
| **Backend / AI** | [Hugging Face Spaces](https://huggingface.co/spaces) (REST API) |
| **Build System** | Gradle (Kotlin DSL) |

---

## 🚀 Getting Started

### Prerequisites

Make sure the following are installed and configured before you begin.

**All platforms:**
- [Android Studio](https://developer.android.com/studio) Hedgehog (2023.1.1) or later
- **JDK 17** or later
  ```bash
  java -version  # should print 17.x or higher
  ```
- Android SDK with **API Level 26+** (install via Android Studio's SDK Manager)

**iOS (macOS only):**
- macOS 13 (Ventura) or later
- [Xcode](https://developer.apple.com/xcode/) 15 or later
- Xcode Command Line Tools
  ```bash
  xcode-select --install
  ```
- iOS Simulator or a physical device running iOS 16+

---

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/kaunghtetsan1101/audio_translator.git
   cd audio_translator
   ```

2. **Open in Android Studio**
   Open the root `audio_translator/` folder. Android Studio will detect the Gradle project and prompt you to sync. Click **"Sync Now"**.

3. **Verify the Gradle sync completes without errors**
   ```bash
   ./gradlew build
   ```

4. **Run on Android** (emulator or connected device)
   ```bash
   ./gradlew :composeApp:installDebug
   ```
   Then launch **AudioTranslator** from your device's app drawer.

5. **Run on iOS** (macOS only)
   Open the Xcode project:
   ```bash
   open iosApp/iosApp.xcodeproj
   ```
   Select your target simulator or device in the Xcode toolbar, then press **⌘ R** to build and run.
   > Xcode automatically triggers `./gradlew :composeApp:embedAndSignAppleFrameworkForXcode` as a build phase to compile the shared Kotlin framework.

---

### Environment Variables

This project does **not** require a `.env` file — the backend base URL is declared as a constant in the networking layer.

To point the app at a different backend, update the following constant in **`shared/src/commonMain/kotlin/com/audiotranslator/data/network/TranslatorApi.kt`**:

```kotlin
companion object {
    // TODO: Replace with your own backend URL if self-hosting
    const val BASE_URL = "https://<YOUR_HUGGING_FACE_SPACE>.hf.space/"
}
```

If you need per-environment configuration (e.g., staging vs. production), add entries to **`gradle.properties`** and read them via `BuildConfig` on Android or a KMP `expect/actual` pair on iOS.

```properties
# gradle.properties — example for per-environment config
# TODO: Update these values for your deployment targets
BASE_URL_DEBUG=https://your-staging-space.hf.space/
BASE_URL_RELEASE=https://your-production-space.hf.space/
```

---

## 💡 Usage

### 1. Choose a mode

At the top of the home screen, use the segmented toggle to select your intent:

- **Transcribe** — no target language needed; the app detects the source language automatically.
- **Translate** — exposes the target language and voice dropdowns.

### 2. Provide audio input

| Action | How |
|---|---|
| **Record live audio** | Tap **Record**. The button turns red and shows a spinner. Tap **Stop** when done. |
| **Cancel a recording** | Tap **Cancel** while recording — the audio is discarded and state is reset. |
| **Import a file** | Tap **Pick File** to open the system file picker and select an `.m4a`, `.mp3`, or `.wav` file. |
| **Clear audio** | Once audio is loaded, tap the red **Clear** button to discard it and start over. |

### 3. Configure translation (Translate mode only)

Select a **target language** from the dropdown. Optionally pick a **voice** for the synthesised audio output.

### 4. Process

Tap **Transcribe** or **Translate**. A progress indicator is shown while the backend processes the request.

### 5. Review results

The results card displays:

- **Detected language** (badge)
- **Transcription** — the original speech as text
- **Translation** — the translated text *(Translate mode only)*
- **Play button** — streams the synthesised translated audio *(Translate mode only)*

To discard results and start fresh, tap **Clear** in the results card header.

### API Reference

The app communicates with a REST backend. The key endpoints are:

```
POST /api/transcribe
  Form: file (audio binary)
  Returns: { "original_text": "...", "detected_language": "..." }

POST /api/translate          (metadata)
POST /api/translate/audio    (binary audio stream)
  Form: file (audio binary)
  Query: target_language, voice (optional)
  Returns: TranslationResponseDto / ByteArray

GET  /api/languages          → list of supported target languages
GET  /api/voices/{langCode}  → list of available TTS voices for a language
```

---

## 🏗️ Architecture & Core Concepts

The project follows **Clean Architecture** layered within a standard KMP module structure.

```
audio_translator/
├── composeApp/                  # UI layer — Compose Multiplatform screens & components
│   └── src/
│       ├── commonMain/          # Shared UI code (HomeScreen, components, DI)
│       ├── androidMain/         # Android entry point (MainActivity, Application)
│       └── iosMain/             # iOS entry point (MainViewController)
│
└── shared/                      # Business logic — compiled for both targets
    └── src/
        ├── commonMain/
        │   ├── audio/           # expect declarations (AudioRecorder, AudioPlayer, AudioFilePicker)
        │   ├── data/
        │   │   ├── network/     # Ktor API client + DTOs
        │   │   └── repository/  # Repository implementations
        │   ├── domain/
        │   │   ├── model/       # Pure data models (Language, Voice, TranslationResult, TranslationMode)
        │   │   ├── repository/  # Repository interfaces
        │   │   └── usecase/     # Use cases (TranslateAudioUseCase, TranscribeAudioUseCase, …)
        │   ├── di/              # Koin modules (network, repository, domain)
        │   └── presentation/   # TranslatorViewModel + TranslatorState
        ├── androidMain/         # actual implementations for Android
        └── iosMain/             # actual implementations for iOS
```

### Key Design Decisions

| Decision | Rationale |
|---|---|
| `expect`/`actual` for audio | `MediaRecorder` and `AVAudioRecorder` have no common abstraction — KMP's `expect`/`actual` mechanism bridges them cleanly. |
| Single `TranslatorState` data class | Immutable state + `StateFlow` makes the UI fully reactive and testable without side-effect complexity. |
| `TranslationResult` with nullable `translation` | The same model is reused for both transcription-only and full translation responses; `null` fields signal transcription mode to the UI. |
| Koin over Hilt | Koin is KMP-compatible; Hilt is Android-only. Using Koin keeps DI shared and avoids platform-specific boilerplate. |

---

## 🧪 Testing

Run the full shared-module test suite:

```bash
./gradlew :shared:allTests
```

Run Android unit tests only:

```bash
./gradlew :shared:testDebugUnitTest
```

Run the Android instrumented tests on a connected device or emulator:

```bash
./gradlew :composeApp:connectedAndroidTest
```

> **Note:** UI snapshot/screenshot tests are not yet included. Contributions welcome — see [Contributing](#-contributing).

---

## 📦 Deployment

### Android — Release APK / AAB

1. Create a keystore (one-time setup):
   ```bash
   keytool -genkey -v -keystore release.jks \
     -alias audio_translator \
     -keyalg RSA -keysize 2048 \
     -validity 10000
   ```

2. Add signing config to **`composeApp/build.gradle.kts`**:
   ```kotlin
   // TODO: Move these to gradle.properties or environment variables — never commit secrets
   signingConfigs {
       create("release") {
           storeFile = file("<PATH_TO>/release.jks")
           storePassword = "<STORE_PASSWORD>"
           keyAlias = "audio_translator"
           keyPassword = "<KEY_PASSWORD>"
       }
   }
   ```

3. Build the release bundle:
   ```bash
   ./gradlew :composeApp:bundleRelease
   # Output: composeApp/build/outputs/bundle/release/composeApp-release.aab
   ```

### iOS — Archive & Distribute

1. Open **`iosApp/iosApp.xcodeproj`** in Xcode.
2. Set the target to **Any iOS Device (arm64)**.
3. Select **Product → Archive**.
4. In the Organizer window, click **Distribute App** and follow the wizard for App Store Connect or Ad Hoc distribution.

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

1. **Fork** the repository.

2. **Create a feature branch:**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes**, following the existing code style and architecture conventions.

4. **Run the tests** to make sure nothing is broken:
   ```bash
   ./gradlew :shared:allTests
   ```

5. **Commit** with a descriptive message:
   ```bash
   git commit -m "feat: add support for real-time streaming transcription"
   ```

6. **Push** to your fork:
   ```bash
   git push origin feature/your-feature-name
   ```

7. **Open a Pull Request** against the `master` branch of this repository.

Please make sure your PR:
- Describes *what* changed and *why*
- Does not introduce breaking changes without discussion
- Includes tests for new business logic where applicable

---

## 📬 Contact

**Kaung Htet San** — <!-- kaunghtetsan1101@gmail.com -->

Project Link: [https://github.com/kaunghtetsan1101/audio_translator](https://github.com/kaunghtetsan1101/audio_translator)

**Navneet Sai Danturi** — <!-- TODO: add your email or social handle -->

Huggingface Profile : [https://huggingface.co/Nav772](https://huggingface.co/Nav772)
GitHub Profile : [https://github.com/Algo-nav](https://github.com/Algo-nav)
Backend (Hugging Face Space): [https://nav772-audio-language-translator.hf.space](https://nav772-audio-language-translator.hf.space)

---

<p align="center">Made with Kotlin Multiplatform</p>
