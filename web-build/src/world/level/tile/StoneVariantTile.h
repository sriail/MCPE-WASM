#ifndef STONE_VARIANT_TILE_H
#define STONE_VARIANT_TILE_H

#include "Tile.h"
#include "../material/Material.h"
#include "../../client/renderer/TerrainAtlas.h"

class StoneVariantTile : public Tile {
    typedef Tile super;
public:
    static const int GRANITE = 0;
    static const int POLISHED_GRANITE = 1;
    static const int DIORITE = 2;
    static const int POLISHED_DIORITE = 3;
    static const int ANDESITE = 4;
    static const int POLISHED_ANDESITE = 5;

    StoneVariantTile(int id)
        : super(id, texCoord(16, 0), Material::stone) {}

    int getTexture(int face, int data) override {
        switch (data) {
            case GRANITE:           return texCoord(16, 0);
            case POLISHED_GRANITE:  return texCoord(16, 1);
            case DIORITE:           return texCoord(16, 2);
            case POLISHED_DIORITE:  return texCoord(16, 3);
            case ANDESITE:          return texCoord(16, 4);
            case POLISHED_ANDESITE: return texCoord(16, 5);
            default:                return texCoord(16, 0);
        }
    }

    int getTexture(LevelSource* level, int x, int y, int z, int face) override {
        return getTexture(face, level->getData(x, y, z));
    }

    int getResource(int data, Random* random) override {
        return id;
    }

    int getSpawnResourcesAuxValue(int data) override {
        return data;
    }
};

#endif
