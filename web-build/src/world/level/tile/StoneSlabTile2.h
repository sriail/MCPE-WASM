#ifndef STONE_SLAB_TILE2_H
#define STONE_SLAB_TILE2_H

#include "Tile.h"
#include "../material/Material.h"
#include "../../client/renderer/TerrainAtlas.h"
#include "../LevelSource.h"
#include "../../Facing.h"

class StoneSlabTile2 : public Tile {
	typedef Tile super;
public:
	static const int TYPE_MASK = 0x7;
	static const int TOP_SLOT_BIT = 0x8;

	static const int ANDESITE_SLAB = 0;
	static const int NETHER_BRICK_SLAB = 1;
	static const int SMOOTH_STONE_SLAB = 2;
	static const int POLISHED_GRANITE_SLAB = 3;
	static const int POLISHED_DIORITE_SLAB = 4;
	static const int POLISHED_ANDESITE_SLAB = 5;
	static const int POLISHED_SANDSTONE_SLAB = 6;

	bool fullSize;

	StoneSlabTile2(int id, bool fullSize)
		: super(id, texCoord(16, 4), Material::stone), fullSize(fullSize)
	{
		if (!fullSize)
			setShape(0, 0, 0, 1, 0.5f, 1);
		setLightBlock(255);
	}

	int getTexture(int face, int data) override {
		switch (data & TYPE_MASK) {
			case ANDESITE_SLAB:           return texCoord(16, 4);
			case NETHER_BRICK_SLAB:       return texCoord(0, 14);
			case SMOOTH_STONE_SLAB:       return texCoord(2, 15);
			case POLISHED_GRANITE_SLAB:   return texCoord(16, 1);
			case POLISHED_DIORITE_SLAB:   return texCoord(16, 3);
			case POLISHED_ANDESITE_SLAB:  return texCoord(16, 5);
			case POLISHED_SANDSTONE_SLAB: return texCoord(5, 14);
			default: return texCoord(16, 4);
		}
	}

	int getTexture(LevelSource* level, int x, int y, int z, int face) override {
		return getTexture(face, level->getData(x, y, z));
	}

	bool isCubeShaped() override { return fullSize; }
	bool isSolidRender() override { return fullSize; }

	void updateShape(LevelSource* level, int x, int y, int z) override {
		if (fullSize) { setShape(0, 0, 0, 1, 1, 1); return; }
		int data = level->getData(x, y, z);
		if (data & TOP_SLOT_BIT) setShape(0, 0.5f, 0, 1, 1, 1);
		else setShape(0, 0, 0, 1, 0.5f, 1);
	}

	void updateDefaultShape() override {
		if (fullSize) setShape(0, 0, 0, 1, 1, 1);
		else setShape(0, 0, 0, 1, 0.5f, 1);
	}

	int getPlacedOnFaceDataValue(Level* level, int x, int y, int z, int face, float clickX, float clickY, float clickZ, int itemValue) override {
		if (fullSize) return itemValue;
		if (face == Facing::DOWN || (face != Facing::UP && clickY > 0.5))
			return itemValue | TOP_SLOT_BIT;
		return itemValue;
	}

	int getResource(int data, Random* random) override {
		return fullSize ? Tile::smoothStoneSlabHalf->id : id;
	}

	int getResourceCount(Random* random) override {
		return fullSize ? 2 : 1;
	}

protected:
	int getSpawnResourcesAuxValue(int data) override {
		return data & TYPE_MASK;
	}
};

#endif
