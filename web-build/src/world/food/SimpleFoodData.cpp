#include "SimpleFoodData.h"
#include "FoodConstants.h"
#include "../item/FoodItem.h"
#include "../entity/player/Player.h"
#include "../../util/Mth.h"

SimpleFoodData::SimpleFoodData()
:	foodLevel(FoodConstants::MAX_FOOD),
    saturationLevel(5.0f),
    exhaustionLevel(0.0f),
    tickCount(0),
    healTick(0),
    starveTick(0)
{
}

void SimpleFoodData::eat( int food ) {
	foodLevel = Mth::Min(food + foodLevel, FoodConstants::MAX_FOOD);
    // Eating also restores saturation
    saturationLevel = Mth::Min(saturationLevel + food * 0.6f, (float)foodLevel);
}

void SimpleFoodData::eat( FoodItem* item ) {
	eat(item->getNutrition());
}

void SimpleFoodData::addExhaustion( float exhaustion ) {
    exhaustionLevel += exhaustion;
    // When exhaustion reaches 4, consume saturation or food
    while (exhaustionLevel >= 4.0f) {
        exhaustionLevel -= 4.0f;
        if (saturationLevel > 0.0f) {
            saturationLevel = Mth::Max(saturationLevel - 1.0f, 0.0f);
        } else if (foodLevel > 0) {
            foodLevel--;
            saturationLevel = 0.0f;
        }
    }
}

void SimpleFoodData::tick( Player* player ) {
    tickCount++;

    // Regenerate health when food is full (>= HEAL_LEVEL) and saturation > 0
    if (foodLevel >= FoodConstants::HEAL_LEVEL && saturationLevel > 0.0f) {
        healTick++;
        if (healTick >= FoodConstants::HEALTH_TICK_COUNT) {
            healTick = 0;
            if (player->health < player->getMaxHealth()) {
                player->heal(1);
                addExhaustion(6.0f);
            }
        }
    } else {
        healTick = 0;
    }

    // Starvation damage when food is empty
    if (foodLevel <= 0) {
        starveTick++;
        if (starveTick >= FoodConstants::HEALTH_TICK_COUNT) {
            starveTick = 0;
            // Only damage if player has more than half a heart (health > 1)
            if (player->health > 1) {
                player->hurt(NULL, 1);
            }
        }
    } else {
        starveTick = 0;
    }
}
