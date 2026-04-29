#ifndef NET_MINECRAFT_WORLD_ENTITY_MONSTER__Slime_H__
#define NET_MINECRAFT_WORLD_ENTITY_MONSTER__Slime_H__

#include "../Mob.h"
#include "../EntityRendererId.h"
#include "../EntityTypes.h"
#include "../../item/Item.h"
#include "../../level/Level.h"
#include "../../level/biome/Biome.h"
#include "../../level/biome/SwampBiome.h"
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
		entityRendererId = ER_ZOMBIE_RENDERER;
		textureName = "mob/slime.png";
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
		int bx = Mth::floor(x);
		int bz = Mth::floor(z);

		// Slimes always spawn in swamp biomes between y=51 and y=69
		Biome* biome = level->getBiome(bx, bz);
		if (dynamic_cast<SwampBiome*>(biome) != NULL) {
			return (y >= 51.0f && y <= 69.0f);
		}

		// Below y=40, slimes spawn in slime chunks (deterministic per chunk)
		if (y < 40.0f) {
			int chunkX = bx >> 4;
			int chunkZ = bz >> 4;
			long seed = level->getSeed();
			// Vanilla slime chunk formula
			long hash = seed
				+ (long)(chunkX * chunkX * 0x4c1906L)
				+ (long)(chunkX * 0x5ac0dbL)
				+ (long)(chunkZ * chunkZ) * 0x4307a7L
				+ (long)(chunkZ * 0x5f24fL)
				^ 987234911L;
			Random chunkRng(hash);
			return chunkRng.nextInt(10) == 0;
		}

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
