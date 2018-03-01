package com.uddernetworks.videoplayer.api;

import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;

public class MapCanvasSection {

    private MapCanvas mapCanvas;
    private Location location;
    private ItemFrame itemFrame;
    private short mapID;

    private int relativeX;
    private int relativeY;

    public MapCanvasSection(MapCanvas mapCanvas, Location location, ItemFrame itemFrame, short mapID, int relativeX, int relativeY) {
        this.mapCanvas = mapCanvas;
        this.location = location;
        this.itemFrame = itemFrame;
        this.mapID = mapID;
        this.relativeX = relativeX;
        this.relativeY = relativeY;
    }

    public MapCanvas getMapCanvas() {
        return mapCanvas;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ItemFrame getItemFrame() {
        return itemFrame;
    }

    public void setItemFrame(ItemFrame itemFrame) {
        this.itemFrame = itemFrame;
    }

    public short getMapID() {
        return mapID;
    }

    public void setMapID(short mapID) {
        this.mapID = mapID;
    }

    public int getRelativeX() {
        return relativeX;
    }

    public void setRelativeX(int relativeX) {
        this.relativeX = relativeX;
    }

    public int getRelativeY() {
        return relativeY;
    }

    public void setRelativeY(int relativeY) {
        this.relativeY = relativeY;
    }

    @Override
    public String toString() {
        return "[MapID = " + mapID + ", x = " + location.getX() + ", y = " + location.getY() + ", z = " + location.getZ() + "]";
    }
}
