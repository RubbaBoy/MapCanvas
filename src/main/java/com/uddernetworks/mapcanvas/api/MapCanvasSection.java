package com.uddernetworks.mapcanvas.api;

import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;

/**
 * A container for each map's ItemFrame entity, containing location, MapCanvas,
 * and other useful information for referring to it.
 */
public class MapCanvasSection {

    private MapCanvas mapCanvas;
    private Location location;
    private ItemFrame itemFrame;
    private short mapID;

    private int relativeX;
    private int relativeY;

    /**
     * Created a MapCanvasSection with the given parameters.
     * @param mapCanvas The parent MapCanvas
     * @param location The location of the block holding the ItemFrame
     * @param itemFrame The ItemFrame entity holding the map
     * @param mapID The current map's ID
     * @param relativeX The relative block X position in the map wall
     * @param relativeY The relative block Y position in the map wall
     */
    public MapCanvasSection(MapCanvas mapCanvas, Location location, ItemFrame itemFrame, short mapID, int relativeX, int relativeY) {
        this.mapCanvas = mapCanvas;
        this.location = location;
        this.itemFrame = itemFrame;
        this.mapID = mapID;
        this.relativeX = relativeX;
        this.relativeY = relativeY;
    }

    /**
     * Gets the parent MapCanvas.
     * @return The parent MapCanvas
     */
    public MapCanvas getMapCanvas() {
        return mapCanvas;
    }

    /**
     * Gets the current block the ItemFrame is on.
     * @return The current block the ItemFrame is on
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the current ItemFrame holding the map.
     * @return The current ItemFrame holding the map
     */
    public ItemFrame getItemFrame() {
        return itemFrame;
    }

    /**
     * Gets the current map's ID.
     * @return The current map's ID
     */
    public short getMapID() {
        return mapID;
    }

    /**
     * Gets the relative block X position in the map wall.
     * @return The relative block X position in the map wall
     */
    public int getRelativeX() {
        return relativeX;
    }

    /**
     * Gets the relative block Y position in the map wall.
     * @return The relative block Y position in the map wall
     */
    public int getRelativeY() {
        return relativeY;
    }

    @Override
    public String toString() {
        return "[MapID = " + mapID + ", x = " + location.getX() + ", y = " + location.getY() + ", z = " + location.getZ() + "]";
    }
}
