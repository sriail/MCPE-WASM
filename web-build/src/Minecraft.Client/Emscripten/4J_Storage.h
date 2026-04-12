#pragma once
// 4J_Storage.h - Storage stubs for Emscripten
// Actual storage is handled by IDBFS via Emscripten

inline void Storage_Init() {}
inline void Storage_Shutdown() {}
inline bool Storage_IsReady() { return true; }
