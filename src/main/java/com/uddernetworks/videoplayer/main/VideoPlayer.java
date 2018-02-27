package com.uddernetworks.videoplayer.main;

import net.minecraft.server.v1_12_R1.PacketPlayOutMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.map.CraftMapRenderer;
import org.bukkit.craftbukkit.v1_12_R1.map.CraftMapView;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Collectors;

public class VideoPlayer extends JavaPlugin implements Listener {

    private Palette palette;
    private int index = 0;
    private List<List<Integer>> mapIDs = new ArrayList<>();
    private List<MapFrame> mapFrames = new ArrayList<>();
    private AtomicReferenceArray<String> files;
    private AtomicInteger finished = new AtomicInteger();

    @Override
    public void onEnable() {
        this.palette = new Palette();

        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        int index = 113;

        for (int y = 0; y < 3; y++) { // Usually 6
            List<Integer> row = new ArrayList<>();

            for (int x = 0; x < 4; x++) { // Usually 10
                row.add(index);

                CraftMapView mapView = (CraftMapView) Bukkit.getServer().getMap((short) index);
                if (mapView.getRenderers().size() != 0) mapView.removeRenderer(mapView.getRenderers().get(0));

                index++;
            }

            mapIDs.add(row);
        }


        Bukkit.getScheduler().runTaskLater(this, () -> {
            final long start = System.currentTimeMillis();

            File framesDir = new File(getDataFolder(), "frames");

            List<String> fileNames = Arrays.stream(framesDir.listFiles()).map(File::getName).filter(name -> name.endsWith(".png"))
//                    .sorted(new Comparator<String>() {
//                public int compare(String o1, String o2) {
//                    return extractInt(o1) - extractInt(o2);
//                }
//
//                int extractInt(String s) {
//                    String num = s.replaceAll("\\D", "");
//                    return num.isEmpty() ? 0 : Integer.parseInt(num);
//                }
//            })
                    .collect(Collectors.toList());

            this.files = new AtomicReferenceArray<>(fileNames.size());

            for (int i = 0; i < fileNames.size(); i++) {
                this.files.set(i, fileNames.get(i));
            }

            for (int i = 0; i < fileNames.size() + 10; i++) {
                this.mapFrames.add(null);
            }

            int numberOfProcessingThreads = 50;


//            Bukkit.getPlayer("RubbaBoy").sendMessage("Started");
            for (int i = 0; i < numberOfProcessingThreads; i++) {
                new Thread(() -> {
                    while (true) {
                        String name = null;
                        for (int i2 = 0; i2 < this.files.length(); i2++) {
                            name = this.files.getAndSet(i2, null);
                            if (name != null) break;
                        }

                        if (name == null) break;

                        int number = Integer.valueOf(name.replaceAll("\\D", ""));

                        System.out.println("name = " + name + "\t#" + number);

                        try {
                            BufferedImage frameImage = ImageIO.read(new File(getDataFolder(), "frames\\" + name));
                            MapFrame mapFrame = new MapFrame(this, frameImage.getWidth(), frameImage.getHeight());
                            mapFrame.register(frameImage);

                            this.mapFrames.set(number, mapFrame);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    finished.addAndGet(1);
                }).start();
            }

            new Thread(() -> {
                while (this.finished.get() < numberOfProcessingThreads);

                System.out.println("Completed in " + (System.currentTimeMillis() - start) + "ms");
            }).start();

//            Bukkit.getPlayer("RubbaBoy").sendMessage("Finished making threads");


        }, 60L);

//        new Thread(() -> {

//        }).start();


        /*
        int index = 52;

        for (int y = 0; y < 6; y++) {
            List<Integer> row = new ArrayList<>();

            for (int x = 0; x < 10; x++) {
                row.add(index);

                CraftMapView mapView = (CraftMapView) Bukkit.getServer().getMap((short) index);
                if (mapView.getRenderers().size() != 0) mapView.removeRenderer(mapView.getRenderers().get(0));

                index++;
            }

            mapIDs.add(row);
        }
         */

    }

    private long start = -1;
    private int amount = 1;

    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent event) {
        if (event.getMessage().toLowerCase().startsWith("start")) {

            event.getPlayer().sendMessage(ChatColor.GOLD + "Starting...");

            showFrames(event.getPlayer());

            index = 0;
            start = System.currentTimeMillis();
        }
    }


    private int currentFrame = 0;
    private int mapIndex = 0;

    private void showFrames(Player player) {
        new Thread(() -> {
            while (currentFrame < this.mapFrames.size()) {
                System.out.println("index = " + index);
                index++;

                byte[] colors = new byte[16384];

                for (int i = 0; i < colors.length; i++) {
                    colors[i] = (byte) palette.getColorById(ThreadLocalRandom.current().nextInt(50));
                }

                MapFrame mapFrame = this.mapFrames.get(currentFrame);

                while (mapFrame == null) {
                    currentFrame++;
                    mapFrame = this.mapFrames.get(currentFrame);
                }

                currentFrame++;

                CraftPlayer craftPlayer = (CraftPlayer) player;

                if (craftPlayer.getHandle().playerConnection == null) return;

                mapIndex = 0;

                MapFrame finalMapFrame = mapFrame;
                this.mapIDs.stream().flatMap(List::stream).forEach(id -> {
//                    System.out.println("mapIndex = " + mapIndex);
                    PacketPlayOutMap packet = new PacketPlayOutMap(id, (byte) 4, false, new ArrayList<>(), finalMapFrame.getForMap(mapIndex), 0, 0, 128, 128);
//                    PacketPlayOutMap packet = new PacketPlayOutMap(id, (byte) 4, false, new ArrayList<>(), colors, 0, 0, 128, 128);
                    craftPlayer.getHandle().playerConnection.sendPacket(packet);
                    mapIndex++;
                });

                try {
                    Thread.sleep(40); // 25 FPS
//                        Thread.sleep(33); // 30 FPS
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("Stopped");
            player.sendMessage(ChatColor.RED + "Stopped!");
        }).start();
    }


//    private int i = 0;
//
//    @EventHandler
//    public void onClickEntity(PlayerInteractAtEntityEvent event) {
//        if (!(event.getRightClicked() instanceof ItemFrame)) return;
//
//        ItemFrame itemFrame = (ItemFrame) event.getRightClicked();
//
//        MapView map = getServer().createMap(event.getPlayer().getWorld());
//
//        Bukkit.getPlayer("RubbaBoy").sendMessage("#" + i + " - " + map.getId());
//
//        i++;
//
//        itemFrame.setItem(new ItemStack(Material.MAP, 1, map.getId()));
//    }

    @EventHandler
    public void onMapLoad(MapInitializeEvent event) {
        Bukkit.getPlayer("RubbaBoy").sendMessage("Map " + event.getMap().getId() + " os trying to load");
    }


}