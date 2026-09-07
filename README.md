# EDGE OS — unified M1–M14 Android launcher

Single native Android APK targeting Android 8.1/API 27 minimum and modern Android. The project is intentionally dependency-light and uses public Android APIs only.

## Implemented in this unified build
- M1 Launcher foundation: HOME role, app discovery, paging, dock, explicit app launching.
- M2 Animation/interaction baseline: smooth clock refresh, touch paging container, lightweight transitions.
- M3 Liquid-style visual system: dark glass surfaces, rounded cards, gradients, translucent overlays.
- M4 App Library + long-press app menu baseline.
- M5 Control Center-style AssistiveTouch panel with Back/Home/Recents/Quick Settings/Notifications/Settings.
- M6 Notification Listener + transient Dynamic Island-style notification pill.
- M7 Spotlight-style app search.
- M8 App Library.
- M9 Dynamic status/island overlay.
- M10 Lock Screen-style preview activity.
- M11 AssistiveTouch accessibility service.
- M12 API 27 compatibility fallbacks and capability-gated APIs.
- M13 low-overhead/event-driven design; no INTERNET permission, bounded work, no polling network.
- M14 build/signing QA workflow and release notes.

## Important platform boundary
This is an Android launcher inspired by the interaction language of modern iOS. A normal APK cannot literally replace iOS, Secure Enclave, Face ID, Apple system services, UIKit/SwiftUI, or Android firmware/system partitions.

## Permissions
Overlay, Accessibility binding, Notification Listener, Foreground Service declaration and vibration. No INTERNET permission.

## Build
GitHub Actions uses JDK 17 + Gradle 8.7 + Android SDK 34. Debug is explicitly signed by the Android Gradle debug signing configuration. The workflow verifies the APK with `apksigner` before upload.
