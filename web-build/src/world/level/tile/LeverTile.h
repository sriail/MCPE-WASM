#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__LeverTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__LeverTile_H__

//package net.minecraft.world.level.tile;

#include "Tile.h"
#include "LevelEvent.h"
#include "../Level.h"
#include "../material/Material.h"
#include "../../entity/Mob.h"
#include "../../entity/player/Player.h"
#include "../../phys/AABB.h"
#include "../../../util/Mth.h"

class LeverTile: public Tile
{
	typedef Tile super;

	// Data: bits 0-2 = direction, bit 3 = on/off
	static const int DIRECTION_MASK = 7;
	static const int POWERED_BIT = 8;

public:
	LeverTile(int id, int tex)
	:	super(id, tex, Material::decoration)
	{
	}

	bool isSolidRender() { return false; }
	bool isCubeShaped() { return false; }

	int getRenderShape() {
		return Tile::SHAPE_TORCH; // Render like a torch for now
	}

	AABB* getAABB(Level* level, int x, int y, int z) {
		return NULL; // no collision
	}

	bool mayPlace(Level* level, int x, int y, int z) {
		// can place on any solid surface
		return level->isSolidBlockingTile(x - 1, y, z)
			|| level->isSolidBlockingTile(x + 1, y, z)
			|| level->isSolidBlockingTile(x, y, z - 1)
			|| level->isSolidBlockingTile(x, y, z + 1)
			|| level->isSolidBlockingTile(x, y - 1, z);
	}

	int getPlacedOnFaceDataValue(Level* level, int x, int y, int z, int face, float clickX, float clickY, float clickZ, int itemValue) {
		int data = 0;
		// face: 0=bottom, 1=top, 2=north, 3=south, 4=west, 5=east
		switch (face) {
			case 1: data = 5; break; // placed on top face - upright
			case 2: data = 4; break; // placed on north face
			case 3: data = 3; break; // placed on south face
			case 4: data = 2; break; // placed on west face
			case 5: data = 1; break; // placed on east face
			default: data = 5; break;
		}
		return data;
	}

	void neighborChanged(Level* level, int x, int y, int z, int type) {
		int data = level->getData(x, y, z);
		int dir = data & DIRECTION_MASK;
		bool canStay = false;

		if (dir == 1 && level->isSolidBlockingTile(x - 1, y, z)) canStay = true;
		if (dir == 2 && level->isSolidBlockingTile(x + 1, y, z)) canStay = true;
		if (dir == 3 && level->isSolidBlockingTile(x, y, z - 1)) canStay = true;
		if (dir == 4 && level->isSolidBlockingTile(x, y, z + 1)) canStay = true;
		if (dir == 5 && level->isSolidBlockingTile(x, y - 1, z)) canStay = true;
		if (dir == 6 && level->isSolidBlockingTile(x, y - 1, z)) canStay = true;

		if (!canStay) {
			spawnResources(level, x, y, z, data);
			level->setTile(x, y, z, 0);
		}
	}

	bool use(Level* level, int x, int y, int z, Player* player) {
		int data = level->getData(x, y, z);
		int dir = data & DIRECTION_MASK;
		data ^= POWERED_BIT; // toggle power bit
		level->setData(x, y, z, data);

		// Play click sound
		level->levelEvent(player, LevelEvent::SOUND_CLICK, x, y, z, 0);

		// Update neighbors
		level->updateNeighborsAt(x, y, z, id);
		// Also update the block the lever is attached to
		if (dir == 1) level->updateNeighborsAt(x - 1, y, z, id);
		if (dir == 2) level->updateNeighborsAt(x + 1, y, z, id);
		if (dir == 3) level->updateNeighborsAt(x, y, z - 1, id);
		if (dir == 4) level->updateNeighborsAt(x, y, z + 1, id);
		if (dir == 5 || dir == 6) level->updateNeighborsAt(x, y - 1, z, id);

		return true;
	}

	bool isSignalSource() {
		return true;
	}

	bool getSignal(LevelSource* level, int x, int y, int z) {
		return (level->getData(x, y, z) & POWERED_BIT) != 0;
	}

	bool getSignal(LevelSource* level, int x, int y, int z, int dir) {
		return (level->getData(x, y, z) & POWERED_BIT) != 0;
	}

	bool getDirectSignal(Level* level, int x, int y, int z, int dir) {
		int data = level->getData(x, y, z);
		if ((data & POWERED_BIT) == 0) return false;
		// Power the block it's attached to
		int facing = data & DIRECTION_MASK;
		if (facing == 1 && dir == 4) return true;
		if (facing == 2 && dir == 5) return true;
		if (facing == 3 && dir == 2) return true;
		if (facing == 4 && dir == 3) return true;
		if ((facing == 5 || facing == 6) && dir == 0) return true;
		return false;
	}

	int getResource(int data, Random* random) {
		return id;
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__LeverTile_H__*/
