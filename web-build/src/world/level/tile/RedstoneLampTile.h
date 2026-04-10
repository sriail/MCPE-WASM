#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__RedstoneLampTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__RedstoneLampTile_H__

//package net.minecraft.world.level.tile;

#include "Tile.h"
#include "../material/Material.h"

class RedstoneLampTile: public Tile
{
	typedef Tile super;
public:
    bool isLit;

    RedstoneLampTile(int id, int tex, bool lit)
    :   super(id, tex, Material::stone),
        isLit(lit)
    {
    }
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__RedstoneLampTile_H__*/
