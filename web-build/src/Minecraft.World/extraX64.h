#pragma once
// extraX64.h - Emscripten/WASM compatible platform definitions
// Replaces Console Edition platform types with cross-platform equivalents

#include <cstdio>
#include <cstdarg>
#include <string>
#include <functional>
#include <vector>
#include <cstdint>
#include <cstring>

#include "EmscriptenCompat.h"

#define MULTITHREAD_ENABLE

typedef unsigned char byte;

const int XUSER_INDEX_ANY = 255;
const int XUSER_INDEX_FOCUS = 254;
const int XUSER_MAX_COUNT = 4;
const int MINECRAFT_NET_MAX_PLAYERS = 8;

// Platform type stubs
typedef uint64_t PlayerUID;
typedef uint64_t SessionID;
typedef PlayerUID GameSessionUID;
typedef PlayerUID *PPlayerUID;
class INVITE_INFO;

typedef void* HXUIOBJ;
typedef void* HXUICLASS;
typedef void* HXUIBRUSH;
typedef void* HXUIDC;
typedef void* LPVOID;

#ifndef HRESULT
typedef long HRESULT;
#endif
#ifndef S_OK
#define S_OK 0
#endif
#ifndef E_FAIL
#define E_FAIL -1
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
#ifndef VOID
typedef void VOID;
#endif
#ifndef WCHAR
typedef wchar_t WCHAR;
#endif
#ifndef CHAR
typedef char CHAR;
#endif
#ifndef UINT
typedef unsigned int UINT;
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
#ifndef InitializeCriticalSectionAndSpinCount
inline BOOL InitializeCriticalSectionAndSpinCount(CRITICAL_SECTION* cs, DWORD spin) {
    (void)cs; (void)spin; return TRUE;
}
#endif

inline bool IsEqualXUID(PlayerUID a, PlayerUID b) { return a == b; }

// XLockFreeStack - simplified single-threaded version for Emscripten
template <typename T> class XLockFreeStack
{
    std::vector<T*> intStack;
public:
    XLockFreeStack() {}
    ~XLockFreeStack() {}
    void Initialize() {}
    void Push(T* data) { intStack.push_back(data); }
    T* Pop() {
        if (intStack.size()) {
            T* ret = intStack.back();
            intStack.pop_back();
            return ret;
        }
        return nullptr;
    }
};

// Memory stubs
inline void XMemCpy(void* a, const void* b, size_t s) { memcpy(a, b, s); }
inline void XMemSet(void* a, int t, size_t s) { memset(a, t, s); }
inline void XMemSet128(void* a, int t, size_t s) { memset(a, t, s); }
inline void* XPhysicalAlloc(SIZE_T a, ULONG_PTR b, ULONG_PTR c, DWORD d) { (void)b; (void)c; (void)d; return malloc(a); }
inline void XPhysicalFree(void* a) { free(a); }

// Forward declarations
class DLCManager;
class LevelRuleset;
class ModelPart;
class LevelChunk;
class ConsoleSchematicFile;

const int XZP_ICON_SHANK_01 = 1;
const int XZP_ICON_SHANK_02 = 2;
const int XZP_ICON_SHANK_03 = 3;

const int XN_SYS_SIGNINCHANGED = 0;
const int XN_SYS_INPUTDEVICESCHANGED = 1;
const int XN_LIVE_CONTENT_INSTALLED = 2;
const int XN_SYS_STORAGEDEVICESCHANGED = 3;

