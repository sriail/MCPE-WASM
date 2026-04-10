#ifndef NET_MINECRAFT_WORLD_ENTITY_MONSTER__Slime_H__
#define NET_MINECRAFT_WORLD_ENTITY_MONSTER__Slime_H__

#include "Monster.h"

class Level;

class Slime: public Monster
{
	typedef Monster super;
public:
	Slime(Level* level);
	Slime(Level* level, int size);
	~Slime();

	int getMaxHealth();
	void aiStep();
	int getEntityTypeId() const;
	void die(Entity* source);
	int getAttackDamage(Entity* target);

	void setSlimeSize(int size);
	int getSlimeSize() const;

	bool canSpawn();

protected:
	const char* getAmbientSound();
	std::string getHurtSound();
	std::string getDeathSound();
	int getDeathLoot();

private:
	int slimeSize;
	float squish;
	float oSquish;
	int jumpDelay;
};

#endif
