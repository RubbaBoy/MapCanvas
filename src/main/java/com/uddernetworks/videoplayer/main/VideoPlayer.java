package com.uddernetworks.videoplayer.main;

import com.uddernetworks.videoplayer.api.MapCanvas;
import com.uddernetworks.videoplayer.api.MapCanvasManager;
import com.uddernetworks.videoplayer.api.MapInteractManager;
import com.uddernetworks.videoplayer.api.Palette;
import com.uddernetworks.videoplayer.api.event.ClickMapEvent;
import com.uddernetworks.videoplayer.api.font.MinecraftFont;
import com.uddernetworks.videoplayer.api.objects.*;
import com.uddernetworks.videoplayer.api.objects.Image;
import com.uddernetworks.videoplayer.api.objects.Rectangle;
import net.minecraft.server.v1_12_R1.WorldMap;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapPalette;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class VideoPlayer extends JavaPlugin implements Listener {

    private Palette palette;

    private int startingMapID = 52;
    private int width = 10;
    private int height = 6;

    private MapCanvasManager mapCanvasManager;

    @Override
    public void onEnable() {
        this.palette = new Palette();

        this.mapCanvasManager = new MapCanvasManager();

        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getServer().getPluginManager().registerEvents(new MapInteractManager(this.mapCanvasManager), this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("canvas")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Players only, dude.");
                return true;
            }

            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Yeah, no");
                return true;
            }

            if (args[0].equalsIgnoreCase("create")) {
                if (args.length != 3) {
                    player.sendMessage(ChatColor.RED + "Yeah, no, dude. Usage: /canvas create [width] [height]");
                    return true;
                }

                this.width = Integer.valueOf(args[1]);
                this.height = Integer.valueOf(args[2]);
                this.startingMapID = 0;

                Location mapLocation = player.getLocation().add(0, this.height, 0);

                short excessMapIndex = 0;

                for (int y = height; y > 0; y--) {
                    for (int x = 0; x < width; x++) {
                        mapLocation.getBlock().setType(Material.WOOL);

                        new WorldMap("map_" + excessMapIndex);

                        Location itemFrameLoc = mapLocation.clone().subtract(1, 0, 0);

                        itemFrameLoc.getBlock().setType(Material.ITEM_FRAME);
                        ItemFrame frame = (ItemFrame) player.getWorld().spawnEntity(itemFrameLoc, EntityType.ITEM_FRAME);
                        frame.setRotation(Rotation.NONE);
                        frame.setItem(new ItemStack(Material.MAP, 1, excessMapIndex));
                        frame.setRotation(Rotation.NONE);

                        excessMapIndex++;

                        mapLocation.add(0, 0, 1);
                    }

                    mapLocation.subtract(0, 1, this.width);
                }

                player.getWorld().getEntities().stream().filter(entity -> entity instanceof ItemFrame).map(ItemFrame.class::cast).forEach(itemFrame -> itemFrame.setRotation(Rotation.NONE));

                player.sendMessage(ChatColor.GOLD + "Width: " + this.width + " Height: " + this.height + " Total maps: " + (this.width * this.height) + "Starting ID: " + this.startingMapID);
            } else if (args[0].equalsIgnoreCase("use")) {
                if (args.length != 4) {
                    player.sendMessage(ChatColor.RED + "Yeah, no, dude. Usage: /canvas use [width] [height] [startingID]");
                    return true;
                }

                this.width = Integer.valueOf(args[1]);
                this.height = Integer.valueOf(args[2]);
                this.startingMapID = Integer.valueOf(args[3]);

                player.getWorld().getEntities().stream().filter(entity -> entity instanceof ItemFrame).map(ItemFrame.class::cast).forEach(itemFrame -> itemFrame.setRotation(Rotation.CLOCKWISE_45));

                player.sendMessage(ChatColor.GOLD + "Width: " + this.width + " Height: " + this.height + " Starting ID: " + this.startingMapID);
            } else if (args[0].equalsIgnoreCase("draw")) {
                player.sendMessage(ChatColor.GOLD + "Starting drawing...");

                List<Integer> mapIDs = new ArrayList<>();


                for (int i = 0; i < width * height; i++) {
                    mapIDs.add(startingMapID + i);
                }

                MapCanvas mapCanvas = new MapCanvas(this, mapCanvasManager, width, height, mapIDs);

                Rectangle rectangle = new Rectangle(256, 0, 1024, 1024, MapPalette.matchColor(Color.GREEN));
                rectangle.setClick((clickingPlayer, action, mapCanvasSection, x, y) -> {
                    clickingPlayer.sendMessage(ChatColor.GREEN + "You clicked the green rectangle, action = " + action + "specifically at (" + x + ", " + y + ")");
                });

                mapCanvas.addObject(rectangle);

                mapCanvas.addObject(new Circle(0, 0, 10, 15, MapPalette.matchColor(Color.RED))); // Top left
                mapCanvas.addObject(new Circle(width * 128, 0, 10, 15, MapPalette.matchColor(Color.GREEN))); // Top right
                mapCanvas.addObject(new Circle(0, height * 128, 10, 15, MapPalette.matchColor(Color.BLUE))); // Bottom left
                mapCanvas.addObject(new Circle(width * 128, height * 128, 10, 15, MapPalette.matchColor(Color.YELLOW))); // Bottom right

                int imageWidth = 600;
                int imageHeight = 600;

                Image image = new Image("https://rubbaboy.me/images/bo9eebg.png", (width * 128 - imageWidth) / 2, (height * 128 - imageHeight) / 2, imageWidth, imageHeight);
                image.setClick((clickingPlayer, action, mapCanvasSection, x, y) -> {
                    clickingPlayer.sendMessage(ChatColor.BLUE + "Clicked image, action = " + action + " specifically at (" + x + ", " + y + ")");
                });

                mapCanvas.addObject(image);

                mapCanvas.addObject(new Text(128, 128, new MinecraftFont(32), MapPalette.matchColor(Color.BLACK), "Hello, World!"));

                mapCanvas.initialize();

                mapCanvas.paint();


                player.sendMessage(ChatColor.GOLD + "Finished drawing on the map: " + mapCanvas.getUUID());
            }
        }

        return true;
    }

    @EventHandler
    public void onClickMapEvent(ClickMapEvent event) {
        MapCanvas mapCanvas = event.getMapCanvas();

        event.getPlayer().sendMessage("Clicked at (" + event.getX() + ", " + event.getY() + ") on map: " + mapCanvas.getUUID());

        Circle circle = new Circle(event.getX(), event.getY(), 50, 70, MapPalette.matchColor(Color.RED));

        mapCanvas.addObject(circle);

        circle.draw(mapCanvas);

        mapCanvas.updateMaps();
    }
}