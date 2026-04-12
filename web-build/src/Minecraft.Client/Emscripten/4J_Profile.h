#pragma once
// 4J_Profile.h - Profiling and profile stubs for Emscripten

#define PROFILE_BEGIN(name)
#define PROFILE_END(name)
#define PROFILE_SCOPE(name)
#define PROFILE_SET_MARKER(name)
#define PROFILE_THREAD(name)

class C_4JProfile {
public:
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

    static void Init() {}
    static void Shutdown() {}
    static void BeginFrame() {}
    static void EndFrame() {}
};
