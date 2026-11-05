# AI Handoff

Project: Claude VPN
Repository: https://github.com/techwithrakibofficial/Claude-VPN
Branch: main
Stack: Kotlin, Jetpack Compose, WireGuard Android tunnel library, GitHub Actions.

## Implemented
- Android Compose dashboard.
- User-selected WireGuard .conf import and validation.
- Imported profile encrypted at rest with Android Keystore AES-GCM.
- WireGuard GoBackend connect/disconnect.
- Android VPN consent flow.
- Connect/disconnect operations off the UI thread.
- Quick Settings tile with Android 13+ add-tile prompt and state refresh.
- CI debug APK workflow.

## Known gaps
- No authorized VPN server inventory, credentials, or provider API is bundled.
- No multi-server profile manager or fastest-server selection yet.
- No physical-device tunnel, traffic-routing, DNS/IP leak, reconnect, or tile lifecycle tests yet.
- UI is a starter dashboard, not the full premium app.
- No production signing or production release.

## Required next work
1. Confirm latest CI run passes after the recent controller, Activity, tile, and documentation changes.
2. Build a secure multi-profile manager for user-authorized WireGuard configs.
3. Add measured server selection only when there are multiple authorized endpoints and a defensible measurement method.
4. Complete UI screens and settings.
5. Test on physical devices and document actual results.
6. Configure production signing through GitHub Actions secrets, never source code.
7. Create a production release only after all acceptance tests pass.

Do not claim a complete free VPN service without a real authorized server fleet. Never invent server locations, speeds, pings, or credentials. Never commit private WireGuard configs.
