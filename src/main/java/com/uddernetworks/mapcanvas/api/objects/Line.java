package com.uddernetworks.mapcanvas.api.objects;

import com.uddernetworks.mapcanvas.api.MapCanvas;

import java.util.ArrayList;
import java.util.List;

/**
 * Makes a line from two points with a specified color.
 */
public class Line implements MapObject {

    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private byte lineColor;

    /**
     * Creates a line from one set of coordinates to another set of coordinates.
     * @param x1 The X value for the first coordinate
     * @param y1 The Y value for the first coordinate
     * @param x2 The X value for the second coordinate
     * @param y2 The Y value for the second coordinate
     * @param lineColor The byte color the line should be
     */
    public Line(int x1, int y1, int x2, int y2, byte lineColor) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.lineColor = lineColor;
    }

    @Override
    public void initialize(MapCanvas mapCanvas) {
        this.x1 = mapCanvas.migrateX(this.x1);
        this.y1 = mapCanvas.migrateY(this.y1);

        this.x2 = mapCanvas.migrateX(this.x2);
        this.y2 = mapCanvas.migrateY(this.y2);
    }

    @Override
    public void draw(MapCanvas mapCanvas) {
        List<int[]> points =  drawLine(x1, y1, x2, y2);
        points.forEach(cords -> mapCanvas.setPixel(cords[0], cords[1], lineColor));
    }

    private List<int[]> drawLine(int x, int y, int x2, int y2) {
        List<int[]> points = new ArrayList<>();
        int w = x2 - x;
        int h = y2 - y;
        int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
        if (w < 0) dx1 = -1;
        else if (w > 0) dx1 = 1;
        if (h < 0) dy1 = -1;
        else if (h > 0) dy1 = 1;
        if (w < 0) dx2 = -1;
        else if (w > 0) dx2 = 1;
        int longest = Math.abs(w);
        int shortest = Math.abs(h);
        if (!(longest > shortest)) {
            longest = Math.abs(h);
            shortest = Math.abs(w);
            if (h < 0) dy2 = -1;
            else if (h > 0) dy2 = 1;
            dx2 = 0;
        }
        int numerator = longest >> 1;
        for (int i = 0; i <= longest; i++) {
            int[] cord = new int[] {x, y};
            points.add(cord);

            numerator += shortest;
            if (!(numerator < longest)) {
                numerator -= longest;
                x += dx1;
                y += dy1;
            } else {
                x += dx2;
                y += dy2;
            }
        }

        return points;
    }
}
