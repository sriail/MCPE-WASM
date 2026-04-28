#ifndef NET_MINECRAFT_WORLD_ENTITY_MONSTER__Slime_H__
#define NET_MINECRAFT_WORLD_ENTITY_MONSTER__Slime_H__

#include "../Mob.h"
#include "../EntityRendererId.h"
#include "../EntityTypes.h"
#include "../../item/Item.h"
#include "../../level/Level.h"
#include "../../../util/Mth.h"
#include "../player/Player.h"

class Slime : public Mob
{
	typedef Mob super;
public:
	int slimeSize; // 0=small, 1=medium, 2=large
	int jumpCooldown;

	Slime(Level* level)
	:	super(level),
		slimeSize(2),
		jumpCooldown(0)
	{
		entityRendererId = ER_ZOMBIE_RENDERER; // reuse zombie renderer for now
		textureName = "mob/zombie.png"; // placeholder - no slime texture in repo
		setSlimeSize(2);
	}

	void setSlimeSize(int size) {
		slimeSize = size;
		float w = 0.6f * (1 << size);
		setSize(w, w);
		health = getMaxHealth();
		jumpCooldown = 20 + random.nextInt(20);
	}

	int getMaxHealth() {
		switch (slimeSize) {
			case 0: return 1;  // small
			case 1: return 4;  // medium
			case 2: return 16; // large
			default: return 1;
		}
	}

	int getMobXpDrop() {
		// Larger slimes give more XP
		return (slimeSize + 1) * 2;
	}

	int getEntityTypeId() const {
		return MobTypes::Slime;
	}

	int getCreatureBaseType() const {
		return MobTypes::BaseEnemy;
	}

	bool canSpawn() {
		// Slimes spawn in swamp biome or below layer 40 in slime chunks
		if (y < 40.0f) return true;
		return false;
	}

	void aiStep() {
		// Jump AI: cooldown ticks down; when expired, leap toward target (or randomly)
		if (onGround) {
			if (jumpCooldown > 0) {
				--jumpCooldown;
			} else {
				// Reset cooldown, scaled by size (larger = slower)
				jumpCooldown = 10 + random.nextInt(20) * (slimeSize + 1);

				// Jump velocity: larger slimes jump higher
				float jumpPower = 0.42f + 0.1f * (float)slimeSize;
				yd = jumpPower;

				// Horizontal velocity toward nearest player or random direction
				Player* target = level->getNearestPlayer(this, 16.0f);
				float angle;
				if (target != NULL) {
					float dx = target->x - x;
					float dz = target->z - z;
					angle = atan2f(dz, dx);
				} else {
					angle = random.nextFloat() * Mth::PI * 2.0f;
				}
				float speed = 0.1f * (float)(slimeSize + 1);
				xd = cosf(angle) * speed;
				zd = sinf(angle) * speed;
			}
		}
		super::aiStep();
	}

	void die(Entity* source) {
		if (!level->isClientSide) {
			if (slimeSize == 0) {
				// Small slimes drop 0-2 slimeballs
				int count = random.nextInt(3);
				for (int i = 0; i < count; i++) {
					spawnAtLocation(Item::slimeBall->id, 1);
				}
			} else {
				// Split into 2-4 smaller slimes
				int count = 2 + random.nextInt(3);
				for (int i = 0; i < count; i++) {
					Slime* baby = new Slime(level);
					baby->setSlimeSize(slimeSize - 1);
					baby->moveTo(
						x + (random.nextFloat() - 0.5f) * slimeSize,
						y + 0.5f,
						z + (random.nextFloat() - 0.5f) * slimeSize,
						random.nextFloat() * 360.0f, 0);
					level->addEntity(baby);
				}
			}
		}
		super::die(source);
	}
};

#endif /*NET_MINECRAFT_WORLD_ENTITY_MONSTER__Slime_H__*/
