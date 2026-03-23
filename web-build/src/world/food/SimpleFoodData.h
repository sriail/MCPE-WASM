#ifndef NET_MINECRAFT_WORLD_FOOD__SimpleFoodData_H__
#define NET_MINECRAFT_WORLD_FOOD__SimpleFoodData_H__

//package net.minecraft.world.food;

class FoodItem;
class Player;

class SimpleFoodData
{
public:
    SimpleFoodData();

    void eat(int food, float saturationModifier = 0.6f);
    void eat(FoodItem* item);

    void tick(Player* player);
    void addExhaustion(float amount);

    float getFoodLevel() const { return (float)foodLevel; }
    float getSaturation() const { return saturation; }
    float getExhaustion() const { return exhaustion; }
private:
	int foodLevel;
	float saturation;
	float exhaustion;
	int healTimer;
	int starveTimer;
};

#endif /*NET_MINECRAFT_WORLD_FOOD__SimpleFoodData_H__*/
