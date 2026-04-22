#ifndef NET_MINECRAFT_WORLD_ENTITY_MONSTER__Slime_H__
#define NET_MINECRAFT_WORLD_ENTITY_MONSTER__Slime_H__

#include "Monster.h"
#include <string>

class Level;

class Slime: public Monster
{
	typedef Monster super;
public:
	Slime(Level* level);
	Slime(Level* level, int size);

	int getMaxHealth();
	void aiStep();
	void tick();
	virtual int getEntityTypeId() const;
	virtual void die(Entity* source);
	bool canSpawn();

	int getSlimeSize() const { return slimeSize; }
	void setSlimeSize(int size);

protected:
	int getDeathLoot();
	void dropDeathLoot();

	const char* getAmbientSound();
	std::string getHurtSound();
	std::string getDeathSound();

	float getWalkTargetValue(int x, int y, int z);

private:
	void initSize(int size);
	int slimeSize;
	int jumpDelay;
};

#endif /*NET_MINECRAFT_WORLD_ENTITY_MONSTER__Slime_H__*/
