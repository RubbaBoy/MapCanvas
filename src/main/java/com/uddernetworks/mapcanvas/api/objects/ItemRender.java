package com.uddernetworks.mapcanvas.api.objects;

import net.minecraft.server.v1_12_R1.Item;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Renders the image of most items and blocks to the canvas.
 */
public class ItemRender extends Image {

    /**
     * Creates a rendering of the specified Material at the specified position.
     * If the textures haven't been downloaded before hand, they will be downloaded
     * to the plugin's data directory in an /items/ directory.
     * @param javaPlugin The plugin's JavaPlugin to get the data directory
     * @param material The Material that should be rendered
     * @param x The lower left X coordinate of where the image should be rendered
     * @param y The lower left Y coordinate of where the image should be rendered
     * @param width The width of the item's image. The image will be scaled to the
     *              specified width.
     * @param height The height of the item's image. The image will be scaled to the
     *              specified height.
     */
    public ItemRender(JavaPlugin javaPlugin, Material material, int x, int y, int width, int height) {
        super(x, y, width, height);

        setImage(new File(javaPlugin.getDataFolder(), "items" + File.separator + getNameFor(material) + ".png"));
    }

    private String getNameFor(Material material) {
        return Item.REGISTRY.b(Item.getById(material.getId())).getKey();
    }
}
