#pragma once
// SentientTelemetryCommon.h - Telemetry stub for Emscripten
// All telemetry operations are no-ops on Emscripten

inline void Telemetry_Init() {}
inline void Telemetry_Shutdown() {}
inline void Telemetry_Flush() {}
