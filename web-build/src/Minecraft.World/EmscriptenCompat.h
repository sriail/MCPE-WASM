// EmscriptenCompat.h - Platform compatibility layer for Emscripten/WASM
// Bridges Console Edition (Xbox/PS/Windows) APIs to Emscripten
#pragma once

#ifdef EMSCRIPTEN

#include <cstdint>
#include <mutex>
#include <thread>
#include <string>
#include <memory>
#include <vector>
#include <map>
#include <set>
#include <algorithm>
#include <functional>
#include <cstring>
#include <cmath>
#include <cstdlib>
#include <cstdio>
#include <cassert>
#include <climits>
#include <cfloat>

// Windows type definitions
#ifndef DWORD
typedef uint32_t DWORD;
#endif
#ifndef BYTE
typedef uint8_t BYTE;
#endif
#ifndef WORD
typedef uint16_t WORD;
#endif
#ifndef LONG
typedef int32_t LONG;
#endif
#ifndef BOOL
typedef int BOOL;
#endif
#ifndef SIZE_T
typedef size_t SIZE_T;
#endif
#ifndef ULONG_PTR
typedef uintptr_t ULONG_PTR;
#endif
#ifndef LONGLONG
typedef long long LONGLONG;
#endif
#ifndef ULONGLONG
typedef unsigned long long ULONGLONG;
#endif
#ifndef LPCWSTR
typedef const wchar_t* LPCWSTR;
#endif
#ifndef LPWSTR
typedef wchar_t* LPWSTR;
#endif
#ifndef LPCSTR
typedef const char* LPCSTR;
#endif
#ifndef PBYTE
typedef unsigned char* PBYTE;
#endif
#ifndef LPVOID
typedef void* LPVOID;
#endif
#ifndef VOID
typedef void VOID;
#endif
#ifndef WCHAR
typedef wchar_t WCHAR;
#endif
#ifndef CHAR
typedef char CHAR;
#endif
#ifndef SHORT
typedef short SHORT;
#endif
#ifndef INT
typedef int INT;
#endif
#ifndef UINT
typedef unsigned int UINT;
#endif
#ifndef WINAPI
#define WINAPI
#endif
#ifndef HRESULT
typedef long HRESULT;
#endif
#ifndef S_OK
#define S_OK 0L
#endif
#ifndef E_FAIL
#define E_FAIL -1L
#endif
#ifndef TRUE
#define TRUE 1
#endif
#ifndef FALSE
#define FALSE 0
#endif
#ifndef INFINITE
#define INFINITE 0xFFFFFFFF
#endif
#ifndef INVALID_HANDLE_VALUE
#define INVALID_HANDLE_VALUE ((void*)(intptr_t)-1)
#endif
#ifndef CONST
#define CONST const
#endif
#ifndef MAX_PATH
#define MAX_PATH 260
#endif
#ifndef FILETIME
typedef struct _FILETIME {
    DWORD dwLowDateTime;
    DWORD dwHighDateTime;
} FILETIME;
#endif
#ifndef SYSTEMTIME
typedef struct _SYSTEMTIME {
    WORD wYear, wMonth, wDayOfWeek, wDay;
    WORD wHour, wMinute, wSecond, wMilliseconds;
} SYSTEMTIME;
#endif
#ifndef _countof
#define _countof(arr) (sizeof(arr) / sizeof((arr)[0]))
#endif
#ifndef ZeroMemory
#define ZeroMemory(dst, len) memset(dst, 0, len)
#endif
#ifndef CopyMemory
#define CopyMemory(dst, src, len) memcpy(dst, src, len)
#endif

// Windows threading - map to no-ops or simple mutex on single-threaded Emscripten
typedef struct _CRITICAL_SECTION {
    // No-op on single-threaded Emscripten
} CRITICAL_SECTION, *LPCRITICAL_SECTION;

inline void InitializeCriticalSection(CRITICAL_SECTION* cs) { (void)cs; }
inline BOOL InitializeCriticalSectionAndSpinCount(CRITICAL_SECTION* cs, DWORD spin) { (void)cs; (void)spin; return TRUE; }
inline void DeleteCriticalSection(CRITICAL_SECTION* cs) { (void)cs; }
inline void EnterCriticalSection(CRITICAL_SECTION* cs) { (void)cs; }
inline void LeaveCriticalSection(CRITICAL_SECTION* cs) { (void)cs; }
inline BOOL TryEnterCriticalSection(CRITICAL_SECTION* cs) { (void)cs; return TRUE; }

