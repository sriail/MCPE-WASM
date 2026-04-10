#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__WoodPlanks_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__WoodPlanks_H__

//package net.minecraft.world.level.tile;

#include "../material/Material.h"

#include "Tile.h"

class WoodPlanks: public Tile
{
	typedef Tile super;
public:
	// Data values
	static const int DATA_OAK    = 0;
	static const int DATA_SPRUCE = 1;
	static const int DATA_BIRCH  = 2;
	static const int DATA_COUNT  = 3;

	// Texture indices (col + row * 16)
	static const int TEX_OAK    = 4;              // existing oak planks texture
	static const int TEX_SPRUCE = 3 + 8 * 16;     // 131
	static const int TEX_BIRCH  = 4 + 8 * 16;     // 132

	WoodPlanks(int id)
	:	super(id, TEX_OAK, Material::wood)
	{
	}

	int getTexture(int face, int data) {
		switch (data) {
			case DATA_OAK:    return TEX_OAK;
			case DATA_SPRUCE: return TEX_SPRUCE;
			case DATA_BIRCH:  return TEX_BIRCH;
			default:          return TEX_OAK;
		}
	}

	int getTexture(int face) {
		return TEX_OAK;
	}

protected:
	int getSpawnResourcesAuxValue(int data) {
		return data;
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__WoodPlanks_H__*/
