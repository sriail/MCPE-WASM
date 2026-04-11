#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__HayBaleTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__HayBaleTile_H__

//package net.minecraft.world.level.tile;

#include "Tile.h"
#include "../material/Material.h"

class HayBaleTile: public Tile
{
	typedef Tile super;
public:
    static const int TEX_TOP = 217;    // 9 + 13*16
    static const int TEX_SIDE = 218;   // 10 + 13*16

    HayBaleTile(int id)
    :   super(id, TEX_SIDE, Material::plant)
    {
    }

    int getTexture(int face) {
        if (face == 0 || face == 1) return TEX_TOP;
        return TEX_SIDE;
    }

    int getTexture(int face, int data) {
        return getTexture(face);
    }
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__HayBaleTile_H__*/
