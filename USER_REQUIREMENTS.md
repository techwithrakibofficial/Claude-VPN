# User Requirements — Claude VPN

## Product
- Project name: Claude VPN.
- Professional Android VPN application; free-first and security-conscious.
- Hybrid server architecture, with as many lawful and reliably usable free endpoints as can actually be sourced.
- Premium glass/liquid-glass visual style, motion graphics and a custom app icon.
- Quick Settings / Control Center tile for connect/disconnect; desired behavior is one-tap connection to the fastest suitable verified server and one-tap disconnect.
- Real encrypted VPN traffic only. Never fake a connected state or bundle unauthorized/leaked credentials.

## Constraints
- Prefer genuinely free resources and explain free-tier limits.
- User has an Oracle Cloud account; verify exact tenancy quotas and Always Free eligibility before provisioning.
- Phone-first workflow; use GitHub Actions for cloud builds where practical.
- Keep code changes minimal and avoid repeating completed work.
- DeepSeek is the primary builder; Claude is the independent auditor/fixer. One active code writer at a time.
- Keep GitHub as the single source of truth and maintain clear AI handoffs.

## Security
- Never ask for passwords, OTPs, SSH private keys, WireGuard private keys, signing keys or personal access tokens in chat.
- Use GitHub Secrets for CI signing material.
- Only use server endpoints and provider APIs with explicit authorization and terms permitting the intended use.
- Do not promise anonymity, leak-proof behavior, unlimited free access or global coverage without evidence.

## Scope decisions
- Local profile import is a valid first MVP.
- User accounts, a database and a backend are not mandatory for a single-user/local-profile client. Add them only if required by a chosen provider, provisioning workflow or product decision.
