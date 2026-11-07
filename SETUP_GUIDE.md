# Setup Guide — Claude VPN

## Current starting point
- Repository: https://github.com/techwithrakibofficial/Claude-VPN
- Branch: main
- Oracle Cloud account already exists according to the project owner.
- Do not send credentials or private keys to an AI chat.

## Create one OCI test endpoint
1. Sign in to OCI Console and open Governance & Administration → Tenancy Management → Limits, Quotas and Usage.
2. Confirm the tenancy home region and remaining Always Free quota.
3. Create an instance in the home region using an Ubuntu 22.04/24.04 ARM64 image labelled Always Free Eligible.
4. Start with VM.Standard.A1.Flex at 1 OCPU and 6 GB RAM, subject to the tenancy-wide current limit. Check the Console labels before creating.
5. Use a public subnet and public IPv4. Add the owner's SSH public key; keep the matching private key private.
6. Use a boot volume within the current free allowance.
7. In the VCN Security List or Network Security Group, allow stateful UDP 51820 from 0.0.0.0/0. Restrict SSH TCP 22 to the owner's public IP where practical.
8. SSH into Ubuntu and run:
   sudo apt update
   sudo apt install -y git
   git clone https://github.com/techwithrakibofficial/Claude-VPN.git
   cd Claude-VPN/server
   sudo bash setup-wireguard.sh
9. In OCI, verify UDP 51820 is allowed. Then run:
   sudo bash add-peer.sh YOUR_SERVER_PUBLIC_IP rakib-phone
10. Transfer /root/wg-clients/rakib-phone.conf securely to the owner's phone. It contains a private key; do not upload it to GitHub or send it to an AI.
11. In Claude VPN, import the .conf and grant Android VPN consent.
12. Test public IP, DNS, IPv6, traffic, Wi-Fi/mobile switching, reconnect and tile state. Save evidence without exposing keys.

## Safe information to share with an AI
- OCI region name, VM shape, OS/image, public IPv4, UDP port reachability and WireGuard service status.
- Server public key is not secret, but do not share the server private key.
- Share error messages after removing IPs or identifiers if desired.
- Never share client/server private keys, full .conf files, SSH private keys, passwords, OTPs or OCI API signing keys.

## CI and release
- Pushes/PRs to main trigger Android CI and upload a debug APK artifact.
- Debug APK is for testing, not a production release.
- Production signing must use a dedicated keystore and GitHub Actions Secrets; never commit the keystore or passwords.
- Publish a signed GitHub Release only after real-device acceptance tests pass.

## Free-tier caution
Oracle Always Free availability and quotas can change; confirm the current labels and limits in the Console before creating resources. Do not upgrade billing or create a paid shape to bypass capacity errors without explicit approval.