// Gamepad input codes (VK_PAD_*)
#define VK_PAD_A                        0x5800
#define VK_PAD_B                        0x5801
#define VK_PAD_X                        0x5802
#define VK_PAD_Y                        0x5803
#define VK_PAD_RSHOULDER                0x5804
#define VK_PAD_LSHOULDER                0x5805
#define VK_PAD_LTRIGGER                 0x5806
#define VK_PAD_RTRIGGER                 0x5807
#define VK_PAD_DPAD_UP                  0x5810
#define VK_PAD_DPAD_DOWN                0x5811
#define VK_PAD_DPAD_LEFT                0x5812
#define VK_PAD_DPAD_RIGHT               0x5813
#define VK_PAD_START                    0x5814
#define VK_PAD_BACK                     0x5815
#define VK_PAD_LTHUMB_PRESS             0x5816
#define VK_PAD_RTHUMB_PRESS             0x5817
#define VK_PAD_LTHUMB_UP                0x5820
#define VK_PAD_LTHUMB_DOWN              0x5821
#define VK_PAD_LTHUMB_RIGHT             0x5822
#define VK_PAD_LTHUMB_LEFT              0x5823
#define VK_PAD_LTHUMB_UPLEFT            0x5824
#define VK_PAD_LTHUMB_UPRIGHT           0x5825
#define VK_PAD_LTHUMB_DOWNRIGHT         0x5826
#define VK_PAD_LTHUMB_DOWNLEFT          0x5827
#define VK_PAD_RTHUMB_UP                0x5830
#define VK_PAD_RTHUMB_DOWN              0x5831
#define VK_PAD_RTHUMB_RIGHT             0x5832
#define VK_PAD_RTHUMB_LEFT              0x5833
#define VK_PAD_RTHUMB_UPLEFT            0x5834
#define VK_PAD_RTHUMB_UPRIGHT           0x5835
#define VK_PAD_RTHUMB_DOWNRIGHT         0x5836
#define VK_PAD_RTHUMB_DOWNLEFT          0x5837

const int XUSER_NAME_SIZE = 32;

// IQNetPlayer - Networking stub
class IQNetPlayer
{
public:
    BYTE GetSmallId() { return m_smallId; }
    void SendData(IQNetPlayer* player, const void* pvData, DWORD dwDataSize, DWORD dwFlags) {
        (void)player; (void)pvData; (void)dwDataSize; (void)dwFlags;
    }
    bool IsSameSystem(IQNetPlayer* player) { (void)player; return true; }
    DWORD GetSendQueueSize(IQNetPlayer* player, DWORD dwFlags) { (void)player; (void)dwFlags; return 0; }
    DWORD GetCurrentRtt() { return 0; }
    bool IsHost() { return m_isHostPlayer; }
    bool IsGuest() { return false; }
    bool IsLocal() { return !m_isRemote; }
    PlayerUID GetXuid() { return m_resolvedXuid; }
    LPCWSTR GetGamertag() { return m_gamertag; }
    int GetSessionIndex() { return 0; }
    bool IsTalking() { return false; }
    bool IsMutedByLocalUser(DWORD dwUserIndex) { (void)dwUserIndex; return false; }
    bool HasVoice() { return false; }
    bool HasCamera() { return false; }
    int GetUserIndex() { return 0; }
    void SetCustomDataValue(ULONG_PTR ulpCustomDataValue) { m_customData = ulpCustomDataValue; }
    ULONG_PTR GetCustomDataValue() { return m_customData; }

    BYTE m_smallId = 0;
    bool m_isRemote = false;
    bool m_isHostPlayer = false;
    PlayerUID m_resolvedXuid = 0;
    wchar_t m_gamertag[32] = {};
private:
    ULONG_PTR m_customData = 0;
};

inline void Win64_SetupRemoteQNetPlayer(IQNetPlayer* player, BYTE smallId, bool isHost, bool isLocal) {
    (void)player; (void)smallId; (void)isHost; (void)isLocal;
}

const int QNET_GETSENDQUEUESIZE_SECONDARY_TYPE = 0;
const int QNET_GETSENDQUEUESIZE_MESSAGES = 0;
const int QNET_GETSENDQUEUESIZE_BYTES = 0;

