#include "Enderman.h"
#include "../../item/Item.h"
#include "../../level/Level.h"
#include "../../level/tile/Tile.h"
#include "../../../util/Mth.h"
#include "../player/Player.h"
#include "../ai/goal/GoalSelector.h"
#include "../ai/control/JumpControl.h"
#include "../ai/goal/RandomStrollGoal.h"
#include "../ai/goal/MeleeAttackGoal.h"
#include "../ai/goal/target/NearestAttackableTargetGoal.h"
#include "../ai/goal/target/HurtByTargetGoal.h"
#include <cmath>

bool Enderman::canPickUp[256] = {false};
bool Enderman::_canPickUpInited = false;

void Enderman::initCanPickUp() {
	if (_canPickUpInited) return;
	_canPickUpInited = true;
	canPickUp[Tile::grass->id] = true;
	canPickUp[Tile::dirt->id] = true;
	canPickUp[Tile::sand->id] = true;
	canPickUp[Tile::gravel->id] = true;
	canPickUp[Tile::flower->id] = true;
	canPickUp[Tile::rose->id] = true;
	canPickUp[Tile::mushroom1->id] = true;
	canPickUp[Tile::mushroom2->id] = true;
	canPickUp[Tile::tnt->id] = true;
	canPickUp[Tile::cactus->id] = true;
	canPickUp[Tile::clay->id] = true;
}

Enderman::Enderman(Level* level)
:	super(level),
	carriedBlock(0),
	carriedBlockData(0),
	isCreepy(false),
	teleportCooldown(0),
	stareTimer(0)
{
	initCanPickUp();

	entityRendererId = ER_ENDERMAN_RENDERER;
	textureName = "mob/enderman.png";
	runSpeed = 0.3f;
	attackDamage = 7;

	setSize(0.6f, 2.9f);

	targetSelector = new GoalSelector();
	targetSelector->addGoal(1, new HurtByTargetGoal(this, false));

	goalSelector = new GoalSelector();
	goalSelector->addGoal(2, new MeleeAttackGoal(this, runSpeed, false, 0));
	goalSelector->addGoal(7, new RandomStrollGoal(this, runSpeed));

	moveControl = new MoveControl(this);
	jumpControl = new JumpControl(this);
}

Enderman::~Enderman() {
	delete goalSelector;
	delete targetSelector;
	delete moveControl;
	delete jumpControl;
}

int Enderman::getMaxHealth() {
	return 40;
}

bool Enderman::isLookingAtMe(Player* player) {
	float dx = player->x - x;
	float dy = (player->y + player->getHeadHeight()) - (y + getHeadHeight());
	float dz = player->z - z;
	float dist = sqrtf(dx * dx + dy * dy + dz * dz);
	if (dist > 64.0f) return false;

	dx /= dist;
	dy /= dist;
	dz /= dist;

	float yaw = player->yRot * 3.14159f / 180.0f;
	float pitch = player->xRot * 3.14159f / 180.0f;
	float lx = -sinf(yaw) * cosf(pitch);
	float ly = -sinf(pitch);
	float lz = cosf(yaw) * cosf(pitch);

	float dot = dx * lx + dy * ly + dz * lz;
	return dot > 0.98f;
}

bool Enderman::teleportRandomly() {
	float tx = x + (random.nextFloat() - 0.5f) * 64.0f;
	float tz = z + (random.nextFloat() - 0.5f) * 64.0f;
	float ty = y + (float)(random.nextInt(64) - 32);
	return teleportTo(tx, ty, tz);
}

bool Enderman::teleportTo(float tx, float ty, float tz) {
	int bx = Mth::floor(tx);
	int by = Mth::floor(ty);
	int bz = Mth::floor(tz);

	while (by > 0 && level->getTile(bx, by - 1, bz) == 0) by--;
	if (by <= 0) return false;

	int tileAt = level->getTile(bx, by, bz);
	int tileAbove = level->getTile(bx, by + 1, bz);
	if (tileAt != 0 || tileAbove != 0) return false;

	if (level->getMaterial(bx, by - 1, bz) == Material::water) return false;

	float oldX = x, oldY = y, oldZ = z;
	setPos(tx, (float)by, tz);

	if (!level->getEntities(this, bb).empty()) {
		setPos(oldX, oldY, oldZ);
		return false;
	}

	return true;
}

void Enderman::aiStep() {
	if (teleportCooldown > 0) teleportCooldown--;

	if (!isCreepy) {
		Player* nearestPlayer = level->getNearestPlayer(this, 64.0);
		if (nearestPlayer && isLookingAtMe(nearestPlayer)) {
			stareTimer++;
			if (stareTimer >= 5) {
				isCreepy = true;
				setTarget(nearestPlayer);
			}
		} else {
			stareTimer = 0;
		}
	}

	if (!isCreepy && teleportCooldown <= 0) {
		if (random.nextInt(200) == 0) {
			teleportRandomly();
			teleportCooldown = 100;
		}
	}

	if (!isCreepy && random.nextInt(400) == 0) {
		if (carriedBlock == 0) {
			int bx = Mth::floor(x) + random.nextInt(3) - 1;
			int by = Mth::floor(y) + random.nextInt(2);
			int bz = Mth::floor(z) + random.nextInt(3) - 1;
			int blockId = level->getTile(bx, by, bz);
			if (blockId > 0 && blockId < 256 && canPickUp[blockId]) {
				carriedBlock = blockId;
				carriedBlockData = level->getData(bx, by, bz);
				level->setTile(bx, by, bz, 0);
			}
		} else {
			int bx = Mth::floor(x) + random.nextInt(3) - 1;
			int by = Mth::floor(y) + random.nextInt(2);
			int bz = Mth::floor(z) + random.nextInt(3) - 1;
			if (level->getTile(bx, by, bz) == 0 && level->getTile(bx, by - 1, bz) != 0) {
				level->setTileAndData(bx, by, bz, carriedBlock, carriedBlockData);
				carriedBlock = 0;
				carriedBlockData = 0;
			}
		}
	}

	super::aiStep();
}

bool Enderman::hurt(Entity* sourceEntity, int dmg) {
	isCreepy = true;
	if (sourceEntity) setTarget((Mob*)sourceEntity);

	if (teleportCooldown <= 0) {
		teleportRandomly();
		teleportCooldown = 40;
	}

	return super::hurt(sourceEntity, dmg);
}

int Enderman::getEntityTypeId() const {
	return MobTypes::Enderman;
}

void Enderman::die(Entity* source) {
	if (!level->isClientSide) {
		int count = random.nextInt(2);
		for (int i = 0; i < count; i++) {
			spawnAtLocation(Item::enderPearl->id, 1);
		}

		if (carriedBlock > 0) {
			spawnAtLocation(carriedBlock, 1);
		}
	}
	super::die(source);
}

int Enderman::getAttackDamage(Entity* target) {
	return attackDamage;
}

const char* Enderman::getAmbientSound() {
	return isCreepy ? "mob.endermen.scream" : "mob.endermen.idle";
}

std::string Enderman::getHurtSound() {
	return "mob.endermen.hit";
}

std::string Enderman::getDeathSound() {
	return "mob.endermen.death";
}

int Enderman::getDeathLoot() {
	return Item::enderPearl->id;
}
