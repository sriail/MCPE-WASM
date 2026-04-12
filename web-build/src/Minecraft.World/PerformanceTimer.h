#pragma once

#ifdef EMSCRIPTEN
#include <cstdint>
typedef union _LARGE_INTEGER {
    struct { uint32_t LowPart; int32_t HighPart; };
    int64_t QuadPart;
} LARGE_INTEGER;
#endif


class PerformanceTimer
{
private:
	LARGE_INTEGER  m_qwStartTime;
	float m_fSecsPerTick;

public:
	PerformanceTimer();
	void Reset();
	void PrintElapsedTime(const wstring &description);
};