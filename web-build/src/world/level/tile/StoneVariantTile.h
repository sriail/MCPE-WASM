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
			case GRANITE:           return 10 + 13 * 16; // granite          (160, 208)
			case POLISHED_GRANITE:  return 10 + 14 * 16; // polished granite  (160, 224)
			case DIORITE:           return  8 + 14 * 16; // diorite           (128, 224)
			case POLISHED_DIORITE:  return  9 + 14 * 16; // polished diorite  (144, 224)
			case ANDESITE:          return  5 + 11 * 16; // andesite          (80, 176)
			case POLISHED_ANDESITE: return  4 + 11 * 16; // polished andesite (64, 176)
			default:                return 1;             // stone fallback
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
