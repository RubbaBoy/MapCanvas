package com.uddernetworks.videoplayer.api;

import com.uddernetworks.videoplayer.api.MapObject;
import com.uddernetworks.videoplayer.main.VideoPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayOutMap;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.map.CraftMapCanvas;
import org.bukkit.entity.Player;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MapCanvas {

    private VideoPlayer videoPlayer;
    private int width;
    private int height;
    private List<Integer> mapIDs;
    private List<MapObject> mapObjects;
    private List<UUID> viewers;
    private int repaintInterval = 500; // In MS
    public byte[] pixels;
    private Palette palette;

//    public MapCanvas(int width, int height, List<Integer> mapIDs, List<UUID> viewers) {
//        this(width, height, mapIDs);
//        this.viewers = viewers;
//    }

    public MapCanvas(VideoPlayer videoPlayer, int width, int height, List<Integer> mapIDs, List<UUID> viewers) {
        this.videoPlayer = videoPlayer;
        this.width = width;
        this.height = height;
        this.mapIDs = mapIDs;
        this.mapObjects = new ArrayList<>();
        this.viewers = viewers;
        this.pixels = new byte[width * height * 128 * 128];
        this.palette = new Palette();

        mapIDs.forEach(mapID -> {
            MapView mapView = Bukkit.getServer().getMap(mapID.shortValue());
            List<MapRenderer> removing = new ArrayList<>(mapView.getRenderers());
            removing.forEach(mapView::removeRenderer);
        });
    }

    public void addObject(MapObject mapObject) {
        this.mapObjects.add(mapObject);
    }

    public void setRepaintInterval(int repaintInterval) {
        this.repaintInterval = repaintInterval;
    }

    public void paint() {
        this.pixels = new byte[width * height * 128 * 128];

//        Thread.sleep(500);

        Arrays.fill(this.pixels, MapPalette.matchColor(Color.WHITE));
//        Arrays.fill(this.pixels, MapPalette.matchColor(Color.WHITE));
//        Arrays.fill(this.pixels, (byte) ThreadLocalRandom.current().nextInt(50));

//        randomize(this.pixels);

        mapObjects.forEach(mapObject -> {
            System.out.println("Rendering " + mapObject);
            mapObject.draw(this);
        });

//        visualizeBytes(pixels, width * 128, height * 128, "main_stuff");

        int mapID = 0;

        for (int imageY = 0; imageY < height; imageY++) {
            for (int imageX = 0; imageX < width; imageX++) {

                System.out.println("(" + imageX + ", " + imageY + ")");

                byte[] colors = getSubImage(pixels, imageX, imageY);

                for (int i = 0; i < colors.length; i++) {
                    if (colors[i] == -1) colors[i] = 0;
                }

                PacketPlayOutMap packet = new PacketPlayOutMap(mapIDs.get(mapID), (byte) 4, false, new ArrayList<>(), colors, 0, 0, 128, 128);

                List<UUID> finalViewers = this.viewers == null ? Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()) : this.viewers;

                finalViewers.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(player -> ((CraftPlayer) player).getHandle().playerConnection.networkManager.sendPacket(packet));

                mapID++;
            }
        }
    }

    private byte[] getSubImage(byte[] image, int xPos, int yPos) {
        System.out.println("(" + xPos + ", " + yPos + ")");
        byte[] sub = getSquare(image, xPos, yPos, 128);

        sub = rotateCube270(sub, 128, 128);

        return sub;
    }


    public void visualizeBytes(byte[] bytes, int width, int height, String name) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, this.palette.getJavaColorById(bytes[y * height + x]).getRGB());
            }
        }

        try {
            ImageIO.write(image, "png", new File(this.videoPlayer.getDataFolder(), "\\temp\\" + name + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public byte[] getSquare(byte[] array, int xQuadrant, int yQuadrant, int sectionSize) {
        byte[] sub = new byte[sectionSize * sectionSize];

        int startX = xQuadrant * sectionSize;
        int startY = yQuadrant * sectionSize;

        int rowLength = this.width * 128;

        for (int y = 0; y < sectionSize; y++) {
            System.arraycopy(array, startX + ((startY + y) * rowLength), sub, y * sectionSize, sectionSize);
        }

        return sub;
    }

    public byte getPixel(int x, int y) {
        return this.pixels[y * (128 * this.width - 1) + x];
    }

    public void setPixel(int x, int y, byte pixel) {
        if (y >= this.height * 128 || y < 0
                || x >= this.width * 128 || x < 0) {
            return;
        }

        this.pixels[(y * 128 * this.width) + x] = pixel;
    }

    private byte[] rotateCube270(byte[] bytes, int width, int height) {
        byte[] ret = bytes.clone();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                setPixelFrom(ret, getPixelFrom(bytes, width, x, y), width, y, width - 1 - x);
            }
        }

        return ret;
    }

    private void setPixelFrom(byte[] bytes, byte pixel, int width, int x, int y) {
        bytes[y * width + x] = pixel;
    }

    private byte getPixelFrom(byte[] bytes, int width, int x, int y) {
        return bytes[y * width + x];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRepaintInterval() {
        return repaintInterval;
    }
}
