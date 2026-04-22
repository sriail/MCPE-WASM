#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__WallTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__WallTile_H__

#include "Tile.h"
#include "../material/Material.h"
#include "../../phys/AABB.h"

class WallTile : public Tile {
	typedef Tile super;
public:
	WallTile(int id, int tex, const Material* material)
	:	super(id, tex, material)
	{
	}

	WallTile(int id, int tex)
	:	super(id, tex, Material::stone)
	{
	}

	bool isSolidRender() { return false; }
	bool isCubeShaped() { return false; }

	int getRenderShape() {
		return Tile::SHAPE_FENCE;
	}

	AABB* getAABB(Level* level, int x, int y, int z) {
		bool n = connectsTo(level, x, y, z - 1);
		bool s = connectsTo(level, x, y, z + 1);
		bool w = connectsTo(level, x - 1, y, z);
		bool e = connectsTo(level, x + 1, y, z);

		float west = 5.0f / 16.0f;
		float east = 11.0f / 16.0f;
		float north = 5.0f / 16.0f;
		float south = 11.0f / 16.0f;

		if (n) north = 0;
		if (s) south = 1;
		if (w) west = 0;
		if (e) east = 1;

		tmpBB.set((float)x + west, (float)y, (float)z + north, (float)x + east, (float)y + 1.5f, (float)z + south);
		return &tmpBB;
	}

	void updateShape(LevelSource* level, int x, int y, int z) {
		bool n = connectsTo(level, x, y, z - 1);
		bool s = connectsTo(level, x, y, z + 1);
		bool w = connectsTo(level, x - 1, y, z);
		bool e = connectsTo(level, x + 1, y, z);

		float west = 5.0f / 16.0f;
		float east = 11.0f / 16.0f;
		float north = 5.0f / 16.0f;
		float south = 11.0f / 16.0f;

		if (n) north = 0;
		if (s) south = 1;
		if (w) west = 0;
		if (e) east = 1;

		setShape(west, 0, north, east, 1.0f, south);
	}

	bool blocksLight() { return false; }

	bool connectsTo(LevelSource* level, int x, int y, int z) {
		int tile = level->getTile(x, y, z);
		if (tile == id) return true;
		Tile* tileInstance = Tile::tiles[tile];
		if (tileInstance != NULL) {
			if (tileInstance->material->isSolidBlocking() && tileInstance->isCubeShaped()) {
				return tileInstance->material != Material::vegetable;
			}
		}
		return false;
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__WallTile_H__*/
