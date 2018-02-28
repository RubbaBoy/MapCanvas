package com.uddernetworks.videoplayer.main;

import com.uddernetworks.videoplayer.api.MapCanvas;
import com.uddernetworks.videoplayer.api.Palette;
import com.uddernetworks.videoplayer.api.objects.Square;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Rotation;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent event) throws InterruptedException {
        if (event.getMessage().toLowerCase().startsWith("start")) {

            int after = Integer.valueOf(event.getMessage().replace("start", "").trim());

            event.getPlayer().sendMessage(ChatColor.GOLD + "Starting with " + after + "...");

            List<Integer> mapIDs = new ArrayList<>();

            for (int i = 0; i < 12; i++) {
                mapIDs.add(113 + i);
            }

            MapCanvas mapCanvas = new MapCanvas(4, 3, mapIDs);

//            mapCanvas.addObject(new Square(0, 0, 64, 64, palette.findClosestPaletteColorTo(Color.RED)));
//            mapCanvas.addObject(new Square(64, 0, 64, 64, palette.findClosestPaletteColorTo(Color.GREEN)));
//
//            mapCanvas.addObject(new Square(0, 64, 64, 64, palette.findClosestPaletteColorTo(Color.BLUE)));
//            mapCanvas.addObject(new Square(64, 64, 64, 64, palette.findClosestPaletteColorTo(Color.YELLOW)));


            mapCanvas.addObject(new Square(64, (int) (128 * 1.5) + after, 128, 128, palette.findClosestPaletteColorTo(Color.GREEN)));


//            mapCanvas.addObject(new Square(64, 64, 128 * 3, 128 * 2, palette.findClosestPaletteColorTo(Color.RED)));

//            mapCanvas.addObject(new Square(128, 64 + after, 256, 64, palette.findClosestPaletteColorTo(Color.RED)));
//
//            mapCanvas.addObject(new Square(128, 64 + 128, 256, 64, palette.findClosestPaletteColorTo(Color.GREEN)));
//
//            mapCanvas.addObject(new Square(128, 64 + 128 * 2, 256, 64, palette.findClosestPaletteColorTo(Color.BLUE)));

            mapCanvas.paint();

            event.getPlayer().sendMessage(ChatColor.GOLD + "Finished.");
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