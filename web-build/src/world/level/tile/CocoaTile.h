#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__CocoaTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__CocoaTile_H__

//package net.minecraft.world.level.tile;

#include "Tile.h"
#include "../Level.h"
#include "../material/Material.h"
#include "../../item/Item.h"
#include "../../item/ItemInstance.h"
#include "../../../util/Random.h"

class CocoaTile: public Tile
{
	typedef Tile super;
public:
    static const int DIR_MASK = 3;
    static const int AGE_SHIFT = 2;
    static const int MAX_AGE = 2;

    static const int TEX_STAGE0 = 220; // 12 + 13*16
    static const int TEX_STAGE1 = 221; // 13 + 13*16
    static const int TEX_STAGE2 = 222; // 14 + 13*16

    CocoaTile(int id)
    :   super(id, TEX_STAGE0, Material::vegetable)
    {
        setTicking(true);
    }

    static int getAge(int data) { return (data >> AGE_SHIFT) & 3; }
    static int getDirection(int data) { return data & DIR_MASK; }

    int getTexture(int face, int data) {
        int age = getAge(data);
        if (age == 0) return TEX_STAGE0;
        if (age == 1) return TEX_STAGE1;
        return TEX_STAGE2;
    }

    int getTexture(int face) {
        return TEX_STAGE2;
    }

    bool isSolidRender() {
        return false;
    }

    bool isCubeShaped() {
        return false;
    }

    int getRenderShape() {
        return Tile::SHAPE_CROSS_TEXTURE;
    }

    void tick(Level* level, int x, int y, int z, Random* random) {
        if (level->isClientSide) return;
        int data = level->getData(x, y, z);
        int age = getAge(data);
        if (age < MAX_AGE && random->nextInt(5) == 0) {
            level->setDataNoUpdate(x, y, z, (data & DIR_MASK) | ((age + 1) << AGE_SHIFT));
        }
    }

    bool canSurvive(Level* level, int x, int y, int z) {
        // Cocoa can only survive when attached to a jungle log
        int data = level->getData(x, y, z);
        int dir = getDirection(data);
        int ax = x, az = z;
        if (dir == 0) az++;
        else if (dir == 1) ax--;
        else if (dir == 2) az--;
        else if (dir == 3) ax++;
        int attachedTile = level->getTile(ax, y, az);
        return attachedTile == Tile::jungleLog->id || attachedTile == Tile::treeTrunk->id;
    }

    void spawnResources(Level* level, int x, int y, int z, int data, float odds) {
        if (!level->isClientSide) {
            int age = getAge(data);
            int count = (age >= MAX_AGE) ? 3 : 1;
            popResource(level, x, y, z, ItemInstance(Item::dye_powder->id, count, 3));
        }
    }
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__CocoaTile_H__*/
