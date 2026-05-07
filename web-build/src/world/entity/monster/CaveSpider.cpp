#include "CaveSpider.h"

#include "../../level/Level.h"
#include "../../../util/Mth.h"

CaveSpider::CaveSpider(Level* level)
:   super(level)
{
    // Cave spider is a smaller version of the spider
    this->setSize(0.7f, 0.5f);
    // Use the dedicated cave spider renderer
    entityRendererId = ER_CAVE_SPIDER_RENDERER;
    // cave_spider.png is imported from the reference repo (mob/cave_spider.png)
    this->textureName = "mob/cave_spider.png";
}

int CaveSpider::getMaxHealth()
{
    return 12;
}

float CaveSpider::getModelScale()
{
    // Render at 70% of normal spider size
    return 0.7f;
}

int CaveSpider::getEntityTypeId() const
{
    return MobTypes::CaveSpider;
}

void CaveSpider::checkHurtTarget(Entity* target, float d)
{
    // Let the base Spider handle the basic hit check
    // then apply poison if the hit connected (attackTime was reset)
    int prevAttackTime = attackTime;
    super::checkHurtTarget(target, d);

    bool hitConnected = (prevAttackTime > 0 && attackTime == getAttackTime());
    if (!hitConnected) {
        // Also handle the case where attackTime was 0 before (first hit)
        hitConnected = (prevAttackTime == 0 && attackTime > 0);
    }

    if (hitConnected && target && target->isMob()) {
        // Apply poison: 7 seconds on Normal, 15 on Hard
        // Difficulty::NORMAL = 2, Difficulty::HARD = 3
        int poisonDuration = 0;
        if (level->difficulty >= 3)       poisonDuration = 15 * 20; // 15 seconds
        else if (level->difficulty >= 2)  poisonDuration = 7  * 20; //  7 seconds

        if (poisonDuration > 0) {
            target->addPoison(poisonDuration);
        }
    }
}
