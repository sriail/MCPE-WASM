#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__RedstoneTorchTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__RedstoneTorchTile_H__

//package net.minecraft.world.level.tile;

#include "TorchTile.h"

class RedstoneTorchTile: public TorchTile
{
	typedef TorchTile super;
	bool lit;
public:
	RedstoneTorchTile(int id, int tex, bool lit)
	:	super(id, tex),
		lit(lit)
	{
		if (lit) {
			setTicking(true);
		}
	}

	int getTickDelay() {
		return 2;
	}

	void tick(Level* level, int x, int y, int z, Random* random) {
		// Redstone torch toggle logic
		// For now, just a basic implementation
	}

	bool isSignalSource() {
		return lit;
	}

	bool getSignal(LevelSource* level, int x, int y, int z) {
		return lit;
	}

	bool getSignal(LevelSource* level, int x, int y, int z, int dir) {
		if (!lit) return false;
		// Don't power the block the torch is attached to
		int data = level->getData(x, y, z);
		if (dir == 0 && data == 5) return false;
		if (dir == 1 && data == 0) return false; // default (on ground) - power upward not downward
		if (dir == 2 && data == 4) return false;
		if (dir == 3 && data == 3) return false;
		if (dir == 4 && data == 2) return false;
		if (dir == 5 && data == 1) return false;
		return true;
	}

	bool getDirectSignal(Level* level, int x, int y, int z, int dir) {
		// direct power downward when on ceiling isn't applicable
		// but power the block below when placed on ground
		if (dir == 0) return lit;
		return false;
	}

	int getResource(int data, Random* random) {
		// Always drop the "on" version
		return Tile::notGate_on->id;
	}

	void onRemove(Level* level, int x, int y, int z) {
		// Notify neighbors when removed
		level->updateNeighborsAt(x, y, z, id);
		level->updateNeighborsAt(x, y - 1, z, id);
		level->updateNeighborsAt(x, y + 1, z, id);
		level->updateNeighborsAt(x - 1, y, z, id);
		level->updateNeighborsAt(x + 1, y, z, id);
		level->updateNeighborsAt(x, y, z - 1, id);
		level->updateNeighborsAt(x, y, z + 1, id);
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__RedstoneTorchTile_H__*/
