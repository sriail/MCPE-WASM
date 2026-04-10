#include "PistonBaseTile.h"
#include "../Level.h"
#include "../../entity/Mob.h"
#include "../../../util/Mth.h"
#include <cmath>

void PistonBaseTile::setPlacedBy(Level* level, int x, int y, int z, Mob* by) {
    // Set direction based on player facing
    float pitch = by->xRot;
    int dir;
    if (pitch > 45.0f) {
        dir = 0; // down
    } else if (pitch < -45.0f) {
        dir = 1; // up
    } else {
        float yaw = by->yRot;
        yaw = fmod(yaw, 360.0f);
        if (yaw < 0) yaw += 360.0f;
        if (yaw >= 315 || yaw < 45) dir = 2;       // south
        else if (yaw >= 45 && yaw < 135) dir = 5;   // west
        else if (yaw >= 135 && yaw < 225) dir = 3;   // north
        else dir = 4;                                  // east
    }
    level->setData(x, y, z, dir);
}
