#!/usr/bin/env bash
set -euo pipefail

if [[ "${EUID}" -ne 0 ]]; then
  echo "Run with sudo: sudo bash add-peer.sh <server-public-ip-or-dns> <peer-name>"
  exit 1
fi

ENDPOINT="${1:-}"
NAME="${2:-}"
if [[ -z "$ENDPOINT" || -z "$NAME" ]]; then
  echo "Usage: sudo bash add-peer.sh <server-public-ip-or-dns> <peer-name>"
  exit 1
fi
if [[ ! "$NAME" =~ ^[a-zA-Z0-9_-]{1,32}$ ]]; then
  echo "Peer name may contain only letters, numbers, underscore and hyphen (max 32 chars)."
  exit 1
fi
if ! systemctl is-active --quiet wg-quick@wg0; then
  echo "wg0 is not active. Run setup-wireguard.sh first."
  exit 1
fi

install -d -m 700 /root/wg-clients
if [[ -e "/root/wg-clients/${NAME}.conf" ]]; then
  echo "A profile named ${NAME} already exists. Choose another name."
  exit 1
fi

PEER_COUNT="$(grep -c '^\[Peer\]' /etc/wireguard/wg0.conf || true)"
OCTET=$((PEER_COUNT + 2))
if (( OCTET > 254 )); then
  echo "No free address remains in 10.66.66.0/24."
  exit 1
fi
CLIENT_IP="10.66.66.${OCTET}"

umask 077
CLIENT_PRIVATE_KEY="$(wg genkey)"
CLIENT_PUBLIC_KEY="$(printf '%s' "$CLIENT_PRIVATE_KEY" | wg pubkey)"
SERVER_PUBLIC_KEY="$(cat /etc/wireguard/server_public.key)"

cat >> /etc/wireguard/wg0.conf <<EOF

[Peer]
# ${NAME}
PublicKey = ${CLIENT_PUBLIC_KEY}
AllowedIPs = ${CLIENT_IP}/32
EOF
chmod 600 /etc/wireguard/wg0.conf
wg syncconf wg0 <(wg-quick strip wg0)

cat > "/root/wg-clients/${NAME}.conf" <<EOF
[Interface]
PrivateKey = ${CLIENT_PRIVATE_KEY}
Address = ${CLIENT_IP}/32
DNS = 1.1.1.1
MTU = 1280

[Peer]
PublicKey = ${SERVER_PUBLIC_KEY}
Endpoint = ${ENDPOINT}:51820
AllowedIPs = 0.0.0.0/0, ::/0
PersistentKeepalive = 25
EOF
chmod 600 "/root/wg-clients/${NAME}.conf"

echo "Created client profile: /root/wg-clients/${NAME}.conf"
echo "Transfer this file securely to the intended device. It contains a private key."
echo "Do not email it, publish it, or commit it to GitHub."
if command -v qrencode >/dev/null 2>&1; then
  echo
  qrencode -t ansiutf8 < "/root/wg-clients/${NAME}.conf"
fi
