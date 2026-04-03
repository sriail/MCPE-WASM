#!/usr/bin/env bash
# setup-ubuntu.sh – Build and serve MCPE-WASM on Ubuntu
# Usage: bash setup-ubuntu.sh
# Tested on Ubuntu 20.04 / 22.04 / 24.04

set -euo pipefail

REPO_DIR="$HOME/MCPE-WASM"
EMSDK_DIR="$HOME/emsdk"   # placed at the same level as MCPE-WASM under $HOME (required by build_web.ps1)

# ── 1. System packages ────────────────────────────────────────────────────────
echo "==> Installing system packages..."
sudo apt-get update -y
sudo apt-get install -y \
    build-essential git curl python3 python3-pip \
    apt-transport-https software-properties-common

# ── 2. Node.js 20.x + npm ─────────────────────────────────────────────────────
if ! command -v node &>/dev/null; then
    echo "==> Installing Node.js 20.x..."
    curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
    sudo apt-get install -y nodejs
else
    echo "==> Node.js already installed: $(node --version)"
fi

if ! command -v npm &>/dev/null; then
    echo "==> npm not found – attempting to install..."
    sudo apt-get install -y npm || {
        echo "ERROR: npm could not be installed automatically."
        echo "  Please install it manually then re-run this script:"
        echo "    sudo apt-get install -y npm"
        exit 1
    }
fi

# ── 3. PowerShell Core (needed to run the repo's build scripts) ───────────────
if ! command -v pwsh &>/dev/null; then
    echo "==> Installing PowerShell Core..."
    # Official Microsoft repo for Ubuntu
    UBUNTU_VER=$(. /etc/os-release && echo "$VERSION_ID")
    wget -q "https://packages.microsoft.com/config/ubuntu/${UBUNTU_VER}/packages-microsoft-prod.deb" \
        -O /tmp/packages-microsoft-prod.deb
    sudo dpkg -i /tmp/packages-microsoft-prod.deb
    sudo apt-get update -y
    sudo apt-get install -y powershell
else
    echo "==> PowerShell already installed: $(pwsh --version)"
fi

# ── 4. Emscripten SDK ─────────────────────────────────────────────────────────
if [ -d "$EMSDK_DIR" ]; then
    echo "==> emsdk directory already exists at $EMSDK_DIR – skipping clone."
    echo "    To update: cd $EMSDK_DIR && git pull && ./emsdk install latest && ./emsdk activate latest"
else
    echo "==> Cloning emsdk into $EMSDK_DIR ..."
    git clone https://github.com/emscripten-core/emsdk.git "$EMSDK_DIR"
fi

echo "==> Installing and activating latest emsdk toolchain..."
cd "$EMSDK_DIR"
./emsdk install latest
./emsdk activate latest
# shellcheck disable=SC1091
source ./emsdk_env.sh

# ── 5. Clone repository & check out PR #5 branch ─────────────────────────────
if [ -d "$REPO_DIR/.git" ]; then
    echo "==> Repository already exists at $REPO_DIR – skipping clone."
    cd "$REPO_DIR"
    git fetch origin
else
    echo "==> Cloning MCPE-WASM..."
    git clone https://github.com/sriail/MCPE-WASM.git "$REPO_DIR"
    cd "$REPO_DIR"
fi

echo "==> Checking out PR #5 (infinite-world) branch..."
git fetch origin pull/5/head:infinite-world 2>/dev/null || true
git checkout infinite-world

# ── 6. Build using the repo's PowerShell build script ────────────────────────
BUILD_SCRIPT="$REPO_DIR/web-build/project/emscripten/build_web.ps1"

if [ ! -f "$BUILD_SCRIPT" ]; then
    echo ""
    echo "WARNING: Build script not found at $BUILD_SCRIPT"
    echo "  The checked-out branch may not include it."
    echo "  Skipping build – serve step will still work if build outputs already exist."
    echo ""
else
    echo "==> Building WASM output via build_web.ps1..."
    cd "$REPO_DIR/web-build/project/emscripten"
    # emsdk_env must be sourced; pass EMSDK env vars so pwsh can locate emcc
    EMSDK="$EMSDK_DIR" \
    EMSDK_PYTHON="$(command -v python3)" \
    pwsh -NoProfile -ExecutionPolicy Bypass -File build_web.ps1
    echo "==> Build complete. Outputs in $REPO_DIR/web-build/project/emscripten/"
fi

# ── 7. Install Node.js dependencies & start server ───────────────────────────
cd "$REPO_DIR"
echo "==> Installing Node.js dependencies (express, ws)..."
npm install

echo ""
echo "==> Starting server..."
echo "    Open http://localhost:8080 in your browser."
echo "    Press Ctrl+C to stop."
node server.js