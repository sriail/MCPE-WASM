#include "Slime.h"
#include "../../item/Item.h"
#include "../../level/Level.h"
#include "../../../util/Mth.h"
#include "../ai/goal/GoalSelector.h"
#include "../ai/control/JumpControl.h"
#include "../ai/goal/RandomStrollGoal.h"
#include "../ai/goal/MeleeAttackGoal.h"
#include "../ai/goal/target/NearestAttackableTargetGoal.h"
#include "../ai/goal/target/HurtByTargetGoal.h"

Slime::Slime(Level* level)
:	super(level),
	slimeSize(1),
	squish(0),
	oSquish(0),
	jumpDelay(0)
{
	entityRendererId = ER_SLIME_RENDERER;
	textureName = "mob/slime.png";
	runSpeed = 0.6f;
	attackDamage = 1;

	setSlimeSize(1 + level->random.nextInt(3));

	targetSelector = new GoalSelector();
	targetSelector->addGoal(1, new HurtByTargetGoal(this, false));
	targetSelector->addGoal(2, new NearestAttackableTargetGoal(this, 1, 16, 0, true));

	goalSelector = new GoalSelector();
	goalSelector->addGoal(2, new MeleeAttackGoal(this, runSpeed, false, 0));
	goalSelector->addGoal(7, new RandomStrollGoal(this, runSpeed));

	moveControl = new MoveControl(this);
	jumpControl = new JumpControl(this);
}

Slime::Slime(Level* level, int size)
:	super(level),
	slimeSize(1),
	squish(0),
	oSquish(0),
	jumpDelay(0)
{
	entityRendererId = ER_SLIME_RENDERER;
	textureName = "mob/slime.png";
	runSpeed = 0.6f;
	attackDamage = 1;

	setSlimeSize(size);

	targetSelector = new GoalSelector();
	targetSelector->addGoal(1, new HurtByTargetGoal(this, false));
	targetSelector->addGoal(2, new NearestAttackableTargetGoal(this, 1, 16, 0, true));

	goalSelector = new GoalSelector();
	goalSelector->addGoal(2, new MeleeAttackGoal(this, runSpeed, false, 0));
	goalSelector->addGoal(7, new RandomStrollGoal(this, runSpeed));

	moveControl = new MoveControl(this);
	jumpControl = new JumpControl(this);
}

Slime::~Slime() {
	delete goalSelector;
	delete targetSelector;
	delete moveControl;
	delete jumpControl;
}

void Slime::setSlimeSize(int size) {
	slimeSize = size;
	setSize(0.6f * size, 0.6f * size);
	health = getMaxHealth();
	attackDamage = size;
}

int Slime::getSlimeSize() const {
	return slimeSize;
}

int Slime::getMaxHealth() {
	return slimeSize * slimeSize;
}

void Slime::aiStep() {
	oSquish = squish;
	squish += (0 - squish) * 0.5f;

	super::aiStep();
}

int Slime::getEntityTypeId() const {
	return MobTypes::Slime;
}

void Slime::die(Entity* source) {
	if (!level->isClientSide) {
		if (slimeSize > 1) {
			int count = 2 + random.nextInt(3);
			for (int i = 0; i < count; i++) {
				int newSize = slimeSize / 2;
				if (newSize < 1) newSize = 1;
				Slime* baby = new Slime(level, newSize);
				baby->setPos(x + (random.nextFloat() - 0.5f) * slimeSize,
				             y + 0.5f,
				             z + (random.nextFloat() - 0.5f) * slimeSize);
				level->addEntity(baby);
			}
		}

		if (slimeSize == 1) {
			int count = random.nextInt(3);
			for (int i = 0; i < count; i++) {
				spawnAtLocation(Item::slimeBall->id, 1);
			}
		}
	}
	super::die(source);
}

int Slime::getAttackDamage(Entity* target) {
	return slimeSize;
}

bool Slime::canSpawn() {
	return y < 40 && super::canSpawn();
}

const char* Slime::getAmbientSound() {
	return slimeSize > 1 ? "mob.slime.big" : "mob.slime.small";
}

std::string Slime::getHurtSound() {
	return slimeSize > 1 ? "mob.slime.big" : "mob.slime.small";
}

std::string Slime::getDeathSound() {
	return slimeSize > 1 ? "mob.slime.big" : "mob.slime.small";
}

int Slime::getDeathLoot() {
	return slimeSize == 1 ? Item::slimeBall->id : 0;
}
