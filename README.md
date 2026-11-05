# Claude VPN

Independent Android VPN client using the WireGuard Android tunnel library.

This project is not affiliated with, endorsed by, or sponsored by Anthropic.

## Functionality
- Kotlin and Jetpack Compose Android app.
- Imports a user-provided WireGuard configuration.
- Uses WireGuard GoBackend for connect/disconnect.
- Android VPN permission consent.
- Quick Settings tile entry point.
- GitHub Actions debug APK build.

## Important
No VPN server is bundled. Import a valid WireGuard configuration from a server you own or are authorized to use. No free public server, fastest-server selection, or anonymity guarantee is claimed.

The imported configuration is stored in app-private storage. Treat it as a secret and never commit it to Git.
