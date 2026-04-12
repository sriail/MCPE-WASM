// stdafx.h - Emscripten/WASM compatible precompiled header
#pragma once

#include <cstdint>
#include <unordered_map>
#include <unordered_set>
#include <vector>
#include <memory>
#include <list>
#include <map>
#include <set>
#include <queue>
#include <deque>
#include <algorithm>
#include <math.h>
#include <limits>
#include <string>
#include <sstream>
#include <iostream>
#include <exception>
#include <cassert>
#include <cstdlib>
#include <cstdio>
#include <cstring>
#include <climits>

using namespace std;

// Platform compatibility for Emscripten
#include "EmscriptenCompat.h"

// Extra X64 / platform compat stubs
#include "extraX64.h"

// Core Minecraft.World headers
#include "Definitions.h"
#include "Class.h"
#include "Exceptions.h"
#include "Mth.h"
#include "StringHelpers.h"
#include "ArrayWithLength.h"
#include "Random.h"
#include "TilePos.h"
#include "ChunkPos.h"
#include "compression.h"
#include "PerformanceTimer.h"

// Minecraft.Client Common headers (stubs for Emscripten)
#include "../Minecraft.Client/Common/App_defines.h"
#include "../Minecraft.Client/Common/UI/UIEnums.h"
#include "../Minecraft.Client/Common/App_enums.h"
#include "../Minecraft.Client/Common/Tutorial/TutorialEnum.h"
#include "../Minecraft.Client/Common/App_structs.h"
#include "../Minecraft.Client/Common/Consoles_App.h"
#include "../Minecraft.Client/Common/Minecraft_Macros.h"
#include "../Minecraft.Client/Common/Colours/ColourTable.h"
#include "../Minecraft.Client/Common/BuildVer.h"
#include "../Minecraft.Client/Common/Network/GameNetworkManager.h"

// Emscripten platform stubs
#include "../Minecraft.Client/Emscripten/Emscripten_App.h"
#include "../Minecraft.Client/Emscripten/SentientTelemetryCommon.h"
#include "../Minecraft.Client/Emscripten/MinecraftTelemetry.h"
#include "../Minecraft.Client/Emscripten/4J_Profile.h"
#include "../Minecraft.Client/Emscripten/4J_Render.h"
#include "../Minecraft.Client/Emscripten/4J_Storage.h"
#include "../Minecraft.Client/Emscripten/4J_Input.h"
#include "../Minecraft.Client/Emscripten/strings.h"

#include "../Minecraft.Client/Common/DLC/DLCSkinFile.h"
#include "../Minecraft.Client/Common/Console_Awards_enum.h"
#include "../Minecraft.Client/Common/Potion_Macros.h"
#include "../Minecraft.Client/Common/Console_Debug_enum.h"
#include "../Minecraft.Client/Common/GameRules/ConsoleGameRulesConstants.h"
#include "../Minecraft.Client/Common/GameRules/ConsoleGameRules.h"
#include "../Minecraft.Client/Common/Telemetry/TelemetryManager.h"

void MemSect(int sect);
