#!/usr/bin/env python3
"""Generate Minecraft-style pixel art textures for the MCPE-WASM terrain atlas."""

import math
import random
from PIL import Image

random.seed(42)

TERRAIN_PATH = "web-build/data/images/terrain.png"
TILE = 16


def noise_color(base, variation=15):
    """Return a color with random noise applied to each channel."""
    return tuple(max(0, min(255, c + random.randint(-variation, variation))) for c in base)


def blend(c1, c2, t):
    """Blend two RGB tuples by factor t (0=c1, 1=c2)."""
    return tuple(int(a + (b - a) * t) for a, b in zip(c1, c2))


def make_stone_texture(colors, variation=12):
    """Create a speckled stone-like texture from a palette of colors."""
    img = Image.new("RGBA", (TILE, TILE))
    for y in range(TILE):
        for x in range(TILE):
            base = random.choice(colors)
            c = noise_color(base, variation)
            img.putpixel((x, y), c + (255,))
    return img


def make_polished_texture(colors, variation=6):
    """Create a smoother polished stone texture with larger patches."""
    img = Image.new("RGBA", (TILE, TILE))
    # Create 4x4 patches for smoother look
    for py in range(0, TILE, 4):
        for px in range(0, TILE, 4):
            base = random.choice(colors)
            for dy in range(4):
                for dx in range(4):
                    x, y = px + dx, py + dy
                    if x < TILE and y < TILE:
                        c = noise_color(base, variation)
                        img.putpixel((x, y), c + (255,))
    # Add subtle grid lines
    line_color = blend(colors[0], (80, 80, 80), 0.3)
    for i in range(0, TILE, 4):
        for j in range(TILE):
            if random.random() < 0.4:
                c = noise_color(line_color, 5)
                img.putpixel((i, j), c + (255,))
                img.putpixel((j, i), c + (255,))
    return img


def make_planks(base_color, dark_color, light_color):
    """Create wooden planks texture with horizontal grain lines."""
    img = Image.new("RGBA", (TILE, TILE))
    # Fill base
    for y in range(TILE):
        for x in range(TILE):
            img.putpixel((x, y), noise_color(base_color, 8) + (255,))

    # Plank divisions (horizontal lines at y=0,4,8,12)
    for plank_y in [0, 4, 8, 12]:
        for x in range(TILE):
            img.putpixel((x, plank_y), noise_color(dark_color, 5) + (255,))

    # Wood grain - subtle horizontal streaks
    for y in range(TILE):
        for x in range(TILE):
            if random.random() < 0.15:
                c = noise_color(dark_color, 6)
                img.putpixel((x, y), c + (255,))
            elif random.random() < 0.1:
                c = noise_color(light_color, 6)
                img.putpixel((x, y), c + (255,))

    # Vertical joint offsets per plank row
    joints = [8, 5, 11, 3]
    for i, plank_y in enumerate([0, 4, 8, 12]):
        jx = joints[i]
        for dy in range(4):
            yy = plank_y + dy
            if yy < TILE:
                img.putpixel((jx, yy), noise_color(dark_color, 5) + (255,))
    return img


def make_hay_side():
    """Create hay bale side texture - golden with horizontal bands."""
    img = Image.new("RGBA", (TILE, TILE))
    hay_colors = [(218, 176, 56), (196, 158, 44), (230, 190, 70)]
    band_color = (170, 130, 30)
    rope_color = (140, 100, 20)

    for y in range(TILE):
        for x in range(TILE):
            base = random.choice(hay_colors)
            img.putpixel((x, y), noise_color(base, 10) + (255,))

    # Horizontal rope bands at y=3,4 and y=11,12
    for band_y in [3, 4, 11, 12]:
        for x in range(TILE):
            c = noise_color(rope_color, 5)
            img.putpixel((x, band_y), c + (255,))

    # Vertical hay streaks
    for x in range(TILE):
        for y in range(TILE):
            if random.random() < 0.12:
                c = noise_color(band_color, 8)
                img.putpixel((x, y), c + (255,))
    return img


