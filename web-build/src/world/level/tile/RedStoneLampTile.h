#ifndef REDSTONE_LAMP_TILE_H
#define REDSTONE_LAMP_TILE_H

#include "Tile.h"
#include "../material/Material.h"
#include "../../client/renderer/TerrainAtlas.h"

class RedStoneLampTile : public Tile {
    typedef Tile super;
public:
    bool isLit;

    RedStoneLampTile(int id, bool isLit)
        : super(id, isLit ? texCoord(1, 16) : texCoord(0, 16), Material::glass), isLit(isLit) {}
};

#endif
