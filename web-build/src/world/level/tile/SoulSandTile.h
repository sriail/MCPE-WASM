#ifndef SOUL_SAND_TILE_H
#define SOUL_SAND_TILE_H

#include "Tile.h"
#include "../material/Material.h"
#include "../../client/renderer/TerrainAtlas.h"

class SoulSandTile : public Tile {
    typedef Tile super;
public:
    SoulSandTile(int id)
        : super(id, texCoord(14, 16), Material::sand)
    {
        setShape(0, 0, 0, 1, 0.875f, 1);
    }

    AABB* getAABB(Level* level, int x, int y, int z) override {
        return nullptr;
    }

    void entityInside(Level* level, int x, int y, int z, Entity* entity) override {
        // Slow entities down
    }
};

#endif