def make_hay_top():
    """Create hay bale top texture - circular hay pattern."""
    img = Image.new("RGBA", (TILE, TILE))
    hay_light = (230, 190, 70)
    hay_dark = (180, 140, 40)
    hay_mid = (210, 170, 55)
    center = 7.5

    for y in range(TILE):
        for x in range(TILE):
            dx = x - center
            dy = y - center
            dist = (dx * dx + dy * dy) ** 0.5
            if dist < 3:
                base = hay_dark
            elif dist < 6:
                base = hay_mid
            else:
                base = hay_light
            img.putpixel((x, y), noise_color(base, 12) + (255,))

    # Radial streaks
    for y in range(TILE):
        for x in range(TILE):
            angle = math.atan2(y - center, x - center)
            if abs(math.sin(angle * 8)) < 0.2:
                px = img.getpixel((x, y))
                darker = tuple(max(0, c - 15) for c in px[:3]) + (255,)
                img.putpixel((x, y), darker)
    return img


def make_redstone_block():
    """Create redstone block - bright red metallic with cross pattern."""
    img = Image.new("RGBA", (TILE, TILE))
    base = (170, 25, 25)
    bright = (210, 45, 35)
    dark = (130, 15, 15)

    for y in range(TILE):
        for x in range(TILE):
            img.putpixel((x, y), noise_color(base, 10) + (255,))

    # Border
    for i in range(TILE):
        for pos in [0, 15]:
            img.putpixel((i, pos), noise_color(dark, 5) + (255,))
            img.putpixel((pos, i), noise_color(dark, 5) + (255,))

    # Cross/grid pattern
    for i in range(2, 14):
        for offset in [7, 8]:
            img.putpixel((i, offset), noise_color(bright, 5) + (255,))
            img.putpixel((offset, i), noise_color(bright, 5) + (255,))

    # Corner highlights
    for dx in range(2, 5):
        for dy in range(2, 5):
            img.putpixel((dx, dy), noise_color(bright, 8) + (255,))
            img.putpixel((TILE - 1 - dx, dy), noise_color(bright, 8) + (255,))
            img.putpixel((dx, TILE - 1 - dy), noise_color(bright, 8) + (255,))
            img.putpixel((TILE - 1 - dx, TILE - 1 - dy), noise_color(bright, 8) + (255,))
    return img


def make_lever_base():
    """Create lever base texture - small cobblestone base."""
    img = Image.new("RGBA", (TILE, TILE), (0, 0, 0, 0))
    stone_colors = [(120, 120, 120), (100, 100, 100), (140, 140, 140)]

    # Small base in center bottom
    for y in range(10, 16):
        for x in range(4, 12):
            base = random.choice(stone_colors)
            img.putpixel((x, y), noise_color(base, 8) + (255,))

    # Lever stick
    for y in range(2, 11):
        for x in [7, 8]:
            img.putpixel((x, y), noise_color((90, 70, 50), 8) + (255,))

    # Border on base
    for x in range(4, 12):
        img.putpixel((x, 10), noise_color((80, 80, 80), 5) + (255,))
    return img


def make_redstone_torch(on=True):
    """Create redstone torch texture."""
    img = Image.new("RGBA", (TILE, TILE), (0, 0, 0, 0))

    if on:
        tip_color = (255, 50, 30)
        body_color = (180, 40, 30)
    else:
        tip_color = (100, 30, 25)
        body_color = (80, 25, 20)

    stick_color = (90, 70, 50)

    # Stick
    for y in range(6, 15):
        for x in [7, 8]:
            img.putpixel((x, y), noise_color(stick_color, 6) + (255,))

    # Torch head
    for y in range(3, 7):
        for x in range(6, 10):
            img.putpixel((x, y), noise_color(body_color, 6) + (255,))

    # Flame tip
    for x in [7, 8]:
        img.putpixel((x, 2), noise_color(tip_color, 8) + (255,))
        img.putpixel((x, 3), noise_color(tip_color, 8) + (255,))

    if on:
        # Glow
        for x in [6, 9]:
            img.putpixel((x, 3), noise_color((200, 60, 30), 10) + (200,))
    return img


