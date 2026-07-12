# Ryzix YT

> **Watch. Download. Own your library.** — A premium, flat-design YouTube client for Android.

[![Build Status](https://github.com/RD7890/ryzix-yt/actions/workflows/build.yml/badge.svg)](https://github.com/RD7890/ryzix-yt/actions/workflows/build.yml)
[![Latest Release](https://img.shields.io/github/v/release/RD7890/ryzix-yt)](https://github.com/RD7890/ryzix-yt/releases/latest)
[![Android](https://img.shields.io/badge/Android-26%2B-green)](https://developer.android.com/about/versions/oreo)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue)](https://kotlinlang.org/)

---

## Features

- **Search** — Find any video by keyword, backed by a real extraction engine (no scraping hacks in the UI layer).
- **Playback** — In-app player built on Media3/ExoPlayer with adaptive quality streams.
- **Download from the player** — Tap Download on the now-playing screen to open a custom, flat, premium-styled quality picker sheet (video + audio-only options, no system share sheet).
- **Watch History** — Every video you open is logged locally (Room) with quick resume/re-download.
- **Notifications** — Live download progress and completion, plus an in-app Notifications feed.
- **Flat Premium Design** — Zero gradients, one deliberate brand color, Material Symbols iconography only (no emoji).

## Tech Stack

| Layer | Technology |
|-------|-----------|
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Hilt DI |
| Playback | Media3 ExoPlayer (HLS/DASH/progressive) |
| Extraction | NewPipeExtractor |
| Local storage | Room (watch history) + DataStore (settings) |
| Background work | WorkManager (downloads + notifications) |
| Build | Gradle 8.9 + AGP 8.5.2 |
| Language | Kotlin 2.0 |

## APK Downloads

Get the latest signed APK from [Releases](https://github.com/RD7890/ryzix-yt/releases/latest) — `arm64-v8a-release` covers virtually all modern Android phones (2018+).

## CI/CD

Every push to `main` or `dev`:
1. Installs Gradle 8.9 + JDK 17
2. Builds a signed Release APK (arm64-v8a)
3. Renames the APK with version + build number
4. Creates a GitHub Release with the APK attached

## Build Locally

```bash
git clone https://github.com/RD7890/ryzix-yt.git
cd ryzix-yt
./gradlew assembleDebug
```

## Legal

Ryzix YT is a personal-use media client. Only download content you own the rights to or are otherwise permitted to save offline — downloading is subject to the terms of the platform the content originates from.

## License

MIT © Rohan
