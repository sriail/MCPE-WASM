#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__StoneSlabTile2_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__StoneSlabTile2_H__

#include "StoneSlabTile.h"
#include "Tile.h"
#include "../material/Material.h"
#include "../../../util/Random.h"
#include "../LevelSource.h"
#include "../../Facing.h"

// Second stone slab tile for additional slab types beyond the first 6
class StoneSlabTile2 : public Tile
{
	typedef Tile super;
public:
	static const int GRANITE_SLAB     = 0;
	static const int DIORITE_SLAB     = 1;
	static const int ANDESITE_SLAB    = 2;
	static const int POLISHED_GRANITE_SLAB = 3;
	static const int POLISHED_DIORITE_SLAB = 4;
	static const int POLISHED_ANDESITE_SLAB = 5;
	static const int NETHER_BRICK_SLAB = 6;
	static const int SMOOTH_STONE_SLAB = 7;

	static const int TYPE_MASK = 7;
	static const int TOP_SLOT_BIT = 8;

	StoneSlabTile2(int id, bool fullSize)
	:	super(id, 6, Material::stone),
		fullSize(fullSize)
	{
		if (!fullSize)
			setShape(0, 0, 0, 1, 0.5f, 1);
		setLightBlock(255);
	}

	int getTexture(int face, int data) {
		switch (data & TYPE_MASK) {
			case GRANITE_SLAB:           return 10 + 13 * 16; // granite          (160, 208)
			case DIORITE_SLAB:           return  8 + 14 * 16; // diorite           (128, 224)
			case ANDESITE_SLAB:          return  5 + 11 * 16; // andesite          (80, 176)
			case POLISHED_GRANITE_SLAB:  return 10 + 14 * 16; // polished granite  (160, 224)
			case POLISHED_DIORITE_SLAB:  return  9 + 14 * 16; // polished diorite  (144, 224)
			case POLISHED_ANDESITE_SLAB: return  4 + 11 * 16; // polished andesite (64, 176)
			case NETHER_BRICK_SLAB:      return  0 + 14 * 16; // nether brick
			case SMOOTH_STONE_SLAB:      return 6;             // smooth stone
			default: return 6;
		}
	}

	int getTexture(int face) {
		return getTexture(face, 0);
	}

	bool isSolidRender() {
		return fullSize;
	}

	bool isCubeShaped() {
		return fullSize;
	}

	void updateShape(LevelSource* level, int x, int y, int z) {
		if (fullSize) {
			setShape(0, 0, 0, 1, 1, 1);
		} else {
			bool upper = (level->getData(x, y, z) & TOP_SLOT_BIT) != 0;
			if (upper) {
				setShape(0, 0.5f, 0, 1, 1, 1);
			} else {
				setShape(0, 0, 0, 1, 0.5f, 1);
			}
		}
	}

	void updateDefaultShape() {
		if (fullSize) {
			setShape(0, 0, 0, 1, 1, 1);
		} else {
			setShape(0, 0, 0, 1, 0.5f, 1);
		}
	}

	int getPlacedOnFaceDataValue(Level* level, int x, int y, int z, int face, float clickX, float clickY, float clickZ, int itemValue) {
		if (fullSize) return itemValue;
		if (face == Facing::DOWN || (face != Facing::UP && clickY > 0.5))
			return itemValue | TOP_SLOT_BIT;
		return itemValue;
	}

	int getResource(int data, Random* random) {
		return id; // drop the half slab variant
	}

	int getResourceCount(Random* random) {
		return fullSize ? 2 : 1;
	}

	void addAABBs(Level* level, int x, int y, int z, const AABB* box, std::vector<AABB>& boxes) {
		updateShape(level, x, y, z);
		super::addAABBs(level, x, y, z, box, boxes);
	}

	bool shouldRenderFace(LevelSource* level, int x, int y, int z, int face) {
		if (fullSize) return super::shouldRenderFace(level, x, y, z, face);

		if (face != Facing::UP && face != Facing::DOWN && !super::shouldRenderFace(level, x, y, z, face))
			return false;

		return true;
	}

protected:
	int getSpawnResourcesAuxValue(int data) {
		return data & TYPE_MASK;
	}

private:
	bool fullSize;
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__StoneSlabTile2_H__*/
