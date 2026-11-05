# Claude VPN

Independent Android VPN client built with Kotlin, Jetpack Compose and the official WireGuard Android tunnel library.

This project is not affiliated with, endorsed by, or sponsored by Anthropic.

## Current functionality
- Android app with a dark, compact VPN dashboard.
- Imports and validates a WireGuard `.conf` profile selected by the user.
- Encrypts the imported profile with AES-256-GCM using an Android Keystore key.
- Uses WireGuard's GoBackend for actual tunnel connect/disconnect.
- Requests Android's VPN consent before connecting.
- Provides a Quick Settings tile and an Android 13+ add-tile prompt.
- Runs tunnel connect/disconnect work off the UI thread.
- Builds a debug APK through GitHub Actions.

## Server availability and important limitations
No VPN servers, server credentials, or provider API are bundled. The app can only connect after the user imports a valid WireGuard profile from a server they own or are authorized to use. A server list, free-server inventory, fastest-server selection across multiple endpoints, automatic server provisioning, and production signing are not yet implemented. The current build must not be represented as a complete free VPN service or as a production release.

Never import leaked credentials or use servers without authorization. Do not commit WireGuard profiles or private keys to this repository.

## Self-hosted WireGuard test server
A starter Oracle Cloud Always Free deployment guide and server scripts are available in [`server/README.md`](server/README.md). This creates one self-managed endpoint for testing; it does not provide a global VPN fleet or automatic multi-user provisioning. Verify OCI Always Free eligibility before creating resources, and never commit generated private keys or client profiles.

## Build
GitHub Actions runs `:app:assembleDebug` on pushes to `main` and uploads `claude-vpn-debug-apk` as a workflow artifact.

A successful CI build proves compilation and APK packaging only. It does not prove that a VPN tunnel works on a physical device. Device tests for traffic routing, DNS/IP leak behavior, reconnects, and Quick Settings lifecycle are still required.

## Security
- WireGuard handles tunnel cryptography.
- Imported profile bytes are encrypted at rest using Android Keystore AES-GCM.
- The app does not promise anonymity or leak-proof behavior until tested on supported devices and networks.