typedef struct { BYTE bFlags; BYTE bReserved; WORD cProbesXmit; WORD cProbesRecv; WORD cbData; BYTE* pbData; WORD wRttMinInMsecs; WORD wRttMedInMsecs; DWORD dwUpBitsPerSec; DWORD dwDnBitsPerSec; } XNQOSINFO;
typedef struct { UINT cxnqos; UINT cxnqosPending; XNQOSINFO axnqosinfo[1]; } XNQOS;
typedef struct _XOVERLAPPED {} XOVERLAPPED, *PXOVERLAPPED;
typedef struct _XSESSION_SEARCHRESULT {} XSESSION_SEARCHRESULT, *PXSESSION_SEARCHRESULT;
typedef struct { DWORD dwContextId; DWORD dwValue; } XUSER_CONTEXT, *PXUSER_CONTEXT;
typedef struct _XSESSION_SEARCHRESULT_HEADER { DWORD dwSearchResults; XSESSION_SEARCHRESULT* pResults; } XSESSION_SEARCHRESULT_HEADER, *PXSESSION_SEARCHRESULT_HEADER;
typedef struct _XONLINE_FRIEND { PlayerUID xuid; CHAR szGamertag[XUSER_NAME_SIZE]; DWORD dwFriendState; SessionID sessionID; DWORD dwTitleID; FILETIME ftUserTime; SessionID xnkidInvite; FILETIME gameinviteTime; DWORD cchRichPresence; } XONLINE_FRIEND, *PXONLINE_FRIEND;

class IQNetCallbacks {};
class IQNetGameSearch {};

typedef enum _QNET_STATE { QNET_STATE_IDLE, QNET_STATE_SESSION_HOSTING, QNET_STATE_SESSION_JOINING, QNET_STATE_GAME_LOBBY, QNET_STATE_SESSION_REGISTERING, QNET_STATE_SESSION_STARTING, QNET_STATE_GAME_PLAY, QNET_STATE_SESSION_ENDING, QNET_STATE_SESSION_LEAVING, QNET_STATE_SESSION_DELETING } QNET_STATE, *PQNET_STATE;

class IQNet
{
public:
    HRESULT AddLocalPlayerByUserIndex(DWORD dwUserIndex) { (void)dwUserIndex; return S_OK; }
    IQNetPlayer* GetHostPlayer() { return &m_player[0]; }
    IQNetPlayer* GetLocalPlayerByUserIndex(DWORD dwUserIndex) { (void)dwUserIndex; return &m_player[0]; }
    IQNetPlayer* GetPlayerByIndex(DWORD dwPlayerIndex) { return &m_player[dwPlayerIndex]; }
    IQNetPlayer* GetPlayerBySmallId(BYTE SmallId) { (void)SmallId; return &m_player[0]; }
    IQNetPlayer* GetPlayerByXuid(PlayerUID xuid) { (void)xuid; return &m_player[0]; }
    DWORD GetPlayerCount() { return s_playerCount; }
    QNET_STATE GetState() { return QNET_STATE_IDLE; }
    bool IsHost() { return s_isHosting; }
    HRESULT JoinGameFromInviteInfo(DWORD dwUserIndex, DWORD dwUserMask, const INVITE_INFO* pInviteInfo) { (void)dwUserIndex; (void)dwUserMask; (void)pInviteInfo; return S_OK; }
    void HostGame() {}
    void ClientJoinGame() {}
    void EndGame() {}
    static IQNetPlayer m_player[MINECRAFT_NET_MAX_PLAYERS];
    static DWORD s_playerCount;
    static bool s_isHosting;
};

// PIX profiling stubs
inline void PIXAddNamedCounter(int a, char* b, ...) { (void)a; (void)b; }
inline void PIXBeginNamedEvent(int a, char* b, ...) { (void)a; (void)b; }
inline void PIXEndNamedEvent() {}
inline void PIXSetMarkerDeprecated(int a, char* b, ...) { (void)a; (void)b; }
inline void XSetThreadProcessor(HANDLE a, int b) { (void)a; (void)b; }

const int QNET_SENDDATA_LOW_PRIORITY = 0;
const int QNET_SENDDATA_SECONDARY = 0;
const int INVALID_XUID = 0;

const int XCONTENT_MAX_DISPLAYNAME_LENGTH = 256;
const int XCONTENT_MAX_FILENAME_LENGTH = 256;
typedef int XCONTENTDEVICEID;

typedef struct _XCONTENT_DATA {
    XCONTENTDEVICEID DeviceID;
    DWORD dwContentType;
    WCHAR szDisplayName[XCONTENT_MAX_DISPLAYNAME_LENGTH];
    CHAR szFileName[XCONTENT_MAX_FILENAME_LENGTH];
} XCONTENT_DATA, *PXCONTENT_DATA;

