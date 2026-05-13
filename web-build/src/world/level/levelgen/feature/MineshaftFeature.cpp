#include "MineshaftFeature.h"

#include "../../Level.h"
#include "../../tile/Tile.h"
#include "../../tile/entity/MobSpawnerTileEntity.h"
#include "../../../item/Item.h"
#include "../../../item/ItemInstance.h"
#include "../../../inventory/FillingContainer.h"
#include "../../../../util/Random.h"
#include "../../../entity/EntityTypes.h"

static const int DIR_PX = 0;   // +X
static const int DIR_NX = 1;   // -X
static const int DIR_PZ = 2;   // +Z
static const int DIR_NZ = 3;   // -Z

// Offset table: dx/dz step for each direction
static const int DX[4] = {  1, -1,  0,  0 };
static const int DZ[4] = {  0,  0,  1, -1 };
// Perpendicular offsets (for 3-wide corridor)
static const int PX[4] = {  0,  0,  1, -1 };
static const int PZ[4] = {  1, -1,  0,  0 };

void MineshaftFeature::fillLootChest(Level* level, int cx, int cy, int cz, Random* random)
{
    TileEntity* te = level->getTileEntity(cx, cy, cz);
    if (!te) return;
    FillingContainer* inv = dynamic_cast<FillingContainer*>(te);
    if (!inv) return;

    // Common loot
    struct LootEntry { int itemId; int maxCount; int weight; };
    static const LootEntry table[] = {
        { Item::ironIngot->id,       4, 10 },
        { Item::bread->id,           2, 10 },
        { Item::coal->id,            4, 10 },
        { Item::redStone->id,        4, 10 },
        { Item::pickAxe_iron->id,    1, 10 },
        { Item::bone->id,            3, 10 },
        { Item::string->id,          3, 10 },
        { Tile::torch->id,           8,  8 },  // torches (common)
        { Item::goldIngot->id,       1,  3 },  // gold (rare, max 1)
        { Item::emerald->id,         1,  2 },  // emerald (rare)
    };
    static const int tableSize = (int)(sizeof(table) / sizeof(table[0]));

    // Calculate total weight
    static int totalWeight = 0;
    if (totalWeight == 0) {
        for (int i = 0; i < tableSize; ++i) totalWeight += table[i].weight;
    }

    int rolls = 3 + random->nextInt(4);
    for (int i = 0; i < rolls; ++i) {
        // Weighted random pick
        int w = random->nextInt(totalWeight);
        int idx = 0;
        for (idx = 0; idx < tableSize - 1; ++idx) {
            w -= table[idx].weight;
            if (w < 0) break;
        }
        const LootEntry& e = table[idx];
        int count = 1 + random->nextInt(e.maxCount);
        int slot  = random->nextInt(inv->getContainerSize());
        ItemInstance* inst = new ItemInstance(Item::items[e.itemId], count, 0);
        inv->setItem(slot, inst);
        delete inst;
    }
}

