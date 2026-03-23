#!/usr/bin/env python3
"""
build_terrain_atlas.py
======================
Stitches individual modern block-texture PNGs (and TGAs) into a single
256×256 terrain.png atlas that matches the MCPE 0.6.2 UV-grid layout.

Atlas format:
  - 16 columns × 16 rows of 16×16 pixel tiles
  - tile index = col + row * 16
  - UV in shader: u = (col*16)/256, v = (row*16)/256  (0..1)

Usage:
    python3 build_terrain_atlas.py [--src <blocks_dir>] [--out <terrain.png>]

Defaults:
    --src  ../../data/images/textures/blocks
    --out  ../../data/images/terrain.png
"""

import sys
import os
import argparse
from PIL import Image

# ---------------------------------------------------------------------------
# Atlas slot → texture filename (relative to the blocks directory)
# Derived from MCPE 0.6.2 Tile.cpp initTiles() and individual Tile subclasses.
# Index = col + row * 16  (col = index % 16, row = index // 16)
# ---------------------------------------------------------------------------
ATLAS_MAP = {
    # ── Row 0  (y=0) ────────────────────────────────────────────────────────
    0:  "grass_top.png",           # GrassTile face-up
    1:  "stone.png",               # Stone (ID 1)
    2:  "dirt.png",                # Dirt (ID 3); also GrassTile face-down
    3:  "grass_side.tga",          # GrassTile sides (tinted at render time)
    4:  "planks_oak.png",          # Oak planks (ID 5), fence, workbench bottom
    5:  "stone_slab_side.png",     # Stone slab side
    6:  "stone_slab_top.png",      # Stone slab top/bottom
    7:  "brick.png",               # Red brick (ID 45)
    8:  "tnt_side.png",            # TNT side (ID 46)
    9:  "tnt_top.png",             # TNT top
    10: "tnt_bottom.png",          # TNT bottom
    11: "web.png",                 # Cobweb (ID 30)
    12: "flower_rose.png",         # Rose (ID 38)
    13: "flower_dandelion.png",    # Dandelion (ID 37)
    14: "portal.png",              # Nether portal
    15: "sapling_oak.png",         # Oak sapling (ID 6)

    # ── Row 1  (y=16) ───────────────────────────────────────────────────────
    16: "cobblestone.png",         # Cobblestone (ID 4)
    17: "bedrock.png",             # Bedrock (ID 7)
    18: "sand.png",                # Sand (ID 12)
    19: "gravel.png",              # Gravel (ID 13)
    20: "log_oak.png",             # Oak log side (ID 17, normal trunk)
    21: "log_oak_top.png",         # Oak log top/bottom
    22: "iron_block.png",          # Iron block (ID 42, tex=38-16=22)
    23: "gold_block.png",          # Gold block (ID 41, tex=39-16=23)
    24: "diamond_block.png",       # Diamond block (ID 57, tex=40-16=24)
    # 25-27: chest uses entity-level textures, not terrain atlas
    28: "mushroom_red.png",        # Red mushroom plant (ID 40, tex=12+16=28)
    29: "mushroom_brown.png",      # Brown mushroom plant (ID 39, tex=13+16=29)

    # ── Row 2  (y=32) ───────────────────────────────────────────────────────
    32: "gold_ore.png",            # Gold ore (ID 14)
    33: "iron_ore.png",            # Iron ore (ID 15)
    34: "coal_ore.png",            # Coal ore (ID 16)
    35: "bookshelf.png",           # Bookshelf side (ID 47)
    36: "cobblestone_mossy.png",   # Mossy cobblestone (ID 48)
    37: "obsidian.png",            # Obsidian (ID 49)
    38: "grass_side_snowed.png",   # Snowed grass side (row 2, col 6)
    39: "tallgrass.png",           # Tall grass plant (ID 31, tex=2*16+7=39)
    # 40-42: reserved / blank
    43: "crafting_table_top.png",  # Workbench top (tex-16 = 59-16 = 43)
    44: "furnace_front_off.png",   # Furnace front unlit (tex-1 = 45-1 = 44)
    45: "furnace_side.png",        # Furnace sides (tex = 13+2*16 = 45)

    # ── Row 3  (y=48) ───────────────────────────────────────────────────────
    # 48: fire animation (handled separately by FireTile; tex=1*16+15=31)
    49: "glass.png",               # Glass (ID 20)
    50: "diamond_ore.png",         # Diamond ore (ID 56, "oreDiamond", tex=3*16+2=50)
    51: "redstone_ore.png",        # Redstone ore (ID 73, tex=3*16+3=51)
    52: "leaves_oak_opaque.png",   # Oak leaves – opaque (ID 18, tex=4+3*16=52)
    # 53: blank
    54: "stonebrick.png",          # Stone brick – smooth (STONE_BRICK_TEXTURES[0]=6+3*16=54)
    55: "deadbush.png",            # Dead shrub (TallGrass data=DEAD_SHRUB → tex+16=55)
    56: "fern.tga",                # Fern (TallGrass data=FERN → tex+16+1=56)
    # 57-58: blank
    59: "crafting_table_front.png",# Workbench south/front face (tex = 11+3*16 = 59)
    60: "crafting_table_side.png", # Workbench east/west face (tex+1 = 60)
    61: "furnace_front_on.png",    # Furnace front lit (tex+16 = 61)
    62: "furnace_top.png",         # Furnace top/bottom (tex+17 = 62)

    # ── Row 4  (y=64) ───────────────────────────────────────────────────────
    # 64-65: water (animated; not stored as static tile)
    66: "snow.png",                # Snow block (ID 80, tex=4*16+2=66)
    67: "ice.png",                 # Ice (ID 79, tex=4*16+3=67)
    68: "grass_side_snowed.png",   # Grass side w/ snow above (GrassTile: 4*16+4=68)
    69: "cactus_top.tga",          # Cactus top (CactusTile tex-1=69)
    70: "cactus_side.tga",         # Cactus side (CactusTile tex=4*16+6=70)
    71: "cactus_bottom.tga",       # Cactus bottom (CactusTile tex+1=71)
    72: "clay.png",                # Clay (ID 82, tex=4*16+8=72)
    73: "reeds.tga",               # Sugarcane (ID 83, tex=4*16+9=73)

    # ── Row 5  (y=80) ───────────────────────────────────────────────────────
    80: "torch_on.png",            # Torch (ID 50, tex=5*16=80)
    81: "door_wood_upper.png",     # Wood door upper (DoorTile wood tex-16=81)
    82: "door_iron_upper.png",     # Iron door upper (DoorTile metal tex-16=82)
    83: "ladder.png",              # Ladder (ID 65, tex=3+5*16=83)
    84: "trapdoor.png",            # Trapdoor (ID 96)
    # 85-86: blank
    87: "iron_bars.png",           # Iron bars (ThinFenceTile tex=7+5*16=87)
    88: "wheat_stage_0.png",       # Wheat stage 0 (CropTile tex=8+5*16=88)
    89: "wheat_stage_1.png",
    90: "wheat_stage_2.png",
    91: "wheat_stage_3.png",
    92: "wheat_stage_4.png",
    93: "wheat_stage_5.png",
    94: "wheat_stage_6.png",
    95: "wheat_stage_7.png",

    # ── Row 6  (y=96) ───────────────────────────────────────────────────────
    97: "door_wood_lower.png",     # Wood door lower half (DoorTile wood tex=1+6*16=97)
    98: "door_iron_lower.png",     # Iron door lower half (DoorTile metal tex=98)
    100: "stonebrick_mossy.png",   # STONE_BRICK_TEXTURES[1]=4+6*16=100
    101: "stonebrick_cracked.png", # STONE_BRICK_TEXTURES[2]=5+6*16=101
    103: "netherrack.png",         # Netherrack (ID 87, tex=7+6*16=103)
    105: "glowstone.png",          # Glowstone (ID 89, tex=9+6*16=105)

    # ── Row 7  (y=112) ──────────────────────────────────────────────────────
    4 + 7*16: "log_spruce.png",    # Spruce log side (TreeTile DARK_TRUNK, tex=4+7*16=116)
    5 + 7*16: "log_birch.png",     # Birch log side (TreeTile BIRCH_TRUNK, tex=5+7*16=117)

    # ── Row 8  (y=128) ──────────────────────────────────────────────────────
    8 + 8*16: "melon_side.png",    # Melon side (MelonTile TEX=8+8*16=136)
    9 + 8*16: "melon_top.png",     # Melon top/bottom (TEX_TOP=9+8*16=137)

    # ── Row 9  (y=144) ──────────────────────────────────────────────────────
    0 + 9*16: "lapis_block.png",   # Lapis lazuli block (ID 22, tex=9*16=144)

    # ── Row 10 (y=160) ──────────────────────────────────────────────────────
    0 + 10*16: "lapis_ore.png",    # Lapis lazuli ore (ID 21, tex=10*16=160)

    # ── Row 11 (y=176) ── sandstone slab top ────────────────────────────────
    0 + 11*16: "sandstone_top.png",       # StoneSlab SAND_SLAB face-up (11*16=176)

    # ── Row 12 (y=192) ── sandstone block, quartz block top ─────────────────
    0 + 12*16: "sandstone_normal.png",    # Sandstone side default (SANDSTONE_TEXTURES[0]=192)
    4 + 12*16: "quartz_block_top.png",    # Quartz top – default (QuartzBlock UP, default=4+12*16=196)
    5 + 12*16: "quartz_block_lines_top.png",    # Quartz top – lines (197)
    6 + 12*16: "quartz_block_chiseled_top.png", # Quartz top – chiseled (198)

    # ── Row 13 (y=208) ── sandstone bottom, quartz sides ────────────────────
    0 + 13*16: "sandstone_bottom.png",    # StoneSlab SAND_SLAB face-down (13*16=208)
    3 + 13*16: "quartz_block_bottom.png", # Quartz bottom face (3+13*16=211)
    4 + 13*16: "quartz_block_side.png",   # Quartz side – default (4+13*16=212)
    5 + 13*16: "quartz_block_lines.png",  # Quartz side – lines (213)
    6 + 13*16: "quartz_block_chiseled.png", # Quartz side – chiseled (214)

    # ── Row 14 (y=224) ── nether brick, sandstone variants, leaves_carried ──
    0 + 14*16: "nether_brick.png",        # Nether brick (ID 112, tex=0+14*16=224)
    5 + 14*16: "sandstone_carved.png",    # Sandstone carved (SANDSTONE_TEXTURES[1]=229)
    6 + 14*16: "sandstone_smooth.png",    # Sandstone smooth (SANDSTONE_TEXTURES[2]=230)
   11 + 14*16: "leaves_oak_opaque.png",  # leaves_carried (ID 254, tex=11+14*16=235)

    # ── Row 15 (y=240) ── info / update tiles ────────────────────────────────
   12 + 15*16: "dirt.png",   # info_updateGame1 placeholder (tex=252)
   13 + 15*16: "dirt.png",   # info_updateGame2 placeholder (tex=253)
}

