#ifndef COAL_BLOCK_TILE_H
#define COAL_BLOCK_TILE_H

#include "Tile.h"
#include "../material/Material.h"
#include "../../client/renderer/TerrainAtlas.h"

class CoalBlockTile : public Tile {
    typedef Tile super;
public:
    CoalBlockTile(int id)
        : super(id, texCoord(16, 13), Material::stone) {}
};

#endif
