package com.uddernetworks.mapcanvas.api;

import com.uddernetworks.mapcanvas.api.objects.Clickable;
import com.uddernetworks.mapcanvas.api.objects.MapObject;
import net.minecraft.server.v1_12_R1.PacketPlayOutMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The class for each individual map canvas.
 */
public class MapCanvas {

    private JavaPlugin javaPlugin;
    private WallDirection wallDirection;
    private BlockFace mapFace;
    private int width;
    private int height;
    private List<MapCanvasSection> mapCanvasSections = new ArrayList<>();
    private List<Integer> mapIDs;
    private List<MapObject> mapObjects;
    private List<UUID> viewers;
    private List<byte[]> cachedSections = new ArrayList<>();
    private byte[] pixels;
    private UUID uuid;

    /**
     * Created a new MapCanvas using a short starting map ID. Anyone can view this map wall.
     * @param javaPlugin The current plugin's JavaPlugin
     * @param mapCanvasAPI The MapCanvasAPI instance
     * @param wallDirection The direction the map wall is in
     * @param mapFace The BlockFace the maps are on the wall
     * @param width The width in blocks the wall of maps is
     * @param height The height in blocks the wall of maps is
     * @param startID The of the first map created for the wall;
     *                The list of Map IDs will be generated in sequential order
     *                starting with this value.
     */
    public MapCanvas(JavaPlugin javaPlugin, MapCanvasAPI mapCanvasAPI, WallDirection wallDirection, BlockFace mapFace, int width, int height, int startID) {
        this(javaPlugin, mapCanvasAPI, wallDirection, mapFace, width, height, (short) startID);
    }

    /**
     * Created a new MapCanvas using an int starting map ID. Anyone can view this map wall.
     * @param javaPlugin The current plugin's JavaPlugin
     * @param mapCanvasAPI The MapCanvasAPI instance
     * @param wallDirection The direction the map wall is in
     * @param mapFace The BlockFace the maps are on the wall
     * @param width The width in blocks the wall of maps is
     * @param height The height in blocks the wall of maps is
     * @param startID The of the first map created for the wall;
     *                The list of Map IDs will be generated in sequential order
     *                starting with this value.
     */
    public MapCanvas(JavaPlugin javaPlugin, MapCanvasAPI mapCanvasAPI, WallDirection wallDirection, BlockFace mapFace, int width, int height, short startID) {
        this(javaPlugin, mapCanvasAPI, wallDirection, mapFace, width, height, IntStream.rangeClosed(startID, startID + (width * height)).boxed().collect(Collectors.toList()));
    }

    /**
     * Created a new MapCanvas using the given Map IDs. Anyone can view this map wall.
     * @param javaPlugin The current plugin's JavaPlugin
     * @param mapCanvasAPI The MapCanvasAPI instance
     * @param wallDirection The direction the map wall is in
     * @param mapFace The BlockFace the maps are on the wall
     * @param width The width in blocks the wall of maps is
     * @param height The height in blocks the wall of maps is
     * @param mapIDs The map wall's IDs to be used
     */
    public MapCanvas(JavaPlugin javaPlugin, MapCanvasAPI mapCanvasAPI, WallDirection wallDirection, BlockFace mapFace, int width, int height, List<Integer> mapIDs) {
        this(javaPlugin, mapCanvasAPI, wallDirection, mapFace, width, height, mapIDs, new ArrayList<>());
    }

