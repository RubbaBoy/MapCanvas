package com.uddernetworks.videoplayer.main;

import org.bukkit.map.MapPalette;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public final class Palette {

    public final List<PaletteColor> colors = Arrays.asList(
            new PaletteColor(1, 127, 178, 56),
            new PaletteColor(2, 247, 233, 163),
            new PaletteColor(3, 199, 199, 199),
            new PaletteColor(4, 255, 0, 0),
            new PaletteColor(5, 160, 160, 255),
            new PaletteColor(6, 167, 167, 167),
            new PaletteColor(7, 0, 124, 0),
            new PaletteColor(8, 255, 255, 255),
            new PaletteColor(9, 164, 168, 184),
            new PaletteColor(10, 151, 109, 77),
            new PaletteColor(11, 112, 112, 112),
            new PaletteColor(12, 64, 64, 255),
            new PaletteColor(13, 143, 119, 72),
            new PaletteColor(14, 255, 252, 245),
            new PaletteColor(15, 216, 127, 51),
            new PaletteColor(16, 178, 76, 216),
            new PaletteColor(17, 102, 153, 216),
            new PaletteColor(18, 229, 229, 51),
            new PaletteColor(19, 127, 204, 25),
            new PaletteColor(20, 242, 127, 165),
            new PaletteColor(21, 76, 76, 76),
            new PaletteColor(22, 153, 153, 153),
            new PaletteColor(23, 76, 127, 153),
            new PaletteColor(24, 127, 63, 178),
            new PaletteColor(25, 51, 76, 178),
            new PaletteColor(26, 102, 76, 51),
            new PaletteColor(27, 102, 127, 51),
            new PaletteColor(28, 153, 51, 51),
            new PaletteColor(29, 25, 25, 25),
            new PaletteColor(30, 250, 238, 77),
            new PaletteColor(31, 92, 219, 213),
            new PaletteColor(32, 74, 128, 255),
            new PaletteColor(33, 0, 217, 58),
            new PaletteColor(34, 129, 86, 49),
            new PaletteColor(35, 112, 2, 0),
            new PaletteColor(36, 209, 177, 161),
            new PaletteColor(37, 159, 82, 36),
            new PaletteColor(38, 149, 87, 108),
            new PaletteColor(39, 112, 108, 138),
            new PaletteColor(40, 186, 133, 36),
            new PaletteColor(41, 103, 117, 53),
            new PaletteColor(42, 160, 77, 78),
            new PaletteColor(43, 57, 41, 35),
            new PaletteColor(44, 135, 107, 98),
            new PaletteColor(45, 87, 92, 92),
            new PaletteColor(46, 122, 73, 88),
            new PaletteColor(47, 76, 62, 92),
            new PaletteColor(48, 76, 50, 35),
            new PaletteColor(49, 76, 82, 42),
            new PaletteColor(50, 142, 60, 46),
            new PaletteColor(51, 37, 22, 16)
    );

    public List<PaletteColor> getColors() {
        return this.colors;
    }

    public int getColorById(int id) {
        return this.colors.stream().filter(color -> color.getId() == id).findFirst().orElse(this.colors.get(7)).getId();
    }

    public byte findClosestPaletteColorTo(int color) {
//        int closestColor = Color.WHITE.getRGB();
//        int closestDistance = Integer.MAX_VALUE;
//        for (PaletteColor paletteColor : this.colors) {
//            int distance = paletteColor.distanceTo(color);
//            if (distance < closestDistance) {
//                closestDistance = distance;
//                closestColor = paletteColor.asInt();
//            }
//        }

        return MapPalette.matchColor(new Color(color));

//        return (byte) closestColor;
    }

    public static final class PaletteColor {
        private final byte id;
        private final int r;
        private final int g;
        private final int b;
        private final int color;

        public PaletteColor(int id, int r, int g, int b) {
            this.id = (byte) id;
            this.r = r;
            this.g = g;
            this.b = b;

            this.color = ((0xFF) << 24) |
                    ((r & 0xFF) << 16) |
                    ((g & 0xFF) << 8) |
                    ((b & 0xFF) << 0);
        }

        public int distanceTo(final int color) {
            final int deltaR = this.r - ((color & 0xff000000) >>> 24);
            final int deltaG = this.g - ((color & 0x00ff0000) >>> 16);
            final int deltaB = this.b - ((color & 0x0000ff00) >>> 8);
            return (deltaR * deltaR) + (deltaG * deltaG) + (deltaB * deltaB);
        }

        public int asInt() {
            return this.color;
        }

        public byte getId() {
            return this.id;
        }
    }
}