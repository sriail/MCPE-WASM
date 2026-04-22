#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__WoodPlanks_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__WoodPlanks_H__

#include "Tile.h"
#include "../material/Material.h"
#include "../../../util/Random.h"

// Wood planks using data values on ID 5:
// 0 = Oak, 1 = Spruce, 2 = Birch, 3 = Jungle

class WoodPlanks : public Tile {
	typedef Tile super;
public:
	static const int OAK = 0;
	static const int SPRUCE = 1;
	static const int BIRCH = 2;
	static const int JUNGLE = 3;
	static const int VARIANT_COUNT = 4;

	static const int PLANK_TEXTURES[VARIANT_COUNT];

	WoodPlanks(int id) : super(id, 4, Material::wood) {
	}

	int getTexture(int face, int data) {
		if (data >= 0 && data < VARIANT_COUNT)
			return PLANK_TEXTURES[data];
		return 4; // oak default
	}

	int getTexture(int face) {
		return getTexture(face, 0);
	}

	int getResource(int data, Random* random) {
		return id;
	}

protected:
	int getSpawnResourcesAuxValue(int data) {
		return data;
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__WoodPlanks_H__*/