# Alternate filenames to try if the primary name isn't found
# (handles slight naming variations between packs)
FALLBACKS = {
    "crafting_table_front.png": ["crafting_table_front.png"],
    "crafting_table_side.png":  ["crafting_table_side.png"],
    "crafting_table_top.png":   ["crafting_table_top.png"],
    "furnace_front_off.png":    ["furnace_front_off.png"],
    "furnace_front_on.png":     ["furnace_front_on.png"],
    "furnace_side.png":         ["furnace_side.png"],
    "furnace_top.png":          ["furnace_top.png"],
    "grass_side.tga":           ["grass_side.tga", "grass_side_snowed.png"],
    "fern.tga":                 ["fern.tga", "tallgrass.png"],
    "reeds.tga":                ["reeds.tga", "tallgrass.png"],
    "cactus_top.tga":           ["cactus_top.tga", "cactus_side.tga"],
    "cactus_side.tga":          ["cactus_side.tga"],
    "cactus_bottom.tga":        ["cactus_bottom.tga", "cactus_side.tga"],
    "mushroom_red.png":         ["mushroom_red.png", "flower_rose.png"],
    "mushroom_brown.png":       ["mushroom_brown.png", "flower_dandelion.png"],
    "iron_bars.png":            ["iron_bars.png", "glass.png"],
    "trapdoor.png":             ["trapdoor.png"],
    "leaves_oak_opaque.png":    ["leaves_oak_opaque.png", "leaves_oak.tga"],
    "quartz_block_top.png":     ["quartz_block_top.png", "stone.png"],
    "quartz_block_bottom.png":  ["quartz_block_bottom.png", "quartz_block_top.png"],
    "quartz_block_side.png":    ["quartz_block_side.png", "stone.png"],
    "quartz_block_lines.png":   ["quartz_block_lines.png", "quartz_block_side.png"],
    "quartz_block_lines_top.png": ["quartz_block_lines_top.png", "quartz_block_top.png"],
    "quartz_block_chiseled.png": ["quartz_block_chiseled.png", "stonebrick.png"],
    "quartz_block_chiseled_top.png": ["quartz_block_chiseled_top.png", "quartz_block_top.png"],
    "stonebrick_carved.png":    ["stonebrick_carved.png", "stonebrick.png"],
}

