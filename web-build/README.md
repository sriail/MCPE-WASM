# Minecraft Web Build

## Dependencies

- **Git** – to clone emsdk
- **Python 3** – used by emsdk
- **Emscripten SDK** – C/C++ to WebAssembly toolchain (installed by the install script)

## Linux / macOS

### Install Dependencies

```bash
chmod +x install_deps.sh
./install_deps.sh
```

### Build

```bash
chmod +x build.sh
./build.sh
```

## Windows

### Install Dependencies

```powershell
.\install_deps.ps1
```

### Build

```powershell
.\build.ps1
```

---

Both install scripts clone emsdk into the parent directory (sibling of `web-build`) if it is not already present, then install and activate the latest toolchain.

Output files are written to `project/emscripten/`: `index.html`, `index.js`, `index.wasm`.
Serve that folder with a local web server to run the game.
