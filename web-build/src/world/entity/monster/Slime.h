#ifndef NET_MINECRAFT_WORLD_ENTITY_MONSTER__Slime_H__
#define NET_MINECRAFT_WORLD_ENTITY_MONSTER__Slime_H__

#include "../Mob.h"
#include "../EntityRendererId.h"
#include "../EntityTypes.h"
#include "../../item/Item.h"
#include "../../level/Level.h"
#include "../../../util/Mth.h"

class Slime : public Mob
{
	typedef Mob super;
public:
	int slimeSize; // 0=small, 1=medium, 2=large

	Slime(Level* level)
	:	super(level),
		slimeSize(2) // default large
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
	}

	int getMaxHealth() {
		switch (slimeSize) {
			case 0: return 1;  // small
			case 1: return 4;  // medium
			case 2: return 16; // large
			default: return 1;
		}
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
