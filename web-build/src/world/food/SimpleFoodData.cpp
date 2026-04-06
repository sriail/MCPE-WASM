#include "SimpleFoodData.h"
#include "FoodConstants.h"
#include "../item/FoodItem.h"
#include "../entity/player/Player.h"

const float SimpleFoodData::EXHAUSTION_SPRINT       = 0.1f;
const float SimpleFoodData::EXHAUSTION_SPRINT_JUMP  = 0.8f;
const float SimpleFoodData::EXHAUSTION_JUMP         = 0.05f;
const float SimpleFoodData::EXHAUSTION_ATTACK       = 0.3f;
const float SimpleFoodData::EXHAUSTION_DAMAGE       = 0.1f;
const float SimpleFoodData::EXHAUSTION_MINE         = 0.025f;
const float SimpleFoodData::EXHAUSTION_WALK         = 0.0f;
const float SimpleFoodData::EXHAUSTION_SWIM         = 0.015f;

SimpleFoodData::SimpleFoodData()
:	foodLevel(20),
	saturationLevel(5.0f),
	exhaustionLevel(0.0f),
	tickTimer(0)
{
}

void SimpleFoodData::eat( int food, float saturationModifier ) {
	foodLevel = Mth::Min(food + foodLevel, FoodConstants::MAX_FOOD);
	saturationLevel = Mth::Min(saturationLevel + (float)(food * 2) * saturationModifier, (float)foodLevel);
}

void SimpleFoodData::eat( FoodItem* item ) {
	eat(item->getNutrition(), 0.6f);
}

void SimpleFoodData::addExhaustion( float amount ) {
	exhaustionLevel = Mth::Min(exhaustionLevel + amount, 40.0f);
}

void SimpleFoodData::tick( Player* player ) {
	// Process exhaustion
	if (exhaustionLevel > 4.0f) {
		exhaustionLevel -= 4.0f;
		if (saturationLevel > 0.0f) {
			saturationLevel = Mth::Max(saturationLevel - 1.0f, 0.0f);
		} else {
			foodLevel = Mth::Max(foodLevel - 1, 0);
		}
	}

	// Regeneration when food level is high enough
	if (foodLevel >= FoodConstants::HEAL_LEVEL && player->isHurt()) {
		tickTimer++;
		if (tickTimer >= FoodConstants::HEALTH_TICK_COUNT) {
			player->heal(1);
			addExhaustion(3.0f);
			tickTimer = 0;
		}
	} else if (foodLevel <= 0) {
		// Starvation damage
		tickTimer++;
		if (tickTimer >= FoodConstants::HEALTH_TICK_COUNT) {
			if (player->health > 1) {
				player->hurt(NULL, 1);
			}
			tickTimer = 0;
		}
	} else {
		tickTimer = 0;
	}
}
