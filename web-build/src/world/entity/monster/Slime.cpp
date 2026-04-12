#include "Slime.h"

#include "../../item/Item.h"
#include "../../level/Level.h"
#include "../../../util/Mth.h"
#include "../EntityTypes.h"
#include "../player/Player.h"

Slime::Slime(Level* level)
:	super(level),
	slimeSize(1),
	jumpDelay(0)
{
	entityRendererId = ER_SLIME_RENDERER;
	this->textureName = "mob/slime.png";

	int size = 1 << (random.nextInt(3)); // 1, 2, or 4
	initSize(size);
}

Slime::Slime(Level* level, int size)
:	super(level),
	slimeSize(1),
	jumpDelay(0)
{
	entityRendererId = ER_SLIME_RENDERER;
	this->textureName = "mob/slime.png";

	initSize(size);
}

void Slime::initSize(int size)
{
	slimeSize = size;
	setSize(0.6f * size, 0.6f * size);
	health = getMaxHealth();
	attackDamage = size;
	if (slimeSize <= 1) attackDamage = 0;
}

void Slime::setSlimeSize(int size)
{
	initSize(size);
}

int Slime::getMaxHealth() {
	return slimeSize * slimeSize;
}

void Slime::aiStep() {
	super::aiStep();
}

void Slime::tick() {
	super::tick();

	if (!level->isClientSide) {
		if (onGround && jumpDelay-- <= 0) {
			jumpDelay = random.nextInt(20) + 10;

			Player* player = level->getNearestPlayer(this, 16);
			if (player != NULL) {
				float dx = player->x - x;
				float dz = player->z - z;
				float dist = Mth::sqrt(dx * dx + dz * dz);
				if (dist > 0) {
					xd += (dx / dist) * 0.2f * slimeSize;
					zd += (dz / dist) * 0.2f * slimeSize;
				}
			}
			yd = 0.42f;
		}
	}
}

int Slime::getEntityTypeId() const {
	return MobTypes::Slime;
}

void Slime::die(Entity* source) {
	if (!level->isClientSide && slimeSize > 1) {
		int count = 2 + random.nextInt(3);
		int newSize = slimeSize / 2;
		if (newSize < 1) newSize = 1;

		for (int i = 0; i < count; i++) {
			Slime* baby = new Slime(level, newSize);
			baby->moveTo(
				x + (random.nextFloat() - 0.5f) * slimeSize,
				y + 0.5f,
				z + (random.nextFloat() - 0.5f) * slimeSize,
				random.nextFloat() * 360.0f, 0);
			level->addEntity(baby);
		}
	}
	super::die(source);
}

bool Slime::canSpawn() {
	// Slimes spawn below Y=40 or in swamp biomes
	if (y < 40) {
		return super::canSpawn();
	}
	return false;
}

int Slime::getDeathLoot() {
	if (slimeSize == 1)
		return Item::slimeBall->id;
	return 0;
}

void Slime::dropDeathLoot() {
	if (slimeSize == 1) {
		int count = random.nextInt(3); // 0-2
		for (int i = 0; i < count; i++) {
			spawnAtLocation(Item::slimeBall->id, 1);
		}
	}
}

const char* Slime::getAmbientSound() {
	return "mob.slime";
}

std::string Slime::getHurtSound() {
	return slimeSize > 1 ? "mob.slime" : "mob.slimesmall";
}

std::string Slime::getDeathSound() {
	return slimeSize > 1 ? "mob.slime" : "mob.slimesmall";
}

float Slime::getWalkTargetValue(int x, int y, int z) {
	return 0.5f;
}
