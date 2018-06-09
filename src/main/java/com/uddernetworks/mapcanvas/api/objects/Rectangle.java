package com.uddernetworks.mapcanvas.api.objects;

import com.uddernetworks.mapcanvas.api.MapCanvas;

/**
 * Makes a rectangle.
 */
public class Rectangle extends Clickable implements MapObject {

    private int x;
    private int y;
    private int width;
    private int height;
    private ObjectBounds objectBounds;

    private byte lineColor;
    private byte fillColor;

    /**
     * Created a filled rectangle with a single color in all.
     * @param x The X position from the bottom left corner of the rectangle where it should be drawn
     * @param y The Y position from the bottom left corner of the rectangle where it should be drawn
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param lineColor The byte color that will outline the rectangle, -1 to use the fillColor
     * @param fillColor The byte color that will fill the rectangle, -1 to not fill with any color
     */
    public Rectangle(int x, int y, int width, int height, byte lineColor, byte fillColor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.lineColor = lineColor;
        this.fillColor = fillColor;
        this.objectBounds = new ObjectBounds(x, y, x + width, y + height);
    }

    @Override
    public void initialize(MapCanvas mapCanvas) {
        this.x = mapCanvas.migrateX(this.x);
        this.y = mapCanvas.migrateY(this.y + this.height);
    }

    @Override
    public void draw(MapCanvas mapCanvas) {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                int imageX = x + this.x;
                int imageY = y + this.y;

                if (x == 0 || x == this.width || y == 0 || y == this.height) {
                    if (this.lineColor == -1) {
                        if (this.fillColor != -1) {
                            mapCanvas.setPixel(imageX, imageY, this.fillColor);
                        }
                    } else {
                        mapCanvas.setPixel(imageX, imageY, this.lineColor);
                    }
                } else {
                    if (this.fillColor != -1) {
                        mapCanvas.setPixel(imageX, imageY, this.fillColor);
                    }
                }
            }
        }
    }

    @Override
    public ObjectBounds getBounds() {
        return this.objectBounds;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}