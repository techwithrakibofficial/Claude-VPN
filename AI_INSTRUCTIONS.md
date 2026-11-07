# AI Instructions — Claude VPN

Last verified: 2026-11-08

## Source of truth
- Repository: https://github.com/techwithrakibofficial/Claude-VPN
- Default branch: main
- Read README.md, AI_HANDOFF.md, PROJECT_STATE.md, USER_REQUIREMENTS.md, ARCHITECTURE.md, BLOCKERS.md and TASKS.md before making changes.
- GitHub code and these documents are the project source of truth. Chat history is supplementary.

## Operating rules
1. Inspect the current branch, latest commit, relevant files and CI before editing.
2. Separate verified implementation from plans, assumptions and untested behavior.
3. Preserve working code. Make the smallest safe change; do not rewrite unrelated files.
4. Use only VPN servers and provider APIs that the project owner is authorized to use. Never scrape, import or distribute leaked credentials or public configs without permission.
5. Never commit passwords, access tokens, SSH private keys, WireGuard private keys/configs, signing keys or keystore files. Use GitHub Actions Secrets for release credentials.
6. Prefer genuinely free, documented resources. Check current official pricing and Always Free eligibility before provisioning. Never create paid resources or upgrade billing without explicit owner approval.
7. A successful compile is not proof of working VPN traffic. Require physical-device tunnel, DNS/IP/IPv6, reconnect, network-switch and Quick Settings tests.
8. Do not claim fastest-server selection until multiple authorized servers are available and measured using a documented health/latency method.
9. Use feature branches and pull requests for code changes. Do not force-push or delete user data.
10. Update PROJECT_STATE.md, TASKS.md, BLOCKERS.md and CHANGELOG.md whenever verified project state changes.

## AI collaboration
- DeepSeek is the primary implementation agent.
- Claude is the independent reviewer/auditor and may provide exact patches.
- ChatGPT can research, audit, plan and use authorized repository tools.
- Keep one active code writer at a time. Reviewers should avoid parallel rewrites.
- If a reviewer has no write permission, provide a patch with exact paths and verification steps for the primary developer to apply.

## Definition of done
A feature is complete only when code, tests, device evidence, documentation and acceptance criteria agree. Debug APKs are not production releases.
