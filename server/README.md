# Oracle Cloud WireGuard server (starter deployment)

This folder prepares one self-managed WireGuard endpoint for the Claude VPN Android client. It is a starter server for controlled testing, not a multi-user commercial VPN platform.

## Free-tier constraints

Oracle's current Always Free allowance includes a total of 2 OCPUs and 12 GB RAM for Ampere A1 in the tenancy's home region, plus 10 TB/month outbound transfer. Availability depends on the tenancy and region. Idle instances may be reclaimed. Check the Console's **Limits, Quotas and Usage** and ensure every resource is labelled **Always Free** before creating it.

Official references:
- https://docs.oracle.com/en-us/iaas/Content/FreeTier/freetier_topic-Always_Free_Resources.htm
- https://docs.oracle.com/en-us/iaas/Content/FreeTier/freetier.htm

## 1. Create the VM in OCI

In OCI Console:

1. Open **Compute → Instances → Create instance**.
2. Choose the tenancy's **home region**.
3. Select an Ubuntu **Always Free Eligible** image, preferably Ubuntu 22.04 or 24.04 ARM64.
4. Select **VM.Standard.A1.Flex** and allocate no more than the free allowance shared across the tenancy: 1 OCPU and 6 GB RAM is a reasonable starting point.
5. Put it in a public subnet and assign a public IPv4 address.
6. Add your SSH public key. Keep the matching private key private.
7. Before creating, check that the shape and boot volume show Always Free eligibility. Do not create paid resources or upgrade the account just to bypass capacity errors.

If A1 capacity is unavailable, do not choose a paid shape. Try later. The small AMD micro shape may be a fallback for a basic single-user test, but performance will be limited.

## 2. Allow the WireGuard UDP port

In the instance's VCN subnet **Security List** or attached **Network Security Group**, add a stateful ingress rule:

- Source CIDR: `0.0.0.0/0`
- IP protocol: UDP (17)
- Destination port: `51820`

Keep SSH (TCP 22) restricted to your own public IP where practical. Do not open all ports.

## 3. Install WireGuard

Connect over SSH using the Ubuntu account and run:

```bash
sudo apt update
sudo apt install -y git
git clone https://github.com/techwithrakibofficial/Claude-VPN.git
cd Claude-VPN/server
sudo bash setup-wireguard.sh
```

The script installs WireGuard, enables IPv4 forwarding, configures NAT and starts `wg0`. It prints the server's public key. It refuses to overwrite an existing `wg0.conf`.

## 4. Create a private client profile

Find the instance's public IPv4 address in OCI, then run on the server:

```bash
sudo bash add-peer.sh YOUR_SERVER_PUBLIC_IP rakib-phone
```

The profile is written to `/root/wg-clients/rakib-phone.conf`. It contains a private key. Transfer it securely to the intended phone, import it into Claude VPN, and delete temporary copies after import. Never upload it to GitHub or share it publicly.

## 5. Verify before relying on it

On the server:

```bash
sudo systemctl status wg-quick@wg0
sudo wg show
sudo ss -lunp | grep 51820
```

On the phone, import the profile and connect. Verify the public IP changes, check DNS and IPv6 leak behavior, test real traffic, and repeat after Wi-Fi/mobile-data changes. A successful service status alone does not prove client traffic works.

## Important limitations

- One VM is one VPN endpoint, not a global server fleet.
- The starter profile script creates a server-generated client private key for initial testing. A production design should generate client keys on-device and expose a secure peer-provisioning API.
- The Android app currently imports a WireGuard `.conf`; automatic provisioning, multi-server inventory, measured fastest-server selection, account management, and production signing still need implementation.
- Do not advertise this as a public unlimited free VPN. A free-tier VM has resource, bandwidth, capacity and acceptable-use limits.
- Never commit server or client private keys, generated configs, SSH keys, OCI credentials, or API tokens.
