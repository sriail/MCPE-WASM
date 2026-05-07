#ifndef NET_MINECRAFT_WORLD_ENTITY_MONSTER__CaveSpider_H__
#define NET_MINECRAFT_WORLD_ENTITY_MONSTER__CaveSpider_H__

#include "Spider.h"

class Level;

// Cave Spider: a smaller, weaker spider that applies a brief poison
// effect on a successful hit (on Normal/Hard difficulty).
// Ported from github.com/sriail/MinecraftConsoles / CaveSpider.cpp
class CaveSpider : public Spider
{
    typedef Spider super;
public:
    CaveSpider(Level* level);

    /*@Override*/
    virtual int getMaxHealth();

    /*@Override*/
    virtual float getModelScale();

    /*@Override*/
    virtual int getEntityTypeId() const;

protected:
    /*@Override*/
    virtual void checkHurtTarget(Entity* target, float d);
};

#endif /*NET_MINECRAFT_WORLD_ENTITY_MONSTER__CaveSpider_H__*/
