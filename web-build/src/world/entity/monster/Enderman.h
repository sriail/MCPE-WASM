#ifndef NET_MINECRAFT_WORLD_ENTITY_MONSTER__Enderman_H__
#define NET_MINECRAFT_WORLD_ENTITY_MONSTER__Enderman_H__

#include "Monster.h"
#include <string>

class Level;

class Enderman: public Monster
{
	typedef Monster super;
public:
	Enderman(Level* level);
	~Enderman();

	int getMaxHealth();
	void aiStep();
	int getEntityTypeId() const;
	void die(Entity* source);
	int getAttackDamage(Entity* target);

	bool hurt(Entity* sourceEntity, int dmg);

	bool isLookingAtMe(Player* player);
	bool teleportRandomly();
	bool teleportTo(float tx, float ty, float tz);

protected:
	const char* getAmbientSound();
	std::string getHurtSound();
	std::string getDeathSound();
	int getDeathLoot();

private:
	int carriedBlock;
	int carriedBlockData;
	bool isCreepy;
	int teleportCooldown;
	int stareTimer;
	static bool canPickUp[];
	static void initCanPickUp();
	static bool _canPickUpInited;
};

#endif
