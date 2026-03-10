#ifndef NET_MINECRAFT_WORLD_FOOD__SimpleFoodData_H__
#define NET_MINECRAFT_WORLD_FOOD__SimpleFoodData_H__

//package net.minecraft.world.food;

class FoodItem;
class Player;

class SimpleFoodData
{
public:
    SimpleFoodData();

    void eat(int food);
    void eat(FoodItem* item);

    void tick(Player* player);

    // Add exhaustion from actions (walking, sprinting, jumping)
    void addExhaustion(float exhaustion);

    int getFoodLevel() const { return foodLevel; }
private:
	int foodLevel;
    float saturationLevel;
    float exhaustionLevel;
    int tickCount;
    int healTick;
    int starveTick;
};

#endif /*NET_MINECRAFT_WORLD_FOOD__SimpleFoodData_H__*/
