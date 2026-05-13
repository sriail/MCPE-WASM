#include "MobSpawnerTileEntity.h"
#include "../../Level.h"
#include "../../../entity/EntityTypes.h"
#include "../../../entity/MobFactory.h"
#include "../../../entity/Mob.h"
#include "../../../../nbt/CompoundTag.h"
#include "../../../../util/Mth.h"
#include "../../../../util/Random.h"

MobSpawnerTileEntity::MobSpawnerTileEntity()
:   super(TileEntityType::MobSpawner),
    mobType(MobTypes::Zombie),
    spin(0.0),
    oSpin(0.0),
    spawnDelay(20 * 10) // 10 second initial delay
{
    rendererId = TR_MOB_SPAWNER_RENDERER;
}

bool MobSpawnerTileEntity::shouldSave()
{
    return true;
}

void MobSpawnerTileEntity::load(CompoundTag* tag)
{
    super::load(tag);
    if (tag->contains("EntityId"))
        mobType = tag->getInt("EntityId");
    if (tag->contains("Delay"))
        spawnDelay = tag->getShort("Delay");
}

bool MobSpawnerTileEntity::save(CompoundTag* tag)
{
    if (!super::save(tag))
        return false;
    tag->putInt("EntityId", mobType);
    tag->putShort("Delay", (short) spawnDelay);
    return true;
}

void MobSpawnerTileEntity::setMobType(int type)
{
    mobType = type;
    spawnDelay = 20;
}

void MobSpawnerTileEntity::tick()
{
    if (!level) return;

    // Always spin the display entity regardless of server/client side
    oSpin = spin;
    spin += 1.0;

    if (level->isClientSide) return;

    // Check for nearby player
    if (level->getNearestPlayer((float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f,
                                (float)REQUIRED_PLAYER_RANGE) == NULL)
        return;

    if (spawnDelay > 0) {
        --spawnDelay;
        return;
    }

    attemptSpawn();

    // Reset delay with jitter
    spawnDelay = MIN_SPAWN_DELAY + level->random.nextInt(MAX_SPAWN_DELAY - MIN_SPAWN_DELAY);
}

void MobSpawnerTileEntity::attemptSpawn()
{
    if (!level || mobType == 0) return;

    for (int attempt = 0; attempt < 4; ++attempt) {
        float sx = (float)x + (level->random.nextFloat() - level->random.nextFloat()) * SPAWN_RANGE;
        float sy = (float)(y + level->random.nextInt(3) - 1);
        float sz = (float)z + (level->random.nextFloat() - level->random.nextFloat()) * SPAWN_RANGE;

        Mob* mob = MobFactory::CreateMob(mobType, level);
        if (!mob) return;

        mob->moveTo(sx, sy, sz, level->random.nextFloat() * 360.0f, 0);
        if (mob->canSpawn()) {
            level->addEntity(mob);
            level->playSound(sx, sy, sz, "mob.zombie.hurt", 1.0f, 1.0f);
            return;
        }
        delete mob;
    }
}
