# EDGE OS v1.0.0

Single-APK Android launcher project inspired by modern iOS-style UX, implemented from scratch with original code/resources.

## Final scope
- Launcher Engine: app grid, paging, dock, lightweight rendering
- Assistive Engine: optional floating control
- Control Center: safe Android settings intents + media volume
- Notification Engine: one NotificationListenerService with bounded in-memory state
- Dynamic Engine: event-driven Dynamic Island-style overlay; no polling/persistent dynamic service
- Lock Screen Experience: clock/date/notification presentation; Android secure authentication remains authoritative
- UI Engine: native drawing, light/dark-aware palette, liquid/glass-inspired surfaces without continuous blur
- Performance/Compatibility: minSdk 27, event-driven updates, bounded notification store, no external SDKs

## Security and privacy
- No root, exploits, hidden API abuse, privilege escalation, credential interception, or lock-screen bypass.
- No ads, analytics, Firebase, WebView, WorkManager, or network dependency in the core.
- Overlay and notification access are explicit user-controlled Android permissions.

## Compatibility
- minSdk 27 (Android 8.1)
- targetSdk 35 / compileSdk 35
- Java 17

## Build
GitHub Actions uses Gradle 8.7 and Android Gradle Plugin 8.6.1. The authoritative build is CI because the development environment may not contain the Android SDK.

Release signing is intentionally not hard-coded; production signing should use a private GitHub Actions keystore secret or local keystore.
