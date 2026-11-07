# Project State — Claude VPN

Status date: 2026-11-08  
Repository: https://github.com/techwithrakibofficial/Claude-VPN  
Branch: main  
Latest commit observed: 52a768ea1257aeeee98d09c6cf363a64414e7d12 (Validate WireGuard scripts in Android CI)

## Product goal
A free-first Android VPN client with a premium interface, real encrypted VPN connectivity, legally usable server sources, hybrid server strategy, measured server selection and a Quick Settings connect/disconnect tile. Do not describe the current repository as a complete public VPN service.

## Verified stack
- Kotlin, Jetpack Compose, Android Gradle Plugin 8.7.3, Kotlin 2.0.21
- compileSdk 35, minSdk 26, targetSdk 35, Java 17
- WireGuard Android tunnel library 1.0.20260102 and GoBackend
- GitHub Actions workflow: .github/workflows/android.yml
- OCI Ubuntu ARM64 WireGuard starter scripts in server/

## Implemented in repository
- Single-screen Android dashboard.
- User-selected WireGuard .conf import and parser validation.
- AES-256-GCM encryption of imported profile using Android Keystore.
- WireGuard GoBackend connect/disconnect and Android VPN consent flow.
- Quick Settings TileService; Android 13+ add-tile request path.
- Background-thread/coroutine handling for main connect/disconnect operations.
- OCI server setup and peer-generation shell scripts.
- CI workflow compiles a debug APK, validates shell syntax and uploads an artifact.

## Partial or unverified
- Quick Settings tile exists, but connect opens the Activity and uses the imported profile; it does not select the fastest server.
- Dashboard is a starter screen, not the requested premium glass/motion UI.
- CI workflow exists. A prior conversation reported a successful debug build, but the latest run was not independently verified during this audit.
- No physical-device evidence for routing, DNS/IP/IPv6 leaks, reconnects or tile lifecycle was available.
- Oracle Cloud account exists; VM deployment and real endpoint setup were still user-dependent in the latest recovered project context.

## Not implemented / not verified
- Multi-server inventory, provider integrations and hybrid fleet orchestration.
- Fastest-server measurement and automatic selection.
- Production server provisioning API, peer lifecycle/revocation and multi-user management.
- Account system/backend/database (not required for a local-profile MVP; only add if product scope requires accounts).
- Production signing, release workflow and verified production APK.
- Automated unit/instrumentation/device security test suite.

## Important
The current app requires a valid profile from a server the user owns or is authorized to use. A successful CI build only proves compilation and packaging.
