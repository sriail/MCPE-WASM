#ifndef NET_MINECRAFT_CLIENT_RENDERER_TILEENTITY__FurnaceRenderer_H__
#define NET_MINECRAFT_CLIENT_RENDERER_TILEENTITY__FurnaceRenderer_H__

#include "TileEntityRenderer.h"

class FurnaceRenderer : public TileEntityRenderer
{
public:
    void render(TileEntity* entity, float x, float y, float z, float a);
};

#endif /*NET_MINECRAFT_CLIENT_RENDERER_TILEENTITY__FurnaceRenderer_H__*/
