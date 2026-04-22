#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__StoneVariantTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__StoneVariantTile_H__

#include "Tile.h"
#include "../material/Material.h"
#include "../../../util/Random.h"

// Stone variants using data values on ID 19:
// 0 = Granite, 1 = Polished Granite
// 2 = Diorite, 3 = Polished Diorite
// 4 = Andesite, 5 = Polished Andesite

class StoneVariantTile : public Tile {
	typedef Tile super;
public:
	static const int GRANITE = 0;
	static const int POLISHED_GRANITE = 1;
	static const int DIORITE = 2;
	static const int POLISHED_DIORITE = 3;
	static const int ANDESITE = 4;
	static const int POLISHED_ANDESITE = 5;
	static const int VARIANT_COUNT = 6;

	static const int VARIANT_TEXTURES[VARIANT_COUNT];

	StoneVariantTile(int id) : super(id, Material::stone) {
		tex = VARIANT_TEXTURES[0]; // default to granite
	}

	int getTexture(int face, int data) {
		if (data >= 0 && data < VARIANT_COUNT)
			return VARIANT_TEXTURES[data];
		return VARIANT_TEXTURES[0];
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

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__StoneVariantTile_H__*/
