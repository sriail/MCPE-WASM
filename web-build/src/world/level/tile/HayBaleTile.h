#ifndef HAY_BALE_TILE_H
#define HAY_BALE_TILE_H

#include "Tile.h"
#include "../material/Material.h"
#include "../../client/renderer/TerrainAtlas.h"

class HayBaleTile : public Tile {
    typedef Tile super;
public:
    HayBaleTile(int id)
        : super(id, texCoord(16, 15), Material::grass) {}

    int getTexture(int face, int data) override {
        if (face == 0 || face == 1) return texCoord(16, 16); // top/bottom
        return texCoord(16, 15); // sides
    }

    int getTexture(LevelSource* level, int x, int y, int z, int face) override {
        return getTexture(face, level->getData(x, y, z));
    }
};

#endif