def make_repeater_top():
    """Create repeater top texture - stone base with redstone line."""
    img = Image.new("RGBA", (TILE, TILE))
    stone = (150, 150, 150)
    stone_dark = (120, 120, 120)

    # Stone base
    for y in range(TILE):
        for x in range(TILE):
            img.putpixel((x, y), noise_color(stone, 8) + (255,))

    # Border
    for i in range(TILE):
        for pos in [0, 15]:
            img.putpixel((i, pos), noise_color(stone_dark, 5) + (255,))
            img.putpixel((pos, i), noise_color(stone_dark, 5) + (255,))

    # Redstone line through middle
    red = (180, 30, 20)
    for x in range(2, 14):
        for y in [7, 8]:
            img.putpixel((x, y), noise_color(red, 8) + (255,))

    # Two torch positions
    for tx, ty in [(5, 5), (10, 5)]:
        for dx in range(-1, 2):
            for dy in range(-1, 2):
                img.putpixel((tx + dx, ty + dy), noise_color((200, 40, 30), 8) + (255,))
    return img


def make_note_block():
    """Create note block - wooden block with dark center note imprint."""
    img = make_planks((100, 75, 50), (70, 50, 30), (120, 90, 60))

    # Dark center area with note symbol impression
    for y in range(5, 12):
        for x in range(5, 12):
            px = img.getpixel((x, y))
            darker = tuple(max(0, c - 40) for c in px[:3]) + (255,)
            img.putpixel((x, y), darker)

    # Simple note symbol (a circle with a stem)
    note_color = (50, 35, 20, 255)
    # Note head
    for dx, dy in [(7, 9), (8, 9), (7, 10), (8, 10), (9, 9)]:
        img.putpixel((dx, dy), note_color)
    # Note stem
    for y in range(6, 10):
        img.putpixel((9, y), note_color)
    # Flag
    img.putpixel((10, 6), note_color)
    img.putpixel((10, 7), note_color)
    return img


def make_dispenser_front(terrain):
    """Create dispenser front - cobblestone with dark mouth."""
    # Get cobblestone from index 16 (col=0, row=1)
    cobble = terrain.crop((0, 16, 16, 32)).copy()

    # Dark mouth in center
    mouth_color = (40, 40, 40, 255)
    for y in range(5, 12):
        for x in range(5, 12):
            cobble.putpixel((x, y), mouth_color)

    # Slightly lighter inner edge
    edge = (60, 60, 60, 255)
    for i in range(5, 12):
        cobble.putpixel((i, 5), edge)
        cobble.putpixel((i, 11), edge)
        cobble.putpixel((5, i), edge)
        cobble.putpixel((11, i), edge)

    # Darker bottom to give depth
    for x in range(6, 11):
        cobble.putpixel((x, 10), (30, 30, 30, 255))
    return cobble


def make_emerald_ore(terrain):
    """Create emerald ore - stone base with green spots."""
    # Get stone from index 1 (col=1, row=0)
    stone = terrain.crop((16, 0, 32, 16)).copy()

    # Add emerald green spots
    green_colors = [(55, 175, 75), (40, 150, 60), (70, 200, 90)]
    spots = [
        (3, 3), (4, 3), (3, 4),
        (10, 2), (11, 2), (11, 3),
        (6, 7), (7, 7), (7, 8),
        (2, 11), (3, 11), (3, 12),
        (12, 10), (13, 10), (12, 11),
        (8, 13), (9, 13), (9, 14),
    ]
    for x, y in spots:
        c = noise_color(random.choice(green_colors), 10)
        stone.putpixel((x, y), c + (255,))

    # Brighter highlights on some spots
    highlights = [(4, 3), (11, 2), (7, 7), (3, 11), (13, 10), (9, 13)]
    for x, y in highlights:
        c = noise_color((100, 220, 120), 10)
        stone.putpixel((x, y), c + (255,))
    return stone


def make_stone_button():
    """Create stone button texture - small raised stone piece."""
    img = Image.new("RGBA", (TILE, TILE), (0, 0, 0, 0))
    stone_top = (160, 160, 160)
    stone_side = (120, 120, 120)
    stone_dark = (90, 90, 90)

    # Button face (small rectangle in center)
    for y in range(6, 11):
        for x in range(5, 11):
            img.putpixel((x, y), noise_color(stone_top, 8) + (255,))

    # Bottom/right shadow
    for x in range(5, 12):
        img.putpixel((x, 11), noise_color(stone_dark, 5) + (255,))
    for y in range(6, 12):
        img.putpixel((11, y), noise_color(stone_dark, 5) + (255,))

    # Top/left highlight
    for x in range(5, 11):
        img.putpixel((x, 5), noise_color((180, 180, 180), 5) + (255,))
    for y in range(5, 11):
        img.putpixel((4, y), noise_color((180, 180, 180), 5) + (255,))
    return img


