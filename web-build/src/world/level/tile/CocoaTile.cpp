#include "CocoaTile.h"
#include "../Level.h"
#include "Tile.h"

bool CocoaTile::canSurvive(Level* level, int x, int y, int z) {
    int dir = level->getData(x, y, z) & DIR_MASK;
    int jx = x, jz = z;
    if (dir == 0) jz++;
    else if (dir == 1) jx--;
    else if (dir == 2) jz--;
    else if (dir == 3) jx++;
    // Must be attached to a jungle log
    return level->getTile(jx, y, jz) == Tile::treeTrunk->id;
}

void CocoaTile::tick(Level* level, int x, int y, int z, Random* random) {
    if (!canSurvive(level, x, y, z)) {
        spawnResources(level, x, y, z, level->getData(x, y, z));
        level->setTile(x, y, z, 0);
        return;
    }
    int data = level->getData(x, y, z);
    int stage = (data & GROWTH_MASK) >> 2;
    if (stage < 2 && random->nextInt(5) == 0) {
        level->setData(x, y, z, (data & DIR_MASK) | ((stage + 1) << 2));
    }
}

int CocoaTile::getResource(int data, Random* random) {
    // Returns cocoa beans item (dye_powder with data 3 = brown dye)
    return 256 + 95; // dye_powder item id
}

int CocoaTile::getResourceCount(Random* random) {
    return 1;
}
