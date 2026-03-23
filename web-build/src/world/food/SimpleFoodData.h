#ifndef NET_MINECRAFT_WORLD_FOOD__SimpleFoodData_H__
#define NET_MINECRAFT_WORLD_FOOD__SimpleFoodData_H__

//package net.minecraft.world.food;

class FoodItem;
class Player;

class SimpleFoodData
{
public:
    SimpleFoodData();

    void eat(int food, float saturation = 0.0f);
    void eat(FoodItem* item);

    void tick(Player* player);

    int getFoodLevel() const { return foodLevel; }
    float getSaturationLevel() const { return saturationLevel; }
private:
	int foodLevel;
	float saturationLevel;
	int tickTimer;
};

#endif /*NET_MINECRAFT_WORLD_FOOD__SimpleFoodData_H__*/
