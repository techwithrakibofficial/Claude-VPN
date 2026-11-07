# AI Handoff — Claude VPN

Audit date: 2026-11-08  
Repository: https://github.com/techwithrakibofficial/Claude-VPN  
Branch: main  
Latest application-source commit observed before migration docs: 52a768ea1257aeeee98d09c6cf363a64414e7d12

## Read first
1. AI_INSTRUCTIONS.md
2. PROJECT_STATE.md
3. USER_REQUIREMENTS.md
4. ARCHITECTURE.md
5. BLOCKERS.md
6. TASKS.md
7. SETUP_GUIDE.md
8. README.md

## Verified implementation
- Kotlin + Jetpack Compose Android app.
- Imports and validates one user-selected WireGuard .conf profile.
- Encrypts profile bytes with AES-256-GCM using Android Keystore.
- Uses WireGuard GoBackend for actual tunnel connect/disconnect.
- Uses Android VPN consent flow.
- Includes a Quick Settings TileService and Android 13+ add-tile request.
- Includes OCI starter scripts for one self-managed WireGuard endpoint.
- Android CI validates shell syntax, builds a debug APK and uploads an artifact.

## Not a production VPN service yet
No server fleet, provider API, bundled server credentials, multi-server inventory, fastest-server selection, account/backend system, production signing or production release is verified. The tile is not a fastest-server selector. Physical-device routing and leak tests remain outstanding.

## User-owned dependency
The project owner has an Oracle Cloud account. The VM, firewall rule, WireGuard service and client profile still require owner-side OCI setup and secure device testing. Never ask for passwords or private keys. Ask only for non-secret status details such as region, VM shape, public IP and port reachability.

## Current audit evidence
- Default branch: main; repository is public.
- One branch was returned by branch search: main.
- Latest observed code commit: 52a768ea1257aeeee98d09c6cf363a64414e7d12.
- CI workflow definition exists at .github/workflows/android.yml. Latest run status was not independently verified during this audit.
- Earlier project conversation reported a successful CI debug build; treat that as historical context, not current run evidence.

## Next actions
Follow TASKS.md in priority order. First deploy and verify one authorized endpoint, then run physical-device acceptance tests. Only after that expand to multiple lawful servers and measured selection. Do not start production signing/release until the acceptance criteria pass.

## AI collaboration
DeepSeek is the primary implementation agent; Claude is the independent reviewer. Keep one active code writer at a time. Any reviewer without write access must provide exact patches and test steps. Keep all progress in GitHub documentation and update the state files after verified changes.
