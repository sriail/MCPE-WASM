#ifndef NET_MINECRAFT_WORLD_ITEM__WoodDoorItem_H__
#define NET_MINECRAFT_WORLD_ITEM__WoodDoorItem_H__

#include "DoorItem.h"

class WoodDoorItem: public Item
{
	typedef Item super;
	Tile* doorTile;
public:
	WoodDoorItem(int id, Tile* tile)
	:	super(id),
		doorTile(tile)
	{
		maxDamage = 64;
		maxStackSize = 1;
	}

	bool useOn(ItemInstance* instance, Player* player, Level* level, int x, int y, int z, int face, float clickX, float clickY, float clickZ) {
		if (face != Facing::UP) return false;
		y++;

		if (!doorTile->mayPlace(level, x, y, z)) return false;

		int dir = Mth::floor(((player->yRot + 180) * 4) / 360 - 0.5f) & 3;

		DoorItem::place(level, x, y, z, dir, doorTile);

		instance->count--;
		return true;
	}
};

#endif /*NET_MINECRAFT_WORLD_ITEM__WoodDoorItem_H__*/
