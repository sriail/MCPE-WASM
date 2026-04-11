#ifndef NET_MINECRAFT_WORLD_LEVEL__FoliageColor_H__
#define NET_MINECRAFT_WORLD_LEVEL__FoliageColor_H__

//package net.minecraft.world.level;

class FoliageColor
{
public:
//     static void init(int[] pixels) {
//         FoliageColor::pixels = pixels;
//     }
// 
//     static int get(float temp, float rain) {
//         rain *= temp;
//         int x = (int) ((1 - temp) * 255);
//         int y = (int) ((1 - rain) * 255);
//         return pixels[y << 8 | x];
//     }

    static int getEvergreenColor() {
        return 0x619961;
    }

    static int getBirchColor() {
        return 0x80a755;
    }

    static int getDefaultColor() {
        return 0x48b518;
    }

    // Temperature-based foliage color computation
    // Interpolates from warm green (tropical) to cold brown-green
    static int get(float temp, float rain) {
        // Clamp values
        if (temp < 0.0f) temp = 0.0f;
        if (temp > 1.0f) temp = 1.0f;
        if (rain < 0.0f) rain = 0.0f;
        if (rain > 1.0f) rain = 1.0f;
        rain *= temp;

        // Warm/wet = bright green, cold/dry = brown-green
        int r = (int)(30 + 60 * (1.0f - temp) + 30 * (1.0f - rain));
        int g = (int)(100 + 80 * temp + 40 * rain);
        int b = (int)(10 + 20 * rain);
        if (r > 255) r = 255;
        if (g > 255) g = 255;
        if (b > 255) b = 255;
        return (r << 16) | (g << 8) | b;
    }

private:
    //static int pixels[256*256];
};

#endif /*NET_MINECRAFT_WORLD_LEVEL__FoliageColor_H__*/
