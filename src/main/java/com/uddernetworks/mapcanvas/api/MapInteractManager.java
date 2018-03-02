package com.uddernetworks.mapcanvas.api;

import com.uddernetworks.mapcanvas.api.event.ClickMapAction;
import com.uddernetworks.mapcanvas.api.event.ClickMapEvent;
import com.uddernetworks.mapcanvas.api.objects.Clickable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for managing the player clicking map canvases.
 */
public class MapInteractManager implements Listener {

    private MapCanvasManager mapCanvasManager;
    private int distance = 5000;

    /**
     * Creates a new MapInteractManager. There should only be one of these
     * created.
     * @param mapCanvasManager The server's single MapCanvasManager
     */
    public MapInteractManager(MapCanvasManager mapCanvasManager) {
        this.mapCanvasManager = mapCanvasManager;
    }

    /**
     * Sets the distance that should be checked if a player is clicking a map.
     * @param distance The distance that should be checked, default is 5000
     */
    public void setCheckingDistance(int distance) {
        this.distance = distance;
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getHand() != EquipmentSlot.HAND) return;

        Vector vector = player.getLocation().getDirection().normalize();

        Vector adding = vector.clone();

        for (int i = 0; i < this.distance; i++) {
            vector.add(adding);

            double posX = vector.getX() + player.getEyeLocation().getX();
            double posY = vector.getY() + player.getEyeLocation().getY();
            double posZ = vector.getZ() + player.getEyeLocation().getZ();

            Block currentBlock = player.getWorld().getBlockAt((int) posX, (int) posY, (int) posZ);

            if (currentBlock.getType() != Material.AIR) {
                MapCanvasSection mapCanvasSection = getItemFrameBlock(this.mapCanvasManager.getViewedBy(player.getUniqueId()), currentBlock.getX(), currentBlock.getY(), currentBlock.getZ());

                if (mapCanvasSection == null) break;

                final double xClicked = (roundToSixteenth(vector.getX() + player.getEyeLocation().getX()) - currentBlock.getX()) * 128D;    // Row /// ((mapCanvasSection.getRelativeZ() - 1) * 128)
                final double yClicked = (roundToSixteenth(vector.getY() + player.getEyeLocation().getY()) - currentBlock.getY()) * 128D;    // Row /// ((mapCanvasSection.getRelativeY() - 1) * 128)
                final double zClicked = (roundToSixteenth(vector.getZ() + player.getEyeLocation().getZ()) - currentBlock.getZ()) * 128D; // Column /// ((mapCanvasSection.getRelativeX() - 1) * 128)

                double rowClicked = 0;
                double columnClicked = 0;

                switch (mapCanvasSection.getMapCanvas().getWallDirection()) {
                    case X_AXIS:
                        if (mapCanvasSection.getMapCanvas().getMapFace() == BlockFace.SOUTH) {
                            rowClicked = yClicked + (mapCanvasSection.getRelativeY() - 1) * 128;
                            columnClicked = xClicked + (mapCanvasSection.getRelativeX() - 1) * 128;
                        } else {
                            rowClicked = yClicked + (mapCanvasSection.getRelativeY() - 1) * 128;
                            columnClicked = xClicked + (mapCanvasSection.getRelativeX() - 1) * 128;
                        }

                        break;
                    case Z_AXIS:
                        if (mapCanvasSection.getMapCanvas().getMapFace() == BlockFace.WEST) {
                            rowClicked = yClicked + (mapCanvasSection.getRelativeY() - 1) * 128;
                            columnClicked = zClicked + (mapCanvasSection.getRelativeX() - 1) * 128;
                        } else {
                            rowClicked = yClicked + (mapCanvasSection.getRelativeY() - 1) * 128;
                            columnClicked = zClicked + (mapCanvasSection.getRelativeX() - 1) * 128;
                        }

                        break;
                }


                ClickMapAction clickMapAction = getActionFrom(event.getAction());

                if (clickMapAction == null) return;

                ClickMapEvent clickMapEvent = new ClickMapEvent(mapCanvasSection.getMapCanvas(), player, clickMapAction, mapCanvasSection, (int) columnClicked, (int) rowClicked);
                Bukkit.getPluginManager().callEvent(clickMapEvent);

                if (!clickMapEvent.isCancelled()) {
                    List<Clickable> objects = mapCanvasSection.getMapCanvas().getClickableInPosition((int) columnClicked, (int) rowClicked);

                    double finalColumnClicked = columnClicked;
                    double finalRowClicked = rowClicked;
                    objects.stream().filter(clickable -> clickable.getClick() != null).forEach(clickable -> clickable.getClick().onClick(player, clickMapAction, mapCanvasSection, (int) finalColumnClicked, (int) finalRowClicked));
                }

                break;
            }
        }
    }

    private ClickMapAction getActionFrom(Action action) {
        switch (action) {
            case RIGHT_CLICK_AIR:
                return ClickMapAction.RIGHT_CLICK_AIR;
            case RIGHT_CLICK_BLOCK:
                return ClickMapAction.RIGHT_CLICK_MAP;
            case LEFT_CLICK_AIR:
                return ClickMapAction.LEFT_CLICK_AIR;
            case LEFT_CLICK_BLOCK:
                return ClickMapAction.LEFT_CLICK_MAP;
            default:
                return null;
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
