#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__HayBaleTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__HayBaleTile_H__

#include "Tile.h"
#include "../material/Material.h"

class HayBaleTile : public Tile {
	typedef Tile super;
public:
	int topTex;

	HayBaleTile(int id, int sideTex, int topBottomTex)
	:	super(id, sideTex, Material::plant),
		topTex(topBottomTex)
	{
	}

	int getTexture(int face, int data) {
		if (face == 0 || face == 1) return topTex;
		return tex;
	}

	int getTexture(int face) {
		return getTexture(face, 0);
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__HayBaleTile_H__*/
