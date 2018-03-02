package com.uddernetworks.mapcanvas.api.objects;

import com.uddernetworks.mapcanvas.api.MapCanvasAPI;
import net.minecraft.server.v1_12_R1.Item;
import org.apache.commons.io.FileUtils;
import org.bukkit.Material;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Renders the image of most items and blocks to the canvas.
 */
public class ItemRender extends Image {

    /**
     * Creates a rendering of the specified Material at the specified position.
     * If the textures haven't been downloaded before hand, they will be downloaded
     * to the plugin's data directory in an /items/ directory.
     * @param mapCanvasAPI The MapCanvasAPI plugin instance
     * @param material The Material that should be rendered
     * @param x The lower left X coordinate of where the image should be rendered
     * @param y The lower left Y coordinate of where the image should be rendered
     * @param width The width of the item's image. The image will be scaled to the
     *              specified width.
     * @param height The height of the item's image. The image will be scaled to the
     *              specified height.
     */
    public ItemRender(MapCanvasAPI mapCanvasAPI, Material material, int x, int y, int width, int height) {
        super(x, y, width, height);

        File itemsFolder = new File(mapCanvasAPI.getDataFolder(), "items");

        if (!itemsFolder.exists()) {
            downloadItems(itemsFolder, material, mapCanvasAPI);
        } else {
            setImage(new File(mapCanvasAPI.getDataFolder(), "items" + File.separator + getNameFor(material) + ".png"));
        }
    }

    private String getNameFor(Material material) {
        return Item.REGISTRY.b(Item.getById(material.getId())).getKey();
    }

    private void downloadItems(File itemsFolder, Material material, MapCanvasAPI mapCanvasAPI) {
        new Thread(() -> {
            try {
                File itemsZip = new File(itemsFolder.getParentFile(), "items.zip");
                itemsZip.createNewFile();

                FileUtils.copyURLToFile(new URL("https://rubbaboy.me/files/items.zip"), itemsZip);

                upZipFile(itemsZip, itemsFolder.getParentFile());
            } catch (IOException e) {
                e.printStackTrace();
            }

            setImage(new File(mapCanvasAPI.getDataFolder(), "items" + File.separator + getNameFor(material) + ".png"));
        }).start();
    }

    public void upZipFile(File zipFile, File outputFolder) {
        byte[] buffer = new byte[1024];

        try {

            //create output directory is not exists
            if (!outputFolder.exists()) {
                outputFolder.mkdir();
            }

            //get the zip file content
            ZipInputStream zipInputStream =
                    new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

                new File(newFile.getParent()).mkdirs();

                FileOutputStream fileOutputStream = new FileOutputStream(newFile);

                int len;
                while ((len = zipInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, len);
                }

                fileOutputStream.close();
                zipEntry = zipInputStream.getNextEntry();
            }

            zipInputStream.closeEntry();
            zipInputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
