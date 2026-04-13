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

	HayBaleTile(int id, int tex)
	:	super(id, tex, Material::grass)
	{
	}

	int getTexture(int face, int data) {
		int axis = data & 0xC;
		// top texture = tex, side texture = tex + 1
		if (axis == AXIS_Y) {
			return (face == 0 || face == 1) ? tex : tex + 1;
		} else if (axis == AXIS_X) {
			return (face == 4 || face == 5) ? tex : tex + 1;
		} else { // AXIS_Z
			return (face == 2 || face == 3) ? tex : tex + 1;
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