    /**
     * Created a new MapCanvas using a short starting map ID.
     * @param javaPlugin The current plugin's JavaPlugin
     * @param mapCanvasAPI The MapCanvasAPI instance
     * @param wallDirection The direction the map wall is in
     * @param mapFace The BlockFace the maps are on the wall
     * @param width The width in blocks the wall of maps is
     * @param height The height in blocks the wall of maps is
     * @param mapIDs The map wall's IDs to be used
     * @param viewers The users' UUIDs that can view this map wall. If empty, all players
     *                can view the map wall.
     */
    public MapCanvas(JavaPlugin javaPlugin, MapCanvasAPI mapCanvasAPI, WallDirection wallDirection, BlockFace mapFace, int width, int height, List<Integer> mapIDs, List<UUID> viewers) {
        this.javaPlugin = javaPlugin;
        this.width = width;
        this.height = height;
        this.mapIDs = mapIDs;
        this.mapObjects = new ArrayList<>();
        this.viewers = viewers;
        this.uuid = UUID.randomUUID();
        this.wallDirection = wallDirection;
        this.mapFace = mapFace;

        if (wallDirection == WallDirection.X_AXIS) {
            if (mapFace != BlockFace.NORTH && mapFace != BlockFace.SOUTH) throw new IllegalArgumentException("Map position must be EAST or WEST for a wall on the X Axis");
        } else {
            if (mapFace != BlockFace.EAST && mapFace != BlockFace.WEST) throw new IllegalArgumentException("Map position must be NORTH or SOUTH for a wall on the Z Axis");
        }

        mapCanvasAPI.getMapCanvasManager().addCanvas(this);

        for (int i = 0; i < width * height; i++) {
            this.cachedSections.add(new byte[0]);
        }
    }

    /**
     * Adds an object to the canvas to be drawn in updates. This initializes the object
     * when the {@link #initialize(World)} is invoked, or immediately if it has already
     * been invoked.
     * @param mapObject The object to add to the canvas.
     */
    public void addObject(MapObject mapObject) {
        this.mapObjects.add(mapObject);

        if (this.pixels != null && this.pixels.length != 0) {
            mapObject.initialize(this);
        }
    }

