#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE_ENTITY__MobSpawnerTileEntity_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE_ENTITY__MobSpawnerTileEntity_H__

#include "TileEntity.h"
#include "../../../../util/Random.h"
#include <string>

class CompoundTag;
class Level;
class Mob;
class Entity;

// Tile entity that holds the mob spawner state:
//   - which mob type to spawn
//   - a spin value used by the client renderer to animate the "display" mob
class MobSpawnerTileEntity : public TileEntity
{
    typedef TileEntity super;
public:
    MobSpawnerTileEntity();

    // TileEntity overrides
    bool shouldSave();
    void load(CompoundTag* tag);
    bool save(CompoundTag* tag);
    void tick();

    // Accessors
    int  getMobType() const  { return mobType; }
    void setMobType(int type);

    double getSpin() const   { return spin; }
    double getOSpin() const  { return oSpin; }

private:
    void attemptSpawn();

public:
    // Mob type ID (MobTypes::Zombie / Skeleton / Spider / CaveSpider)
    int     mobType;

    // Spin angle for the display entity (animated on the client)
    double  spin;
    double  oSpin;

private:
    // Ticks until next spawn attempt
    int     spawnDelay;

    static const int MIN_SPAWN_DELAY = 200;
    static const int MAX_SPAWN_DELAY = 800;
    static const int REQUIRED_PLAYER_RANGE = 16;
    static const int SPAWN_RANGE = 4;
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE_ENTITY__MobSpawnerTileEntity_H__*/
