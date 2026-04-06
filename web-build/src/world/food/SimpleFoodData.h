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

    int getFoodLevel() const { return foodLevel; }
    float getSaturationLevel() const { return saturationLevel; }
    bool needsFood() const { return foodLevel < 20; }

    static const float EXHAUSTION_SPRINT;
    static const float EXHAUSTION_SPRINT_JUMP;
    static const float EXHAUSTION_JUMP;
    static const float EXHAUSTION_ATTACK;
    static const float EXHAUSTION_DAMAGE;
    static const float EXHAUSTION_MINE;
    static const float EXHAUSTION_WALK;
    static const float EXHAUSTION_SWIM;

private:
    int foodLevel;
    float saturationLevel;
    float exhaustionLevel;
    int tickTimer;
};

#endif /*NET_MINECRAFT_WORLD_FOOD__SimpleFoodData_H__*/
