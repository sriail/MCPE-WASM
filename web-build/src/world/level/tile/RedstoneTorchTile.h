#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__RedstoneTorchTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__RedstoneTorchTile_H__

#include "TorchTile.h"

// Redstone torch tile — behaves like a regular torch but uses a redstone
// texture.  Two variants:
//   Off: terrain.png (48,112) = col=3, row=7 = 3+7*16=115  — no light
//   On:  terrain.png (48, 96) = col=3, row=6 = 3+6*16= 99  — emits light level 7
class RedstoneTorchTile : public TorchTile
{
	typedef TorchTile super;
	bool _powered;
public:
	RedstoneTorchTile(int id, int tex, bool powered)
	:	super(id, tex),
		_powered(powered)
	{
		if (powered) {
			setLightEmission(7 / 16.0f);
		}
	}

	void animateTick(Level* level, int xt, int yt, int zt, Random* random) {
		if (!_powered) return;
		// Only the lit torch emits particles
		super::animateTick(level, xt, yt, zt, random);
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__RedstoneTorchTile_H__*/