ATLAS_COLS = 16
ATLAS_ROWS = 16
TILE_PX    = 16   # each tile is 16×16 px in the atlas


def load_tile(src_dir: str, filename: str) -> Image.Image | None:
    """Load a texture file, trying fallback names if the primary isn't found."""
    candidates = FALLBACKS.get(filename, [filename])
    for name in candidates:
        path = os.path.join(src_dir, name)
        if os.path.isfile(path):
            try:
                img = Image.open(path).convert("RGBA")
                return img
            except Exception as e:
                print(f"  Warning: could not open {path}: {e}")
    return None


def build_atlas(src_dir: str, out_path: str) -> None:
    atlas = Image.new("RGBA", (ATLAS_COLS * TILE_PX, ATLAS_ROWS * TILE_PX), (0, 0, 0, 0))

    placed = 0
    missing = []

    for slot, filename in sorted(ATLAS_MAP.items()):
        col = slot % ATLAS_COLS
        row = slot // ATLAS_COLS

        tile_img = load_tile(src_dir, filename)
        if tile_img is None:
            missing.append((slot, filename))
            continue

        # Resize to 16×16 if necessary (modern textures may be 16×16 already)
        if tile_img.size != (TILE_PX, TILE_PX):
            tile_img = tile_img.resize((TILE_PX, TILE_PX), Image.NEAREST)

        x = col * TILE_PX
        y = row * TILE_PX
        atlas.paste(tile_img, (x, y))
        placed += 1

    print(f"Placed {placed} tiles; {len(missing)} slots left blank:")
    for slot, fname in missing:
        col = slot % ATLAS_COLS
        row = slot // ATLAS_COLS
        print(f"  slot {slot:3d} (col={col}, row={row}): {fname}")

    atlas.save(out_path)
    print(f"\nAtlas saved to: {out_path}")


def main():
    script_dir  = os.path.dirname(os.path.abspath(__file__))
    default_src = os.path.normpath(os.path.join(script_dir, "..", "data", "images", "textures", "blocks"))
    default_out = os.path.normpath(os.path.join(script_dir, "..", "data", "images", "terrain.png"))

    parser = argparse.ArgumentParser(description="Build MCPE 0.6.2 terrain.png atlas from individual block textures.")
    parser.add_argument("--src", default=default_src, help="Directory containing individual block PNG/TGA files")
    parser.add_argument("--out", default=default_out, help="Output terrain.png path")
    args = parser.parse_args()

    if not os.path.isdir(args.src):
        print(f"Error: source directory not found: {args.src}", file=sys.stderr)
        sys.exit(1)

    os.makedirs(os.path.dirname(args.out), exist_ok=True)
    print(f"Building terrain atlas…")
    print(f"  Source : {args.src}")
    print(f"  Output : {args.out}")
    build_atlas(args.src, args.out)


if __name__ == "__main__":
    main()
