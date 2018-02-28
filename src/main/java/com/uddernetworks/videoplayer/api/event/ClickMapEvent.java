package com.uddernetworks.videoplayer.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClickMapEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private String message;

    public ClickMapEvent(String example) {
        message = example;
    }

    public String getMessage() {
        return message;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
