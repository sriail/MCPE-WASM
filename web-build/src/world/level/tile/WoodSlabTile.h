#ifndef WOOD_SLAB_TILE_H
#define WOOD_SLAB_TILE_H

#include "Tile.h"
#include "../material/Material.h"
#include "../../client/renderer/TerrainAtlas.h"

class WoodSlabTile : public Tile {
    typedef Tile super;
public:
    static const int TYPE_MASK = 0x3;
    static const int TOP_SLOT_BIT = 0x8;

    static const int OAK_SLAB = 0;
    static const int SPRUCE_SLAB = 1;
    static const int BIRCH_SLAB = 2;
    static const int JUNGLE_SLAB = 3;

    bool isFull;

    WoodSlabTile(int id, bool isFull)
        : super(id, 4, Material::wood), isFull(isFull)
    {
        if (!isFull)
            setShape(0, 0, 0, 1, 0.5f, 1);
    }

    int getTexture(int face, int data) override {
        switch (data & TYPE_MASK) {
            case OAK_SLAB:    return 4;
            case SPRUCE_SLAB: return texCoord(16, 6);
            case BIRCH_SLAB:  return texCoord(16, 7);
            case JUNGLE_SLAB: return texCoord(16, 8);
            default:          return 4;
        }
    }

    int getTexture(LevelSource* level, int x, int y, int z, int face) override {
        return getTexture(face, level->getData(x, y, z));
    }

    bool isCubeShaped() override { return isFull; }
    bool isSolidRender() override { return isFull; }

    void updateShape(LevelSource* level, int x, int y, int z) override {
        if (isFull) {
            setShape(0, 0, 0, 1, 1, 1);
        } else {
            int data = level->getData(x, y, z);
            if (data & TOP_SLOT_BIT)
                setShape(0, 0.5f, 0, 1, 1, 1);
            else
                setShape(0, 0, 0, 1, 0.5f, 1);
        }
    }

    void updateDefaultShape() override {
        if (isFull)
            setShape(0, 0, 0, 1, 1, 1);
        else
            setShape(0, 0, 0, 1, 0.5f, 1);
    }

    int getPlacedOnFaceDataValue(Level* level, int x, int y, int z, int face, float clickX, float clickY, float clickZ, int itemValue) override {
        int type = itemValue & TYPE_MASK;
        if (face == 0) return type | TOP_SLOT_BIT;
        if (face == 1) return type;
        if (clickY > 0.5f) return type | TOP_SLOT_BIT;
        return type;
    }

    int getResource(int data, Random* random) override {
        return isFull ? Tile::woodSlabHalf->id : id;
    }

    int getResourceCount(Random* random) override {
        return isFull ? 2 : 1;
    }

    int getSpawnResourcesAuxValue(int data) override {
        return data & TYPE_MASK;
    }
};

#endif
