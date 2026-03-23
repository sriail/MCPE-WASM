#!/usr/bin/env bash
# Install Emscripten SDK for Minecraft Web Build
# Run this from the web-build folder before building.
# Requires: git, python3

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_ROOT="$SCRIPT_DIR"
# emsdk at parent (sibling of web-build)
EMSDK_PATH="$(dirname "$BUILD_ROOT")/emsdk"

if [ ! -d "$EMSDK_PATH" ]; then
    echo "Cloning emsdk into $EMSDK_PATH ..."
    git clone https://github.com/emscripten-core/emsdk.git "$EMSDK_PATH"
fi

cd "$EMSDK_PATH"

echo "Installing latest emsdk toolchain (this may take several minutes)..."
./emsdk install latest

echo "Activating latest emsdk toolchain..."
./emsdk activate latest

echo ""
echo "Dependencies installed successfully."
echo "Run: ./build.sh"
