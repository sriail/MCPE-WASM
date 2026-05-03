#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__MagmaBlockTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__MagmaBlockTile_H__

#include "Tile.h"
#include "../material/Material.h"
#include "../../entity/Entity.h"

// Magma block — damages entities walking on it.
// terrain.png position: (192, 128) = col=12, row=8 = 12+8*16=140
class MagmaBlockTile : public Tile
{
	typedef Tile super;
public:
	MagmaBlockTile(int id, int tex)
	:	super(id, tex, Material::stone)
	{
	}

	void stepOn(Level* level, int x, int y, int z, Entity* entity) {
		// Deal 1 fire damage per tick to any entity walking on magma
		if (entity != NULL) {
			entity->hurt(NULL, 1);
		}
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__MagmaBlockTile_H__*/
