#!/usr/bin/env bash
# build_web.sh — Linux Emscripten build script for MCPE-WASM
#
# Usage:
#   ./build_web.sh            # incremental compile + link
#   ./build_web.sh --clean    # wipe obj/ and out/, then full rebuild
#   ./build_web.sh --serve    # build then serve out/ with COOP/COEP headers
#
# Requirements:
#   - emsdk activated in the current shell (run emsdk_env.sh first), OR
#     set EMSDK_ROOT to the directory that contains emsdk_env.sh.
#
# Networking note (multiplayer):
#   Raw UDP is not available in browsers.  RakNet's UDP-based multiplayer is
#   therefore disabled for the web target.  The build compiles and links
#   cleanly in single-player mode.  If you need multiplayer in the future,
#   enable -sPROXY_POSIX_SOCKETS=1 and run websocket_to_posix_proxy beside
#   the web server; then also add -DEMSCRIPTEN_WEBSOCKET_SUPPORT to CFLAGS.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_DIR="$SCRIPT_DIR/../../src"
DATA_DIR="$SCRIPT_DIR/../../data"
OUT_DIR="$SCRIPT_DIR/../../../out"   # web-build/out/
OBJ_DIR="$SCRIPT_DIR/obj"

# ── Detect emcc ─────────────────────────────────────────────────────────────
if ! command -v emcc &>/dev/null; then
    # Try sourcing emsdk_env.sh if EMSDK_ROOT is set
    if [ -n "${EMSDK_ROOT:-}" ] && [ -f "$EMSDK_ROOT/emsdk_env.sh" ]; then
        # shellcheck source=/dev/null
        source "$EMSDK_ROOT/emsdk_env.sh" --build=Release 2>/dev/null || true
    fi
    if ! command -v emcc &>/dev/null; then
        echo "ERROR: emcc not found in PATH."
        echo "  Activate emsdk first:  source /path/to/emsdk/emsdk_env.sh"
        exit 1
    fi
fi
EMCC="emcc"
echo "Using emcc: $(command -v emcc)"
emcc --version | head -1

# ── Option parsing ───────────────────────────────────────────────────────────
DO_CLEAN=0
DO_SERVE=0
for arg in "$@"; do
    case "$arg" in
        --clean) DO_CLEAN=1 ;;
        --serve) DO_SERVE=1 ;;
        *) echo "Unknown option: $arg"; exit 1 ;;
    esac
done

if [ "$DO_CLEAN" -eq 1 ]; then
    echo "Cleaning obj/ and out/ ..."
    rm -rf "$OBJ_DIR" "$OUT_DIR"
fi

mkdir -p "$OBJ_DIR" "$OUT_DIR"

# ── Compile flags ────────────────────────────────────────────────────────────
COMPILE_FLAGS=(
    -O2
    -std=c++17
    -DOPENGL_ES
    -DUSE_VBO
    -DEMSCRIPTEN
    -Wno-c++11-narrowing
    -Wno-register
    -Wno-unused-value
    -Wno-comment
    -sUSE_SDL=2
    -sUSE_LIBPNG=1
)

# ── Link flags ───────────────────────────────────────────────────────────────
LINK_FLAGS=(
    -sASYNCIFY=1
    -sUSE_SDL=2
    -sUSE_LIBPNG=1
    -sLEGACY_GL_EMULATION=1
    -sGL_UNSAFE_OPTS=0
    -lopenal
    -lidbfs.js
    --js-library "$SCRIPT_DIR/mc_platform.js"
    --shell-file  "$SCRIPT_DIR/shell.html"
    --preload-file "$DATA_DIR@/data"
    -sALLOW_MEMORY_GROWTH=1
    -sMAX_WEBGL_VERSION=2
    -sASSERTIONS=2
    "-sEXPORTED_FUNCTIONS=['_main','_idbfsReady','_syncSaves','_malloc','_free']"
    "-sEXPORTED_RUNTIME_METHODS=['FS','ccall','cwrap','lengthBytesUTF8','stringToUTF8']"
    -O2
)

# ── Collect source files (mirrors the PowerShell exclusion list) ─────────────
EXCLUDE_PATTERN="main_win|main_android|main_iOS|main_rpi|main_dedicated"
EXCLUDE_PATTERN+="|AppPlatform_win32|AppPlatform_android|AppPlatform_iOS|AppPlatform_rpi"
EXCLUDE_PATTERN+="|/main\.cpp|SoundSystemSL|/rhi/"

mapfile -d '' SOURCES < <(
    find "$SRC_DIR" -name '*.cpp' -print0 | \
    grep -zZEv "$EXCLUDE_PATTERN"
)

echo "Found ${#SOURCES[@]} source files."

# ── Incremental compile ───────────────────────────────────────────────────────
ERRORS=()
TOTAL=${#SOURCES[@]}
IDX=0
for SRC in "${SOURCES[@]}"; do
    IDX=$((IDX + 1))
    # Map full path → flat object name (replicate PowerShell logic)
    REL="${SRC#$SRC_DIR/}"
    REL_FLAT="${REL//\//_}"
    OBJ="$OBJ_DIR/${REL_FLAT}.o"

    # Skip if up-to-date
    if [ -f "$OBJ" ] && [ "$OBJ" -nt "$SRC" ]; then
        continue
    fi

    if (( IDX % 50 == 0 )); then
        echo "  Compiling $IDX / $TOTAL ..."
    fi

    if ! "$EMCC" "${COMPILE_FLAGS[@]}" -c "$SRC" -o "$OBJ" 2>&1; then
        ERRORS+=("$SRC")
    fi
done

if [ "${#ERRORS[@]}" -gt 0 ]; then
    echo "Compilation FAILED for:"
    printf '  %s\n' "${ERRORS[@]}"
    exit 1
fi

echo "Compilation complete.  Linking..."

# ── Link ──────────────────────────────────────────────────────────────────────
mapfile -d '' OBJ_FILES < <(find "$OBJ_DIR" -name '*.o' -print0)

"$EMCC" "${OBJ_FILES[@]}" \
    -o "$OUT_DIR/index.html" \
    "${LINK_FLAGS[@]}"

echo ""
echo "Build complete:"
echo "  $OUT_DIR/index.html"
echo "  $OUT_DIR/index.js"
echo "  $OUT_DIR/index.wasm"

# ── Optional local server with COOP/COEP headers ────────────────────────────
if [ "$DO_SERVE" -eq 1 ]; then
    echo ""
    echo "Starting local server on http://localhost:8000 ..."
    echo "  (SharedArrayBuffer requires COOP/COEP — headers are set automatically)"
    cd "$OUT_DIR"
    python3 - "$OUT_DIR" <<'PYSERVER'
import sys, http.server, socketserver

class COEPHandler(http.server.SimpleHTTPRequestHandler):
    def end_headers(self):
        self.send_header("Cross-Origin-Opener-Policy",   "same-origin")
        self.send_header("Cross-Origin-Embedder-Policy", "require-corp")
        super().end_headers()
    def log_message(self, fmt, *args):
        pass  # suppress per-request noise

PORT = 8000
with socketserver.TCPServer(("", PORT), COEPHandler) as httpd:
    print(f"Serving at http://localhost:{PORT}")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\nServer stopped.")
PYSERVER
fi
