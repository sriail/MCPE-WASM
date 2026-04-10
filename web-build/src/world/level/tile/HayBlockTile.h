#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__HayBlockTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__HayBlockTile_H__

//package net.minecraft.world.level.tile;

#include "../material/Material.h"

#include "Tile.h"

// Hay bale block - similar to TreeTile in that it has different top/side textures
class HayBlockTile: public Tile
{
	typedef Tile super;
public:
	// Texture indices
	static const int TEX_SIDE = 14 + 8 * 16; // 142
	static const int TEX_TOP  = 15 + 8 * 16; // 143

	HayBlockTile(int id)
	:	super(id, TEX_SIDE, Material::grass)
	{
	}

	int getTexture(int face) {
		if (face == 0 || face == 1) return TEX_TOP; // top and bottom
		return TEX_SIDE;
	}

	int getTexture(int face, int data) {
		return getTexture(face);
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__HayBlockTile_H__*/
