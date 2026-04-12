#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__SlimeBlockTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__SlimeBlockTile_H__

#include "HalfTransparentTile.h"
#include "../material/Material.h"

class SlimeBlockTile : public HalfTransparentTile {
	typedef HalfTransparentTile super;
public:
	SlimeBlockTile(int id, int tex)
	:	super(id, tex, Material::clay, true)
	{
	}

	int getRenderLayer() {
		return Tile::RENDERLAYER_BLEND;
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__SlimeBlockTile_H__*/
