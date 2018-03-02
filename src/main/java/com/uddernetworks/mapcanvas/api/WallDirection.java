package com.uddernetworks.mapcanvas.api;

/**
 * The directions the wall should be created and recognised in.
 */
public enum WallDirection {
    X_AXIS(1, 0),
    Z_AXIS(0, 1);

    private final int wallModX;
    private final int wallModZ;

    WallDirection(int wallModX, int wallModZ) {
        this.wallModX = wallModX;
        this.wallModZ = wallModZ;
    }

    public int getWallModX() {
        return wallModX;
    }

    public int getWallModZ() {
        return wallModZ;
    }
}
