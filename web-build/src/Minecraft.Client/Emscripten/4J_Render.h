#pragma once
// 4J_Render.h - Rendering stubs for Emscripten

class C4JRender {
public:
    enum eTextureFormat {
        TEXTURE_FORMAT_RxGyBzAw = 0,
        TEXTURE_FORMAT_AxRyGzBw,
        TEXTURE_FORMAT_RxGy,
        TEXTURE_FORMAT_DXT1,
        TEXTURE_FORMAT_DXT3,
        TEXTURE_FORMAT_DXT5
    };
    
    static eTextureFormat TEXTURE_FORMAT;
    
    static void Init() {}
    static void Shutdown() {}
    static void BeginFrame() {}
    static void EndFrame() {}
    static void SetViewport(int x, int y, int w, int h) {
        (void)x; (void)y; (void)w; (void)h;
    }
};

inline C4JRender::eTextureFormat C4JRender::TEXTURE_FORMAT = C4JRender::TEXTURE_FORMAT_RxGyBzAw;
