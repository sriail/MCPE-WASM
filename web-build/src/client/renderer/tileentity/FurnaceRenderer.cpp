#include "FurnaceRenderer.h"
#include "TileEntityRenderer.h"
#include "../gles.h"
#include "../Tesselator.h"
#include "../../../world/level/tile/entity/FurnaceTileEntity.h"
#include "../../../world/level/tile/FurnaceTile.h"
#include "../../../world/level/Level.h"
#include "../../../world/level/tile/Tile.h"

void FurnaceRenderer::render(TileEntity* entity, float x, float y, float z, float a)
{
    if (!entity->level) return;

    FurnaceTileEntity* furnace = (FurnaceTileEntity*)entity;

    // Look up the live tile to retrieve correct face textures (lit / unlit)
    int tileId = entity->level->getTile(entity->x, entity->y, entity->z);
    FurnaceTile* tile = (FurnaceTile*)Tile::tiles[tileId];
    if (!tile) return;

    // Face indices: 0=bottom, 1=top, 2=north(z-), 3=south(z+/front), 4=west(x-), 5=east(x+)
    int frontTex = tile->getTexture(3); // front face — lit when burning
    int sideTex  = tile->getTexture(2); // sides / back
    int topTex   = tile->getTexture(1); // top & bottom

    bindTexture("terrain.png");

    float br = entity->level->getBrightness(entity->x, entity->y, entity->z);
    glColor4f2(br, br, br, 1.0f);

    glPushMatrix2();
    glTranslatef2(x, y, z);

    const float tw = 1.0f / 16.0f;
    Tesselator& t = Tesselator::instance;
    t.begin();

    // Helper to emit one quad given corner vertices and a texture tile index
    auto quad = [&](float ax, float ay, float az,
                    float bx, float by, float bz,
                    float cx, float cy, float cz,
                    float dx, float dy, float dz, int tex) {
        float u0 = (tex % 16) * tw;
        float v0 = (tex / 16) * tw;
        float u1 = u0 + tw;
        float v1 = v0 + tw;
        t.vertexUV(ax, ay, az, u0, v0);
        t.vertexUV(bx, by, bz, u0, v1);
        t.vertexUV(cx, cy, cz, u1, v1);
        t.vertexUV(dx, dy, dz, u1, v0);
    };

    // Top face (y=1)
    quad(0,1,0, 0,1,1, 1,1,1, 1,1,0, topTex);
    // Bottom face (y=0)
    quad(0,0,1, 0,0,0, 1,0,0, 1,0,1, topTex);
    // North face (z=0)
    quad(1,1,0, 1,0,0, 0,0,0, 0,1,0, sideTex);
    // South face (z=1) — front with lit/unlit texture
    quad(0,1,1, 0,0,1, 1,0,1, 1,1,1, frontTex);
    // West face (x=0)
    quad(0,1,0, 0,0,0, 0,0,1, 0,1,1, sideTex);
    // East face (x=1)
    quad(1,1,1, 1,0,1, 1,0,0, 1,1,0, sideTex);

    t.draw();
    glPopMatrix2();
    glColor4f2(1, 1, 1, 1);
}