void MineshaftFeature::carveCorridor(Level* level, Random* random,
                                     int sx, int sy, int sz,
                                     int dir, int length, int depth)
{
    if (depth > MAX_DEPTH) return;

    int ddx = DX[dir];
    int ddz = DZ[dir];
    int ppx = PX[dir];
    int ppz = PZ[dir];

    for (int seg = 0; seg < length; ++seg) {
        int cx = sx + ddx * seg;
        int cz = sz + ddz * seg;

        // Carve 3-wide, 3-tall corridor
        for (int side = -1; side <= 1; ++side) {
            int bx = cx + ppx * side;
            int bz = cz + ppz * side;

            for (int h = 0; h < 3; ++h) {
                int by = sy + h;
                int tileId = level->getTile(bx, by, bz);

                // Only carve through stone/rock/dirt/gravel (skip air, water)
                if (tileId == Tile::rock->id   ||
                    tileId == Tile::dirt->id   ||
                    tileId == Tile::gravel->id ||
                    tileId == Tile::sand->id)
                {
                    placeBlock(level, bx, by, bz, 0); // air
                }
            }

            // Place plank floor (if there's something to stand on)
            {
                int floorTile = level->getTile(bx, sy - 1, bz);
                if (floorTile != 0 && floorTile != Tile::web->id) {
                    // Occasionally leave gaps
                    if (random->nextInt(4) != 0) {
                        // No plank floor here to keep it natural
                    }
                }
            }

            // Place cobwebs randomly
            if (random->nextInt(16) == 0 && side == 0) {
                int webY = sy + 1 + random->nextInt(2);
                if (level->getTile(bx, webY, bz) == 0)
                    placeBlock(level, bx, webY, bz, Tile::web->id);
            }

            // Fence-post pillars every 4 blocks, only on solid floor blocks
            if (seg % 4 == 2 && side != 0) {
                int floorTile = level->getTile(bx, sy - 1, bz);
                if (floorTile != 0 && floorTile != Tile::web->id) {
                    // Oak fence pillar
                    placeBlock(level, bx, sy,     bz, Tile::fence->id);
                    placeBlock(level, bx, sy + 1, bz, Tile::fence->id);
                    // Overhead plank beam (Tile::wood = oak planks, ID 5)
                    placeBlock(level, bx, sy + 2, bz, Tile::wood->id);
                }
            }
        }

        // Torch on the wall every 8 blocks
        if (seg % 8 == 4) {
            // Try to place torch on the left wall
            int wx = cx + ppx * 2;
            int wz = cz + ppz * 2;
            if (level->isSolidBlockingTile(wx, sy + 1, wz)) {
                // wall exists – place torch on inner face
                int tx = cx + ppx;
                int tz = cz + ppz;
                if (level->getTile(tx, sy + 1, tz) == 0)
                    placeBlock(level, tx, sy + 1, tz, Tile::torch->id);
            }
        }

        // Random cave spider spawner
        if (depth == 1 && seg == length / 2 && random->nextInt(8) == 0) {
            int ssx = cx;
            int ssz = cz;
            if (level->getTile(ssx, sy, ssz) == 0) {
                placeBlock(level, ssx, sy, ssz, Tile::mobSpawner->id);
                MobSpawnerTileEntity* sp =
                    (MobSpawnerTileEntity*) level->getTileEntity(ssx, sy, ssz);
                if (sp) sp->setMobType(MobTypes::CaveSpider);
            }
        }

        // Random loot chest
        if (seg == length - 2 && random->nextInt(5) == 0 && depth > 0) {
            // Find floor
            int chestX = cx;
            int chestZ = cz;
            if (level->getTile(chestX, sy, chestZ) == 0 &&
                level->isSolidBlockingTile(chestX, sy - 1, chestZ))
            {
                placeBlock(level, chestX, sy, chestZ, Tile::chest->id);
                fillLootChest(level, chestX, sy, chestZ, random);
            }
        }
    }

    // Branch: recurse in up to 2 perpendicular directions
    if (depth < MAX_DEPTH) {
        // Branch left (~50% chance)
        if (random->nextInt(2) == 0) {
            int newDir = (dir + 1) & 3;
            int newLen = MIN_LENGTH + random->nextInt(MAX_LENGTH - MIN_LENGTH);
            int nx = sx + ddx * (length / 2) + DX[newDir];
            int nz = sz + ddz * (length / 2) + DZ[newDir];
            carveCorridor(level, random, nx, sy, nz, newDir, newLen, depth + 1);
        }
        // Branch right (~50% chance)
        if (random->nextInt(2) == 0) {
            int newDir = (dir + 3) & 3;
            int newLen = MIN_LENGTH + random->nextInt(MAX_LENGTH - MIN_LENGTH);
            int nx = sx + ddx * (length / 2) + DX[newDir];
            int nz = sz + ddz * (length / 2) + DZ[newDir];
            carveCorridor(level, random, nx, sy, nz, newDir, newLen, depth + 1);
        }
    }
}

bool MineshaftFeature::place(Level* level, Random* random, int x, int y, int z)
{
    // Only generate between Y=10 and Y=40
    if (y < 10 || y > 40) return false;

    // Check there's enough rock to carve through
    int rockCount = 0;
    for (int dx = -2; dx <= 2; ++dx)
    for (int dy = 0; dy <= 2; ++dy)
    for (int dz = -2; dz <= 2; ++dz) {
        if (level->getTile(x + dx, y + dy, z + dz) == Tile::rock->id)
            ++rockCount;
    }
    if (rockCount < 20) return false;

    // Starting room (5×3×5)
    for (int dx = -2; dx <= 2; ++dx)
    for (int dy = 0; dy <= 2; ++dy)
    for (int dz = -2; dz <= 2; ++dz) {
        int tid = level->getTile(x + dx, y + dy, z + dz);
        if (tid == Tile::rock->id || tid == Tile::dirt->id || tid == Tile::gravel->id)
            placeBlock(level, x + dx, y + dy, z + dz, 0);
    }

    // Carve corridors in 2 opposite directions (chosen randomly) to avoid dense clumps
    int primaryDir = random->nextInt(4);
    int oppositeDir = (primaryDir + 2) & 3;
    for (int pass = 0; pass < 2; ++pass) {
        int dir = (pass == 0) ? primaryDir : oppositeDir;
        int len = MIN_LENGTH + random->nextInt(MAX_LENGTH - MIN_LENGTH);
        carveCorridor(level, random, x + DX[dir] * 3, y, z + DZ[dir] * 3, dir, len, 0);
    }

    return true;
}
