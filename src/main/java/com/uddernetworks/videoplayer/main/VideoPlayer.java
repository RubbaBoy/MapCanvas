package com.uddernetworks.videoplayer.main;

import com.uddernetworks.videoplayer.api.MapCanvas;
import com.uddernetworks.videoplayer.api.Palette;
import com.uddernetworks.videoplayer.api.objects.Circle;
import com.uddernetworks.videoplayer.api.objects.Line;
import com.uddernetworks.videoplayer.api.objects.Rectangle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Rotation;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapPalette;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class VideoPlayer extends JavaPlugin implements Listener {

    private Palette palette;
    private int index = 0;
    private List<List<Integer>> mapIDs = new ArrayList<>();

    @Override
    public void onEnable() {
        this.palette = new Palette();

        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    private long start = -1;
    private int amount = 1;

    public static int REMOVE = 0;

    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent event) throws InterruptedException {
        if (event.getMessage().toLowerCase().startsWith("start")) {

            int after = Integer.valueOf(event.getMessage().replace("start", "").trim());

            event.getPlayer().sendMessage(ChatColor.GOLD + "Starting with " + after + "...");

            this.REMOVE = after;

            List<Integer> mapIDs = new ArrayList<>();

            for (int i = 0; i < 12; i++) {
                mapIDs.add(113 + i);
            }

            MapCanvas mapCanvas = new MapCanvas(this, 4, 3, mapIDs, Collections.singletonList(UUID.fromString("946dba0a-1eee-489a-ab6d-516927fb2df7")));

            mapCanvas.addObject(new Rectangle(128 / 2, 128 / 2, 128 * 3, 128 * 2, MapPalette.matchColor(Color.YELLOW)));

            mapCanvas.addObject(new Line(128 / 2, 128 / 2, (int) (128 * 3.5), (int) (128 * 2.5), MapPalette.matchColor(Color.RED)));
            mapCanvas.addObject(new Line((int) (128 * 3.5), 128 / 2, 128 / 2, (int) (128 * 2.5), MapPalette.matchColor(Color.RED)));

            mapCanvas.addObject(new Circle(128 * 2, (int) (128 * 1.5), 64, 66, MapPalette.matchColor(Color.BLUE)));

            mapCanvas.paint();

            event.getPlayer().sendMessage(ChatColor.GOLD + "Finished.");


//            mapCanvas.addObject(new Rectangle(0, 0, 64, 64, palette.findClosestPaletteColorTo(Color.RED)));
//            mapCanvas.addObject(new Rectangle(64, 0, 64, 64, palette.findClosestPaletteColorTo(Color.GREEN)));
//
//            mapCanvas.addObject(new Rectangle(0, 64, 64, 64, palette.findClosestPaletteColorTo(Color.BLUE)));
//            mapCanvas.addObject(new Rectangle(64, 64, 64, 64, palette.findClosestPaletteColorTo(Color.YELLOW)));


//            mapCanvas.addObject(new Rectangle(128 / 2, 128 / 2, 128 * 3, 128 * 2, MapPalette.matchColor(Color.YELLOW)));
//
//            mapCanvas.addObject(new Line(128 / 2, 128 / 2, (int) (128 * 2), (int) (128 / 1.5), MapPalette.matchColor(Color.RED)));
        }
    }


    private int i = 0;

    @EventHandler
    public void onClickEntity(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame)) return;

        ItemFrame itemFrame = (ItemFrame) event.getRightClicked();

        itemFrame.setRotation(Rotation.NONE);

        System.out.println(event.getClickedPosition());

//        MapView map = getServer().createMap(event.getPlayer().getWorld());
//
//        Bukkit.getPlayer("RubbaBoy").sendMessage("#" + i + " - " + map.getId());
//
//        i++;
//
//        itemFrame.setItem(new ItemStack(Material.MAP, 1, map.getId()));
    }

    @EventHandler
    public void onMapLoad(MapInitializeEvent event) {
        Bukkit.getPlayer("RubbaBoy").sendMessage("Map " + event.getMap().getId() + " os trying to load");
    }


}