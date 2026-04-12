#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__SmoothStoneTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__SmoothStoneTile_H__

#include "Tile.h"
#include "../material/Material.h"

class SmoothStoneTile : public Tile {
	typedef Tile super;
public:
	int topTex;

	SmoothStoneTile(int id, int sideTex, int topBottomTex)
	:	super(id, sideTex, Material::stone),
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

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__SmoothStoneTile_H__*/
