#ifndef NET_MINECRAFT_WORLD_ITEM__SignItem_H__
#define NET_MINECRAFT_WORLD_ITEM__SignItem_H__

//package net.minecraft.world.item;

#include "../entity/player/Player.h"
#include "../level/Level.h"
#include "../level/tile/Tile.h"
#include "../level/tile/entity/SignTileEntity.h"

class SignItem: public Item
{
	typedef Item super;
	Tile* signStanding;  // standing sign tile (NULL = default oak)
	Tile* signWall;      // wall sign tile (NULL = default oak)
public:
    SignItem(int id)
    :   super(id),
		signStanding(NULL),
		signWall(NULL)
    {
        maxStackSize = 16;
    }

	SignItem(int id, Tile* standingSign, Tile* wallSign)
    :   super(id),
		signStanding(standingSign),
		signWall(wallSign)
    {
        maxStackSize = 16;
    }

    /*@Override*/
    bool useOn(ItemInstance* instance, Player* player, Level* level, int x, int y, int z, int face, float clickX, float clickY, float clickZ) {
        if (face == 0) return false;
        if (!level->getMaterial(x, y, z)->isSolid()) return false;

        if (face == 1) y++;

        if (face == 2) z--;
        if (face == 3) z++;
        if (face == 4) x--;
        if (face == 5) x++;

		Tile* standTile = signStanding ? signStanding : Tile::sign;
		Tile* wallTile  = signWall ? signWall : Tile::wallSign;

        //if (!player->mayUseItemAt(x, y, z, face, instance)) return false;
        if (!standTile->mayPlace(level, x, y, z)) return false;

        if (face == 1) {
            int rot = Mth::floor(((player->yRot + 180) * 16) / 360 + 0.5f) & 15;
            level->setTileAndData(x, y, z, standTile->id, rot);
        } else {
            level->setTileAndData(x, y, z, wallTile->id, face);
        }

        instance->count--;
        SignTileEntity* ste = (SignTileEntity*) level->getTileEntity(x, y, z);
        if (ste != NULL) player->openTextEdit(ste);
        return true;
    }
};

#endif /*NET_MINECRAFT_WORLD_ITEM__SignItem_H__*/
