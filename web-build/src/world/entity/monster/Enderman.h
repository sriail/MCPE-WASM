#ifndef NET_MINECRAFT_WORLD_ENTITY_MONSTER__Enderman_H__
#define NET_MINECRAFT_WORLD_ENTITY_MONSTER__Enderman_H__

#include "Monster.h"
#include <string>

class Level;
class Entity;

class Enderman: public Monster
{
	typedef Monster super;
public:
	Enderman(Level* level);
	~Enderman();

	int getMaxHealth();
	void aiStep();
	void tick();
	virtual int getEntityTypeId() const;
	virtual void die(Entity* source);
	virtual int getAttackDamage(Entity* target);
	bool hurt(Entity* source, int dmg);

	int getCarriedBlock() const { return carriedBlock; }

protected:
	int getDeathLoot();
	void dropDeathLoot();

	Entity* findAttackTarget();
	void checkHurtTarget(Entity* target, float distance);

	const char* getAmbientSound();
	std::string getHurtSound();
	std::string getDeathSound();

private:
	bool isLookedAt(Entity* player);
	bool teleportRandomly();
	bool teleportTowards(Entity* target);
	bool teleportTo(float tx, float ty, float tz);

	int carriedBlock;
	int teleportCooldown;
	int waterDamageTick;
	bool isAngry;
};

#endif /*NET_MINECRAFT_WORLD_ENTITY_MONSTER__Enderman_H__*/
