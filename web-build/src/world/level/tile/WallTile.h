#ifndef WALL_TILE_H
#define WALL_TILE_H

#include "Tile.h"
#include "../material/Material.h"

class WallTile : public Tile {
    typedef Tile super;
public:
    WallTile(int id, int tex)
        : super(id, tex, Material::stone) {}

    int getRenderShape() override { return SHAPE_FENCE; }
    bool isCubeShaped() override { return false; }
    bool isSolidRender() override { return false; }

    bool connectsTo(int tileId) {
        if (tileId == id) return true;
        Tile* t = Tile::tiles[tileId];
        return t != nullptr && t->material == Material::stone && t->isCubeShaped();
    }

    void updateShape(LevelSource* level, int x, int y, int z) override {
        float a = 0.25f, b = 0.75f;
        float ya = 0, yb = 1.0f;

        bool n = connectsTo(level->getTile(x, y, z - 1));
        bool s = connectsTo(level->getTile(x, y, z + 1));
        bool w = connectsTo(level->getTile(x - 1, y, z));
        bool e = connectsTo(level->getTile(x + 1, y, z));

        float x0 = w ? 0 : a;
        float x1 = e ? 1 : b;
        float z0 = n ? 0 : a;
        float z1 = s ? 1 : b;

        setShape(x0, ya, z0, x1, yb, z1);
    }
};

#endif
