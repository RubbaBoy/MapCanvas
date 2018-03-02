package com.uddernetworks.mapcanvas.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Should only be one instance throughout the plugin, this manages all the MapCanvases.
 */
public class MapCanvasManager {

    private List<MapCanvas> mapCanvasList = new ArrayList<>();

    /**
     * Gets all maps viewed by the given player.
     * @param player The UUID of the player to get maps from
     * @return All maps viewed by the given player
     */
    public List<MapCanvas> getViewedBy(UUID player) {
        return this.mapCanvasList.stream().filter(mapCanvas -> mapCanvas.getViewers().isEmpty() || mapCanvas.getViewers().contains(player)).collect(Collectors.toList());
    }

    /**
     * Adds a MapCanvas to the internal list for searching and managing.
     * @param mapCanvas The MapCanvas to add
     */
    public void addCanvas(MapCanvas mapCanvas) {
        this.mapCanvasList.add(mapCanvas);
    }

    /**
     * Gets the MapCanvas by the specified UUID
     * @param uuid The UUID of the MapCanvas
     * @return The MapCanvas with the given UUID
     */
    public MapCanvas getMapCanvas(UUID uuid) {
        return mapCanvasList.stream().filter(mapCanvas -> mapCanvas.getUUID().equals(uuid)).findFirst().orElse(null);
    }
}
