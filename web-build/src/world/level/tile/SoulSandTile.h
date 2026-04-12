#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__SoulSandTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__SoulSandTile_H__

#include "Tile.h"
#include "../material/Material.h"
#include "../../phys/AABB.h"

class SoulSandTile : public Tile {
	typedef Tile super;
public:
	SoulSandTile(int id, int tex)
	:	super(id, tex, Material::sand)
	{
		setShape(0, 0, 0, 1, 0.875f, 1);
	}

	AABB* getAABB(Level* level, int x, int y, int z) {
		tmpBB.set((float)x, (float)y, (float)z, (float)x + 1, (float)y + 0.875f, (float)z + 1);
		return &tmpBB;
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__SoulSandTile_H__*/
