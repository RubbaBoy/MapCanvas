package com.uddernetworks.videoplayer.api;

public class PaletteColor {
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