#ifndef NET_MINECRAFT_WORLD_ITEM__WoodSignItem_H__
#define NET_MINECRAFT_WORLD_ITEM__WoodSignItem_H__

#include "SignItem.h"

class WoodSignItem: public Item
{
	typedef Item super;
	Tile* groundSign;
	Tile* wallSign;
public:
	WoodSignItem(int id, Tile* ground, Tile* wall)
	:	super(id),
		groundSign(ground),
		wallSign(wall)
	{
		maxStackSize = 16;
	}

	bool useOn(ItemInstance* instance, Player* player, Level* level, int x, int y, int z, int face, float clickX, float clickY, float clickZ) {
		if (face == 0) return false;
		if (!level->getMaterial(x, y, z)->isSolid()) return false;

		if (face == 1) y++;

		if (face == 2) z--;
		if (face == 3) z++;
		if (face == 4) x--;
		if (face == 5) x++;

		if (!groundSign->mayPlace(level, x, y, z)) return false;

		if (face == 1) {
			int rot = Mth::floor(((player->yRot + 180) * 16) / 360 + 0.5f) & 15;
			level->setTileAndData(x, y, z, groundSign->id, rot);
		} else {
			level->setTileAndData(x, y, z, wallSign->id, face);
		}

		instance->count--;
		SignTileEntity* ste = (SignTileEntity*) level->getTileEntity(x, y, z);
		if (ste != NULL) player->openTextEdit(ste);
		return true;
	}
};

#endif /*NET_MINECRAFT_WORLD_ITEM__WoodSignItem_H__*/
