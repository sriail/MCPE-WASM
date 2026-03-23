#include "SimpleFoodData.h"
#include "FoodConstants.h"
#include "../item/FoodItem.h"
#include "../entity/player/Player.h"
#include "../level/Level.h"
#include "../Difficulty.h"

SimpleFoodData::SimpleFoodData()
:	foodLevel(FoodConstants::MAX_FOOD),
	saturationLevel(FoodConstants::INITIAL_SATURATION),
	tickTimer(0)
{
}

void SimpleFoodData::eat( int food, float saturation ) {
	foodLevel = Mth::Min(food + foodLevel, FoodConstants::MAX_FOOD);
	saturationLevel = Mth::Min(saturationLevel + saturation, (float)foodLevel);
}

void SimpleFoodData::eat( FoodItem* item ) {
	float saturation = (float)item->getNutrition() * item->getSaturationModifier() * 2.0f;
	eat(item->getNutrition(), saturation);
}

void SimpleFoodData::tick( Player* player ) {
	int difficulty = player->level->difficulty;

	// Peaceful: gradually restore food and saturation
	if (difficulty == Difficulty::PEACEFUL) {
		if (foodLevel < FoodConstants::MAX_FOOD) {
			foodLevel++;
		}
		if (saturationLevel < (float)foodLevel) {
			saturationLevel = Mth::Min(saturationLevel + 1.0f, (float)foodLevel);
		}
		tickTimer = 0;
		return;
	}

	// Sprint check: disable sprinting when food level is too low
	if (foodLevel <= FoodConstants::NO_SPRINT_FOOD_LEVEL && player->isSprinting()) {
		player->setSprinting(false);
	}

	// Health regeneration based on food level
	if (foodLevel >= FoodConstants::HEAL_LEVEL) {
		tickTimer++;
		int ticksNeeded;
		if (foodLevel >= FoodConstants::FAST_HEAL_LEVEL && saturationLevel > 0.0f) {
			// Fast regen: 1 HP per 0.5 seconds when full + has saturation
			ticksNeeded = FoodConstants::FAST_HEAL_TICK_COUNT;
		} else {
			// Slow regen: 1 HP per 4 seconds when food >= 18
			ticksNeeded = FoodConstants::HEALTH_TICK_COUNT;
		}
		if (tickTimer >= ticksNeeded && player->health < player->getMaxHealth()) {
			player->heal(1);
			if (saturationLevel > 0.0f) {
				saturationLevel = Mth::Max(0.0f, saturationLevel - 6.0f);
			}
			tickTimer = 0;
		}
	}
	// Starvation: deal damage when food is empty
	else if (foodLevel == 0) {
		tickTimer++;
		if (tickTimer >= FoodConstants::STARVATION_TICK_COUNT) {
			bool shouldDamage;
			if (difficulty == Difficulty::EASY) {
				shouldDamage = player->health > 10;
			} else if (difficulty == Difficulty::HARD) {
				shouldDamage = true;
			} else {
				// Normal: health stops depleting at 1
				shouldDamage = player->health > 1;
			}
			if (shouldDamage) {
				// Starvation damage bypasses armor
				player->bypassArmor = true;
				player->actuallyHurt(1);
				player->bypassArmor = false;
				if (player->health <= 0) {
					player->die(NULL);
				}
			}
			tickTimer = 0;
		}
	} else {
		// No regen, no starvation: reset timer
		tickTimer = 0;
	}
}
