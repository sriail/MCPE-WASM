#pragma once
// MinecraftTelemetry.h - Game telemetry stub for Emscripten

inline void MinecraftTelemetry_Init() {}
inline void MinecraftTelemetry_TrackEvent(const char* event) { (void)event; }
inline void MinecraftTelemetry_Shutdown() {}