static const int XMARKETPLACE_CONTENT_ID_LEN = 4;

typedef VOID* XMEMDECOMPRESSION_CONTEXT;
typedef VOID* XMEMCOMPRESSION_CONTEXT;
typedef enum _XMEMCODEC_TYPE { XMEMCODEC_DEFAULT = 0, XMEMCODEC_LZX = 1 } XMEMCODEC_TYPE;

inline HRESULT XMemDecompress(XMEMDECOMPRESSION_CONTEXT ctx, VOID* dst, SIZE_T* dstSize, const VOID* src, SIZE_T srcSize) { (void)ctx; (void)dst; (void)dstSize; (void)src; (void)srcSize; return E_FAIL; }
inline HRESULT XMemCompress(XMEMCOMPRESSION_CONTEXT ctx, VOID* dst, SIZE_T* dstSize, const VOID* src, SIZE_T srcSize) { (void)ctx; (void)dst; (void)dstSize; (void)src; (void)srcSize; return E_FAIL; }
inline HRESULT XMemCreateCompressionContext(XMEMCODEC_TYPE type, const VOID* params, DWORD flags, XMEMCOMPRESSION_CONTEXT* ctx) { (void)type; (void)params; (void)flags; *ctx = nullptr; return S_OK; }
inline HRESULT XMemCreateDecompressionContext(XMEMCODEC_TYPE type, const VOID* params, DWORD flags, XMEMDECOMPRESSION_CONTEXT* ctx) { (void)type; (void)params; (void)flags; *ctx = nullptr; return S_OK; }
typedef struct _XMEMCODEC_PARAMETERS_LZX { DWORD Flags; DWORD WindowSize; DWORD CompressionPartitionSize; } XMEMCODEC_PARAMETERS_LZX;
inline void XMemDestroyCompressionContext(XMEMCOMPRESSION_CONTEXT ctx) { (void)ctx; }
inline void XMemDestroyDecompressionContext(XMEMDECOMPRESSION_CONTEXT ctx) { (void)ctx; }

typedef struct { BYTE type; union { LONG nData; LONGLONG i64Data; double dblData; struct { DWORD cbData; LPWSTR pwszData; } string; float fData; struct { DWORD cbData; PBYTE pbData; } binary; FILETIME ftData; }; } XUSER_DATA, *PXUSER_DATA;
typedef struct { DWORD dwPropertyId; XUSER_DATA value; } XUSER_PROPERTY, *PXUSER_PROPERTY;
typedef struct _XMARKETPLACE_CONTENTOFFER_INFO { ULONGLONG qwOfferID; ULONGLONG qwPreviewOfferID; DWORD dwOfferNameLength; WCHAR* wszOfferName; DWORD dwOfferType; BYTE contentId[XMARKETPLACE_CONTENT_ID_LEN]; BOOL fIsUnrestrictedLicense; DWORD dwLicenseMask; DWORD dwTitleID; DWORD dwContentCategory; DWORD dwTitleNameLength; WCHAR* wszTitleName; BOOL fUserHasPurchased; DWORD dwPackageSize; DWORD dwInstallSize; DWORD dwSellTextLength; WCHAR* wszSellText; DWORD dwAssetID; DWORD dwPurchaseQuantity; DWORD dwPointsPrice; } XMARKETPLACE_CONTENTOFFER_INFO, *PXMARKETPLACE_CONTENTOFFER_INFO;
typedef enum { XMARKETPLACE_OFFERING_TYPE_CONTENT = 0x00000002, XMARKETPLACE_OFFERING_TYPE_GAME_DEMO = 0x00000020, XMARKETPLACE_OFFERING_TYPE_GAME_TRAILER = 0x00000040, XMARKETPLACE_OFFERING_TYPE_THEME = 0x00000080, XMARKETPLACE_OFFERING_TYPE_TILE = 0x00000800, XMARKETPLACE_OFFERING_TYPE_ARCADE = 0x00002000, XMARKETPLACE_OFFERING_TYPE_VIDEO = 0x00004000, XMARKETPLACE_OFFERING_TYPE_CONSUMABLE = 0x00010000, XMARKETPLACE_OFFERING_TYPE_AVATARITEM = 0x00100000 } XMARKETPLACE_OFFERING_TYPE;

