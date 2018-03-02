package com.uddernetworks.mapcanvas.api;

import net.minecraft.server.v1_12_R1.WorldMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

/**
 * A class responsible of creating walls that will look normal
 * with the rest of the plugin.
 */
public class MapWall {

    /**
     * Created the blocks and entities required for a map wall.
     * @param startLocation The location to start the wall creation
     * @param wallDirection The WallDirection the wall is to be built in
     * @param blockFace The BlockFace the ItemFrame should be on.
     *                  If the WallDirection is set to {@link WallDirection#X_AXIS}, the block face can not be
     *                  {@link BlockFace#NORTH} or {@link BlockFace#SOUTH}.
     *                  Likewise, if the WallDirection is set to {@link WallDirection#Z_AXIS}, the block face can not be
     *                  {@link BlockFace#EAST} or {@link BlockFace#WEST}.
     * @param material The Material of the block behind the ItemFrames
     * @param width The width in blocks the MapWall should be
     * @param height The height in blocks the MapWall should be
     * @param startID The starting map ID that should be iterated from to use
     */
    public MapWall(Location startLocation, WallDirection wallDirection, BlockFace blockFace, Material material, int width, int height, short startID) {
        if (wallDirection == WallDirection.X_AXIS) {
            if (blockFace != BlockFace.NORTH && blockFace != BlockFace.SOUTH) throw new IllegalArgumentException("Map position must be EAST or WEST for a wall on the X Axis");
        } else {
            if (blockFace != BlockFace.EAST && blockFace != BlockFace.WEST) throw new IllegalArgumentException("Map position must be NORTH or SOUTH for a wall on the Z Axis");
        }

        Location mapLocation = startLocation.clone().add(0, height, 0);

        short excessMapIndex = startID;

        int xMod = (blockFace == BlockFace.NORTH ? -1 : 1) * wallDirection.getWallModX();
        int zMod = (blockFace == BlockFace.EAST ? -1 : 1) * wallDirection.getWallModZ();

        for (int y = height; y > 0; y--) {
            for (int x = 0; x < width; x++) {
                mapLocation.getBlock().setType(material);

                new WorldMap("map_" + excessMapIndex);

                Location itemFrameLoc = mapLocation.clone().add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());

                itemFrameLoc.getBlock().setType(Material.ITEM_FRAME);
                ItemFrame frame = (ItemFrame) startLocation.getWorld().spawnEntity(itemFrameLoc, EntityType.ITEM_FRAME);
                frame.setFacingDirection(blockFace);
                frame.setItem(new ItemStack(Material.MAP, 1, excessMapIndex));

                excessMapIndex++;

                mapLocation.add(xMod, 0, zMod);
            }

            mapLocation.subtract(xMod * width, 1, zMod * width);
        }

        startLocation.getWorld().getEntities().stream().filter(entity -> entity instanceof ItemFrame).map(ItemFrame.class::cast).forEach(itemFrame -> itemFrame.setRotation(Rotation.CLOCKWISE_45));
    }
}
