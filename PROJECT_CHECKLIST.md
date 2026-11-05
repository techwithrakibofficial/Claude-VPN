# Project checklist

## Implemented in source
- [x] Kotlin Android app foundation
- [x] Compose VPN dashboard
- [x] WireGuard tunnel dependency and GoBackend integration
- [x] User-imported WireGuard configuration parsing
- [x] Encrypt imported profile at rest with Android Keystore AES-GCM
- [x] Android VPN consent flow
- [x] Connect/disconnect operations moved off the UI thread
- [x] Quick Settings tile service
- [x] Android 13+ request-to-add tile flow
- [x] Tile status refresh and asynchronous disconnect

## Still required before calling this a complete VPN service
- [ ] Confirm latest source revision compiles in CI
- [ ] Install and test on physical Android devices
- [ ] Verify real tunnel connection with an authorized WireGuard endpoint
- [ ] Verify traffic routing, DNS/IP behavior, reconnect, and failure handling
- [ ] Verify Quick Settings add/toggle behavior across Android versions and OEMs
- [ ] Add multi-profile/server inventory backed by authorized provider endpoints
- [ ] Implement measured fastest-server selection across those endpoints
- [ ] Add full premium UI, onboarding, settings, diagnostics, and privacy screens
- [ ] Configure production signing securely
- [ ] Create and verify a production release APK

## Explicit limitations
No server fleet, free server credentials, or provider API is included. Without authorized server endpoints, this app cannot independently provide free VPN service. The debug APK is not a production release.

Never mark runtime tests passed without evidence.
