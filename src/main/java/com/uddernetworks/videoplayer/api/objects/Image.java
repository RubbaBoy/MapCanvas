package com.uddernetworks.videoplayer.api.objects;

import com.uddernetworks.videoplayer.api.MapCanvas;
import org.bukkit.map.MapPalette;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Image extends Clickable implements MapObject {

    private MapCanvas mapCanvas;
    private AtomicReference<BufferedImage> bufferedImage = new AtomicReference<>();
    private int x;
    private int y;
    private int width;
    private int height;
    private ObjectBounds objectBounds;
    private AtomicBoolean imageLoaded = new AtomicBoolean(false);
    private AtomicBoolean tryingToDraw = new AtomicBoolean(false);

    public Image(String url, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.objectBounds = new ObjectBounds(x, y, x + width, y + height);

        new Thread(() -> {
            try {
                System.out.println("GEtting image");
                this.bufferedImage.set(resizeImage(ImageIO.read(new URL(url)), width, height));
                System.out.println("Donbe");
                this.imageLoaded.set(true);

                if (this.tryingToDraw.get()) {
                    this.draw(mapCanvas);
                    mapCanvas.updateMaps();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void initialize(MapCanvas mapCanvas) {
        this.x = mapCanvas.migrateX(this.x);
        this.y = mapCanvas.migrateY(this.y + this.height);
    }

    @Override
    public void draw(MapCanvas mapCanvas) {
        System.out.println("Drawing");
        this.mapCanvas = mapCanvas;
        if (!this.imageLoaded.get()) {
            tryingToDraw.set(true);
            return;
        }

        System.out.println("Drawing");

        BufferedImage image = this.bufferedImage.get();

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                int imageX = x + this.x;
                int imageY = y + this.y;

                Color imageColor = new Color(image.getRGB(x, y), true);

                if (imageColor.getAlpha() == 255) {
                    mapCanvas.setPixel(imageX, imageY, MapPalette.matchColor(imageColor));
                }
            }
        }
    }

    @Override
    public ObjectBounds getBounds() {
        return objectBounds;
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

    public boolean imageLoaded() {
        return this.imageLoaded.get();
    }
}
