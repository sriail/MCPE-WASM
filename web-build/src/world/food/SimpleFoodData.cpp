#include "SimpleFoodData.h"
#include "FoodConstants.h"
#include "../item/FoodItem.h"
#include "../entity/player/Player.h"
#include "../level/Level.h"
#include "../../util/Mth.h"

SimpleFoodData::SimpleFoodData()
:	foodLevel(FoodConstants::MAX_FOOD),
	saturation(5.0f),
	exhaustion(0.0f),
	healTimer(0),
	starveTimer(0)
{
}

void SimpleFoodData::eat( int food, float saturationModifier ) {
	foodLevel = Mth::Min(food + foodLevel, FoodConstants::MAX_FOOD);
	saturation = Mth::Min(saturation + (float)food * saturationModifier * 2.0f, (float)foodLevel);
}

void SimpleFoodData::eat( FoodItem* item ) {
	eat(item->getNutrition(), item->getSaturationModifier());
}

void SimpleFoodData::addExhaustion( float amount ) {
	exhaustion += amount;
	// Every 4 exhaustion points, consume 1 saturation (or 1 food if saturation is empty)
	while (exhaustion >= 4.0f) {
		exhaustion -= 4.0f;
		if (saturation > 0.0f) {
			saturation = Mth::Max(saturation - 1.0f, 0.0f);
		} else if (foodLevel > 0) {
			foodLevel--;
		}
	}
}

void SimpleFoodData::tick( Player* player ) {
	if (!player->abilities.invulnerable) {
		// Natural healing: food >= 18, heal 1 HP every 4 seconds (80 ticks)
		if (foodLevel >= FoodConstants::HEAL_LEVEL && player->health < player->getMaxHealth()) {
			healTimer++;
			if (healTimer >= FoodConstants::HEALTH_TICK_COUNT) {
				player->heal(1);
				addExhaustion(3.0f);
				healTimer = 0;
			}
		} else {
			healTimer = 0;
		}

		// Starvation: food <= 0, take 1 damage every 4 seconds (80 ticks)
		if (foodLevel <= 0) {
			starveTimer++;
			if (starveTimer >= FoodConstants::HEALTH_TICK_COUNT) {
				if (player->health > 1 || player->level->difficulty > 2) {
					player->hurt(NULL, 1);
				}
				starveTimer = 0;
			}
		} else {
			starveTimer = 0;
		}
	}
}
