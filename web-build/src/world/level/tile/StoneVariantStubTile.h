#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__StoneVariantStubTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__StoneVariantStubTile_H__

// Stub tiles that wrap a single texture from StoneVariantTile, so that
// StairTile/SlabTile can use them as a base.

#include "Tile.h"
#include "../material/Material.h"

class StoneVariantStubTile: public Tile
{
public:
	int texIdx;

	StoneVariantStubTile(int id, int texIdx)
	:	Tile(id, texIdx, Material::stone),
		texIdx(texIdx)
	{
	}

	int getTexture(int face) {
		return texIdx;
	}

	int getTexture(int face, int data) {
		return texIdx;
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__StoneVariantStubTile_H__*/
