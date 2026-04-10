#ifndef SLIME_BLOCK_TILE_H
#define SLIME_BLOCK_TILE_H

#include "Tile.h"
#include "../material/Material.h"
#include "../../client/renderer/TerrainAtlas.h"

class SlimeBlockTile : public Tile {
    typedef Tile super;
public:
    SlimeBlockTile(int id)
        : super(id, texCoord(16, 14), Material::clay) {}

    bool isSolidRender() override { return false; }
    int getRenderLayer() override { return RENDERLAYER_BLEND; }
};

#endif
