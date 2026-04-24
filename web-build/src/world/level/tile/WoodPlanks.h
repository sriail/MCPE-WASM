#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__WoodPlanks_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__WoodPlanks_H__

#include "Tile.h"
#include "../material/Material.h"

class WoodPlanks : public Tile
{
	typedef Tile super;
public:
	static const int OAK    = 0;
	static const int SPRUCE = 1;
	static const int BIRCH  = 2;

	static const int TYPE_MASK = 3;

	WoodPlanks(int id)
	:	super(id, 4, Material::wood)
	{
	}

	int getTexture(int face, int data) {
		switch (data & TYPE_MASK) {
			case OAK:    return 4;            // oak planks (tex 4)
			case SPRUCE: return 7 + 12 * 16;  // spruce planks (112, 192)
			case BIRCH:  return 8 + 11 * 16;  // birch planks  (128, 176)
			default:     return 4;
		}
	}

	int getTexture(int face) {
		return getTexture(face, 0);
	}

protected:
	int getSpawnResourcesAuxValue(int data) {
		return data & TYPE_MASK;
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__WoodPlanks_H__*/
