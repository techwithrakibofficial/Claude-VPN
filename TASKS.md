# Tasks — Claude VPN

Priority order; update status only with evidence.

## P0 — Real connection proof
- [ ] In OCI Console, verify home region, quotas and Always Free eligibility.
- [ ] Create one eligible Ubuntu ARM64 VM within the current tenancy allowance.
- [ ] Configure public IPv4 and allow only required inbound UDP 51820; restrict SSH 22 to the owner's IP where practical.
- [ ] Run server/setup-wireguard.sh and verify wg-quick@wg0, UDP listener and wg show.
- [ ] Run server/add-peer.sh for the owner's phone and transfer the client profile securely.
- [ ] Import the profile in the Android app and verify real traffic.
- [ ] Test public IPv4, DNS, IPv6 behavior, Wi-Fi/mobile switching, reconnect, disconnect and Quick Settings state on a physical Android device.

## P1 — Client reliability and security
- [ ] Add observable tunnel state and refresh state after process recreation/system VPN changes.
- [ ] Handle tile connect/disconnect failures visibly; avoid silently swallowing errors.
- [ ] Add profile list, rename, delete and safe replacement/backup behavior.
- [ ] Review file size limits, atomic encrypted writes, key invalidation and error handling.
- [ ] Decide and implement IPv6 routing or explicitly prevent unsupported IPv6 routing.
- [ ] Add server peer revocation and secure client-key provisioning; prefer generating client private keys on-device.

## P2 — Product features
- [ ] Design premium glass dashboard, motion system, settings and original launcher/tile icon.
- [ ] Define the lawful hybrid server/provider model and document provider terms/limits.
- [ ] Add multiple authorized server profiles/endpoints.
- [ ] Implement measured health/latency selection only after multiple endpoints exist.
- [ ] Decide whether accounts/backend are actually required; do not add them by default.

## P3 — Release readiness
- [ ] Add unit tests, instrumentation tests and documented manual device acceptance tests.
- [ ] Verify Android 13+ tile add flow and older-version manual tile instructions.
- [ ] Verify notification, Always-on VPN compatibility, battery behavior and lifecycle.
- [ ] Add release build workflow and signing via GitHub Secrets.
- [ ] Build, install and verify a signed release APK; publish GitHub Release only after acceptance tests.
- [ ] Update all project state documents and release notes.
