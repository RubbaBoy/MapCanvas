package com.uddernetworks.videoplayer.api.objects;

import com.uddernetworks.videoplayer.api.MapCanvas;
import com.uddernetworks.videoplayer.api.MapObject;
import com.uddernetworks.videoplayer.api.Palette;
import com.uddernetworks.videoplayer.api.PaletteColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.map.MapPalette;
import org.bukkit.potion.PotionEffectType;

import java.awt.*;
import java.util.Arrays;

public class Square implements MapObject {

    private int x;
    private int y;
    private int width;
    private int height;
    private MapCanvas mapCanvas;

    private byte lineColor;
    private byte fillColor;

    public Square(int x, int y, int width, int height, byte fillColor) {
        this(x, y, width, height, (byte) -1, fillColor);
    }

    public Square(int x, int y, int width, int height, byte lineColor, byte fillColor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.lineColor = lineColor;
        this.fillColor = fillColor;
    }

    @Override
    public void draw(MapCanvas mapCanvas) {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                int imageX = x + this.x;
                int imageY = y + this.y;

//                if (x == 0 && y == 0) {
//                    System.out.println("Green at (" + imageX + ", " + imageY + ")");
//                    mapCanvas.setPixel(imageX, imageY, MapPalette.matchColor(Color.GREEN));
//                } else if (x == this.width - 1 && y == this.height - 1) {
//                    mapCanvas.setPixel(imageX, imageY, MapPalette.matchColor(Color.RED));
//                }

//                if (lineColor != -1 && x == 0 || x == this.width - 1 || y == 0 || y == this.height - 1) { // If it's on the edge
////                    mapCanvas.setPixel(imageX, imageY, this.lineColor);
//                } else {
                    mapCanvas.setPixel(imageX, imageY, this.fillColor);
//                }
            }
        }
    }

    private void setPixel(byte[] pixels, int x, int y, byte pixel) {
        pixels[y * (128 * mapCanvas.getHeight()) + x] = pixel;
    }
}
