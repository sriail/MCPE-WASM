#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__SoulSandTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__SoulSandTile_H__

//package net.minecraft.world.level.tile;

#include "Tile.h"
#include "../Level.h"
#include "../material/Material.h"
#include "../../entity/Entity.h"
#include "../../phys/AABB.h"

class SoulSandTile: public Tile
{
	typedef Tile super;
public:
    SoulSandTile(int id, int tex)
    :   super(id, tex, Material::sand)
    {
        setShape(0, 0, 0, 1, 14.0f / 16.0f, 1);
    }

    /*@Override*/
    AABB* getAABB(Level* level, int x, int y, int z) {
		tmpBB.set((float)x, (float)y, (float)z, (float)x + 1, (float)y + 1, (float)z + 1);
		return &tmpBB;
    }

    /*@Override*/
    void entityInside(Level* level, int x, int y, int z, Entity* entity) {
        entity->xd *= 0.4;
        entity->zd *= 0.4;
    }
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__SoulSandTile_H__*/