// Language constants
const int XC_LANGUAGE_ENGLISH = 0x01;
const int XC_LANGUAGE_JAPANESE = 0x02;
const int XC_LANGUAGE_GERMAN = 0x03;
const int XC_LANGUAGE_FRENCH = 0x04;
const int XC_LANGUAGE_SPANISH = 0x05;
const int XC_LANGUAGE_ITALIAN = 0x06;
const int XC_LANGUAGE_KOREAN = 0x07;
const int XC_LANGUAGE_TCHINESE = 0x08;
const int XC_LANGUAGE_PORTUGUESE = 0x09;
const int XC_LANGUAGE_POLISH = 0x0B;
const int XC_LANGUAGE_RUSSIAN = 0x0C;
const int XC_LANGUAGE_SWEDISH = 0x0D;
const int XC_LANGUAGE_TURKISH = 0x0E;
const int XC_LANGUAGE_BNORWEGIAN = 0x0F;
const int XC_LANGUAGE_DUTCH = 0x10;
const int XC_LANGUAGE_SCHINESE = 0x11;
const int XC_LANGUAGE_FINISH = 0xF1;
const int XC_LANGUAGE_GREEK = 0xF2;
const int XC_LANGUAGE_DANISH = 0xF3;
const int XC_LANGUAGE_CZECH = 0xF4;
const int XC_LANGUAGE_SLOVAK = 0xF5;

const int XC_LOCALE_UNITED_STATES = 36;
const int XC_LOCALE_GREAT_BRITAIN = 35;
const int XC_LOCALE_LATIN_AMERICA = 240;

inline DWORD XGetLanguage() { return XC_LANGUAGE_ENGLISH; }
inline DWORD XGetLocale() { return XC_LOCALE_UNITED_STATES; }
inline DWORD XEnableGuestSignin(BOOL fEnable) { (void)fEnable; return 0; }

typedef struct _XUSER_SIGNIN_INFO { PlayerUID xuid; DWORD dwGuestNumber; } XUSER_SIGNIN_INFO, *PXUSER_SIGNIN_INFO;
#define XUSER_GET_SIGNIN_INFO_ONLINE_XUID_ONLY 0x00000001
#define XUSER_GET_SIGNIN_INFO_OFFLINE_XUID_ONLY 0x00000002
inline DWORD XUserGetSigninInfo(DWORD dwUserIndex, DWORD dwFlags, PXUSER_SIGNIN_INFO pSigninInfo) { (void)dwUserIndex; (void)dwFlags; (void)pSigninInfo; return 0; }

class CXuiStringTable {
public:
    LPCWSTR Lookup(LPCWSTR szId) { (void)szId; return L""; }
    LPCWSTR Lookup(UINT nIndex) { (void)nIndex; return L""; }
    void Clear() {}
    HRESULT Load(LPCWSTR szId) { (void)szId; return S_OK; }
};

class D3DXVECTOR3 {
public:
    D3DXVECTOR3() : x(0), y(0), z(0), pad(0) {}
    D3DXVECTOR3(float x_, float y_, float z_) : x(x_), y(y_), z(z_), pad(0) {}
    float x, y, z, pad;
    D3DXVECTOR3& operator+=(const D3DXVECTOR3& add) { x += add.x; y += add.y; z += add.z; return *this; }
};

const int QNET_SENDDATA_RELIABLE = 0;
const int QNET_SENDDATA_SEQUENTIAL = 0;
struct XRNM_SEND_BUFFER { DWORD dwDataSize; byte* pbyData; };
const int D3DBLEND_CONSTANTALPHA = 0;
const int D3DBLEND_INVCONSTANTALPHA = 0;
const int D3DPT_QUADLIST = 0;
#define QNET_E_SESSION_FULL 0
#define QNET_USER_MASK_USER0 1
#define QNET_USER_MASK_USER1 2
#define QNET_USER_MASK_USER2 4
#define QNET_USER_MASK_USER3 8
