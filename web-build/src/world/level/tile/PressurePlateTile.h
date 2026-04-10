#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__PressurePlateTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__PressurePlateTile_H__

//package net.minecraft.world.level.tile;

#include "Tile.h"
#include "../Level.h"
#include "../../phys/AABB.h"
#include "../../entity/Entity.h"

class PressurePlateTile: public Tile
{
	typedef Tile super;

	static const int PRESSED_BIT = 1;
	int plateTex;

public:
	PressurePlateTile(int id, int tex, const Material* material)
	:	super(id, tex, material),
		plateTex(tex)
	{
		setTicking(true);
		setShape(1.0f/16, 0, 1.0f/16, 15.0f/16, 1.0f/16, 15.0f/16);
	}

	bool isSolidRender() { return false; }
	bool isCubeShaped() { return false; }

	int getRenderShape() {
		return Tile::SHAPE_BLOCK;
	}

	AABB* getAABB(Level* level, int x, int y, int z) {
		return NULL; // no collision box
	}

	void updateShape(LevelSource* level, int x, int y, int z) {
		int data = level->getData(x, y, z);
		bool pressed = (data & PRESSED_BIT) != 0;
		if (pressed) {
			setShape(1.0f/16, 0, 1.0f/16, 15.0f/16, 0.5f/16, 15.0f/16);
		} else {
			setShape(1.0f/16, 0, 1.0f/16, 15.0f/16, 1.0f/16, 15.0f/16);
		}
	}

	int getTickDelay() {
		return 20;
	}

	bool mayPlace(Level* level, int x, int y, int z) {
		return level->isSolidBlockingTile(x, y - 1, z);
	}

	void neighborChanged(Level* level, int x, int y, int z, int type) {
		if (!level->isSolidBlockingTile(x, y - 1, z)) {
			spawnResources(level, x, y, z, level->getData(x, y, z));
			level->setTile(x, y, z, 0);
		}
	}

	void tick(Level* level, int x, int y, int z, Random* random) {
		int data = level->getData(x, y, z);
		if ((data & PRESSED_BIT) == 0) return;

		// Check if any entities are still on the plate
		AABB checkBox(x + 1.0f/16, y, z + 1.0f/16, x + 15.0f/16, y + 0.25f, z + 15.0f/16);
		EntityList& entities = level->getEntities(NULL, checkBox);
		if (entities.empty()) {
			level->setData(x, y, z, 0); // unpress
			level->updateNeighborsAt(x, y, z, id);
			level->updateNeighborsAt(x, y - 1, z, id);
		} else {
			level->addToTickNextTick(x, y, z, id, getTickDelay());
		}
	}

	void entityInside(Level* level, int x, int y, int z, Entity* entity) {
		int data = level->getData(x, y, z);
		if ((data & PRESSED_BIT) == 0) {
			level->setData(x, y, z, PRESSED_BIT);
			level->updateNeighborsAt(x, y, z, id);
			level->updateNeighborsAt(x, y - 1, z, id);
			level->addToTickNextTick(x, y, z, id, getTickDelay());
		}
	}

	bool isSignalSource() {
		return true;
	}

	bool getSignal(LevelSource* level, int x, int y, int z) {
		return (level->getData(x, y, z) & PRESSED_BIT) != 0;
	}

	bool getSignal(LevelSource* level, int x, int y, int z, int dir) {
		return (level->getData(x, y, z) & PRESSED_BIT) != 0;
	}

	bool getDirectSignal(Level* level, int x, int y, int z, int dir) {
		if ((level->getData(x, y, z) & PRESSED_BIT) == 0) return false;
		return dir == 0; // power downward
	}

	int getResource(int data, Random* random) {
		return id;
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__PressurePlateTile_H__*/
