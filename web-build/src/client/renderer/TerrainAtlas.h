#ifndef TERRAIN_ATLAS_H
#define TERRAIN_ATLAS_H

// Terrain texture atlas constants for 272x272 (17x17 tiles of 16px each)
static const int TERRAIN_ATLAS_COLS = 17;
static const int TERRAIN_ATLAS_TILE_SIZE = 16;
static const float TERRAIN_ATLAS_PIXELS = 272.0f;

// Convert texture index to pixel coordinates
inline int texToPixelX(int tex) { return (tex % TERRAIN_ATLAS_COLS) * TERRAIN_ATLAS_TILE_SIZE; }
inline int texToPixelY(int tex) { return (tex / TERRAIN_ATLAS_COLS) * TERRAIN_ATLAS_TILE_SIZE; }

// Create texture index from column and row
inline int texCoord(int col, int row) { return col + row * TERRAIN_ATLAS_COLS; }

#endif
