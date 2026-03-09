#!/usr/bin/env bash
# Run the Emscripten web build from the project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/project/emscripten"
exec bash "$SCRIPT_DIR/project/emscripten/build_web.sh"
