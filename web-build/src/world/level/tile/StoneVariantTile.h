#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__StoneVariantTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__StoneVariantTile_H__

#include "Tile.h"
#include "../material/Material.h"

class StoneVariantTile : public Tile
{
	typedef Tile super;
public:
	static const int GRANITE          = 0;
	static const int POLISHED_GRANITE = 1;
	static const int DIORITE          = 2;
	static const int POLISHED_DIORITE = 3;
	static const int ANDESITE         = 4;
	static const int POLISHED_ANDESITE= 5;

	static const int TYPE_MASK = 7;

	StoneVariantTile(int id)
	:	super(id, Material::stone)
	{
		tex = 1; // fallback to stone texture
	}

	int getTexture(int face, int data) {
		switch (data & TYPE_MASK) {
			case GRANITE:           return 2 + 8 * 16;  // granite
			case POLISHED_GRANITE:  return 3 + 8 * 16;  // polished granite
			case DIORITE:           return 4 + 8 * 16;  // diorite
			case POLISHED_DIORITE:  return 5 + 8 * 16;  // polished diorite
			case ANDESITE:          return 6 + 8 * 16;  // andesite
			case POLISHED_ANDESITE: return 7 + 8 * 16;  // polished andesite
			default:                return 1;            // stone fallback
		}
	}

	int getTexture(int face) {
		return getTexture(face, 0);
	}

	int getResource(int data, Random* random) {
		return id;
	}

protected:
	int getSpawnResourcesAuxValue(int data) {
		return data & TYPE_MASK;
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__StoneVariantTile_H__*/
