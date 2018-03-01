package com.uddernetworks.videoplayer.api.objects;

import net.minecraft.server.v1_12_R1.Item;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ItemRender extends Image {

    public ItemRender(JavaPlugin javaPlugin, Material material, int x, int y, int width, int height) {
        super(x, y, width, height);

        setImage(new File(javaPlugin.getDataFolder(), "items" + File.separator + getNameFor(material) + ".png"));
    }

    private String getNameFor(Material material) {
        return Item.REGISTRY.b(Item.getById(material.getId())).getKey();
    }
}
