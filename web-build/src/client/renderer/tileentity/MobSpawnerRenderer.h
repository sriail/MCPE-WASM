#ifndef NET_MINECRAFT_CLIENT_RENDERER_TILEENTITY__MobSpawnerRenderer_H__
#define NET_MINECRAFT_CLIENT_RENDERER_TILEENTITY__MobSpawnerRenderer_H__

#include "TileEntityRenderer.h"
#include "../../model/SpiderModel.h"
#include "../../model/HumanoidModel.h"

class MobSpawnerTileEntity;

// Renders the spinning mob inside a mob spawner cage.
// The "cage" face itself is drawn by the normal tile renderer; this renderer
// only draws the miniature mob that spins inside.
class MobSpawnerRenderer : public TileEntityRenderer
{
public:
    MobSpawnerRenderer();
    ~MobSpawnerRenderer();

    /*@Override*/
    void render(TileEntity* entity, float x, float y, float z, float a);

private:
    SpiderModel*    spiderModel;
    HumanoidModel*  humanoidModel;
};

#endif /*NET_MINECRAFT_CLIENT_RENDERER_TILEENTITY__MobSpawnerRenderer_H__*/