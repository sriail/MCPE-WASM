#include "MobSpawnerRenderer.h"
#include "TileEntityRenderer.h"
#include "../gles.h"
#include "../../model/SpiderModel.h"
#include "../../model/HumanoidModel.h"
#include "../../model/ZombieModel.h"
#include "../../model/SkeletonModel.h"
#include "../../../world/level/tile/entity/MobSpawnerTileEntity.h"
#include "../../../world/entity/EntityTypes.h"
#include "../../../util/Mth.h"

MobSpawnerRenderer::MobSpawnerRenderer()
:   spiderModel(new SpiderModel()),
    humanoidModel(new HumanoidModel())
{}

MobSpawnerRenderer::~MobSpawnerRenderer()
{
    delete spiderModel;
    delete humanoidModel;
}

void MobSpawnerRenderer::render(TileEntity* entity, float x, float y, float z, float a)
{
    MobSpawnerTileEntity* spawner = (MobSpawnerTileEntity*) entity;

    // Interpolated spin angle (degrees)
    float spin = (float)(spawner->getOSpin() + (spawner->getSpin() - spawner->getOSpin()) * a);
    float spinDeg = spin * 10.0f; // rotate 10° per tick

    // Select model and texture based on mob type
    Model*      model   = humanoidModel;
    const char* texture = "mob/zombie.png";

    switch (spawner->getMobType()) {
        case MobTypes::Skeleton:
            model   = humanoidModel;
            texture = "mob/skeleton.png";
            break;
        case MobTypes::Spider:
            model   = spiderModel;
            texture = "mob/spider.png";
            break;
        case MobTypes::CaveSpider:
            model   = spiderModel;
            // cave_spider.png is imported from the reference repo;
            // falls back to spider if not present
            texture = "mob/cave_spider.png";
            break;
        case MobTypes::Zombie:
        default:
            model   = humanoidModel;
            texture = "mob/zombie.png";
            break;
    }

    const float scale  = 1.0f / 16.0f;
    const float mobScale = 0.5f;  // half-size so it fits inside the cage

    glPushMatrix2();
    glDisable2(GL_CULL_FACE);

    // Translate to the center of the spawner cage block
    glTranslatef2(x + 0.5f, y + 0.5f, z + 0.5f);

    // Spin around Y axis
    glRotatef2(spinDeg, 0.0f, 1.0f, 0.0f);

    // Flip and scale so the mob fits inside the 1×1×1 cage
    glScalef2(-mobScale, -mobScale, mobScale);
    glTranslatef2(0.0f, -24.0f * scale, 0.0f);

    bindTexture(texture);

    // Render the model with a neutral walk pose
    model->render(NULL, 0.0f, 0.0f, (float)(spawner->getSpin() * 0.1f), 0.0f, 0.0f, scale);

    glEnable2(GL_CULL_FACE);
    glPopMatrix2();
}
