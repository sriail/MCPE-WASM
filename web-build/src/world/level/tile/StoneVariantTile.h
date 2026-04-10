#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__StoneVariantTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__StoneVariantTile_H__

//package net.minecraft.world.level.tile;

#include "../material/Material.h"

#include "Tile.h"

class StoneVariantTile: public Tile
{
	typedef Tile super;
public:
	// Data values
	static const int DATA_GRANITE          = 0;
	static const int DATA_POLISHED_GRANITE = 1;
	static const int DATA_DIORITE          = 2;
	static const int DATA_POLISHED_DIORITE = 3;
	static const int DATA_ANDESITE         = 4;
	static const int DATA_POLISHED_ANDESITE = 5;
	static const int DATA_COUNT            = 6;

	// Texture indices (col + row * 16)
	static const int TEX_GRANITE           = 5 + 8 * 16;  // 133
	static const int TEX_POLISHED_GRANITE  = 6 + 8 * 16;  // 134
	static const int TEX_DIORITE           = 7 + 8 * 16;  // 135
	static const int TEX_POLISHED_DIORITE  = 11 + 8 * 16; // 139
	static const int TEX_ANDESITE          = 12 + 8 * 16; // 140
	static const int TEX_POLISHED_ANDESITE = 13 + 8 * 16; // 141

	StoneVariantTile(int id)
	:	super(id, TEX_GRANITE, Material::stone)
	{
	}

	virtual int getTexture(int face, int data) {
		switch (data) {
			case DATA_GRANITE:           return TEX_GRANITE;
			case DATA_POLISHED_GRANITE:  return TEX_POLISHED_GRANITE;
			case DATA_DIORITE:           return TEX_DIORITE;
			case DATA_POLISHED_DIORITE:  return TEX_POLISHED_DIORITE;
			case DATA_ANDESITE:          return TEX_ANDESITE;
			case DATA_POLISHED_ANDESITE: return TEX_POLISHED_ANDESITE;
			default:                     return TEX_GRANITE;
		}
	}

	static int getTileDataForItemAuxValue(int auxValue) {
		return (auxValue & 0xf);
	}

protected:
	int getSpawnResourcesAuxValue(int data) {
		return data;
	}

	static int getItemAuxValueForTileData(int data) {
		return data;
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__StoneVariantTile_H__*/
