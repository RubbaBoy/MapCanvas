package com.uddernetworks.videoplayer.api.objects;

import com.uddernetworks.videoplayer.api.MapCanvasSection;
import com.uddernetworks.videoplayer.api.event.ClickMapAction;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface ObjectClick {
    void onClick(Player player, ClickMapAction action, MapCanvasSection mapCanvasSection, int x, int y);
}