def make_pressure_plate(is_wood=False):
    """Create pressure plate texture - flat plate."""
    img = Image.new("RGBA", (TILE, TILE), (0, 0, 0, 0))

    if is_wood:
        base = (160, 120, 75)
        dark = (120, 85, 50)
        light = (185, 145, 95)
    else:
        base = (140, 140, 140)
        dark = (100, 100, 100)
        light = (170, 170, 170)

    # Plate surface
    for y in range(6, 10):
        for x in range(2, 14):
            img.putpixel((x, y), noise_color(base, 8) + (255,))

    # Top edge highlight
    for x in range(2, 14):
        img.putpixel((x, 6), noise_color(light, 5) + (255,))

    # Bottom edge shadow
    for x in range(2, 14):
        img.putpixel((x, 9), noise_color(dark, 5) + (255,))

    # Side edges
    for y in range(6, 10):
        img.putpixel((2, y), noise_color(light, 5) + (255,))
        img.putpixel((13, y), noise_color(dark, 5) + (255,))

    if is_wood:
        # Wood grain
        for x in range(3, 13):
            if random.random() < 0.2:
                img.putpixel((x, 7), noise_color(dark, 5) + (255,))
                img.putpixel((x, 8), noise_color(dark, 5) + (255,))
    return img


def make_log_side(bark_base, bark_dark, bark_light, has_spots=False):
    """Create log side texture with bark pattern."""
    img = Image.new("RGBA", (TILE, TILE))

    # Fill with bark base
    for y in range(TILE):
        for x in range(TILE):
            img.putpixel((x, y), noise_color(bark_base, 8) + (255,))

    # Vertical bark lines
    for x in range(TILE):
        if random.random() < 0.3:
            for y in range(TILE):
                if random.random() < 0.7:
                    img.putpixel((x, y), noise_color(bark_dark, 6) + (255,))

    # Horizontal segments for bark texture
    y = 0
    while y < TILE:
        for x in range(TILE):
            if random.random() < 0.25:
                img.putpixel((x, y), noise_color(bark_dark, 5) + (255,))
        y += random.randint(3, 5)

    # Light highlights
    for y in range(TILE):
        for x in range(TILE):
            if random.random() < 0.08:
                img.putpixel((x, y), noise_color(bark_light, 8) + (255,))

    if has_spots:
        # Birch-style dark spots/dashes
        spot_color = (80, 80, 70)
        for _ in range(12):
            sx = random.randint(0, 14)
            sy = random.randint(0, 15)
            length = random.randint(1, 3)
            for dx in range(length):
                if sx + dx < TILE:
                    img.putpixel((sx + dx, sy), noise_color(spot_color, 8) + (255,))
    return img


