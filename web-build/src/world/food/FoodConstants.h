#ifndef NET_MINECRAFT_WORLD_FOOD__FoodConstants_H__
#define NET_MINECRAFT_WORLD_FOOD__FoodConstants_H__

//package net.minecraft.world.food;

class FoodConstants
{
public:
    static const int MAX_FOOD = 20;
	// Number of game ticks (4 seconds) to regenerate 1 HP when food >= HEAL_LEVEL
    static const int HEALTH_TICK_COUNT = 80;
	// Number of game ticks (0.5 seconds) for fast regen at full food with saturation
	static const int FAST_HEAL_TICK_COUNT = 10;
	// Number of game ticks (4 seconds) between starvation damage ticks
	static const int STARVATION_TICK_COUNT = 80;
    // Minimum food level required for slow health regeneration
    static const int HEAL_LEVEL = 18;
    // Food level required for fast health regeneration (needs saturation > 0)
    static const int FAST_HEAL_LEVEL = 20;
    // Food level at or below which the player cannot sprint
    static const int NO_SPRINT_FOOD_LEVEL = 6;
    // Initial food saturation
    static constexpr float INITIAL_SATURATION = 5.0f;
};

#endif /*NET_MINECRAFT_WORLD_FOOD__FoodConstants_H__*/
