package com.uddernetworks.videoplayer.main;

import com.uddernetworks.videoplayer.api.MapCanvas;
import com.uddernetworks.videoplayer.api.MapCanvasSection;
import com.uddernetworks.videoplayer.api.Palette;
import com.uddernetworks.videoplayer.api.objects.Circle;
import com.uddernetworks.videoplayer.api.objects.Image;
import com.uddernetworks.videoplayer.api.objects.Line;
import com.uddernetworks.videoplayer.api.objects.Rectangle;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.WorldMap;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.fusesource.jansi.Ansi;

import java.awt.*;
import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private int startingMapID = 52;
    private int width = 10;
    private int height = 6;

    private MapCanvas mapCanvas;
    private List<MapCanvasSection> mapCanvasSections = new ArrayList<>();

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

                        WorldMap worldMap = new WorldMap("map_" + excessMapIndex);

//                        MapView mapView = Bukkit.createMap(player.getWorld());
//
//                        if (excessMapIndex != 0 || (mapView == null || mapView.getId() < 0)) {
//                            System.out.println("using old map");
//
//                            mapView = getServer().getMap(excessMapIndex);
//                            excessMapIndex++;
//                        }

//                        if (this.startingMapID == -1) this.startingMapID = mapView.getId();

                        Location itemFrameLoc = mapLocation.clone().subtract(1, 0, 0);

                        itemFrameLoc.getBlock().setType(Material.ITEM_FRAME);
                        ItemFrame frame = (ItemFrame) player.getWorld().spawnEntity(itemFrameLoc, EntityType.ITEM_FRAME);
                        frame.setRotation(Rotation.NONE);
//                        System.out.println("id = " + mapView.getId());
//                        frame.setItem(new ItemStack(Material.MAP, 1, mapView.getId()));
                        frame.setItem(new ItemStack(Material.MAP, 1, excessMapIndex));
                        frame.setRotation(Rotation.NONE);

//                        MapCanvasSection mapCanvasSection = new MapCanvasSection(mapLocation, frame, excessMapIndex, x, y);
//                        this.mapCanvasSections.add(mapCanvasSection);

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

                mapCanvas = new MapCanvas(this, width, height, mapIDs);

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

                Image image = null;
                try {
                    image = new Image(new URL("https://rubbaboy.me/images/bo9eebg.png"), (width * 128 - imageWidth) / 2, (height * 128 - imageHeight) / 2, imageWidth, imageHeight);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                mapCanvas.addObject(image);

                mapCanvas.initialize();

                mapCanvas.paint();


                player.sendMessage(ChatColor.GOLD + "Finished drawing.");
            } else if (args[0].equalsIgnoreCase("register")) {
//                int x = Integer.parseInt(args[1]);
//                int y = Integer.parseInt(args[2]);
//                int z = Integer.parseInt(args[3]);

                Location edgeLocation = new Location(player.getWorld(), 76.9, 91, 307);

                // Add Z 1 total

                for (int i = 0; i < 128; i++) {
                    clicks.add(edgeLocation.clone());
                    edgeLocation.add(0, 0, 0.0078125D);
                }


            }
        }

        return true;
    }

    private List<Location> clicks = new ArrayList<>();

    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent event) {
        if (event.getMessage().toLowerCase().startsWith("start")) {

            int after = Integer.valueOf(event.getMessage().replace("start", "").trim());


        }
    }


    private Vector vector;
//    private List<Integer[]> itemFrameBlocks = new ArrayList<>();

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        System.out.println(event.getAction());

        player.sendMessage(ChatColor.GOLD + "Clicked");

        if (event.getHand() != EquipmentSlot.HAND) return;

//        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            player.sendMessage(ChatColor.GOLD + "Starting shit...");
            this.vector = player.getLocation().getDirection().normalize();

//            Location target = new Location(player.getWorld(), 77, 91, 307);

            new Thread(() -> {
//                Vector adding = vector.clone().divide(new Vector(128, 128, 128));
                Vector adding = vector.clone();

                for (int i = 0; i < 10000; i++) {
                    vector.add(adding);

                    double posX = vector.getX() + player.getEyeLocation().getX();
                    double posY = vector.getY() + player.getEyeLocation().getY();
                    double posZ = vector.getZ() + player.getEyeLocation().getZ();

                    Block currentBlock = player.getWorld().getBlockAt((int) posX, (int) posY, (int) posZ);

                    if (currentBlock.getType() != Material.AIR) {
                        MapCanvasSection mapCanvasSection = getItemFrameBlock(this.mapCanvas, currentBlock.getX(), currentBlock.getY(), currentBlock.getZ());

                        if (mapCanvasSection == null) {
                            player.sendMessage(ChatColor.RED + "Nulllll!");
                            return;
                        }

                        double columnClicked = (roundToSixteenth(vector.getZ() + player.getEyeLocation().getZ()) - currentBlock.getZ()) * 128D;
                        double rowClicked = (roundToSixteenth(vector.getY() + player.getEyeLocation().getY()) - currentBlock.getY()) * 128D;

                        columnClicked += (mapCanvasSection.getRelativeX() - 1) * 128;
                        rowClicked += (mapCanvasSection.getRelativeY() - 1) * 128;

                        System.out.println("Think it was: (" + columnClicked + ", " + rowClicked + ")");
                        player.sendMessage("Think it was: (" + columnClicked + ", " + rowClicked + ")");
                        player.sendMessage(ChatColor.GOLD + "Relative: (" + mapCanvasSection.getRelativeX() + ", " + mapCanvasSection.getRelativeY() + ")");


                        Circle circle = new Circle((int) columnClicked, (int) rowClicked, 30, 50, MapPalette.matchColor(Color.RED));

                        this.mapCanvas.addObject(circle);
                        circle.initialize(this.mapCanvas);
                        circle.draw(this.mapCanvas);

                        this.mapCanvas.updateMaps();

                        break;
                    }
                }

                player.sendMessage(ChatColor.GOLD + "Done");
            }).start();
//        }
    }

    private MapCanvasSection getItemFrameBlock(MapCanvas mapCanvas, int x, int y, int z) {
//        System.out.println("mapCanvas = [" + mapCanvas + "], x = [" + x + "], y = [" + y + "], z = [" + z + "]");
//        System.out.println(mapCanvas.getMapCanvasSections());
        return mapCanvas.getMapCanvasSections().stream().filter(mapCanvasSection -> mapCanvasSection.getLocation().getX() == x && mapCanvasSection.getLocation().getY() == y && mapCanvasSection.getLocation().getZ() == z).findFirst().orElse(null);
    }

    public static double roundToSixteenth(double d) {
        return Math.round(d * 128) / 128.0;
    }

    private Vector adding = new Vector(0.1, 0.1, 0.1);

    private void moveForward(Vector vector) {
        vector.add(vector.normalize().multiply(2));
    }

    @EventHandler
    public void onClickEntity(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame)) return;

        ItemFrame itemFrame = (ItemFrame) event.getRightClicked();

        itemFrame.setRotation(Rotation.NONE);

        Bukkit.getPlayer("RubbaBoy").sendMessage(String.valueOf(event.getClickedPosition().getY()));

//        System.out.println(event.getClickedPosition());

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