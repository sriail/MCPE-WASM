#!/usr/bin/env bash
# Emscripten web build script for Linux/macOS
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
EMSDK_PATH="$(cd "$SCRIPT_DIR/../../../emsdk" 2>/dev/null && pwd)" || {
    echo "ERROR: emsdk not found at ../../../emsdk. Run install_deps.sh first." >&2
    exit 1
}

# Source emsdk environment
source "$EMSDK_PATH/emsdk_env.sh"

if ! command -v emcc &>/dev/null; then
    echo "ERROR: emcc not found in PATH." >&2
    exit 1
fi

# Emscripten compile flags
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

# Emscripten link flags
LINK_FLAGS=(
    -sASYNCIFY=1
    -sUSE_SDL=2
    -sUSE_LIBPNG=1
    -sLEGACY_GL_EMULATION=1
    -sGL_UNSAFE_OPTS=0
    -lopenal
    -lidbfs.js
    --js-library mc_platform.js
    --shell-file shell.html
    --preload-file ../../data@/data
    -sALLOW_MEMORY_GROWTH=1
    -sMAX_WEBGL_VERSION=2
    -sASSERTIONS=2
    "-sEXPORTED_FUNCTIONS=['_main','_idbfsReady','_syncSaves','_malloc','_free']"
    "-sEXPORTED_RUNTIME_METHODS=['FS','ccall','cwrap','lengthBytesUTF8','stringToUTF8']"
    -O2
)

SRC_BASE="$(realpath "$SCRIPT_DIR/../../src")"
OBJ_DIR="$SCRIPT_DIR/obj"
mkdir -p "$OBJ_DIR"

# Collect source files, excluding platform-specific ones
SOURCES=()
while IFS= read -r -d '' SRC; do
    case "$SRC" in
        *main_win*|*main_android*|*main_iOS*|*main_rpi*|*main_dedicated*) continue ;;
        *AppPlatform_win32*|*AppPlatform_android*|*AppPlatform_iOS*|*AppPlatform_rpi*) continue ;;
        */main.cpp) continue ;;
        *SoundSystemSL*|*/rhi/*) continue ;;
    esac
    SOURCES+=("$SRC")
done < <(find "$SRC_BASE" -name "*.cpp" -print0)

TOTAL=${#SOURCES[@]}
echo "Compiling $TOTAL source files..."

ERRORS=()
i=0
for SRC in "${SOURCES[@]}"; do
    i=$((i + 1))
    if (( i % 50 == 0 )); then
        echo "  $i / $TOTAL..."
    fi

    # Build a flat object filename from the relative path
    REL="${SRC#$SRC_BASE/}"
    REL_FLAT="${REL//\//_}"
    OBJ="$OBJ_DIR/${REL_FLAT}.o"

    # Incremental: skip if object is newer than source
    if [ -f "$OBJ" ] && [ "$OBJ" -nt "$SRC" ]; then
        continue
    fi

    if ! emcc -c "$SRC" -o "$OBJ" "${COMPILE_FLAGS[@]}" 2>&1; then
        ERRORS+=("$SRC")
    fi
done

if [ ${#ERRORS[@]} -gt 0 ]; then
    echo "Compilation failed for:" >&2
    printf '  %s\n' "${ERRORS[@]}" >&2
    exit 1
fi

echo "Linking Emscripten target..."
OBJ_FILES=("$OBJ_DIR"/*.o)

emcc "${OBJ_FILES[@]}" -o index.html "${LINK_FLAGS[@]}"

if [ $? -eq 0 ]; then
    echo "Build complete: index.html, index.js, index.wasm"
else
    echo "Build failed." >&2
    exit 1
fi
