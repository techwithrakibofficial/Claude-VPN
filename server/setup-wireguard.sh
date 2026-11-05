#!/usr/bin/env bash
set -euo pipefail

if [[ "${EUID}" -ne 0 ]]; then
  echo "Run with sudo: sudo bash setup-wireguard.sh"
  exit 1
fi

if [[ -f /etc/wireguard/wg0.conf ]]; then
  echo "WireGuard already appears to be configured at /etc/wireguard/wg0.conf."
  echo "Refusing to overwrite an existing server configuration."
  exit 1
fi

export DEBIAN_FRONTEND=noninteractive
apt-get update
apt-get install -y wireguard iptables qrencode

install -d -m 700 /etc/wireguard
umask 077
wg genkey | tee /etc/wireguard/server_private.key | wg pubkey > /etc/wireguard/server_public.key
chmod 600 /etc/wireguard/server_private.key
chmod 644 /etc/wireguard/server_public.key

WAN_IF="$(ip -o -4 route show to default | awk '{print $5; exit}')"
if [[ -z "$WAN_IF" ]]; then
  echo "Could not detect the public network interface."
  exit 1
fi

SERVER_PRIVATE_KEY="$(cat /etc/wireguard/server_private.key)"
cat > /etc/wireguard/wg0.conf <<EOF
[Interface]
Address = 10.66.66.1/24
ListenPort = 51820
PrivateKey = ${SERVER_PRIVATE_KEY}
SaveConfig = false
PostUp = iptables -A FORWARD -i %i -j ACCEPT; iptables -A FORWARD -o %i -j ACCEPT; iptables -t nat -A POSTROUTING -o ${WAN_IF} -j MASQUERADE
PostDown = iptables -D FORWARD -i %i -j ACCEPT; iptables -D FORWARD -o %i -j ACCEPT; iptables -t nat -D POSTROUTING -o ${WAN_IF} -j MASQUERADE
EOF
chmod 600 /etc/wireguard/wg0.conf

cat > /etc/sysctl.d/99-claude-vpn-wireguard.conf <<'EOF'
net.ipv4.ip_forward=1
EOF
sysctl --system >/dev/null

systemctl enable --now wg-quick@wg0

echo
echo "WireGuard server is installed and running."
echo "Server public key:"
cat /etc/wireguard/server_public.key
echo
echo "Listen port: UDP 51820"
echo "Detected WAN interface: $WAN_IF"
echo
echo "NEXT: In the OCI VCN security list or Network Security Group, add a STATEFUL ingress rule:"
echo "Source CIDR: 0.0.0.0/0 | IP protocol: UDP | Destination port: 51820"
echo "Then create a client profile using: sudo bash add-peer.sh <server-public-ip-or-dns> <peer-name>"
