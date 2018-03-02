package com.uddernetworks.mapcanvas.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MapCanvasAPI extends JavaPlugin {

    private MapCanvasManager mapCanvasManager;
    private MapInteractManager mapInteractManager;

    @Override
    public void onEnable() {
        this.mapCanvasManager = new MapCanvasManager();

        Bukkit.getServer().getPluginManager().registerEvents(mapInteractManager = new MapInteractManager(this.mapCanvasManager), this);
    }

    /**
     * Gets the MapCanvasManager
     * @return The MapCanvasManager
     */
    public MapCanvasManager getMapCanvasManager() {
        return mapCanvasManager;
    }

    /**
     * Gets the MapInteractManager
     * @return The MapInteractManager
     */
    public MapInteractManager getMapInteractManager() {
        return mapInteractManager;
    }
}
