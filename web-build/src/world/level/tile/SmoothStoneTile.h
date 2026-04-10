#ifndef SMOOTH_STONE_TILE_H
#define SMOOTH_STONE_TILE_H

#include "Tile.h"
#include "../material/Material.h"
#include "../../client/renderer/TerrainAtlas.h"

class SmoothStoneTile : public Tile {
    typedef Tile super;
public:
    SmoothStoneTile(int id)
        : super(id, texCoord(2, 15), Material::stone) {}
};

#endif
