#pragma once
// SentientTelemetryCommon.h - Telemetry stubs for Emscripten

enum ESen_FriendOrMatch { eSen_Friends = 0, eSen_Match, eSen_Solo };
enum ESen_CompeteOrCoop { eSen_Compete = 0, eSen_Coop };
enum ESen_LevelExitStatus { eSen_NormalExit = 0, eSen_CrashExit };
enum ESen_MediaDestination { eSen_MediaLocal = 0, eSen_MediaCloud };
enum ESen_MediaType { eSen_Screenshot = 0, eSen_Video };
enum ESen_UpsellID { eSen_UpsellNone = 0, eSen_UpsellFullGame, eSen_UpsellDLC };
enum ESen_ContentType { eSen_ContentSkin = 0, eSen_ContentTexture, eSen_ContentMashup };
enum ESen_UpsellResult { eSen_UpsellPurchased = 0, eSen_UpsellDeclined };
enum ESen_UpsellOutcome { eSen_Accepted = 0, eSen_Rejected };
enum ETelemetryChallenges { eTelChallenge_None = 0 };

inline void Telemetry_Init() {}
inline void Telemetry_Shutdown() {}
inline void Telemetry_Flush() {}
