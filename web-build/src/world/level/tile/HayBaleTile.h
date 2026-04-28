#ifndef NET_MINECRAFT_WORLD_LEVEL_TILE__HayBaleTile_H__
#define NET_MINECRAFT_WORLD_LEVEL_TILE__HayBaleTile_H__

#include "Tile.h"
#include "../material/Material.h"
#include "../Level.h"
#include "../../entity/Mob.h"
#include "../../../util/Mth.h"

class HayBaleTile : public Tile
{
	typedef Tile super;
public:
	// Axis orientation stored in data bits 2-3
	static const int AXIS_Y = 0; // default: upright
	static const int AXIS_X = 4;
	static const int AXIS_Z = 8;

	static const int TEX_TOP  = 9 + 11 * 16;  // hay bale top  (144, 176)
	static const int TEX_SIDE = 9 + 12 * 16;  // hay bale side (144, 192)

	HayBaleTile(int id)
	:	super(id, TEX_SIDE, Material::plant)
	{
	}

	int getTexture(int face, int data) {
		int axis = data & 0xC;
		if (axis == AXIS_Y) {
			return (face == 0 || face == 1) ? TEX_TOP : TEX_SIDE;
		} else if (axis == AXIS_X) {
			return (face == 4 || face == 5) ? TEX_TOP : TEX_SIDE;
		} else { // AXIS_Z
			return (face == 2 || face == 3) ? TEX_TOP : TEX_SIDE;
		}
	}

	int getTexture(int face) {
		return getTexture(face, 0);
	}

	void setPlacedBy(Level* level, int x, int y, int z, Mob* by) {
		// Axis rotation is set based on the face clicked
		// Default is Y-axis (upright)
	}

	int getPlacedOnFaceDataValue(Level* level, int x, int y, int z, int face, float clickX, float clickY, float clickZ, int itemValue) {
		// Map placed face to axis
		if (face == 0 || face == 1) return AXIS_Y; // top/bottom → Y axis
		if (face == 2 || face == 3) return AXIS_Z; // north/south → Z axis
		if (face == 4 || face == 5) return AXIS_X; // east/west → X axis
		return AXIS_Y;
	}

protected:
	int getSpawnResourcesAuxValue(int data) {
		return 0; // always drop default
	}
};

#endif /*NET_MINECRAFT_WORLD_LEVEL_TILE__HayBaleTile_H__*/
