#ifndef NET_MINECRAFT_WORLD_LEVEL_LEVELGEN_FEATURE__MonsterRoomFeature_H__
#define NET_MINECRAFT_WORLD_LEVEL_LEVELGEN_FEATURE__MonsterRoomFeature_H__

#include "Feature.h"

class Level;
class Random;

// Generates a small dungeon room:
//   - 5×5 (or 7×7) cobblestone / mossy-cobblestone walls
//   - 1 mob spawner in the centre (variant chosen randomly)
//   - 2 chests placed randomly on the floor, filled with loot
class MonsterRoomFeature : public Feature
{
public:
    bool place(Level* level, Random* random, int x, int y, int z);
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_LEVELGEN_FEATURE__MonsterRoomFeature_H__*/
