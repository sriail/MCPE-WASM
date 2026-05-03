#ifndef NET_MINECRAFT_WORLD_ENTITY_MONSTER__Enderman_H__
#define NET_MINECRAFT_WORLD_ENTITY_MONSTER__Enderman_H__

#include "Monster.h"
#include "../EntityRendererId.h"
#include "../EntityTypes.h"
#include "../../item/Item.h"
#include "../../level/Level.h"
#include "../../level/tile/Tile.h"
#include "../../../util/Mth.h"

class Enderman : public Monster
{
	typedef Monster super;
public:
	int carriedBlock;
	int carriedData;

	Enderman(Level* level)
	:	super(level),
		carriedBlock(0),
		carriedData(0)
	{
		entityRendererId = ER_ZOMBIE_RENDERER; // reuse zombie renderer for now
		textureName = "mob/zombie.png"; // placeholder - no enderman texture in repo
		setSize(0.6f, 2.9f);
		runSpeed = 0.7f;
		attackDamage = 7;
	}

	int getMaxHealth() {
		return 40;
	}

	int getEntityTypeId() const {
		return MobTypes::Enderman;
	}

	bool canSpawn() {
		// Spawns at night in overworld
		return !level->isDay();
	}

	void aiStep() {
		// Teleport away from water/rain
		// Simplified: just check if touching water
		int bx = Mth::floor(x);
		int by = Mth::floor(y);
		int bz = Mth::floor(z);
		int tile = level->getTile(bx, by, bz);
		if (tile == Tile::water->id || tile == Tile::calmWater->id) {
			teleportRandomly();
		}

		super::aiStep();
	}

	bool hurt(Entity* source, int dmg) {
		// Teleport on damage
		if (super::hurt(source, dmg)) {
			teleportRandomly();
			return true;
		}
		return false;
	}

	void die(Entity* source) {
		if (!level->isClientSide) {
			// Drop 0-1 Ender Pearl
			if (random.nextInt(2) == 0) {
				spawnAtLocation(Item::enderPearl->id, 1);
			}
		}
		super::die(source);
	}

	void teleportRandomly() {
		float nx = x + (random.nextFloat() - 0.5f) * 64.0f;
		float ny = y + (random.nextInt(64) - 32);
		float nz = z + (random.nextFloat() - 0.5f) * 64.0f;
		teleportTo(nx, ny, nz);
	}

	void teleportTo(float nx, float ny, float nz) {
		int bx = Mth::floor(nx);
		int by = Mth::floor(ny);
		int bz = Mth::floor(nz);

		// Search upward from target Y for a valid two-block-tall air gap above solid ground
		for (int i = 0; i < 32; i++) {
			int ty = by + i;
			if (ty >= 0 && ty < 127) {
				int below = level->getTile(bx, ty - 1, bz);
				int at    = level->getTile(bx, ty,     bz);
				int above = level->getTile(bx, ty + 1, bz);
				if (below != 0 && at == 0 && above == 0) {
					moveTo(nx, (float)ty, nz, yRot, xRot);
					return;
				}
			}
		}
		// Fallback: search downward from original Y
		for (int i = 1; i < 32; i++) {
			int ty = by - i;
			if (ty > 0 && ty < 127) {
				int below = level->getTile(bx, ty - 1, bz);
				int at    = level->getTile(bx, ty,     bz);
				int above = level->getTile(bx, ty + 1, bz);
				if (below != 0 && at == 0 && above == 0) {
					moveTo(nx, (float)ty, nz, yRot, xRot);
					return;
				}
			}
		}
	}

	// Can pick up and place certain blocks
	static bool canPickUpBlock(int blockId) {
		return blockId == Tile::grass->id ||
			   blockId == Tile::dirt->id ||
			   blockId == Tile::sand->id ||
			   blockId == Tile::gravel->id ||
			   blockId == Tile::flower->id ||
			   blockId == Tile::rose->id ||
			   blockId == Tile::mushroom1->id ||
			   blockId == Tile::mushroom2->id ||
			   blockId == Tile::clay->id;
	}
};

#endif /*NET_MINECRAFT_WORLD_ENTITY_MONSTER__Enderman_H__*/
