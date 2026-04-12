#pragma once
// 4J_Storage.h - Storage stubs for Emscripten

#include <cstdint>
#include <cstring>
#include <ctime>

#define MAX_DISPLAYNAME_LENGTH 128
#define MAX_SAVEFILENAME_LENGTH 64
#define USER_INDEX_ANY 0x000000FF
#define RESULT long

typedef struct {
    char UTF8SaveFilename[MAX_SAVEFILENAME_LENGTH];
    char UTF8SaveTitle[MAX_DISPLAYNAME_LENGTH];
    time_t modifiedTime;
    unsigned char* thumbnailData;
    unsigned int thumbnailSize;
} SAVE_INFO, *PSAVE_INFO;

typedef struct {
    int iSaveC;
    PSAVE_INFO SaveInfoA;
} SAVE_DETAILS, *PSAVE_DETAILS;

#define MARKETPLACE_CONTENTOFFER_INFO int
#define CURRENT_DLC_VERSION_NUM 3

class C4JStorage {
public:
    enum eGlobalStorage {
        eGlobalStorage_Default = 0,
        eGlobalStorage_User = 1
    };

    enum eTMS_FILETYPEVAL {
        eTMS_FILETYPEVAL_Default = 0
    };

    enum ESavingMessage {
        eSavingMessage_None = 0,
        eSavingMessage_Saving,
        eSavingMessage_Done
    };

    enum EMessageResult {
        eMessageResult_OK = 0,
        eMessageResult_Cancel,
        eMessageResult_Yes,
        eMessageResult_No,
        eMessageResult_Error
    };

    struct TMSPP_FILEDATA {
        void* pData;
        unsigned int uiSize;
    };
    typedef TMSPP_FILEDATA* PTMSPP_FILEDATA;

    struct DLC_TMS_DETAILS {
        int iCount;
        void* pDetails;
    };

    struct PROFILESETTINGS {
        int iYAxisInversion;
        int iXAxisInversion;
        int iLookSensitivity;
        int iLeftHandedMode;
        int iAutoJumpEnabled;
        int iControllerLayout;
        int iChatMuted;
        int iFOV;
        int iLanguage;
        int iTutorialLevel;
        int iRenderDistance;
        int iGamma;
    };
};

// Forward declarations for types used by UI
typedef int (C4JThreadStartFunc)(void* lpThreadParameter);

// FriendSessionInfo is defined in SessionInfo.h - forward declare only
class FriendSessionInfo;

enum eUpsellType {
    eUpsellType_None = 0,
    eUpsellType_FullGame,
    eUpsellType_DLC
};

enum eUpsellResponse {
    eUpsellResponse_Purchased = 0,
    eUpsellResponse_Declined,
    eUpsellResponse_Error
};
