#ifndef PISTON_BASE_TILE_H
#define PISTON_BASE_TILE_H

#include "Tile.h"
#include "../material/Material.h"
#include "../../client/renderer/TerrainAtlas.h"

class PistonBaseTile : public Tile {
    typedef Tile super;
public:
    bool isSticky;

    PistonBaseTile(int id, bool isSticky)
        : super(id, texCoord(10, 16), Material::stone), isSticky(isSticky) {}

    int getTexture(int face, int data) override {
        int dir = data & 7;
        // Face mapping based on direction
        if (face == dir) return isSticky ? texCoord(12, 16) : texCoord(9, 16); // top
        if (face == (dir ^ 1)) return texCoord(11, 16); // bottom (opposite face)
        return texCoord(10, 16); // sides
    }

    int getTexture(LevelSource* level, int x, int y, int z, int face) override {
        return getTexture(face, level->getData(x, y, z));
    }

    void setPlacedBy(Level* level, int x, int y, int z, Mob* by) override;
};

#endif
