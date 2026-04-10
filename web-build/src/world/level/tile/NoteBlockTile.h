#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__NoteBlockTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__NoteBlockTile_H__

//package net.minecraft.world.level.tile;

#include "Tile.h"
#include "LevelEvent.h"
#include "../Level.h"
#include "../material/Material.h"
#include "../../entity/player/Player.h"

class NoteBlockTile: public Tile
{
	typedef Tile super;
public:
	// Texture indices
	static const int TEX_SIDE = 11 + 10 * 16; // 171
	static const int TEX_TOP  = 4;             // reuse oak plank texture for top/bottom

	NoteBlockTile(int id)
	:	super(id, TEX_SIDE, Material::wood)
	{
	}

	int getTexture(int face) {
		if (face == 0 || face == 1) return TEX_TOP;
		return TEX_SIDE;
	}

	int getTexture(int face, int data) {
		return getTexture(face);
	}

	bool use(Level* level, int x, int y, int z, Player* player) {
		if (level->isClientSide) return true;
		int data = level->getData(x, y, z);
		data = (data + 1) % 25; // 25 notes
		level->setData(x, y, z, data);
		// Play note sound
		level->levelEvent(player, LevelEvent::SOUND_CLICK, x, y, z, 0);
		return true;
	}

	void attack(Level* level, int x, int y, int z, Player* player) {
		if (level->isClientSide) return;
		// Play the note
		level->levelEvent(player, LevelEvent::SOUND_CLICK, x, y, z, 0);
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__NoteBlockTile_H__*/
