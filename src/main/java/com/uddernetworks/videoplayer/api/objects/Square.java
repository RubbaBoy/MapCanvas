package com.uddernetworks.videoplayer.api.objects;

import com.uddernetworks.videoplayer.api.MapCanvas;
import com.uddernetworks.videoplayer.api.MapObject;
import com.uddernetworks.videoplayer.api.Palette;
import com.uddernetworks.videoplayer.api.PaletteColor;
import com.uddernetworks.videoplayer.main.VideoPlayer;
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

//        byte col1 = MapPalette.matchColor(Color.BLACK);
//        byte col2 = MapPalette.matchColor(Color.WHITE);
//
//        for (int i = 0; i < VideoPlayer.REMOVE; i++) {
//            System.out.println("i = " + i);
//            mapCanvas.pixels[i] = i % 2 == 0 ? col1 : col2;
//        }
//
//        if (true) return;

        System.out.println("Started!");

        int index = 0;
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
//                    mapCanvas.setPixel(imageX, imageY, this.lineColor);
//                } else {

//                System.out.println("Set at (" + x + ", " + y + ")\treal [" + (y * 512 + x) + "]");

//                    mapCanvas.setPixel(imageX, imageY, this.fillColor);
                    mapCanvas.setPixel(imageX, imageY, this.fillColor);



//                mapCanvas.pixels[y * 512 + x] = this.fillColor;
//                mapCanvas.pixels[index + (1 * 512)] = this.fillColor;

//                    mapCanvas.visualizeBytes(mapCanvas.pixels, mapCanvas.getWidth() * 128, mapCanvas.getHeight() * 128, "draw_" + index + "__" + x + "_" + y);
//                    index++;

//                }
            }
        }

        System.out.println("Finished!");

//        int green = MapPalette.matchColor(Color.GREEN);
//
//        int greenPixels = 0;
//
//        for (byte pixel : mapCanvas.pixels) {
//            if (pixel == green) greenPixels++;
//        }

//        System.out.println("GREEEENNNNNN IN SQUAREEEEEE::: " + greenPixels);
    }

    private void setPixel(byte[] pixels, int x, int y, byte pixel) {
        pixels[y * (128 * mapCanvas.getHeight()) + x] = pixel;
    }
}
