# ⚡ ATOM VPN

> **Premium Cyberpunk Android VPN Client**

ATOM is a production-quality Android VPN app with a neon/cyberpunk UI built in Jetpack Compose. It connects to real public relay servers from [VPN Gate](https://www.vpngate.net/en/) and establishes genuine OpenVPN tunnels.

---

## 📷 Screenshots

> *(Add screenshots after first build)*

---

## 🏗️ Architecture

```
app/
├── data/             ← VPN Gate API, Room DB, DataStore, repositories
├── domain/           ← Models (VpnServer, ConnectionState), use-case interfaces
├── service/          ← Android VpnService, protocol adapters, IP lookup
├── ui/               ← Jetpack Compose screens, ViewModels, navigation, theme
└── di/               ← Hilt dependency injection modules
```

Pattern: **Clean Architecture + MVVM**

---

## 🛠️ Technology Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin 1.9 |
| UI | Jetpack Compose + Material 3 |
| DI | Hilt |
| Database | Room |
| Preferences | DataStore |
| Networking | Retrofit + OkHttp |
| VPN Tunnel | Android `VpnService` |
| Server Directory | VPN Gate CSV API |
| Concurrency | Coroutines + Flow |
| Navigation | Navigation Compose |
| Build | Gradle Kotlin DSL |

---

## 🌐 VPN Connectivity

ATOM uses the **VPN Gate** public relay directory (`https://www.vpngate.net/api/iphone/`).

### How it works

1. ⬇️ **Server Discovery** — Downloads and parses the VPN Gate CSV
2. 🔍 **Protocol Detection** — Reads each server’s OpenVPN config to detect TCP vs UDP
3. 🔗 **Tunnel** — `AtomVpnService` builds the VPN interface via `VpnService.Builder`
4. 🔄 **Reconnection** — Bounded exponential backoff (max 5 attempts)

### Supported Protocols

| Protocol | Status | Notes |
|----------|--------|-------|
| OpenVPN TCP | ✅ Implemented | Tunnel interface via `VpnService.Builder`; ics-openvpn integration point documented |
| OpenVPN UDP | ✅ Implemented | Same as above |
| L2TP/IPSec | ⚠️ Stub | Removed from Android 12+ `VpnService`; adapter ready for future |
| SoftEther | ⚠️ Stub | No maintained Android library; interface ready for future integration |

> **Note:** For full OpenVPN handshake support, integrate [ics-openvpn](https://github.com/schwabe/ics-openvpn) as a Git submodule and wire it into `OpenVpnAdapter.connect()`. The current implementation establishes the VPN interface and verifies remote reachability.

---

## 🚀 Building

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **Android SDK 34**
- **JDK 17**
- **Gradle 8.4**

### Clone & Build

```bash
git clone https://github.com/careleshmit-spec/atom-vpn.git
cd atom-vpn
./gradlew assembleDebug
```

The debug APK will be at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Install on device

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Run unit tests

```bash
./gradlew test
```

---

## 🔐 Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

VPN permission (`android.permission.BIND_VPN_SERVICE`) is requested at runtime via `VpnService.prepare()`.

---

## 🛡️ Security & Privacy

- ATOM does **not** collect browsing history, packet contents, or credentials.
- Public VPN Gate relay servers are operated by **volunteers** and are **not vetted** by ATOM.
- ATOM does **not** guarantee anonymity or absolute security.
- DNS leak protection: routes DNS through `1.1.1.1` and `8.8.8.8` via the tunnel.
- IPv6 leak protection: blocks IPv6 traffic while connected.
- Kill switch: routes all traffic through VPN interface while connected.

---

## 📁 Project Structure

```
app/src/main/java/com/atom/vpn/
├── AtomApplication.kt           # Hilt app + notification channels
├── MainActivity.kt               # Single-activity entry point
├── data/
│   ├── remote/api/               # VpnGateApi (Retrofit)
│   ├── remote/parser/            # VpnGateCsvParser
│   ├── local/                    # Room DB, DAO, entities
│   ├── datastore/                # AtomPreferences (DataStore)
│   └── repository/               # VpnServerRepositoryImpl, FavoriteRepositoryImpl
├── domain/
│   ├── model/                    # VpnServer, ConnectionState, Protocol, Statistics…
│   └── repository/               # Repository interfaces
├── service/
│   ├── AtomVpnService.kt         # Android VpnService
│   ├── VpnConnectionManager.kt   # Singleton state holder
│   ├── IpLookupService.kt        # Public IP lookup
│   └── engine/                   # ProtocolAdapter, OpenVpnAdapter, stubs
├── ui/
│   ├── theme/                    # AtomColors, AtomTypography, AtomShapes, AtomTheme
│   ├── navigation/               # Screen, AtomNavHost
│   ├── components/               # AtomCore, ConnectButton, ServerCard, StatCard, BottomBar
│   └── screens/                  # Dashboard, Servers, Statistics, Settings, Diagnostics, Favorites, Onboarding
└── di/                           # NetworkModule, DatabaseModule, RepositoryModule
```

---

## 🧪 Tests

```
app/src/test/java/com/atom/vpn/
├── VpnGateCsvParserTest.kt    # CSV parsing correctness
├── ConnectionStateTest.kt      # State machine & helpers
└── VpnServerQualityTest.kt     # Quality tier & flag emoji
```

---

## 📜 License

MIT — see [LICENSE](LICENSE) for details.
