#ifndef NET_MINECRAFT_WORLD_ENTITY_ITEM__XpOrbEntity_H__
#define NET_MINECRAFT_WORLD_ENTITY_ITEM__XpOrbEntity_H__

#include "../Entity.h"
#include "../EntityTypes.h"
#include "../EntityRendererId.h"
#include "../../level/Level.h"

class Player;

// Experience orb. Floats toward nearby players and is collected on contact.
class XpOrbEntity : public Entity
{
    typedef Entity super;

    static const int DESPAWN_TICKS = 5 * 60 * 20; // 5 minutes
    static const int COLLECTION_RADIUS_SQ = 1; // 1 block squared

public:
    int value;  // XP point value carried by this orb

    XpOrbEntity(Level* level, float x, float y, float z, int value)
    :   super(level),
        value(value),
        age(0)
    {
        entityRendererId = ER_ITEM_RENDERER; // reuse item renderer as spinning billboard
        setSize(0.5f, 0.5f);
        setPos(x, y, z);
        // Random initial velocity
        yRot = (float)(rand() % 360);
        xd = (float)(((rand() % 1000) / 1000.0f) * 0.2f - 0.1f);
        yd = 0.2f;
        zd = (float)(((rand() % 1000) / 1000.0f) * 0.2f - 0.1f);
    }

    XpOrbEntity(Level* level) : super(level), value(1), age(0) {
        entityRendererId = ER_ITEM_RENDERER;
        setSize(0.5f, 0.5f);
    }

    int getEntityTypeId() const {
        return EntityTypes::IdXpOrb;
    }

    void tick() {
        super::tick();
        ++age;
        if (age > DESPAWN_TICKS) {
            remove();
            return;
        }

        // Gravity
        yd -= 0.03f;

        // Move
        move(xd, yd, zd);

        // Friction
        if (onGround) {
            xd *= 0.7f;
            zd *= 0.7f;
            yd *= -0.5f;
        } else {
            xd *= 0.98f;
            yd *= 0.98f;
            zd *= 0.98f;
        }

        // Attract toward nearest player within 8 blocks
        Player* nearest = level->getNearestPlayer(x, y, z, 8.0f);
        if (nearest != NULL) {
            float dx = nearest->x - x;
            float dy = (nearest->y + 1.0f) - y;
            float dz = nearest->z - z;
            float distSq = dx*dx + dy*dy + dz*dz;
            if (distSq > 0.0001f) {
                float dist = sqrtf(distSq);
                float speed = 0.1f / dist;
                xd += dx * speed;
                yd += dy * speed;
                zd += dz * speed;
            }

            // Collect if close enough
            if (distSq < (float)COLLECTION_RADIUS_SQ) {
                nearest->awardXp(value);
                remove();
            }
        }
    }

    bool isPickable() { return false; }
    bool isPushable() { return false; }

private:
    int age;
};

#endif /*NET_MINECRAFT_WORLD_ENTITY_ITEM__XpOrbEntity_H__*/
