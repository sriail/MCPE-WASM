#ifndef NET_MINECRAFT_WORLD_ENTITY_MONSTER__Enderman_H__
#define NET_MINECRAFT_WORLD_ENTITY_MONSTER__Enderman_H__

#include "Monster.h"
#include "../EntityRendererId.h"
#include "../EntityTypes.h"
#include "../../item/Item.h"
#include "../../level/Level.h"
#include "../../level/tile/Tile.h"
#include "../../../util/Mth.h"
#include "../player/Player.h"

class Enderman : public Monster
{
	typedef Monster super;
public:
	int carriedBlock;
	int carriedData;
	bool isAngry;      // true when provoked by player stare
	int angerCooldown; // ticks until anger resets (200 ticks = ~10 s at 20 TPS)

	Enderman(Level* level)
	:	super(level),
		carriedBlock(0),
		carriedData(0),
		isAngry(false),
		angerCooldown(0)
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

	int getMobXpDrop() { return 5; } // Enderman drops 5 XP

	int getEntityTypeId() const {
		return MobTypes::Enderman;
	}

	bool canSpawn() {
		// Spawns at night in overworld
		return !level->isDay();
	}

	void aiStep() {
		// Teleport away from water/rain
		int bx = Mth::floor(x);
		int by = Mth::floor(y);
		int bz = Mth::floor(z);
		int tile = level->getTile(bx, by, bz);
		if (tile == Tile::water->id || tile == Tile::calmWater->id) {
			teleportRandomly();
		}
		// Also teleport if it's raining (check sky exposure — use sky light depth as proxy)
		if (level->canSeeSky(bx, by + 1, bz)) {
			// Sky-exposed enderman teleport away (simulates rain avoidance)
			if (random.nextInt(20) == 0) {
				teleportRandomly();
			}
		}

		// Stare detection: check if any player is looking at us
		if (!isAngry) {
			Player* looking = getPlayerStaringAtMe();
			if (looking != NULL) {
				isAngry = true;
				// Stay angry for 200 ticks (~10 s at 20 TPS)
				static const int ANGER_DURATION_TICKS = 200;
				angerCooldown = ANGER_DURATION_TICKS;
			}
		} else {
			if (--angerCooldown <= 0) {
				isAngry = false;
			}
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
		int bz = Mth::floor(nz);

		// Search downward from ny+16 to ny-16 for a valid landing spot
		for (int i = 16; i >= -16; i--) {
			int ty = Mth::floor(ny) + i;
			if (ty < 1 || ty >= 127) continue;
			int below = level->getTile(bx, ty - 1, bz);
			int at    = level->getTile(bx, ty,     bz);
			int above = level->getTile(bx, ty + 1, bz);
			if (below != 0 && at == 0 && above == 0) {
				moveTo(nx, (float)ty, nz, yRot, xRot);
				return;
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

private:
	// Returns any player that is currently looking at this Enderman (within 64 blocks)
	Player* getPlayerStaringAtMe() {
		Player* nearest = level->getNearestPlayer(this, 64.0f);
		if (nearest == NULL) return NULL;

		// Check if the player's look vector aims within ~8 degrees of this Enderman's eyes
		// (dot product threshold 0.99 corresponds to arccos(0.99) ≈ 8.1°)
		float dx = x - nearest->x;
		float dy = (y + 1.5f) - (nearest->y + nearest->getHeadHeight());
		float dz = z - nearest->z;
		float dist = sqrtf(dx*dx + dy*dy + dz*dz);
		if (dist > 64.0f || dist < 0.001f) return NULL;

		// Player's look direction in world space (yaw/pitch → unit vector)
		float yawRad   = nearest->yRot  * Mth::PI / 180.0f;
		float pitchRad = nearest->xRot  * Mth::PI / 180.0f;
		float lx = -sinf(yawRad) * cosf(pitchRad);
		float ly = -sinf(pitchRad);
		float lz =  cosf(yawRad) * cosf(pitchRad);

		// Dot product to measure alignment (no separate comment needed; see header comment above)
		float dot = (dx/dist)*lx + (dy/dist)*ly + (dz/dist)*lz;
		return (dot > 0.99f) ? nearest : NULL; // ~8.1° cone
	}
};

#endif /*NET_MINECRAFT_WORLD_ENTITY_MONSTER__Enderman_H__*/
