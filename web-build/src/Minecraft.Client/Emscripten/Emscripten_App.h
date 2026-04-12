#pragma once
// Emscripten_App.h - Emscripten platform application stub

#include <string>
using namespace std;

// Platform identifiers
#define PLATFORM_EMSCRIPTEN 1
#define IS_EMSCRIPTEN 1

// App lifecycle stubs
inline void App_Init() {}
inline void App_Shutdown() {}
inline void App_Update() {}
