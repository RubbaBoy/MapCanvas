package com.uddernetworks.mapcanvas.api.objects;

import com.uddernetworks.mapcanvas.api.MapCanvasSection;
import com.uddernetworks.mapcanvas.api.event.ClickMapAction;
import org.bukkit.entity.Player;

/**
 * Interface for when an object is clicked.
 */
@FunctionalInterface
public interface ObjectClick {

    /**
     * Invoked when the element tied to this element is clicked.
     * @param player The player who clicked the element
     * @param action The action the player did to the map
     * @param mapCanvasSection The map canvas section clicked
     * @param x The map relative X position clicked
     * @param y The map relative Y position clicked
     */
    void onClick(Player player, ClickMapAction action, MapCanvasSection mapCanvasSection, int x, int y);
}
