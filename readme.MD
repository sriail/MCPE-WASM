# MCPE-WASM

Minecraft Pocket Edition compiled to WebAssembly via Emscripten.

---

## Ubuntu Setup, Build & Serve

### Quick start (one script)

```bash
git clone https://github.com/sriail/MCPE-WASM.git ~/MCPE-WASM
cd ~/MCPE-WASM
bash setup-ubuntu.sh
```

`setup-ubuntu.sh` will:
1. Install system packages (`build-essential`, `git`, `python3`, …)
2. Install **Node.js 20** and **npm**
3. Install **PowerShell Core** (`pwsh`) – required by the repo's build scripts
4. Clone / update **emsdk** into `~/emsdk` and activate the latest toolchain
5. Check out the **PR #5 `infinite-world`** branch
6. Compile C++ → WebAssembly via `web-build/project/emscripten/build_web.ps1`
7. Run `npm install` and start the WebSocket + static-file server on port 8080

---

### Step-by-step commands

#### 1 – Install dependencies

```bash
sudo apt-get update -y
sudo apt-get install -y build-essential git curl python3 python3-pip

# Node.js 20 + npm
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt-get install -y nodejs

# PowerShell Core (for the repo's build scripts)
UBUNTU_VER=$(. /etc/os-release && echo "$VERSION_ID")
wget -q "https://packages.microsoft.com/config/ubuntu/${UBUNTU_VER}/packages-microsoft-prod.deb" \
    -O /tmp/packages-microsoft-prod.deb
sudo dpkg -i /tmp/packages-microsoft-prod.deb
sudo apt-get update -y && sudo apt-get install -y powershell
```

#### 2 – Install Emscripten SDK

> **Note:** emsdk must sit at `~/emsdk` (a sibling of `~/MCPE-WASM`).  
> That is where `web-build/project/emscripten/build_web.ps1` expects to find it.

```bash
# Skip clone if directory already exists
if [ ! -d ~/emsdk ]; then
    git clone https://github.com/emscripten-core/emsdk.git ~/emsdk
fi

cd ~/emsdk
./emsdk install latest
./emsdk activate latest
source ./emsdk_env.sh

# Persist for future shells
echo 'source ~/emsdk/emsdk_env.sh' >> ~/.bashrc
```

#### 3 – Clone repo & check out PR #5

```bash
git clone https://github.com/sriail/MCPE-WASM.git ~/MCPE-WASM
cd ~/MCPE-WASM

git fetch origin pull/5/head:infinite-world
git checkout infinite-world
```

#### 4 – Build the WASM output

> The build system uses **PowerShell scripts that call `emcc` directly** – there is  
> **no `CMakeLists.txt`** at the repo root.  Do not run `emcmake cmake ..`.

```bash
cd ~/MCPE-WASM/web-build/project/emscripten
pwsh -NoProfile -ExecutionPolicy Bypass -File build_web.ps1
```

Output files: `index.html`, `index.js`, `index.wasm` in the same directory.

#### 5 – Install Node.js dependencies

```bash
cd ~/MCPE-WASM
npm install
```

This installs `express` and `ws` (declared in `package.json`).

#### 6 – Start the server

```bash
cd ~/MCPE-WASM
node server.js
```

Open **http://localhost:8080** in your browser.  
WebSocket connections for world data are handled automatically at `ws://localhost:8080`.

> **Tip:** The server binds to `127.0.0.1` by default. To expose it on all
> network interfaces (e.g. to access from another machine):
> ```bash
> HOST=0.0.0.0 node server.js
> ```

---

## Troubleshooting

### `Error: Cannot find module '…/server.js'`

`server.js` must exist in the repo root before you run `node server.js`.  
It is committed to this branch; if it is missing, pull the latest changes:

```bash
cd ~/MCPE-WASM
git pull origin infinite-world
```

### `CMake Error: The source directory … does not appear to contain CMakeLists.txt`

This repo does **not** use CMake.  Delete any stray `build/` directory and use
the PowerShell build script instead (step 4 above):

```bash
rm -rf ~/MCPE-WASM/build   # remove any leftover cmake attempt
cd ~/MCPE-WASM/web-build/project/emscripten
pwsh -NoProfile -ExecutionPolicy Bypass -File build_web.ps1
```

### `npm: command not found`

Node.js and npm must be installed before running `npm install`:

```bash
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt-get install -y nodejs
node --version && npm --version   # verify
```

### `emsdk directory already exists` warning

The script skips re-cloning if `~/emsdk` exists.  To update to the latest
toolchain:

```bash
cd ~/emsdk
git pull
./emsdk install latest
./emsdk activate latest
source ./emsdk_env.sh
```

### Build script not found (`build_web.ps1`)

Ensure you are on the `infinite-world` branch and have pulled the latest code:

```bash
cd ~/MCPE-WASM
git fetch origin pull/5/head:infinite-world
git checkout infinite-world
git status
```

---

## Architecture notes

| Component | Path | Purpose |
|-----------|------|---------|
| C++ source | `web-build/src/` | Game engine code |
| Build script | `web-build/project/emscripten/build_web.ps1` | Compiles C++ → WASM via emcc |
| WASM output | `web-build/project/emscripten/index.{html,js,wasm}` | Served to browser |
| WebSocket server | `server.js` | Serves static files + world sync over WS |
| Setup script | `setup-ubuntu.sh` | End-to-end Ubuntu installer |

The WebSocket server (`server.js`) broadcasts messages between all connected
clients, enabling real-time chunk loading / world sync for the infinite-world
feature introduced in PR #5.

