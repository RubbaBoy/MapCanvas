package com.uddernetworks.mapcanvas.api.event;

import com.uddernetworks.mapcanvas.api.MapCanvas;
import com.uddernetworks.mapcanvas.api.MapCanvasSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a player interacts with a map canvas. It will be called even if they did not click on an element specifically.
 */
public class ClickMapEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private MapCanvas mapCanvas;
    private final Player player;
    private ClickMapAction action;
    private MapCanvasSection mapCanvasSection;
    private final int x;
    private final int y;
    private boolean cancelled = false;

    public ClickMapEvent(MapCanvas mapCanvas, Player player, ClickMapAction action, MapCanvasSection mapCanvasSection, int x, int y) {
        this.mapCanvas = mapCanvas;
        this.player = player;
        this.action = action;
        this.mapCanvasSection = mapCanvasSection;
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the clicked map canvas.
     * @return The clicked map canvas
     */
    public MapCanvas getMapCanvas() {
        return mapCanvas;
    }

    /**
     * Gets the player who clicked.
     * @return The player who clicked
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the action the player did on the MapCanvas.
     * @return The action the player did on the MapCanvas
     */
    public ClickMapAction getAction() {
        return action;
    }

    /**
     * Gets the MapCanvasSection clicked by the player.
     * @return The MapCanvasSection clicked by the player
     */
    public MapCanvasSection getMapCanvasSection() {
        return mapCanvasSection;
    }

    /**
     * Gets the relative X coordinate clicked on the MapCanvas.
     * @return The relative X coordinate clicked on the MapCanvas
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the relative Y coordinate clicked on the MapCanvas.
     * @return The relative Y coordinate clicked on the MapCanvas
     */
    public int getY() {
        return y;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
