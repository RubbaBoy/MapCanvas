package com.uddernetworks.mapcanvas.api.objects;

/**
 * Bounds for an element, containing top left and top right position coordinates.
 */
public class ObjectBounds {

    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;

    /**
     * Creates a new ObjectBounds object.
     * @param x1 The bottom left X bound
     * @param y1 The bottom left Y bound
     * @param x2 The top right X bound
     * @param y2 The top right Y bound
     */
    public ObjectBounds(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Checks if a coordinate is within the current bounds.
     * @param x The X position to be checked
     * @param y The Y position to be checked
     * @return If the given position is in the current bounds
     */
    public boolean positionIsIn(int x, int y) {
        return x > x1 && y > y1 && x < x2 && y < y2;
    }

    /**
     * Gets the bottom left X bound.
     * @return The bottom left X bound
     */
    public int getX1() {
        return x1;
    }

    /**
     * Gets the bottom left Y bound.
     * @return The bottom left Y bound
     */
    public int getY1() {
        return y1;
    }

    /**
     * Gets the top right X bound.
     * @return The top right X bound
     */
    public int getX2() {
        return x2;
    }

    /**
     * Gets the top right Y bound.
     * @return The top right Y bound
     */
    public int getY2() {
        return y2;
    }
}
