#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__RedstoneDustTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__RedstoneDustTile_H__

#include "Tile.h"
#include "../material/Material.h"

// Placed redstone dust tile.
// Renders using SHAPE_RED_DUST.
// terrain.png line texture: (80,160) = col=5, row=10 = 5+10*16=165
// terrain.png intersection: (64,160) = col=4, row=10 = 4+10*16=164
class RedstoneDustTile : public Tile
{
	typedef Tile super;
public:
	RedstoneDustTile(int id, int tex)
	:	super(id, tex, Material::decoration)
	{
	}

	bool isSolidRender() {
		return false;
	}

	bool isCubeShaped() {
		return false;
	}

	int getRenderShape() {
		return Tile::SHAPE_RED_DUST;
	}

	int getRenderLayer() {
		return Tile::RENDERLAYER_ALPHATEST;
	}

	AABB* getAABB(Level* level, int x, int y, int z) {
		return NULL;
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__RedstoneDustTile_H__*/
