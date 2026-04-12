#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__RedstoneLampTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__RedstoneLampTile_H__

#include "Tile.h"
#include "../material/Material.h"

class RedstoneLampTile : public Tile {
	typedef Tile super;
public:
	bool lit;

	RedstoneLampTile(int id, int tex, bool isLit)
	:	super(id, tex, Material::glass),
		lit(isLit)
	{
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__RedstoneLampTile_H__*/
