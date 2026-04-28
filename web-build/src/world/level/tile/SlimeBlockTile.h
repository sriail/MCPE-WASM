#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__SlimeBlockTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__SlimeBlockTile_H__

#include "Tile.h"
#include "../material/Material.h"
#include "../../entity/Entity.h"
#include <cmath>

class SlimeBlockTile : public Tile
{
	typedef Tile super;
public:
	SlimeBlockTile(int id, int tex)
	:	super(id, tex, Material::clay)
	{
	}

	bool isSolidRender() {
		return false;
	}

	int getRenderLayer() {
		return Tile::RENDERLAYER_BLEND;
	}

	void fallOn(Level* level, int x, int y, int z, Entity* entity, float fallDistance) {
		// Negate fall damage
		entity->fallDistance = 0;
	}

	void stepOn(Level* level, int x, int y, int z, Entity* entity) {
		// Bounce entity upward if falling
		if (entity->yd < 0) {
			entity->yd = -entity->yd;
		}
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__SlimeBlockTile_H__*/
