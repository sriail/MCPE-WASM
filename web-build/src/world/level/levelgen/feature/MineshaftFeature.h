#ifndef NET_MINECRAFT_WORLD_LEVEL_LEVELGEN_FEATURE__MineshaftFeature_H__
#define NET_MINECRAFT_WORLD_LEVEL_LEVELGEN_FEATURE__MineshaftFeature_H__

#include "Feature.h"

class Level;
class Random;

// Generates a simple procedural mineshaft at a chunk position.
// The mineshaft consists of a series of corridor segments that branch out
// from a central starting room. Each corridor:
//   - is 3 blocks wide and 3 blocks tall
//   - uses oak planks for floor supports and fence posts for pillars
//   - places cobwebs randomly throughout
//   - places torches on the walls
//   - occasionally contains a chest with loot
class MineshaftFeature : public Feature
{
public:
    bool place(Level* level, Random* random, int x, int y, int z);

private:
    // Carve a single corridor segment in the given direction
    // direction: 0=+X, 1=-X, 2=+Z, 3=-Z
    void carveCorridor(Level* level, Random* random,
                       int startX, int startY, int startZ,
                       int direction, int length, int depth);

    // Fill a loot chest with mineshaft loot
    void fillLootChest(Level* level, int cx, int cy, int cz, Random* random);

    static const int MAX_DEPTH = 2;
    static const int MIN_LENGTH = 8;
    static const int MAX_LENGTH = 20;
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_LEVELGEN_FEATURE__MineshaftFeature_H__*/
