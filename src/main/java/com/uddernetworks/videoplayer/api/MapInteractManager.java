package com.uddernetworks.videoplayer.api;

import com.uddernetworks.videoplayer.api.event.ClickMapAction;
import com.uddernetworks.videoplayer.api.event.ClickMapEvent;
import com.uddernetworks.videoplayer.api.objects.Clickable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapInteractManager implements Listener {

    private MapCanvasManager mapCanvasManager;
    private ExecutorService executor;

    public MapInteractManager(MapCanvasManager mapCanvasManager) {
        this.mapCanvasManager = mapCanvasManager;

        executor = Executors.newFixedThreadPool(2);
    }


    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        System.out.println(event.getAction());

        player.sendMessage(ChatColor.GOLD + "Clicked");

        if (event.getHand() != EquipmentSlot.HAND) return;

        player.sendMessage(ChatColor.GOLD + "Starting shit...");
        Vector vector = player.getLocation().getDirection().normalize();

        this.executor.submit(() -> {
            Vector adding = vector.clone();

            for (int i = 0; i < 10000; i++) {
                vector.add(adding);

                double posX = vector.getX() + player.getEyeLocation().getX();
                double posY = vector.getY() + player.getEyeLocation().getY();
                double posZ = vector.getZ() + player.getEyeLocation().getZ();

                Block currentBlock = player.getWorld().getBlockAt((int) posX, (int) posY, (int) posZ);

                if (currentBlock.getType() != Material.AIR) {
                    MapCanvasSection mapCanvasSection = getItemFrameBlock(this.mapCanvasManager.getViewedBy(player.getUniqueId()), currentBlock.getX(), currentBlock.getY(), currentBlock.getZ());

                    if (mapCanvasSection == null) break;

                    final double columnClicked = (roundToSixteenth(vector.getZ() + player.getEyeLocation().getZ()) - currentBlock.getZ()) * 128D + ((mapCanvasSection.getRelativeX() - 1) * 128);
                    final double rowClicked = (roundToSixteenth(vector.getY() + player.getEyeLocation().getY()) - currentBlock.getY()) * 128D + ((mapCanvasSection.getRelativeY() - 1) * 128);

                    ClickMapAction clickMapAction = getActionFrom(event.getAction());

                    if (clickMapAction == null) return;

                    ClickMapEvent clickMapEvent = new ClickMapEvent(mapCanvasSection.getMapCanvas(), player, clickMapAction, mapCanvasSection, (int) columnClicked, (int) rowClicked);
                    Bukkit.getPluginManager().callEvent(clickMapEvent);

                    if (!clickMapEvent.isCancelled()) {
                        List<Clickable> objects = mapCanvasSection.getMapCanvas().getClickableInPosition((int) columnClicked, (int) rowClicked);

                        objects.stream().filter(clickable -> clickable.getClick() != null).forEach(clickable -> clickable.getClick().onClick(player, clickMapAction, mapCanvasSection, (int) columnClicked, (int) rowClicked));
                    }

                    break;
                }
            }

            player.sendMessage(ChatColor.GOLD + "Done");
        });
    }

    private ClickMapAction getActionFrom(Action action) {
        switch (action) {
            case RIGHT_CLICK_AIR: return ClickMapAction.RIGHT_CLICK_AIR;
            case RIGHT_CLICK_BLOCK: return ClickMapAction.RIGHT_CLICK_MAP;
            case LEFT_CLICK_AIR: return ClickMapAction.LEFT_CLICK_AIR;
            case LEFT_CLICK_BLOCK: return ClickMapAction.LEFT_CLICK_MAP;
            default: return null;
        }
    }

    private MapCanvasSection getItemFrameBlock(List<MapCanvas> mapCanvases, int x, int y, int z) {
        return new ArrayList<>(mapCanvases).stream()
                .flatMap(mapCanvas -> mapCanvas.getMapCanvasSections().stream())
                .filter(mapCanvasSection -> mapCanvasSection.getLocation().getX() == x
                        && mapCanvasSection.getLocation().getY() == y
                        && mapCanvasSection.getLocation().getZ() == z)
                .findFirst()
                .orElse(null);
    }

    private static double roundToSixteenth(double d) {
        return Math.round(d * 128) / 128.0;
    }
}
