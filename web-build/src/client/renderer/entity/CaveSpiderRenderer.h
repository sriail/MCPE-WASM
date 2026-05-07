#ifndef NET_MINECRAFT_CLIENT_RENDERER_ENTITY__CaveSpiderRenderer_H__
#define NET_MINECRAFT_CLIENT_RENDERER_ENTITY__CaveSpiderRenderer_H__

#include "SpiderRenderer.h"
#include "../gles.h"
#include "../../../world/entity/monster/CaveSpider.h"

// Identical to SpiderRenderer but uses the cave spider texture and is scaled
// down to 70% of spider size (matching CaveSpider::getModelScale()).
class CaveSpiderRenderer : public SpiderRenderer
{
    typedef SpiderRenderer super;
protected:
    /*@Override*/
    void scale(Mob* mob, float a) {
        float s = ((Spider*)mob)->getModelScale();
        glScalef(s, s, s);
    }
};

#endif /*NET_MINECRAFT_CLIENT_RENDERER_ENTITY__CaveSpiderRenderer_H__*/
