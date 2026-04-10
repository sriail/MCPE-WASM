#ifndef WOOD_PLANKS_H
#define WOOD_PLANKS_H

#include "Tile.h"
#include "../material/Material.h"
#include "../../client/renderer/TerrainAtlas.h"

class WoodPlanks : public Tile {
    typedef Tile super;
public:
    static const int OAK = 0;
    static const int SPRUCE = 1;
    static const int BIRCH = 2;
    static const int JUNGLE = 3;

    WoodPlanks(int id)
        : super(id, 4, Material::wood) {}

    int getTexture(int face, int data) override {
        switch (data) {
            case OAK:    return 4;
            case SPRUCE: return texCoord(16, 6);
            case BIRCH:  return texCoord(16, 7);
            case JUNGLE: return texCoord(16, 8);
            default:     return 4;
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
