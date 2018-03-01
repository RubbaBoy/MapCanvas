package com.uddernetworks.videoplayer.api.event;

import com.uddernetworks.videoplayer.api.MapCanvas;
import com.uddernetworks.videoplayer.api.MapCanvasSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

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

    public MapCanvas getMapCanvas() {
        return mapCanvas;
    }

    public Player getPlayer() {
        return player;
    }

    public ClickMapAction getAction() {
        return action;
    }

    public MapCanvasSection getMapCanvasSection() {
        return mapCanvasSection;
    }

    public int getX() {
        return x;
    }

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
