package com.uddernetworks.videoplayer.api.objects;

import com.uddernetworks.videoplayer.api.MapCanvas;
import com.uddernetworks.videoplayer.api.MapObject;

public class Rectangle implements MapObject {

    private int x;
    private int y;
    private int width;
    private int height;

    private byte lineColor;
    private byte fillColor;

    public Rectangle(int x, int y, int width, int height, byte fillColor) {
        this(x, y, width, height, (byte) -1, fillColor);
    }

    public Rectangle(int x, int y, int width, int height, byte lineColor, byte fillColor) {
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

                if (lineColor != -1 && (x == 0 || x == this.width || y == 0 || y == this.height)) {
                    mapCanvas.setPixel(imageX, imageY, this.lineColor);
                } else {
                    mapCanvas.setPixel(imageX, imageY, this.fillColor);
                }
            }
        }
    }
}