#ifndef REDSTONE_BLOCK_TILE_H
#define REDSTONE_BLOCK_TILE_H

#include "Tile.h"
#include "../material/Material.h"
#include "../../client/renderer/TerrainAtlas.h"

class RedStoneBlockTile : public Tile {
    typedef Tile super;
public:
    RedStoneBlockTile(int id)
        : super(id, texCoord(2, 16), Material::metal) {}

    bool isSignalSource() override { return true; }
    bool getSignal(LevelSource* level, int x, int y, int z) override { return true; }
    bool getSignal(LevelSource* level, int x, int y, int z, int dir) override { return true; }
};

#endif