// Thread Local Storage
inline DWORD TlsAlloc() { return 0; }
inline BOOL TlsFree(DWORD idx) { (void)idx; return TRUE; }
inline void* TlsGetValue(DWORD idx) { (void)idx; return nullptr; }
inline BOOL TlsSetValue(DWORD idx, void* val) { (void)idx; (void)val; return TRUE; }

// Interlocked operations
inline LONG InterlockedIncrement(volatile LONG* val) { return ++(*val); }
inline LONG InterlockedDecrement(volatile LONG* val) { return --(*val); }
inline LONG InterlockedExchange(volatile LONG* target, LONG value) {
    LONG old = *target;
    *target = value;
    return old;
}
inline LONG InterlockedCompareExchange(volatile LONG* dest, LONG exchange, LONG comparand) {
    LONG old = *dest;
    if (*dest == comparand) *dest = exchange;
    return old;
}

// Sleep
inline void Sleep(DWORD milliseconds) {
    // No-op on Emscripten (single-threaded)
    (void)milliseconds;
}

// GetTickCount
inline DWORD GetTickCount() {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return (DWORD)(ts.tv_sec * 1000 + ts.tv_nsec / 1000000);
}

// Event handles - simplified
typedef void* HANDLE;

inline HANDLE CreateEvent(void* attr, BOOL manual, BOOL initial, const char* name) {
    (void)attr; (void)manual; (void)initial; (void)name;
    return nullptr;
}
inline BOOL SetEvent(HANDLE h) { (void)h; return TRUE; }
inline BOOL ResetEvent(HANDLE h) { (void)h; return TRUE; }
inline void CloseHandle(HANDLE h) { (void)h; }
inline DWORD WaitForSingleObject(HANDLE h, DWORD ms) { (void)h; (void)ms; return 0; }

// OutputDebugString
inline void OutputDebugStringA(const char* str) { (void)str; }
inline void OutputDebugStringW(const wchar_t* str) { (void)str; }
#define OutputDebugString OutputDebugStringA

// String conversion helpers
#ifndef _wtoi
inline int _wtoi(const wchar_t* str) {
    return (int)wcstol(str, nullptr, 10);
}
#endif
#ifndef _wtof
inline double _wtof(const wchar_t* str) {
    return wcstod(str, nullptr);
}
#endif
#ifndef _itow_s
inline void _itow_s(int value, wchar_t* buffer, size_t sizeInWords, int radix) {
    (void)radix;
    swprintf(buffer, sizeInWords, L"%d", value);
}
#endif
#ifndef swprintf_s
#define swprintf_s swprintf
#endif
#ifndef sprintf_s
#define sprintf_s snprintf
#endif
#ifndef _snwprintf_s
#define _snwprintf_s(buf, size, count, fmt, ...) swprintf(buf, size, fmt, ##__VA_ARGS__)
#endif
#ifndef _snprintf_s
#define _snprintf_s(buf, size, count, fmt, ...) snprintf(buf, size, fmt, ##__VA_ARGS__)
#endif
#ifndef wcscpy_s
inline int wcscpy_s(wchar_t* dest, size_t destsz, const wchar_t* src) {
    wcsncpy(dest, src, destsz);
    dest[destsz-1] = L'\0';
    return 0;
}
#endif
#ifndef wcscat_s
inline int wcscat_s(wchar_t* dest, size_t destsz, const wchar_t* src) {
    wcsncat(dest, src, destsz - wcslen(dest) - 1);
    return 0;
}
#endif

// Misc Windows macros
#ifndef min
#define min(a,b) ((a) < (b) ? (a) : (b))
#endif
#ifndef max
#define max(a,b) ((a) > (b) ? (a) : (b))
#endif

// MSVC intrinsics
#ifndef __debugbreak
#define __debugbreak() ((void)0)
#endif
#ifndef __forceinline
#define __forceinline inline
#endif
#ifndef __declspec
#define __declspec(x)
#endif

// PIX profiling markers - no-op
#define PIXBeginEvent(...)
#define PIXEndEvent(...)
#define PIXSetMarker(...)
#define PIXScopedEvent(...)

// Console-specific features - stub out
#define TELEMETRY_ENABLED 0

#endif // EMSCRIPTEN
