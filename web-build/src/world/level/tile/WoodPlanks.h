#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__WoodPlanks_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__WoodPlanks_H__

//package net.minecraft.world.level.tile;

#include "Tile.h"
#include "../material/Material.h"

class WoodPlanks: public Tile
{
	typedef Tile super;
public:
    static const int OAK = 0;
    static const int SPRUCE = 1;
    static const int BIRCH = 2;
    static const int JUNGLE = 3;
    static const int TYPE_MASK = 3;

    WoodPlanks(int id)
    :   super(id, 4, Material::wood)
    {
    }

    int getTexture(int face, int data) {
        switch (data & TYPE_MASK) {
            case OAK:     return 4;
            case SPRUCE:  return 198; // 6 + 12*16
            case BIRCH:   return 199; // 7 + 12*16
            case JUNGLE:  return 200; // 8 + 12*16
            default:      return 4;
        }
    }

    int getTexture(int face) {
        return 4; // Default oak
    }

protected:
    int getSpawnResourcesAuxValue(int data) {
        return data & TYPE_MASK;
    }
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__WoodPlanks_H__*/
