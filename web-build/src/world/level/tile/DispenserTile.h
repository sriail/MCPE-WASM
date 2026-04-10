#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__DispenserTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__DispenserTile_H__

//package net.minecraft.world.level.tile;

#include "Tile.h"
#include "../Level.h"
#include "../material/Material.h"
#include "../../entity/Mob.h"
#include "../../../util/Mth.h"

class DispenserTile: public Tile
{
	typedef Tile super;
public:
	// Texture indices
	static const int TEX_FRONT   = 12 + 10 * 16; // 172
	static const int TEX_SIDE    = 16;            // cobblestone
	static const int TEX_TOP     = 1 + 6 * 16;   // furnace top

	DispenserTile(int id)
	:	super(id, TEX_SIDE, Material::stone)
	{
	}

	int getTexture(int face, int data) {
		// data = 2,3,4,5 for facing directions (N/S/W/E)
		if (face == 1) return TEX_TOP; // top
		if (face == 0) return TEX_TOP; // bottom
		if (face == data) return TEX_FRONT; // front face
		return TEX_SIDE;
	}

	int getTexture(int face) {
		if (face == 1 || face == 0) return TEX_TOP;
		if (face == 3) return TEX_FRONT; // default front is south
		return TEX_SIDE;
	}

	void setPlacedBy(Level* level, int x, int y, int z, Mob* by) {
		int dir = (Mth::floor(by->yRot * 4 / 360 + 0.5f)) & 3;
		// Convert rotation to facing data
		if (dir == 0) level->setData(x, y, z, 2); // north
		if (dir == 1) level->setData(x, y, z, 5); // east
		if (dir == 2) level->setData(x, y, z, 3); // south
		if (dir == 3) level->setData(x, y, z, 4); // west
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__DispenserTile_H__*/
