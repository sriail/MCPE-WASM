#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__PistonTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__PistonTile_H__

//package net.minecraft.world.level.tile;

#include "Tile.h"
#include "../material/Material.h"
#include "../../Facing.h"

class PistonTile: public Tile
{
	typedef Tile super;
public:
    bool isSticky;

    static const int TEX_TOP = 213;        // 5 + 13*16
    static const int TEX_BOTTOM = 214;     // 6 + 13*16
    static const int TEX_SIDE = 215;       // 7 + 13*16
    static const int TEX_STICKY_TOP = 216; // 8 + 13*16

    PistonTile(int id, bool sticky)
    :   super(id, TEX_SIDE, Material::stone),
        isSticky(sticky)
    {
    }

    int getTexture(int face, int data) {
        int dir = data & 7;
        if (face == dir) {
            return isSticky ? TEX_STICKY_TOP : TEX_TOP;
        }
        if (Facing::OPPOSITE_FACING[face] == dir) {
            return TEX_BOTTOM;
        }
        return TEX_SIDE;
    }

    int getTexture(int face) {
        if (face == 1) return isSticky ? TEX_STICKY_TOP : TEX_TOP;
        if (face == 0) return TEX_BOTTOM;
        return TEX_SIDE;
    }
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__PistonTile_H__*/
