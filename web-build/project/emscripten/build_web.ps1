$ErrorActionPreference = "Continue"

# Setup emsdk env
. "../../../emsdk/emsdk_env.ps1"

if (!(Get-Command emcc -ErrorAction SilentlyContinue)) {
    Write-Error "emcc not found in PATH."
    exit 1
}

# Emscripten flags
$COMPILE_FLAGS = @(
    "-O2",
    "-std=c++17",
    "-DOPENGL_ES",
    "-DUSE_VBO",
    "-DEMSCRIPTEN",
    "-Wno-c++11-narrowing",
    "-Wno-register",
    "-Wno-unused-value",
    "-Wno-comment",
    "-sUSE_SDL=2",
    "-sUSE_LIBPNG=1"
)

$LINK_FLAGS = @(
    "-sASYNCIFY=1",
    "-sUSE_SDL=2",
    "-sUSE_LIBPNG=1",
    "-sLEGACY_GL_EMULATION=1",
    "-sGL_UNSAFE_OPTS=0",
    "-lopenal",
    "-lidbfs.js",
    "--js-library", "mc_platform.js",
    "--shell-file", "shell.html",
    "--preload-file", "../../data@/data",
    "-sALLOW_MEMORY_GROWTH=1",
    "-sMAX_WEBGL_VERSION=2",
    "-sASSERTIONS=2",
    "-sEXPORTED_FUNCTIONS=['_main','_idbfsReady','_syncSaves','_malloc','_free']",
    "-sEXPORTED_RUNTIME_METHODS=['FS','ccall','cwrap','lengthBytesUTF8','stringToUTF8']",
    "-O2"
)

# Source files
$basePath = (Resolve-Path "../../src").ProviderPath
$basePathFwd = $basePath.Replace("\", "/")
$rawSources = Get-ChildItem -Path $basePath -Recurse -Filter "*.cpp"
$SOURCES = @()
foreach ($sObj in $rawSources) {
    if ($sObj.FullName -match "main_win|main_android|main_iOS|main_rpi|main_dedicated|AppPlatform_win32|AppPlatform_android|AppPlatform_iOS|AppPlatform_rpi|[/\\]main\.cpp|SoundSystemSL|[/\\]rhi[/\\]") {
        continue
    }
    $SOURCES += $sObj.FullName
}

$objDir = "obj"
if (!(Test-Path $objDir)) { New-Item -ItemType Directory -Path $objDir }

Write-Host "Compiling $($SOURCES.Count) source files..."
$errors = @()
$i = 0
foreach ($srcFile in $SOURCES) {
    $i++
    if ($i % 50 -eq 0) { Write-Host "  $i / $($SOURCES.Count)..." }
    $srcFile = $srcFile.Replace("\", "/")
    $relName = $srcFile.Substring($basePathFwd.Length + 1).Replace("/", "_")
    $objFile = ("obj/" + $relName + ".o").Replace("\", "/")

    $needsRebuild = $true
    if (Test-Path $objFile) {
        if ((Get-Item $srcFile).LastWriteTime -le (Get-Item $objFile).LastWriteTime) {
            $needsRebuild = $false
        }
    }

    if ($needsRebuild) {
        # Invoke emcc directly (no cmd wrapper needed on PowerShell 7+ / cross-platform pwsh)
        $allArgs = @("-c", $srcFile, "-o", $objFile) + $COMPILE_FLAGS
        & emcc @allArgs 2>&1
        if ($LASTEXITCODE -ne 0) {
            $errors += $srcFile
        }
    }
}
if ($errors) {
    Write-Host "Compilation failed for one or more files:" -ForegroundColor Red
    foreach ($f in $errors) { Write-Host "  $f" -ForegroundColor Yellow }
    exit 1
}

Write-Host "Linking Emscripten target..."
$objFiles = Get-ChildItem -Path $objDir -Filter "*.o" | ForEach-Object { $_.FullName.Replace("\", "/") }
$allLinkArgs = $objFiles + @("-o", "index.html") + $LINK_FLAGS
& emcc @allLinkArgs

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed with exit code $LASTEXITCODE"
    exit $LASTEXITCODE
}
else {
    Write-Host "Build complete: index.html, index.js, index.wasm"
}
