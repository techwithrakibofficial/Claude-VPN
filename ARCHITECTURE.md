# Architecture — Claude VPN

## Android client
- MainActivity.kt: Compose dashboard, document picker, VPN consent flow and tile-add request.
- VpnController.kt: WireGuard GoBackend lifecycle, profile parsing, AES-GCM encryption/decryption and in-memory tunnel state.
- ClaudeVpnTileService.kt: Quick Settings tile state and connect/disconnect entry point.
- WireGuard tunnel implementation is supplied by com.wireguard.android:tunnel; its merged manifest declares GoBackend$VpnService.

## Profile flow
1. User selects a WireGuard .conf file.
2. Config.parse validates it.
3. Raw bytes are encrypted with AES/GCM/NoPadding using a 256-bit Android Keystore key.
4. Encrypted bytes are stored in app-private files as imported.conf.enc.
5. On connect, the app decrypts and parses the profile, requests Android VPN consent if needed, then asks GoBackend to bring the tunnel UP.

Only one imported profile is currently stored. There is no profile inventory or provider API.

## Quick Settings flow
- TileService reflects GoBackend state while listening.
- Disconnect is requested from the tile.
- Connect launches MainActivity so profile checks and Android VPN consent can be handled.
- This is not a fastest-server tile: there is no server inventory or selection algorithm.

## Self-hosted server
- server/setup-wireguard.sh installs WireGuard, enables IPv4 forwarding, configures IPv4 NAT and starts wg0 on UDP 51820.
- server/add-peer.sh creates a peer, appends its public key to wg0.conf and writes a client .conf containing a private key.
- server/README.md documents OCI Always Free setup and manual secure transfer.

## CI
.github/workflows/android.yml installs JDK 17 and Android SDK 35, syntax-checks server scripts, runs :app:assembleDebug and uploads the debug APK artifact. It does not sign or publish a production release.
