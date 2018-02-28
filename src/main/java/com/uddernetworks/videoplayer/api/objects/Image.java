package com.uddernetworks.videoplayer.api.objects;

import com.uddernetworks.videoplayer.api.MapCanvas;
import com.uddernetworks.videoplayer.api.MapObject;
import org.bukkit.map.MapPalette;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Image implements MapObject {

    private BufferedImage bufferedImage;
    private int x;
    private int y;
    private int width;
    private int height;

    public Image(URL url, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        try {
            this.bufferedImage = resizeImage(ImageIO.read(url), width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(MapCanvas mapCanvas) {
        this.x = mapCanvas.migrateX(this.x);
        this.y = mapCanvas.migrateY(this.y + this.height);
    }

    @Override
    public void draw(MapCanvas mapCanvas) {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                int imageX = x + this.x;
                int imageY = y + this.y;

                Color imageColor = new Color(this.bufferedImage.getRGB(x, y), true);

                if (imageColor.getAlpha() == 255) {
                    mapCanvas.setPixel(imageX, imageY, MapPalette.matchColor(imageColor));
                }
            }
        }
    }

    private BufferedImage resizeImage(BufferedImage image, int width, int height) {
        if (image.getWidth() == width && image.getHeight() == height) return image;
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
