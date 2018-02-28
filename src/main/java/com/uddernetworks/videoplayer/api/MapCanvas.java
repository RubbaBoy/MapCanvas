package com.uddernetworks.videoplayer.api;

import com.uddernetworks.videoplayer.api.MapObject;
import net.minecraft.server.v1_12_R1.PacketPlayOutMap;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapPalette;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MapCanvas {

    private int width;
    private int height;
    private List<Integer> mapIDs;
    private List<MapObject> mapObjects;
    private List<UUID> viewers;
    private int repaintInterval = 500; // In MS
    private byte[] pixels;

    public MapCanvas(int width, int height, List<Integer> mapIDs, List<UUID> viewers) {
        this(width, height, mapIDs);
        this.viewers = viewers;
    }

    public MapCanvas(int width, int height, List<Integer> mapIDs) {
        this.width = width;
        this.height = height;
        this.mapIDs = mapIDs;
        this.mapObjects = new ArrayList<>();
        this.viewers = null;
        this.pixels = new byte[width * height * 128 * 128];
    }

    public void addObject(MapObject mapObject) {
//        mapObject.initialize(this);
        this.mapObjects.add(mapObject);
    }

    public void setRepaintInterval(int repaintInterval) {
        this.repaintInterval = repaintInterval;
    }

    public void paint() throws InterruptedException {
        this.pixels = new byte[width * height * 128 * 128];

        clear();

        Thread.sleep(3000);

        Arrays.fill(this.pixels, (byte) ThreadLocalRandom.current().nextInt(50));

        mapObjects.forEach(mapObject -> mapObject.draw(this));

        int mapID = 0;

        for (int imageY = 0; imageY < height; imageY++) {
            for (int imageX = 0; imageX < width; imageX++) {

//                System.out.println("(" + imageX + ", " + imageY + ")");

                int green = MapPalette.matchColor(Color.GREEN);

                int greenPixels = 0;

                for (byte pixel : pixels) {
                    if (pixel == green) greenPixels++;
                }

                System.out.println("Green pixels = " + greenPixels);

                byte[] colors = getSubImage(pixels, imageX, imageY);
//                byte[] colors = getSubImage(pixels, imageX * 128, imageY * 128);

                PacketPlayOutMap packet = new PacketPlayOutMap(mapIDs.get(mapID), (byte) 4, false, new ArrayList<>(), colors, 0, 0, 128, 128);

                List<UUID> finalViewers = this.viewers == null ? Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()) : this.viewers;

                finalViewers.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(player -> ((CraftPlayer) player).getHandle().playerConnection.networkManager.sendPacket(packet));

                mapID++;
            }
        }

//        System.out.println("MAX X IS: " + this.x);
    }

    private byte[] getSubImage(byte[] image, int xPos, int yPos) {
        System.out.println("(" + xPos + ", " + yPos + ")");
        byte[] sub = getSquare(image, xPos, yPos, 128);

        sub = rotateCube270(sub, 128, 128);

//        for (int y = 0; y < 128; y++) {
//            for (int x = 0; x < 128; x++) {
//                int imageX = x + xPos;
//                int imageY = y + yPos;
//
//                int[] coords = rotateCoords270(x, y);
//
//                setPixel(sub, coords[0], coords[1], getPixel(image, imageX, imageY));
////                setPixel(image, imageX, imageY, (byte) -1);
//            }
//        }

//        int green = MapPalette.matchColor(Color.GREEN);
//
//        int greenPixels = 0;
//
//        for (byte pixel : sub) {
//            if (pixel == green) greenPixels++;
//        }
//
//        System.out.println("Green pixels FOR SUBBBB = " + greenPixels);

//        System.out.println("(" + xPos + ", " + yPos + ")");

        return sub;
    }

    public static byte[] getSquare(byte[] array, int xQuadrant, int yQuadrant, int sectionSize) {
        byte[] sub = new byte[sectionSize * sectionSize];

        int startX = xQuadrant * sectionSize;
        int startY = yQuadrant * sectionSize;

        int rowLength = (int) Math.sqrt(array.length);

        for (int y = 0; y < sectionSize; y++) {
            System.arraycopy(array, startX + ((startY + y) * rowLength), sub, y * sectionSize, sectionSize);
        }

        return sub;
    }


    public byte getPixel(int x, int y) {
        return this.pixels[y * (128 * this.height) + x];
    }

    public void setPixel(int x, int y, byte pixel) {
        this.pixels[y * 128 + x] = pixel;
    }

    private byte[] rotateCube270(byte[] bytes, int width, int height) {
        byte[] ret = bytes.clone();
//        BufferedImage dest = new BufferedImage(h, w, src.getType());
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                setPixelFrom(ret, getPixelFrom(bytes, width, x, y), width, y, x);
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

    private int[] rotateCoords270(int x, int y) {
        int[] ret = new int[2];

//        System.out.println("x = " + x);

//        if (x > this.x) this.x = x;

        ret[0] = y;     // Setting X
        ret[1] = 127 - x;     // Setting Y

        return ret;
    }

    public static BufferedImage rotateClockwise90(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage dest = new BufferedImage(h, w, src.getType());
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                dest.setRGB(y,x,src.getRGB(x,y));
        return dest;
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


    private void clear() {
        int mapID = 0;

        for (int imageY = 0; imageY < height; imageY++) {
            for (int imageX = 0; imageX < width; imageX++) {
                byte[] colors = new byte[width * height * 128 * 128];

                Arrays.fill(colors, MapPalette.matchColor(Color.WHITE));

                PacketPlayOutMap packet = new PacketPlayOutMap(mapIDs.get(mapID), (byte) 4, false, new ArrayList<>(), colors, 0, 0, 128, 128);

                List<UUID> finalViewers = this.viewers == null ? Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()) : this.viewers;

                finalViewers.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(player -> ((CraftPlayer) player).getHandle().playerConnection.networkManager.sendPacket(packet));

                mapID++;
            }
        }
    }
}