    /**
     * Should only be ran once, this initializes each MapObject in the internal list,
     * and created all the MapSections used.
     * @param world The world where the map wall is
     */
    public void initialize(World world) {
        List<ItemFrame> itemFrames = world.getEntities().stream().filter(ItemFrame.class::isInstance).map(ItemFrame.class::cast).filter(itemFrame -> itemFrame.getItem().getType() == Material.MAP).collect(Collectors.toList());

        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                short mapID = this.mapIDs.get(index).shortValue();
                MapView mapView = Bukkit.getServer().getMap(mapID);
                List<MapRenderer> removing = new ArrayList<>(mapView.getRenderers());
                removing.forEach(mapView::removeRenderer);

                ItemFrame itemFrame = itemFrames.stream().filter(frame -> frame.getItem().getDurability() == mapID).findFirst().orElse(null);

                if (itemFrame == null) return;

                Location location = itemFrame.getLocation().getBlock().getLocation().clone().add(itemFrame.getAttachedFace().getModX(), itemFrame.getAttachedFace().getModY(), itemFrame.getAttachedFace().getModZ());

                MapCanvasSection mapCanvasSection = new MapCanvasSection(this, location, itemFrame, mapID, x + 1, height - y);
                this.mapCanvasSections.add(mapCanvasSection);
                index++;
            }
        }

        mapObjects.forEach(mapObject -> mapObject.initialize(this));

        paint();
    }

    /**
     * Invokes the {@link MapObject#draw(MapCanvas)} method on each added MapObject,
     * and then invokes the {@link #updateMaps()} method.
     */
    public void paint() {
        this.pixels = new byte[width * height * 128 * 128];

        Arrays.fill(this.pixels, MapPalette.matchColor(Color.WHITE));

        mapObjects.forEach(mapObject -> mapObject.draw(this));

        updateMaps();
    }

    /**
     * Updates all maps using the internal pixel array, by sending packets to each viewer
     * of the MapCanvas. Packets for each map are only sent if the map has not been modified
     * to prevent the entire wall from being resent with one pixel modified.
     */
    public void updateMaps() {
        int mapID = 0;

        for (int imageY = 0; imageY < height; imageY++) {
            for (int imageX = 0; imageX < width; imageX++) {
                byte[] colors = getSubImage(pixels, imageX, imageY);

                for (int i = 0; i < colors.length; i++) {
                    if (colors[i] == -1) colors[i] = 0;
                }

                if (!Arrays.equals(cachedSections.get(mapID), colors)) {
                    cachedSections.set(mapID, colors);

                    PacketPlayOutMap packet = new PacketPlayOutMap(this.mapCanvasSections.get(mapID).getMapID(), (byte) 4, false, new ArrayList<>(), colors, 0, 0, 128, 128);

                    List<UUID> finalViewers = this.viewers == null || this.viewers.isEmpty() ? Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()) : this.viewers;

                    finalViewers.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(player -> ((CraftPlayer) player).getHandle().playerConnection.networkManager.sendPacket(packet));
                }

                mapID++;
            }
        }
    }

    private byte[] getSubImage(byte[] image, int xPos, int yPos) {
        byte[] sub = getSquare(image, xPos, yPos, 128);

        sub = rotateCube270(sub, 128, 128);

        return sub;
    }

    private byte[] getSquare(byte[] array, int xQuadrant, int yQuadrant, int sectionSize) {
        byte[] sub = new byte[sectionSize * sectionSize];

        int startX = xQuadrant * sectionSize;
        int startY = yQuadrant * sectionSize;

        int rowLength = this.width * 128;

        for (int y = 0; y < sectionSize; y++) {
            System.arraycopy(array, startX + ((startY + y) * rowLength), sub, y * sectionSize, sectionSize);
        }

        return sub;
    }

    /**
     * Gets all clickable objects that intersect with the given coordinates.
     * @param x The X coordinate
     * @param y The Y coordinate
     * @return All clickable objects intersecting with the given coordinates
     */
    public List<Clickable> getClickableInPosition(int x, int y) {
        return this.mapObjects.stream().filter(Clickable.class::isInstance).map(Clickable.class::cast).filter(objectBounds -> objectBounds.getBounds().positionIsIn(x, y)).collect(Collectors.toList());
    }

    /**
     * Currently useless - Meant for touching up internal position modification.
     * when setting positions of elements.
     * @param x X coordinate to modify
     * @return Modified X coordinate
     */
    public int migrateX(int x) {
        return x;
    }

    /**
     * Meant for touching up internal position modification, in this case it inverts.
     * the given Y coordinate.
     * when setting positions of elements.
     * @param y Y coordinate to modify
     * @return Modified X coordinate
     */
    public int migrateY(int y) {
        return (this.height * 128) - 1 - y;
    }

    /**
     * Gets the current pixel at the given location.
     * @param x X position to get pixel from
     * @param y Y position to get pixel from
     * @return The pixel color at the given position
     */
    public byte getPixel(int x, int y) {
        return this.pixels[y * (128 * this.width - 1) + x];
    }

    /**
     * Sets the pixel at the given coordinates to the given byte color.
     * @param x The X coordinate to set the pixel to
     * @param y The Y coordinate to set the pixel to
     * @param pixel The byte color of the pixel at the given coordinates should be
     */
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

    /**
     * Gets the width in blocks of the current map wall
     * @return The width in blocks of the current map wall
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height in blocks of the current map wall
     * @return The height in blocks of the current map wall
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the given JavaPlugin for use in MapObjects
     * @return The given JavaPlugin for use in MapObjects
     */
    public JavaPlugin getPlugin() {
        return javaPlugin;
    }

    /**
     * Gets all the current MapCanvasSections used
     * @return All the current MapCanvasSections used
     */
    public List<MapCanvasSection> getMapCanvasSections() {
        return mapCanvasSections;
    }

    /**
     * Gets all the set viewers of the MapCanvas
     * @return All the set viewers of the MapCanvas
     */
    public List<UUID> getViewers() {
        return viewers;
    }

    /**
     * Gets the internal UUID of the MapCanvas
     * @return The internal UUID of the MapCanvas
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Gets the wall direction the map wall is in
     * @return The wall direction the map wall is in
     */
    public WallDirection getWallDirection() {
        return wallDirection;
    }

    /**
     * Gets the BlockFace all the maps are on the wall
     * @return The BlockFace all the maps are on the wall
     */
    public BlockFace getMapFace() {
        return mapFace;
    }

    /**
     * Removes a viewer from the viewer list
     * @param uuid What viewer to remove
     */
    public void removeViewer(UUID uuid) {
        this.viewers.remove(uuid);
    }
}
