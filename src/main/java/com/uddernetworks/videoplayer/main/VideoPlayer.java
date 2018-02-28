package com.uddernetworks.videoplayer.main;

import com.uddernetworks.videoplayer.api.MapCanvas;
import com.uddernetworks.videoplayer.api.Palette;
import com.uddernetworks.videoplayer.api.objects.Circle;
import com.uddernetworks.videoplayer.api.objects.Image;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class VideoPlayer extends JavaPlugin implements Listener {

    private Palette palette;
    private int index = 0;
    private List<List<Integer>> mapIDs = new ArrayList<>();

    @Override
    public void onEnable() {
        this.palette = new Palette();

        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    private double rotation = 0;

    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent event) throws MalformedURLException {
        if (event.getMessage().toLowerCase().startsWith("start")) {

            int after = Integer.valueOf(event.getMessage().replace("start", "").trim());

            event.getPlayer().sendMessage(ChatColor.GOLD + "Starting with " + after + "...");

            List<Integer> mapIDs = new ArrayList<>();

            int startingMapID = 52;
            int width = 10;
            int height = 6;


            for (int i = 0; i < width * height; i++) {
                mapIDs.add(startingMapID + i);
            }

            MapCanvas mapCanvas = new MapCanvas(this, width, height, mapIDs);

            for (int i = 0; i < 10; i++) {
                mapCanvas.addObject(new Rectangle(
                        ThreadLocalRandom.current().nextInt(128 * width),
                        ThreadLocalRandom.current().nextInt(128 * height),
                        ThreadLocalRandom.current().nextInt(128 * width / 2),
                        ThreadLocalRandom.current().nextInt(128 * height / 2),
                        palette.getColorById(ThreadLocalRandom.current().nextInt(50))));
            }

            for (int i = 0; i < 10; i++) {
                mapCanvas.addObject(new Line(
                        ThreadLocalRandom.current().nextInt(128 * width / 2),
                        ThreadLocalRandom.current().nextInt(128 * height / 2),
                        ThreadLocalRandom.current().nextInt(128 * width),
                        ThreadLocalRandom.current().nextInt(128 * height),
                        palette.getColorById(ThreadLocalRandom.current().nextInt(50))));
            }

            for (int i = 0; i < 10; i++) {
                int inner = ThreadLocalRandom.current().nextInt(64);
                mapCanvas.addObject(new Circle(
                        ThreadLocalRandom.current().nextInt(128 * width),
                        ThreadLocalRandom.current().nextInt(128 * height),
                        inner,
                        inner + ThreadLocalRandom.current().nextInt(5),
                        palette.getColorById(ThreadLocalRandom.current().nextInt(50))));
            }

            mapCanvas.addObject(new Circle(0, 0, 10, 15, MapPalette.matchColor(Color.RED))); // Top left
            mapCanvas.addObject(new Circle(width * 128, 0, 10, 15, MapPalette.matchColor(Color.GREEN))); // Top right
            mapCanvas.addObject(new Circle(0, height * 128, 10, 15, MapPalette.matchColor(Color.BLUE))); // Bottom left
            mapCanvas.addObject(new Circle(width * 128, height * 128, 10, 15, MapPalette.matchColor(Color.YELLOW))); // Bottom right

            int imageWidth = 600;
            int imageHeight = 600;

            Image image = new Image(new URL("https://rubbaboy.me/images/bo9eebg.png"), (width * 128 - imageWidth) / 2, (height * 128 - imageHeight) / 2, imageWidth, imageHeight);

            mapCanvas.addObject(image);

            mapCanvas.initialize();

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