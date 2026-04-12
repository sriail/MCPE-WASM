#pragma once
// 4J_Profile.h - Profiling stubs for Emscripten

#define PROFILE_BEGIN(name)
#define PROFILE_END(name)
#define PROFILE_SCOPE(name)
#define PROFILE_SET_MARKER(name)
#define PROFILE_THREAD(name)

inline void Profile_Init() {}
inline void Profile_Shutdown() {}
inline void Profile_BeginFrame() {}
inline void Profile_EndFrame() {}
