# Blockers and Risks — Claude VPN

| ID | Priority | Status | Blocker / evidence | Required action |
|---|---|---|---|---|
| B-01 | Critical | User action | No deployed authorized VPN endpoint was verified; README says no server credentials/provider API are bundled. | Owner creates and verifies OCI VM or supplies another authorized endpoint. |
| B-02 | Critical | User + AI | No physical-device proof of real traffic, DNS/IP/IPv6 behavior or reconnect. | Import a private profile on device and run acceptance tests. |
| B-03 | High | Missing feature | One imported profile only; no server inventory or provider integrations. | Define lawful server sources and implement multi-profile/provider layer. |
| B-04 | High | Missing feature | Fastest-server selection cannot work without multiple endpoints and measurements. | Add endpoints, health checks and measured selection. |
| B-05 | High | Missing feature | Premium glass UI, motion and original launcher icon are not implemented in the current screen. | Complete product UI design and implementation. |
| B-06 | High | Missing feature | No release signing or production release workflow is present in the inspected CI file. | Configure signing credentials securely and add release pipeline. |
| B-07 | Medium | Unverified | Latest GitHub Actions run was not independently confirmed; connector status returned no status records and its commit-run lookup only covers PR-triggered runs. | Check Actions UI and retain run URL/logs as evidence. |
| B-08 | Medium | Code review | Tunnel state is held in memory; process/system lifecycle and UI refresh need stronger handling. | Add observable state and lifecycle tests. |
| B-09 | Medium | Code review | Server scripts do not provide peer revocation or production-grade provisioning; client private keys are generated on the server. | Add revocation and move client key generation to device-side provisioning. |
| B-10 | Medium | Code review | Server config enables IPv4 forwarding only while generated client profile routes IPv4 and IPv6 defaults. | Implement IPv6 routing correctly or disable unsupported IPv6 route and test. |

Do not mark a blocker resolved without a commit, test result or user-confirmed resource setup.
