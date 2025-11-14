#!/bin/bash
set -e

# === Cấu hình các host & port ===
# FE_IP="100.89.19.53"
# FE_HOST="fe.local.test"
# FE_PORT=3000
BE_IP="100.89.19.53"
BE_HOST="be.local.test"
BE_PORT=8080
CADDY_ADMIN_PORT=2020   # đổi cổng admin

# === Đường dẫn repo, nơi muốn đặt Caddyfile ===
REPO_DIR="$(pwd)"
CADDYFILE_DIR="$REPO_DIR/caddy"
mkdir -p "$CADDYFILE_DIR"
CADDYFILE_PATH="$CADDYFILE_DIR/Caddyfile"

# === Tạo Caddyfile ===
cat > "$CADDYFILE_PATH" <<EOL
{
    admin 127.0.0.1:$CADDY_ADMIN_PORT
}

# FE
# $FE_HOST {
#     reverse_proxy http://$FE_IP:$FE_PORT
#     tls internal
#     encode gzip
# }

# BE
$BE_HOST {
    reverse_proxy http://$BE_IP:$BE_PORT
    tls internal
    encode gzip
}
EOL

echo "==> Caddyfile created at $CADDYFILE_PATH"

# === Thêm host entries vào /etc/hosts ===
# "$FE_IP $FE_HOST"
HOSTS_LINES=(
"$BE_IP $BE_HOST"
)

for line in "${HOSTS_LINES[@]}"; do
    if ! grep -q "$line" /etc/hosts; then
        echo "==> Adding host entry: $line"
        echo "$line" | sudo tee -a /etc/hosts
    fi
done

# === Dừng Caddy đang chạy (nếu có) ===
sudo pkill caddy || true

# === Chạy Caddy với Caddyfile mới ===
echo "==> Starting Caddy..."
sudo caddy run --config "$CADDYFILE_PATH"
