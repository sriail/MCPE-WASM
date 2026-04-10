#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__DiodeTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__DiodeTile_H__

//package net.minecraft.world.level.tile;

#include "Tile.h"
#include "LevelEvent.h"
#include "../Level.h"
#include "../material/Material.h"
#include "../../entity/Mob.h"
#include "../../entity/player/Player.h"
#include "../../../util/Mth.h"

class DiodeTile: public Tile
{
	typedef Tile super;

	static const int DIRECTION_MASK = 3;
	static const int DELAY_MASK = 12;

	bool lit;

public:
	// Texture indices
	static const int TEX_TOP  = 10 + 10 * 16; // 170

	DiodeTile(int id, bool lit)
	:	super(id, TEX_TOP, Material::decoration),
		lit(lit)
	{
		setShape(0, 0, 0, 1, 2.0f / 16.0f, 1);
		if (lit) setTicking(true);
	}

	bool isSolidRender() { return false; }
	bool isCubeShaped() { return false; }

	int getRenderShape() {
		return Tile::SHAPE_DIODE;
	}

	bool mayPlace(Level* level, int x, int y, int z) {
		return level->isSolidBlockingTile(x, y - 1, z);
	}

	AABB* getAABB(Level* level, int x, int y, int z) {
		tmpBB.set((float)x, (float)y, (float)z, (float)(x + 1), (float)y + 2.0f / 16.0f, (float)(z + 1));
		return &tmpBB;
	}

	void setPlacedBy(Level* level, int x, int y, int z, Mob* by) {
		int dir = (Mth::floor(by->yRot * 4 / 360 + 0.5f)) & 3;
		level->setData(x, y, z, dir);
	}

	bool use(Level* level, int x, int y, int z, Player* player) {
		int data = level->getData(x, y, z);
		int delay = (data & DELAY_MASK) >> 2;
		delay = (delay + 1) % 4;
		level->setData(x, y, z, (data & DIRECTION_MASK) | (delay << 2));
		return true;
	}

	void neighborChanged(Level* level, int x, int y, int z, int type) {
		if (!level->isSolidBlockingTile(x, y - 1, z)) {
			spawnResources(level, x, y, z, level->getData(x, y, z));
			level->setTile(x, y, z, 0);
			return;
		}

		// Check for input signal change
		bool powered = hasInputSignal(level, x, y, z);
		if (lit && !powered) {
			level->addToTickNextTick(x, y, z, id, getTickDelay());
		} else if (!lit && powered) {
			level->addToTickNextTick(x, y, z, id, getTickDelay());
		}
	}

	int getTickDelay() {
		return 2; // base delay, multiplied by delay setting
	}

	void tick(Level* level, int x, int y, int z, Random* random) {
		int data = level->getData(x, y, z);
		bool powered = hasInputSignal(level, x, y, z);

		if (lit && !powered) {
			// Turn off
			level->setTileAndData(x, y, z, Tile::diode_off->id, data);
		} else if (!lit) {
			// Turn on
			level->setTileAndData(x, y, z, Tile::diode_on->id, data);
			if (!powered) {
				int delay = (data & DELAY_MASK) >> 2;
				level->addToTickNextTick(x, y, z, Tile::diode_on->id, (delay + 1) * 2);
			}
		}
	}

	bool hasInputSignal(Level* level, int x, int y, int z) {
		int data = level->getData(x, y, z);
		int dir = data & DIRECTION_MASK;
		// Check the block behind the repeater
		switch (dir) {
			case 0: return level->getSignal(x, y, z + 1, 3);
			case 1: return level->getSignal(x - 1, y, z, 4);
			case 2: return level->getSignal(x, y, z - 1, 2);
			case 3: return level->getSignal(x + 1, y, z, 5);
		}
		return false;
	}

	bool isSignalSource() {
		return true;
	}

	bool getSignal(LevelSource* level, int x, int y, int z, int dir) {
		return lit;
	}

	bool getSignal(LevelSource* level, int x, int y, int z) {
		return lit;
	}

	int getResource(int data, Random* random) {
		return Item::diode->id;
	}

	int getResourceCount(Random* random) {
		return 1;
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__DiodeTile_H__*/
