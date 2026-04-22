#include "Enderman.h"

#include "../../item/Item.h"
#include "../../level/Level.h"
#include "../../level/material/Material.h"
#include "../../level/tile/Tile.h"
#include "../../../util/Mth.h"
#include "../EntityTypes.h"
#include "../player/Player.h"
#include "../ai/goal/GoalSelector.h"
#include "../ai/control/JumpControl.h"
#include "../ai/goal/RandomStrollGoal.h"
#include "../ai/goal/MeleeAttackGoal.h"
#include "../ai/goal/target/NearestAttackableTargetGoal.h"
#include "../ai/goal/target/HurtByTargetGoal.h"

Enderman::Enderman(Level* level)
:	super(level),
	carriedBlock(0),
	teleportCooldown(0),
	waterDamageTick(0),
	isAngry(false)
{
	entityRendererId = ER_ENDERMAN_RENDERER;
	this->textureName = "mob/enderman.png";

	this->setSize(0.6f, 2.9f);
	runSpeed = 0.5f;
	attackDamage = 7;

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

void Enderman::aiStep() {
	super::aiStep();
}

void Enderman::tick() {
	super::tick();

	if (level->isClientSide) return;

	// Take damage from water
	if (++waterDamageTick >= 20) {
		waterDamageTick = 0;
		if (isInWater()) {
			hurt(NULL, 1);
			teleportRandomly();
		}
	}

	// Try to teleport away if hurt
	if (teleportCooldown > 0) {
		teleportCooldown--;
	}

	// If angry, occasionally teleport towards target
	if (isAngry && attackTargetId != 0 && teleportCooldown <= 0) {
		Entity* target = level->getEntity(attackTargetId);
		if (target != NULL && distanceTo(target) > 4.0f) {
			teleportTowards(target);
			teleportCooldown = 30;
		}
	}

	// Pick up a random block occasionally
	if (carriedBlock == 0 && random.nextInt(200) == 0) {
		int bx = Mth::floor(x) + random.nextInt(3) - 1;
		int by = Mth::floor(y) + random.nextInt(2);
		int bz = Mth::floor(z) + random.nextInt(3) - 1;
		int tile = level->getTile(bx, by, bz);
		if (tile == ((Tile*)Tile::grass)->id || tile == ((Tile*)Tile::dirt)->id || tile == ((Tile*)Tile::sand)->id) {
			carriedBlock = tile;
			level->setTile(bx, by, bz, 0);
		}
	}
}

int Enderman::getEntityTypeId() const {
	return MobTypes::Enderman;
}

void Enderman::die(Entity* source) {
	// Place carried block back
	if (carriedBlock != 0 && !level->isClientSide) {
		int bx = Mth::floor(x);
		int by = Mth::floor(y);
		int bz = Mth::floor(z);
		if (level->isEmptyTile(bx, by, bz)) {
			level->setTile(bx, by, bz, carriedBlock);
		}
		carriedBlock = 0;
	}
	super::die(source);
}

int Enderman::getAttackDamage(Entity* target) {
	return attackDamage;
}

bool Enderman::hurt(Entity* source, int dmg) {
	if (super::hurt(source, dmg)) {
		if (source != NULL) {
			isAngry = true;
			attackTargetId = source->entityId;
		}
		// Try to teleport when hurt
		if (random.nextInt(3) == 0) {
			teleportRandomly();
		}
		return true;
	}
	return false;
}

Entity* Enderman::findAttackTarget() {
	Player* player = level->getNearestPlayer(this, 64);
	if (player != NULL && isLookedAt(player)) {
		isAngry = true;
		return player;
	}
	if (isAngry) {
		return super::findAttackTarget();
	}
	return NULL;
}

bool Enderman::isLookedAt(Entity* player) {
	float dx = x - player->x;
	float dy = (y + bbHeight) - (player->y + player->getHeadHeight());
	float dz = z - player->z;
	float dist = Mth::sqrt(dx * dx + dy * dy + dz * dz);
	if (dist > 64) return false;

	// Rough check: see if the player's look direction points at us
	float lookX = -Mth::sin(player->yRot * Mth::DEGRAD) * Mth::cos(player->xRot * Mth::DEGRAD);
	float lookY = -Mth::sin(player->xRot * Mth::DEGRAD);
	float lookZ = Mth::cos(player->yRot * Mth::DEGRAD) * Mth::cos(player->xRot * Mth::DEGRAD);

	float ndx = dx / dist;
	float ndy = dy / dist;
	float ndz = dz / dist;

	float dot = lookX * ndx + lookY * ndy + lookZ * ndz;
	return dot > 0.9f;
}

void Enderman::checkHurtTarget(Entity* target, float distance) {
	if (attackTime <= 0 && distance < 2.5f && target->bb.y1 > bb.y0 && target->bb.y0 < bb.y1) {
		attackTime = getAttackTime();
		doHurtTarget(target);
	}
}

bool Enderman::teleportRandomly() {
	float tx = x + (random.nextFloat() - 0.5f) * 32.0f;
	float ty = y + (float)(random.nextInt(16) - 8);
	float tz = z + (random.nextFloat() - 0.5f) * 32.0f;
	return teleportTo(tx, ty, tz);
}

bool Enderman::teleportTowards(Entity* target) {
	float dx = target->x - x;
	float dz = target->z - z;
	float dist = Mth::sqrt(dx * dx + dz * dz);
	if (dist < 1.0f) return false;

	float tx = x + (dx / dist) * 8.0f + (random.nextFloat() - 0.5f) * 4.0f;
	float ty = y + (float)(random.nextInt(8) - 4);
	float tz = z + (dz / dist) * 8.0f + (random.nextFloat() - 0.5f) * 4.0f;
	return teleportTo(tx, ty, tz);
}

bool Enderman::teleportTo(float tx, float ty, float tz) {
	int bx = Mth::floor(tx);
	int by = Mth::floor(ty);
	int bz = Mth::floor(tz);

	// Find solid ground
	if (by < 1) by = 1;
	if (by > 120) by = 120;

	while (by > 1 && level->isEmptyTile(bx, by - 1, bz)) {
		by--;
	}

	if (!level->isEmptyTile(bx, by, bz) || !level->isEmptyTile(bx, by + 1, bz) || !level->isEmptyTile(bx, by + 2, bz)) {
		return false;
	}

	// Check not teleporting into water
	if (level->getMaterial(bx, by, bz) == Material::water) {
		return false;
	}

	moveTo(tx, (float)by, tz, yRot, xRot);

	level->playSound(this, "mob.endermen.portal", 1.0f, 1.0f);

	return true;
}

int Enderman::getDeathLoot() {
	return Item::enderPearl->id;
}

void Enderman::dropDeathLoot() {
	int count = random.nextInt(2); // 0-1
	for (int i = 0; i < count; i++) {
		spawnAtLocation(Item::enderPearl->id, 1);
	}
}

const char* Enderman::getAmbientSound() {
	return "mob.endermen.idle";
}

std::string Enderman::getHurtSound() {
	return "mob.endermen.hit";
}

std::string Enderman::getDeathSound() {
	return "mob.endermen.death";
}
