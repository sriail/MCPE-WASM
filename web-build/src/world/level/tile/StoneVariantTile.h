#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__StoneVariantTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__StoneVariantTile_H__

//package net.minecraft.world.level.tile;

#include "Tile.h"
#include "../material/Material.h"

class StoneVariantTile: public Tile
{
	typedef Tile super;
public:
    static const int GRANITE = 0;
    static const int POLISHED_GRANITE = 1;
    static const int DIORITE = 2;
    static const int POLISHED_DIORITE = 3;
    static const int ANDESITE = 4;
    static const int POLISHED_ANDESITE = 5;
    static const int TYPE_MASK = 7;

    StoneVariantTile(int id)
    :   super(id, 130, Material::stone)
    {
    }

    int getTexture(int face, int data) {
        switch (data & TYPE_MASK) {
            case GRANITE:           return 130; // 2 + 8*16
            case POLISHED_GRANITE:  return 131; // 3 + 8*16
            case DIORITE:           return 132; // 4 + 8*16
            case POLISHED_DIORITE:  return 133; // 5 + 8*16
            case ANDESITE:          return 134; // 6 + 8*16
            case POLISHED_ANDESITE: return 135; // 7 + 8*16
            default:                return 130;
        }
    }

    int getTexture(int face) {
        return getTexture(face, 0);
    }
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__StoneVariantTile_H__*/