def main():
    terrain = Image.open(TERRAIN_PATH)

    # --- Row 8 textures ---

    # Index 131: Spruce planks
    spruce = make_planks((75, 55, 37), (50, 35, 20), (95, 72, 50))
    terrain.paste(spruce, (3 * TILE, 8 * TILE))

    # Index 132: Birch planks
    birch = make_planks((199, 181, 128), (170, 150, 100), (220, 205, 155))
    terrain.paste(birch, (4 * TILE, 8 * TILE))

    # Index 133: Granite
    granite_colors = [(158, 107, 90), (123, 79, 66), (196, 140, 123), (140, 95, 78)]
    granite = make_stone_texture(granite_colors, 12)
    terrain.paste(granite, (5 * TILE, 8 * TILE))

    # Index 134: Polished granite
    polished_granite = make_polished_texture(granite_colors, 6)
    terrain.paste(polished_granite, (6 * TILE, 8 * TILE))

    # Index 135: Diorite
    diorite_colors = [(200, 200, 200), (139, 139, 139), (230, 230, 230), (170, 170, 170)]
    diorite = make_stone_texture(diorite_colors, 10)
    terrain.paste(diorite, (7 * TILE, 8 * TILE))

    # Index 139: Polished diorite
    polished_diorite = make_polished_texture(diorite_colors, 5)
    terrain.paste(polished_diorite, (11 * TILE, 8 * TILE))

    # Index 140: Andesite
    andesite_colors = [(123, 123, 123), (142, 142, 142), (105, 105, 105), (130, 135, 128)]
    andesite = make_stone_texture(andesite_colors, 10)
    terrain.paste(andesite, (12 * TILE, 8 * TILE))

    # Index 141: Polished andesite
    polished_andesite = make_polished_texture(andesite_colors, 5)
    terrain.paste(polished_andesite, (13 * TILE, 8 * TILE))

    # Index 142: Hay bale side
    hay_side = make_hay_side()
    terrain.paste(hay_side, (14 * TILE, 8 * TILE))

    # Index 143: Hay bale top
    hay_top = make_hay_top()
    terrain.paste(hay_top, (15 * TILE, 8 * TILE))

    # --- Row 10 textures ---

    # Index 166: Redstone block
    redstone_block = make_redstone_block()
    terrain.paste(redstone_block, (6 * TILE, 10 * TILE))

    # Index 167: Lever base (clear first for transparency)
    lever = make_lever_base()
    clear = Image.new("RGBA", (TILE, TILE), (0, 0, 0, 0))
    terrain.paste(clear, (7 * TILE, 10 * TILE))
    terrain.paste(lever, (7 * TILE, 10 * TILE), lever)

    # Index 168: Redstone torch on (clear first)
    rs_torch_on = make_redstone_torch(on=True)
    terrain.paste(clear, (8 * TILE, 10 * TILE))
    terrain.paste(rs_torch_on, (8 * TILE, 10 * TILE), rs_torch_on)

    # Index 169: Redstone torch off (clear first)
    rs_torch_off = make_redstone_torch(on=False)
    terrain.paste(clear, (9 * TILE, 10 * TILE))
    terrain.paste(rs_torch_off, (9 * TILE, 10 * TILE), rs_torch_off)

    # Index 170: Repeater top
    repeater = make_repeater_top()
    terrain.paste(repeater, (10 * TILE, 10 * TILE))

    # Index 171: Note block
    note = make_note_block()
    terrain.paste(note, (11 * TILE, 10 * TILE))

    # Index 172: Dispenser front
    dispenser = make_dispenser_front(terrain)
    terrain.paste(dispenser, (12 * TILE, 10 * TILE))

    # Index 173: Emerald ore
    emerald = make_emerald_ore(terrain)
    terrain.paste(emerald, (13 * TILE, 10 * TILE))

    # --- Row 11 textures ---

    # Index 176: Stone button (clear first for transparency)
    stone_btn = make_stone_button()
    clear = Image.new("RGBA", (TILE, TILE), (0, 0, 0, 0))
    terrain.paste(clear, (0 * TILE, 11 * TILE))
    terrain.paste(stone_btn, (0 * TILE, 11 * TILE), stone_btn)

    # Index 177: Stone pressure plate (clear first)
    stone_plate = make_pressure_plate(is_wood=False)
    terrain.paste(clear, (1 * TILE, 11 * TILE))
    terrain.paste(stone_plate, (1 * TILE, 11 * TILE), stone_plate)

    # Index 178: Wood pressure plate (clear first)
    wood_plate = make_pressure_plate(is_wood=True)
    terrain.paste(clear, (2 * TILE, 11 * TILE))
    terrain.paste(wood_plate, (2 * TILE, 11 * TILE), wood_plate)

    # Index 179: Spruce log side
    spruce_log = make_log_side(
        bark_base=(60, 45, 30),
        bark_dark=(40, 28, 15),
        bark_light=(80, 60, 42)
    )
    terrain.paste(spruce_log, (3 * TILE, 11 * TILE))

    # Index 180: Birch log side
    birch_log = make_log_side(
        bark_base=(210, 205, 195),
        bark_dark=(180, 175, 165),
        bark_light=(235, 230, 220),
        has_spots=True
    )
    terrain.paste(birch_log, (4 * TILE, 11 * TILE))

    terrain.save(TERRAIN_PATH)
    print(f"Saved updated terrain atlas to {TERRAIN_PATH}")
    print(f"Generated 23 new textures across rows 8, 10, and 11")


if __name__ == "__main__":
    main()
