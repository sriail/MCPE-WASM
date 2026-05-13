#include "MonsterRoomFeature.h"

#include "../../Level.h"
#include "../../tile/Tile.h"
#include "../../tile/entity/MobSpawnerTileEntity.h"
#include "../../../item/Item.h"
#include "../../../item/ItemInstance.h"
#include "../../../inventory/FillingContainer.h"
#include "../../../../util/Random.h"
#include "../../../entity/EntityTypes.h"

// Picks one of the 3 spawner mob variants
static int pickMobType(Random* random)
{
    switch (random->nextInt(3)) {
    case 0: return MobTypes::Spider;
    case 1: return MobTypes::Skeleton;
    default: return MobTypes::Zombie;
    }
}

// Populates a chest with random dungeon loot (items from the vanilla dungeon loot table)
static void fillLootChest(Level* level, int cx, int cy, int cz, Random* random)
{
    // Retrieve the tile entity that was just placed
    TileEntity* te = level->getTileEntity(cx, cy, cz);
    if (!te) return;

    // Build a small loot table (item id, max count)
    struct LootEntry { int itemId; int maxCount; };
    static const LootEntry table[] = {
        { Item::ironIngot->id,         4 },
        { Item::goldIngot->id,         4 },
        { Item::emerald->id,           2 },
        { Item::bread->id,             2 },
        { Item::wheat->id,             4 },
        { Item::pickAxe_iron->id,      1 },
        { Item::sword_iron->id,        1 },
        { Item::chestplate_iron->id,   1 },
        { Item::apple->id,             3 },
        { Item::bone->id,              4 },
        { Item::string->id,            4 },
        { Item::redStone->id,          4 },
        { Item::coal->id,              4 },
    };
    static const int tableSize = (int)(sizeof(table) / sizeof(table[0]));

    // Cast to FillingContainer so we can fill slots
    FillingContainer* inv = dynamic_cast<FillingContainer*>(te);
    if (!inv) return;

    // Place 3-7 random item stacks
    int rolls = 3 + random->nextInt(5);
    for (int i = 0; i < rolls; ++i) {
        const LootEntry& entry = table[random->nextInt(tableSize)];
        int count = 1 + random->nextInt(entry.maxCount);
        int slot  = random->nextInt(inv->getContainerSize());
        ItemInstance* inst = new ItemInstance(Item::items[entry.itemId], count, 0);
        inv->setItem(slot, inst);
        delete inst;  // setItem copies
    }
}

bool MonsterRoomFeature::place(Level* level, Random* random, int x, int y, int z)
{
    // Half-sizes of the dungeon room (inner hollow part)
    int hw = 3 + random->nextInt(2); // 3 or 4
    int hh = 2;
    int hd = 3 + random->nextInt(2);

    // Count air blocks inside the room volume to confirm we are underground
    int airBlocks = 0;
    for (int dx = -hw - 1; dx <= hw + 1; ++dx)
    for (int dy = -1; dy <= hh + 1; ++dy)
    for (int dz = -hd - 1; dz <= hd + 1; ++dz) {
        if (level->getTile(x + dx, y + dy, z + dz) == 0)
            ++airBlocks;
    }
    int totalVol = (2*hw+3) * (hh+3) * (2*hd+3);
    if (airBlocks > totalVol / 2) return false;  // Too much air – not underground

    // Carve the room and build walls
    for (int dx = -hw - 1; dx <= hw + 1; ++dx)
    for (int dy = -1; dy <= hh + 1; ++dy)
    for (int dz = -hd - 1; dz <= hd + 1; ++dz) {
        int tileId = level->getTile(x + dx, y + dy, z + dz);

        bool isWall = dx == -(hw + 1) || dx == (hw + 1)
                   || dz == -(hd + 1) || dz == (hd + 1)
                   || dy == -1        || dy == (hh + 1);

        if (isWall) {
            if (dy < 0 && tileId != 0)  {
                // Use mossy cobblestone for floor, randomly cobblestone (stoneBrick = ID 4)
                int wallId = (random->nextInt(4) == 0)
                           ? Tile::mossStone->id
                           : Tile::stoneBrick->id;
                placeBlock(level, x + dx, y + dy, z + dz, wallId);
            } else if (dy >= 0 && tileId != 0) {
                int wallId = (random->nextInt(4) == 0)
                           ? Tile::mossStone->id
                           : Tile::stoneBrick->id;
                placeBlock(level, x + dx, y + dy, z + dz, wallId);
            }
        } else {
            // Interior – clear to air
            placeBlock(level, x + dx, y + dy, z + dz, 0);
        }
    }

    // Place the mob spawner in the centre
    placeBlock(level, x, y, z, Tile::mobSpawner->id);
    {
        MobSpawnerTileEntity* spawner =
            (MobSpawnerTileEntity*) level->getTileEntity(x, y, z);
        if (spawner) {
            spawner->setMobType(pickMobType(random));
        }
    }

    // Place 2 loot chests on the floor at random positions
    int chestsPlaced = 0;
    for (int attempt = 0; chestsPlaced < 2 && attempt < 16; ++attempt) {
        int cx = x + random->nextInt(2 * hw + 1) - hw;
        int cz = z + random->nextInt(2 * hd + 1) - hd;
        int cy = y;

        if (level->getTile(cx, cy, cz) == 0) {
            // Floor must be solid
            if (!level->isSolidBlockingTile(cx, cy - 1, cz)) continue;

            placeBlock(level, cx, cy, cz, Tile::chest->id);
            fillLootChest(level, cx, cy, cz, random);
            ++chestsPlaced;
        }
    }

    return true;
}
