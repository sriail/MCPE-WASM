#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__MobSpawnerTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__MobSpawnerTile_H__

#include "EntityTile.h"
#include "entity/MobSpawnerTileEntity.h"
#include "../material/Material.h"

// The monster-spawner cage block (ID 52).
// Texture: col 1, row 4 on the terrain atlas = index 1 + 4*16 = 65
class MobSpawnerTile : public EntityTile
{
    typedef EntityTile super;
public:
    MobSpawnerTile(int id)
    :   super(id, 65, Material::stone)
    {}

    /*@Override*/
    TileEntity* newTileEntity()
    {
        return new MobSpawnerTileEntity();
    }

    /*@Override*/
    bool isSolidRender() { return false; }

    /*@Override*/
    int getRenderShape() { return SHAPE_BLOCK; }

    /*@Override*/
    bool blocksLight() { return false; }

    /*@Override*/
    int getResource(int /*data*/, Random* /*random*/) { return 0; }
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__MobSpawnerTile_H__*/
