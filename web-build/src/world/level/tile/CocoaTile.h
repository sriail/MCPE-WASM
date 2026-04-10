#ifndef COCOA_TILE_H
#define COCOA_TILE_H

#include "Tile.h"
#include "../material/Material.h"
#include "../../client/renderer/TerrainAtlas.h"

class CocoaTile : public Tile {
    typedef Tile super;
public:
    static const int GROWTH_MASK = 0xC;  // bits 2-3 for growth stage (0-2)
    static const int DIR_MASK = 0x3;     // bits 0-1 for direction

    CocoaTile(int id)
        : super(id, texCoord(3, 15), Material::plant)
    {
        setShape(0.25f, 0.1875f, 0.25f, 0.75f, 0.75f, 0.75f);
    }

    int getTexture(int face, int data) override {
        int stage = (data & GROWTH_MASK) >> 2;
        return texCoord(3 + stage, 15);
    }

    int getTexture(LevelSource* level, int x, int y, int z, int face) override {
        return getTexture(face, level->getData(x, y, z));
    }

    bool isCubeShaped() override { return false; }
    bool isSolidRender() override { return false; }
    int getRenderShape() override { return SHAPE_BLOCK; }

    bool canSurvive(Level* level, int x, int y, int z) override;
    void tick(Level* level, int x, int y, int z, Random* random) override;
    int getResource(int data, Random* random) override;
    int getResourceCount(Random* random) override;
};

#endif
