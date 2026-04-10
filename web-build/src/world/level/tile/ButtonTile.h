#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__ButtonTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__ButtonTile_H__

//package net.minecraft.world.level.tile;

#include "Tile.h"
#include "LevelEvent.h"
#include "../Level.h"
#include "../material/Material.h"
#include "../../entity/player/Player.h"
#include "../../phys/AABB.h"

class ButtonTile: public Tile
{
	typedef Tile super;

	static const int DIRECTION_MASK = 7;
	static const int PRESSED_BIT = 8;

public:
	ButtonTile(int id, int tex)
	:	super(id, tex, Material::decoration)
	{
		setTicking(true);
	}

	bool isSolidRender() { return false; }
	bool isCubeShaped() { return false; }

	int getRenderShape() {
		return Tile::SHAPE_BLOCK; // Renders as a small cube
	}

	AABB* getAABB(Level* level, int x, int y, int z) {
		return NULL; // no collision
	}

	bool mayPlace(Level* level, int x, int y, int z) {
		return level->isSolidBlockingTile(x - 1, y, z)
			|| level->isSolidBlockingTile(x + 1, y, z)
			|| level->isSolidBlockingTile(x, y, z - 1)
			|| level->isSolidBlockingTile(x, y, z + 1);
	}

	int getPlacedOnFaceDataValue(Level* level, int x, int y, int z, int face, float clickX, float clickY, float clickZ, int itemValue) {
		int data = 0;
		switch (face) {
			case 2: data = 4; break;
			case 3: data = 3; break;
			case 4: data = 2; break;
			case 5: data = 1; break;
			default: data = 1; break;
		}
		return data;
	}

	void updateShape(LevelSource* level, int x, int y, int z) {
		int data = level->getData(x, y, z);
		int dir = data & DIRECTION_MASK;
		bool pressed = (data & PRESSED_BIT) != 0;
		float depth = pressed ? 1.0f / 16.0f : 2.0f / 16.0f;

		if (dir == 1) setShape(0, 5.0f/16, 6.0f/16, depth, 11.0f/16, 10.0f/16);
		else if (dir == 2) setShape(1.0f-depth, 5.0f/16, 6.0f/16, 1, 11.0f/16, 10.0f/16);
		else if (dir == 3) setShape(6.0f/16, 5.0f/16, 0, 10.0f/16, 11.0f/16, depth);
		else if (dir == 4) setShape(6.0f/16, 5.0f/16, 1.0f-depth, 10.0f/16, 11.0f/16, 1);
	}

	void neighborChanged(Level* level, int x, int y, int z, int type) {
		int data = level->getData(x, y, z);
		int dir = data & DIRECTION_MASK;
		bool canStay = false;

		if (dir == 1 && level->isSolidBlockingTile(x - 1, y, z)) canStay = true;
		if (dir == 2 && level->isSolidBlockingTile(x + 1, y, z)) canStay = true;
		if (dir == 3 && level->isSolidBlockingTile(x, y, z - 1)) canStay = true;
		if (dir == 4 && level->isSolidBlockingTile(x, y, z + 1)) canStay = true;

		if (!canStay) {
			spawnResources(level, x, y, z, data);
			level->setTile(x, y, z, 0);
		}
	}

	bool use(Level* level, int x, int y, int z, Player* player) {
		int data = level->getData(x, y, z);
		int dir = data & DIRECTION_MASK;
		level->setData(x, y, z, dir | PRESSED_BIT);

		level->levelEvent(player, LevelEvent::SOUND_CLICK, x, y, z, 0);

		level->updateNeighborsAt(x, y, z, id);
		if (dir == 1) level->updateNeighborsAt(x - 1, y, z, id);
		if (dir == 2) level->updateNeighborsAt(x + 1, y, z, id);
		if (dir == 3) level->updateNeighborsAt(x, y, z - 1, id);
		if (dir == 4) level->updateNeighborsAt(x, y, z + 1, id);

		level->addToTickNextTick(x, y, z, id, 20); // 1 second

		return true;
	}

	void tick(Level* level, int x, int y, int z, Random* random) {
		int data = level->getData(x, y, z);
		if ((data & PRESSED_BIT) == 0) return;

		int dir = data & DIRECTION_MASK;
		level->setData(x, y, z, dir); // un-press

		level->updateNeighborsAt(x, y, z, id);
		if (dir == 1) level->updateNeighborsAt(x - 1, y, z, id);
		if (dir == 2) level->updateNeighborsAt(x + 1, y, z, id);
		if (dir == 3) level->updateNeighborsAt(x, y, z - 1, id);
		if (dir == 4) level->updateNeighborsAt(x, y, z + 1, id);

		level->levelEvent(NULL, LevelEvent::SOUND_CLICK, x, y, z, 0);
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
		int data = level->getData(x, y, z);
		if ((data & PRESSED_BIT) == 0) return false;
		int facing = data & DIRECTION_MASK;
		if (facing == 1 && dir == 4) return true;
		if (facing == 2 && dir == 5) return true;
		if (facing == 3 && dir == 2) return true;
		if (facing == 4 && dir == 3) return true;
		return false;
	}

	int getResource(int data, Random* random) {
		return id;
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__ButtonTile_H__*/
