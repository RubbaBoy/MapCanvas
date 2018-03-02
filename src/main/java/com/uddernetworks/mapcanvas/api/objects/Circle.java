package com.uddernetworks.mapcanvas.api.objects;

import com.uddernetworks.mapcanvas.api.MapCanvas;

/**
 * Makes the shape of a circle.
 */
public class Circle extends Clickable implements MapObject {
    private int x;
    private int y;
    private int innerRadius;
    private int outerRadius;
    private byte fillColor;
    private ObjectBounds objectBounds;

    /**
     * Creates a circle from a single radius, with an outer width of 1.
     * @param x The center X position on the map canvas where the circle should be drawn
     * @param y The center Y position on the map canvas where the circle should be drawn
     * @param radius The radius the drawn circle should be
     * @param fillColor The byte color the circle should be
     */
    public Circle(int x, int y, int radius, byte fillColor) {
        this(x, y, radius, radius, fillColor);
    }

    /**
     * Creates a circle from two radii. The buggest the circle will be is outerRadius, the
     * smallest inner part of the circle will be is the innerRadius. Pixels will be drawn
     * in the difference between the two radii, so the difference between the arguments will
     * be the circle's line thickness.
     * @param x The center X position on the map canvas where the circle should be drawn
     * @param y The center Y position on the map canvas where the circle should be drawn
     * @param innerRadius The inner radius the drawn circle should be
     * @param outerRadius The outer radius the drawn circle should be
     * @param fillColor The byte color the circle should be
     */
    public Circle(int x, int y, int innerRadius, int outerRadius, byte fillColor) {
        this.x = x;
        this.y = y;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.fillColor = fillColor;
        this.objectBounds = new ObjectBounds(x - outerRadius, y - outerRadius, x + outerRadius, y + outerRadius);
    }

    @Override
    public void initialize(MapCanvas mapCanvas) {
        this.x = mapCanvas.migrateX(this.x);
        this.y = mapCanvas.migrateY(this.y);
    }

    @Override
    public void draw(MapCanvas mapCanvas) {
        int xo = outerRadius;
        int xi = innerRadius;
        int y = 0;
        int erro = 1 - xo;
        int erri = 1 - xi;

        while (xo >= y) {
            xLine(mapCanvas, this.x + xi, this.x + xo, this.y + y, fillColor);
            yLine(mapCanvas, this.x + y, this.y + xi, this.y + xo, fillColor);
            xLine(mapCanvas, this.x - xo, this.x - xi, this.y + y, fillColor);
            yLine(mapCanvas, this.x - y, this.y + xi, this.y + xo, fillColor);
            xLine(mapCanvas, this.x - xo, this.x - xi, this.y - y, fillColor);
            yLine(mapCanvas, this.x - y, this.y - xo, this.y - xi, fillColor);
            xLine(mapCanvas, this.x + xi, this.x + xo, this.y - y, fillColor);
            yLine(mapCanvas, this.x + y, this.y - xo, this.y - xi, fillColor);

            y++;

            if (erro < 0) {
                erro += 2 * y + 1;
            } else {
                xo--;
                erro += 2 * (y - xo + 1);
            }

            if (y > innerRadius) {
                xi = y;
            } else {
                if (erri < 0) {
                    erri += 2 * y + 1;
                } else {
                    xi--;
                    erri += 2 * (y - xi + 1);
                }
            }
        }
    }

    @Override
    public ObjectBounds getBounds() {
        return this.objectBounds;
    }

    private void xLine(MapCanvas mapCanvas, int x1, int x2, int y, byte fillColor) {
        while (x1 <= x2) mapCanvas.setPixel(x1++, y, fillColor);
    }

    private void yLine(MapCanvas mapCanvas, int x, int y1, int y2, byte fillColor) {
        while (y1 <= y2) mapCanvas.setPixel(x, y1++, fillColor);
    }
}
